package com.example.musicrec;

import java.util.List;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.echonest.api.v4.Artist;
import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Params;
import com.echonest.api.v4.Song;
import com.echonest.api.v4.SongParams;
import com.google.android.youtube.player.YouTubeIntents;

public class CurrSongWindow extends Activity {

  private EchoNestAPI en;

  String artist, title;
  String url;
  Artist currEchoArtist;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.curr_song_window);

    /* TEMP SOL */
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
        .permitAll().build();
    StrictMode.setThreadPolicy(policy);
    en = new EchoNestAPI("FUS98WPLXFNIHZHHG");

    Bundle extras = getIntent().getExtras();
    if (extras != null) {
      artist = extras.getString("ARTIST");
      title = extras.getString("TITLE");
    }

    // SongParams p = new Params();
    // p.add("title", title);
    // p.add("results", 10);
    // p.add("artist", artist);
    // p.includeTracks(); // the album art is in the track data
    // p.setLimit(true); // only return songs that have track data
    // p.addIDSpace("7digital-US");

    SongParams p = new SongParams();
    p.setArtist(artist);
    p.setTitle(title);
    p.includeTracks(); // the album art is in the track data
    p.setLimit(true); // only return songs that have track data
    
    p.addIDSpace("7digital-US");

    try {
      List<Song> songs = en.searchSongs(p);
      for (Song song : songs) {
        // get the release data from the first track returned for each song
        String url = song.getString("tracks[0].release_image");
        Log.i("SONG INFO", song.getTitle() + " " + url);

      }
    } catch (EchoNestException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  public void openYoutubeSearch(View v) {
    Intent intent = YouTubeIntents.createSearchIntent(this, title + " by "
        + artist);
    startActivity(intent);
  }

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
