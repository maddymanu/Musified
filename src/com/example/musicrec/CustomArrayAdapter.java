package com.example.musicrec;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseUser;

public class CustomArrayAdapter extends ArrayAdapter<Song> {

  private final Context context;
  private final ArrayList<Song> songArrayList;

  public CustomArrayAdapter(Context context, ArrayList<Song> itemsArrayList) {

    super(context, R.layout.single_song_item, itemsArrayList);

    this.context = context;
    this.songArrayList = itemsArrayList;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {

    // 1. Create inflater
    LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    // 2. Get rowView from inflater
    View rowView = inflater.inflate(R.layout.single_song_item, parent, false);

    // 3. Get the two text view from the rowView
    TextView titleView = (TextView) rowView.findViewById(R.id.title);
    TextView artistView = (TextView) rowView.findViewById(R.id.artist);

    // 4. Set the text for textView
    Song currSong = (Song) songArrayList.get(position);
    // currSong.get
    titleView.setText(currSong.get("title").toString());
    artistView.setText(currSong.get("artist").toString());

    final ParseUser songUser = currSong.getAuthor();
    try {
      songUser.fetchIfNeeded();
    } catch (ParseException e1) {
      e1.printStackTrace();
    }

    AsyncTask<Void, Void, Bitmap> t = new AsyncTask<Void, Void, Bitmap>() {
      protected Bitmap doInBackground(Void... p) {
        Bitmap bm = null;
        try {
          URL aURL = new URL("http://graph.facebook.com/" + songUser.get("fbId")
              + "/picture?type=large");
          URLConnection conn = aURL.openConnection();
          conn.setUseCaches(true);
          conn.connect();
          InputStream is = conn.getInputStream();
          BufferedInputStream bis = new BufferedInputStream(is);
          bm = BitmapFactory.decodeStream(bis);
          bis.close();
          is.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
        return bm;
      }

      protected void onPostExecute(Bitmap bm) {

//        Drawable drawable = new BitmapDrawable(getResources(), bm);
//
//        photoImageView.setBackgroundDrawable(drawable);

      }
    };
    t.execute();

    // 5. return rowView
    return rowView;
  }

}
