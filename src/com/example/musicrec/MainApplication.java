package com.example.musicrec;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.PushService;

/*
 * This class is essentially beginning the different SDK's for the app.
 * Parse and Facebook.
 */
public class MainApplication extends Application {
  @Override
  public void onCreate() {
      super.onCreate();
      Parse.initialize(this, "gyy3EnWqM4shEJQTBDvz01HHKERCmt6ldNZFei9H",
          "j8H1tYNTndi5SdmMmxbRBUyaKZ8X3kJmvLWQvAIc");
      ParseFacebookUtils.initialize("830750263621357");
      ParseObject.registerSubclass(Song.class);
      PushService.setDefaultPushCallback(this, CurrSongWindow.class);
      ParseInstallation.getCurrentInstallation().saveInBackground();
      //ParseAnalytics.trackAppOpened(getIntent());
      Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);
  }
}
