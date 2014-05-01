package com.example.musicrec;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class CurrSongWindow extends Activity{
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //setContentView(R.layout.activity_main);
    
    Bundle extras = getIntent().getExtras();
    if (extras != null) {
        String value = extras.getString("ARTIST");
        Log.i("test" , value);
    }
    
  }
}
