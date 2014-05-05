package com.example.musicrec;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseUser;

@SuppressLint("NewApi")
public class CustomArrayAdapter extends ArrayAdapter<Song> {

  private final Context context;
  private final ArrayList<Song> songArrayList;
  private int count = 0;

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
    final TextView likeButtonTV = (TextView) rowView
        .findViewById(R.id.likeButton);
    final TextView heartTV = (TextView) rowView.findViewById(R.id.heartTV);

    final ImageView profileImage = (ImageView) rowView
        .findViewById(R.id.profileImage);

    // 4. Set the text for textView
    final Song currSong = (Song) songArrayList.get(position);
    // currSong.get
    String formattedTitle = currSong.get("title").toString().substring(0, 1)
        .toUpperCase()
        + currSong.get("title").toString().substring(1).toLowerCase();
    titleView.setText(formattedTitle);
    artistView.setText(currSong.get("artist").toString());

    final ParseUser songUser = currSong.getAuthor();
    try {
      songUser.fetchIfNeeded();
    } catch (ParseException e1) {
      e1.printStackTrace();
    }

    likeButtonTV.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        Log.i("Liked!", "Should only show once for 1 song!");
        count = currSong.getLikes();
        count++;
        currSong.setLikes(count);
        likeButtonTV.setClickable(false);
        currSong.saveInBackground();
      }
    });

    Typeface font = Typeface.createFromAsset(context.getAssets(),
        "fontawesome-webfont.ttf");
    heartTV.setTypeface(font);

    final String userFacebookId = songUser.get("fbId").toString();
    AsyncTask<Void, Void, Bitmap> t = new AsyncTask<Void, Void, Bitmap>() {
      protected Bitmap doInBackground(Void... p) {
        Bitmap bm = null;
        try {

          String url = String.format("https://graph.facebook.com/%s/picture",
              userFacebookId);

          InputStream is = new URL(url).openStream();
          // BufferedInputStream bis = new BufferedInputStream(is);
          bm = BitmapFactory.decodeStream(is);

          // bis.close();
          is.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
        return bm;
      }

      protected void onPostExecute(Bitmap bm) {

        profileImage.setImageBitmap(bm);

      }
    };
    t.execute();

    return rowView;
  }

}
