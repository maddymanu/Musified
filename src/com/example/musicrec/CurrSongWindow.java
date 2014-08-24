package com.example.musicrec;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.echonest.api.v4.Artist;
import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Song;
import com.echonest.api.v4.SongParams;
import com.facebook.android.Facebook;
import com.google.android.youtube.player.YouTubeIntents;

/*
 * This class is used to show the intent for a particular song
 * You open this by clicking on a song's image.
 * 
 * 
 */
public class CurrSongWindow extends Activity {
  
  //Api to get details of the song. (song image, artist details etc.)
  private EchoNestAPI en;
  
  //Strings to hold song details.
  String artist, title;
  //string to hold image url
  String url;
  Artist currEchoArtist;
  //Particular echonest Song
  Song song;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.curr_song_window);
    
    //initializing the imageview
    final ImageView songView = (ImageView) findViewById(R.id.songImage);

    
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
        .permitAll().build();
    StrictMode.setThreadPolicy(policy);
    
    //initializing the song API
    en = new EchoNestAPI("FUS98WPLXFNIHZHHG");
    
    //getting the song details from the calling activity
    Bundle extras = getIntent().getExtras();
    if (extras != null) {
      artist = extras.getString("ARTIST");
      title = extras.getString("TITLE");
    }
    
    //This is for notifications. Once a user clicks on a notification, the window opens
    //with particular song
    String jsonData = extras.getString("com.parse.Data");
    
    //if data is not null, extract song details
    if(jsonData != null) {
      JSONObject json;
      try {
        json = new JSONObject(jsonData);
        artist = json.getString("songartist");
        title = json.getString("songtitle");
        
      } catch (JSONException e1) {
        e1.printStackTrace();
      }
    }

    
    
    //Parameters to search for songs on the Echonest API
    SongParams p1 = new SongParams();
    p1.setArtist(artist);
    p1.setTitle(title);
    p1.includeTracks(); // the album art is in the track data
    p1.setLimit(true); // only return songs that have track data
    p1.addIDSpace("7digital-US");

    try {
      //trying to search for the list of songs
      List<com.echonest.api.v4.Song> songs = en.searchSongs(p1);
      
      //if list is not empty, get the first song
      if (songs.size() != 0) {
        song = songs.get(0);
        try {
          //also get the url of the track image
          url = song.getString("tracks[0].release_image");
        } catch (IndexOutOfBoundsException e) {

        }
      } else {
        //TODO ---- CHANGE TO ARTIST IMAGE
        //if no song is found, look for other images
        SongParams p2 = new SongParams();
        p2.setTitle(title);
        p2.includeTracks(); // the album art is in the track data
        p2.setLimit(true); // only return songs that have track data
        p2.addIDSpace("7digital-US");
        
        
        List<com.echonest.api.v4.Song> songsWithoutArtists = en
            .searchSongs(p2);
        if (songsWithoutArtists.size() != 0) {
          song = songsWithoutArtists.get(0);
          try {
            url = song.getString("tracks[0].release_image");
          } catch (IndexOutOfBoundsException e) {

          }
        }
      }

      /* for SONG image */
      //Sets the song image on the imageview.
      AsyncTask<Void, Void, Bitmap> songImageTask = new AsyncTask<Void, Void, Bitmap>() {
        protected Bitmap doInBackground(Void... p) {
          Bitmap bm = null;
          try {
            
            //Inputstream for the image url
            InputStream is = new URL(url).openStream();
            
            //Custom options for the Bitmap
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            
            BitmapFactory.decodeStream(is, null, options);
            
            //calulates the image size based on the parameters
            options.inSampleSize = calculateInSampleSize(options, 200, 200);
            options.inJustDecodeBounds = false;
            is.close();
            
            //reopens the inputstream.
            is = new URL(url).openStream();
            bm = BitmapFactory.decodeStream(is, null, options);
            
            //calculates the new width based on original width
            int width = bm.getWidth();
            int height = bm.getHeight();
            int newWidth = 200;
            int newHeight = 200;
            
            //calculates the scaled width
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            
            Matrix matrix = new Matrix();
            // resize the bit map
            matrix.postScale(scaleWidth, scaleHeight);
            
            //creates a new bitmap with the given details
            bm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
            //used to round of the corners of the image
            bm = CustomArrayAdapter.getRoundedCornerBitmap(bm, 12f);

          } catch (IOException e) {
            e.printStackTrace();
          }
          //returns the bitmap
          return bm;
        }

        protected void onPostExecute(Bitmap bm) {

          /* SET THE SONG IMAAGE VIEW */
          songView.setImageBitmap(bm);

        }
      };
      //executing the asynctask
      songImageTask.execute();

    } catch (EchoNestException e) {
      e.printStackTrace();
    }

  }
  
  /*
   * used to calculate the size for image.
   * 
   */
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
 
  /*
   * Opens the youtube app and searches for the song with the details
   */
  public void openYoutubeSearch(View v) {
    Intent intent = YouTubeIntents.createSearchIntent(this, title + " by "
        + artist);
    startActivity(intent);
  }
  
  /*
   * checks if rdio is installed
   */
  public boolean isRdioAvailable(Context context) {
    
    PackageManager packageManager = context.getPackageManager();
    Intent intent = packageManager
        .getLaunchIntentForPackage("com.rdio.android.ui");

    if (intent == null) {
      // Check if the Brazil version of Rdio is installed
      intent = packageManager
          .getLaunchIntentForPackage("com.rdio.oi.android.ui");
    }

    return intent != null;
  }
  
  /*
   * Is rdio is available, opens it with the given details,
   * otherwise opens the play store
   */
  public void openRdioSearch(View v) {
    boolean isRdioAvail = isRdioAvailable(this);
    if (isRdioAvail) {
      url = new String("rdio://search/" + title + "%20" + artist);
    } else {
      url = "https://play.google.com/store/apps/details?id=com.rdio.android.ui";
    }

    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setData(Uri.parse(url));
    startActivity(intent);
  }
  
  /*
   * Opens the spotify app if installed, otherwise opens the playstore
   */
  public void openSpotifySearch(View v) {

    Intent intent = new Intent(Intent.ACTION_MAIN);
    intent.setAction(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
    intent.setComponent(new ComponentName("com.spotify.mobile.android.ui",
        "com.spotify.mobile.android.ui.Launcher"));
    intent.putExtra(SearchManager.QUERY, title + " " + artist);
    try {
      startActivity(intent);
    } catch (ActivityNotFoundException e) {
      Toast.makeText(this, "You must first install Spotify", Toast.LENGTH_LONG)
          .show();
      Intent i = new Intent(Intent.ACTION_VIEW,
          Uri.parse("market://details?id=com.spotify.mobile.android.ui"));
      startActivity(i);
    }
    

  }

}
