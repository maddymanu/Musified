package com.example.musicrec;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.youtube.player.YouTubeIntents;

public class CurrSongWindow extends Activity {

  String artist, title;

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

}
