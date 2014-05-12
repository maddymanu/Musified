package com.example.musicrec;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.echonest.api.v4.Artist;
import com.echonest.api.v4.EchoNestAPI;
import com.google.android.youtube.player.YouTubeIntents;

public class CurrSongWindow extends Activity {
  
  private EchoNestAPI en;

  String artist, title;
  String url;
  Artist currEchoArtist;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.curr_song_window);
    
   

    Bundle extras = getIntent().getExtras();
    if (extras != null) {
      artist = extras.getString("ARTIST");
      title = extras.getString("TITLE");
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
    intent.setComponent(new ComponentName("com.spotify.mobile.android.ui", "com.spotify.mobile.android.ui.Launcher"));
    intent.putExtra(SearchManager.QUERY, title + " " + artist);
    try {
      startActivity(intent);
    }catch (ActivityNotFoundException e) {
      Toast.makeText(this, "You must first install Spotify", Toast.LENGTH_LONG).show();  
      Intent i = new Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=com.spotify.mobile.android.ui"));
      startActivity(i);
    }
    
  }

}
