package com.example.musicrec;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.google.android.youtube.player.YouTubeIntents;

public class CurrSongWindow extends Activity {

  String artist, title;
  String url;

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
    Intent intent = packageManager.getLaunchIntentForPackage("com.rdio.android.ui");

    if (intent == null) {
        // Check if the Brazil version of Rdio is installed
        intent = packageManager.getLaunchIntentForPackage("com.rdio.oi.android.ui");
    }

    return intent != null;
}
  
  public void openRdioSearch(View v) {
    boolean isRdioAvail = isRdioAvailable(this);
    if(isRdioAvail) {
       url = new String("rdio://search/" + title + "%20" + artist);
    } else {
      url = "https://play.google.com/store/apps/details?id=com.rdio.android.ui";
    }
    
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setData(Uri.parse(url));
    startActivity(intent);
  }

}
