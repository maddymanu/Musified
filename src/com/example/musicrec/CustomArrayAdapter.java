package com.example.musicrec;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.echonest.api.v4.Artist;
import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Image;
import com.echonest.api.v4.PagedList;
import com.parse.ParseException;
import com.parse.ParseUser;

@SuppressLint("NewApi")
public class CustomArrayAdapter extends ArrayAdapter<Song> {

  private final Context context;

  private final ArrayList<Song> songArrayList;
  private int count = 0;
  Artist currEchoArtist;
  private EchoNestAPI en;
  Image currEchoArtistImage;

  public CustomArrayAdapter(Context context, ArrayList<Song> itemsArrayList) {

    super(context, R.layout.single_song_item, itemsArrayList);
    /* TEMP SOL */
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
        .permitAll().build();
    StrictMode.setThreadPolicy(policy);
    en = new EchoNestAPI("FUS98WPLXFNIHZHHG");

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

    final ImageView artistImage = (ImageView) rowView
        .findViewById(R.id.artistImage);

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

    try {
      List<Artist> artists = en
          .searchArtists(currSong.get("artist").toString());
      currEchoArtist = artists.get(0);
      PagedList<Image> imageList = currEchoArtist.getImages(0, 1);
      currEchoArtistImage = imageList.get(0);
      Log.i("CURR", currEchoArtistImage.getURL());
    } catch (EchoNestException e) {
      e.printStackTrace();
    }

    final String userFacebookId = songUser.get("fbId").toString();

    /* for facebook Image */
    AsyncTask<Void, Void, Bitmap> t = new AsyncTask<Void, Void, Bitmap>() {
      protected Bitmap doInBackground(Void... p) {
        Bitmap bm = null;
        try {

          String url = String.format("https://graph.facebook.com/%s/picture",
              userFacebookId);

          InputStream is = new URL(url).openStream();
          bm = BitmapFactory.decodeStream(is);

          // bis.close();
          is.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
        return bm;
      }

      protected void onPostExecute(Bitmap bm) {

        // profileImage.setImageBitmap(bm);

      }
    };
    t.execute();

    /* for artist image */
    AsyncTask<Void, Void, Bitmap> artistImageTask = new AsyncTask<Void, Void, Bitmap>() {
      protected Bitmap doInBackground(Void... p) {
        Bitmap bm = null;
        try {

          

          String url = currEchoArtistImage.getURL();
          InputStream is = new URL(url).openStream();

          BitmapFactory.Options options = new BitmapFactory.Options();
          options.inJustDecodeBounds = true;
          
          BitmapFactory.decodeStream(is, null, options);
          
          options.inSampleSize = calculateInSampleSize(options, 50, 50);
          options.inJustDecodeBounds = false;
          is.close();

          is = new URL(url).openStream();
          bm = BitmapFactory.decodeStream(is, null, options);

          
        } catch (IOException e) {
          e.printStackTrace();
        }
        return bm;
      }

      protected void onPostExecute(Bitmap bm) {

        profileImage.setImageBitmap(bm);
        // Log.i("BITMAP", "hello " + bm.getHeight());

      }
    };
    artistImageTask.execute();

    return rowView;
  }

  private static int calculateInSampleSize(BitmapFactory.Options options,
      int reqWidth, int reqHeight) {

    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

      final int heightRatio = Math.round((float) height / (float) reqHeight);
      final int widthRatio = Math.round((float) width / (float) reqWidth);

      inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
    }
    return inSampleSize;
  }

}
