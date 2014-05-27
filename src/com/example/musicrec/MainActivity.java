package com.example.musicrec;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.PushService;

public class MainActivity extends Activity {

  Map<String, String> map = new HashMap<String, String>();

  // public static final String SERVICECMD =
  // "com.android.music.musicservicecommand";
  // public static final String CMDNAME = "command";
  // public static final String CMDTOGGLEPAUSE = "togglepause";
  // public static final String CMDSTOP = "stop";
  // public static final String CMDPAUSE = "pause";
  // public static final String CMDPREVIOUS = "previous";
  // public static final String CMDNEXT = "next";
  
//  private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
//
//    @Override
//    public void onReceive(Context context, Intent intent) {         
//      Toast.makeText(getApplicationContext(), "onReceive invoked!", Toast.LENGTH_LONG).show();
//    }
//};

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    
    Parse.initialize(this, "gyy3EnWqM4shEJQTBDvz01HHKERCmt6ldNZFei9H",
        "j8H1tYNTndi5SdmMmxbRBUyaKZ8X3kJmvLWQvAIc");
    ParseFacebookUtils.initialize("830750263621357");
    ParseObject.registerSubclass(Song.class);
    PushService.setDefaultPushCallback(this, CurrSongWindow.class);
    ParseInstallation.getCurrentInstallation().saveInBackground();
    ParseAnalytics.trackAppOpened(getIntent());
    Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);

    ParseUser currentUser = ParseUser.getCurrentUser();
    if (currentUser == null) {
      Intent intent = new Intent(this, LoginActivity.class);
      startActivity(intent);
      finish();
    } else {
      ParseInstallation installation = new ParseInstallation();
      installation.put("user",ParseUser.getCurrentUser().getUsername());
      installation.saveInBackground();
      Intent intent = new Intent(MainActivity.this, Welcome.class);
      startActivity(intent);
      
    }
    
    String requestId;
    Uri intentUri = getIntent().getData();
    if (intentUri != null) {
        String requestIdParam = intentUri.getQueryParameter("request_ids");
        if (requestIdParam != null) {
            String array[] = requestIdParam.split(",");
            requestId = array[0];
            Log.i("FB-MainAc", "Request id: "+requestId);
        }
    }
//    IntentFilter iF = new IntentFilter();
//    iF.addAction("com.android.music.metachanged");
//    iF.addAction("com.android.music.playstatechanged");
//    iF.addAction("com.android.music.playbackcomplete");
//    iF.addAction("com.android.music.queuechanged");
//    registerReceiver(mReceiver, iF);

  }
  

//  private BroadcastReceiver mReceiver = new BroadcastReceiver() {
//    @Override
//    public void onReceive(Context context, Intent intent) {
//
//      String action = intent.getAction();
//      String cmd = intent.getStringExtra("command");
//
//      String artist = intent.getStringExtra("artist");
//      String album = intent.getStringExtra("album");
//      String track = intent.getStringExtra("track");
//      
//
//      if (map.get(track) != null) {
//        
//      } else {
//        Log.d("mIntentReceiver.onReceive", action + "/" + cmd);
//        Log.d("Music", artist + ":" + album + ":" + track);
//
//      }
//
//      map.put(track, artist);
//
//      try {
//        Thread.sleep(1000);
//      } catch (InterruptedException e) {
//        e.printStackTrace();
//      }
//
//    }
//  };

}
