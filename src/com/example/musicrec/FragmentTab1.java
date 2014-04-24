package com.example.musicrec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

//THIS CLASS SHOULD YOU FB FRIENDS FEED - MOVE THE UPLOAD PART TO WELCOME ACTIVITY

public class FragmentTab1 extends SherlockFragment {

  public static Boolean IS_RUNNING = false;
  Map<String, String> map = new HashMap<String, String>();

  // public static final String SERVICECMD =
  // "com.android.music.musicservicecommand";
  // public static final String CMDNAME = "command";
  // public static final String CMDTOGGLEPAUSE = "togglepause";
  // public static final String CMDSTOP = "stop";
  // public static final String CMDPAUSE = "pause";
  // public static final String CMDPREVIOUS = "previous";
  // public static final String CMDNEXT = "next";

  Song currSong;
  private ArrayList<Song> songArrayList = null;
  int i = 0;

  ListView listview;
  ArrayAdapter<String> adapter;
  CustomArrayAdapter adapter2;

  @SuppressWarnings("deprecation")
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.tab_feed, container, false);
    songArrayList = new ArrayList<Song>();

    getFacebookIdInBackground();

    com.beardedhen.androidbootstrap.BootstrapButton btn = (com.beardedhen.androidbootstrap.BootstrapButton) rootView
        .findViewById(R.id.logout_btn);
    btn.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        ParseUser.getCurrentUser();
        ParseUser.logOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);

      }
    });

//    try {
//      Thread.sleep(2000);
//    } catch (InterruptedException e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//    }

    RequestAsyncTask r = Request.executeMyFriendsRequestAsync(
        ParseFacebookUtils.getSession(), new Request.GraphUserListCallback() {

          @SuppressWarnings("unchecked")
          @Override
          public void onCompleted(List<GraphUser> users, Response response) {
            if (users != null) {

              List<String> friendsList = new ArrayList<String>();
              for (GraphUser user : users) {
                friendsList.add(user.getId());
              }

              @SuppressWarnings("rawtypes")
              final ParseQuery friendQuery = ParseQuery.getUserQuery();
              friendQuery.whereContainedIn("fbId", friendsList);

              friendQuery.findInBackground(new FindCallback<ParseObject>() {

                @Override
                public void done(List<ParseObject> friendUsers,
                    ParseException e2) {

                  for (ParseObject obj : friendUsers) {
                    // wtf
                    ParseUser currUser = (ParseUser) obj;
                    Log.d("User", "name is " + currUser.get("fbId"));

                    try {
                      currUser.fetchIfNeeded();
                    } catch (ParseException e1) {
                      e1.printStackTrace();
                    }

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Song");
                    query.whereEqualTo("author", currUser);

                    query.findInBackground(new FindCallback<ParseObject>() {

                      @Override
                      public void done(List<ParseObject> songListForSingleUser,
                          ParseException e) {

                        if (e == null && songListForSingleUser != null) {

                          for (i = 0; i < songListForSingleUser.size(); i++) {

                            songArrayList.add((Song) songListForSingleUser
                                .get(i));
                            Log.d("User", "Sizes are WHOLE + CURRENT "
                                + songArrayList.size() + " "
                                + songListForSingleUser.size());

                          }
                          //
                        }

                        Log.d("User", "Whole size is + " + songArrayList.size());

                        // Add to the arrayadapter over here
                        listview = (ListView) getActivity().getWindow()
                            .getDecorView().findViewById(R.id.listview);

                        adapter2 = new CustomArrayAdapter(getActivity(),
                            songArrayList);
                        listview.setAdapter(adapter2);

                      } // here were done getting the songList for each
                        // followed.

                    });

                  }

                }
              });

            }

          }

        });

    IntentFilter iF = new IntentFilter();
    iF.addAction("com.android.music.metachanged");
    iF.addAction("com.android.music.playstatechanged");
    iF.addAction("com.android.music.musicservicecommand");
    iF.addAction("fm.last.android.metachanged");
    iF.addAction("com.sec.android.app.music.metachanged");
    iF.addAction("com.nullsoft.winamp.metachanged");
    iF.addAction("com.amazon.mp3.metachanged");
    iF.addAction("com.miui.player.metachanged");
    iF.addAction("com.real.IMP.metachanged");
    iF.addAction("com.sonyericsson.music.metachanged");
    iF.addAction("com.rdio.android.metachanged");
    iF.addAction("com.samsung.sec.android.MusicPlayer.metachanged");
    iF.addAction("com.andrew.apollo.metachanged");
    iF.addAction("com.spotify.mobile.android.metadatachanged");

    getActivity().registerReceiver(mReceiver, iF);
    return rootView;
  }

  private BroadcastReceiver mReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {

      String action = intent.getAction();
      String cmd = intent.getStringExtra("command");

      String artist = intent.getStringExtra("artist");
      String album = intent.getStringExtra("album");
      String track = intent.getStringExtra("track");

      if (map.get(track) != null) {

      } else {
        Log.d("mIntentReceiver.onReceive", action + "/" + cmd);
        Log.d("Music", artist + ":" + album + ":" + track);
        // everytime theres an update, push it to Parse track.
        currSong = new Song();
        currSong.setAuthor(ParseUser.getCurrentUser());

        if (track != null) {
          currSong.setTitle(track);
        } else {
          currSong.setTitle("unknown");
        }

        if (artist != null) {
          currSong.setArtist(artist);
        } else {
          currSong.setArtist("unknown");
        }

        // currSong.saveInBackground();

      }

      map.put(track, artist);

    }
  };

  @SuppressWarnings("deprecation")
  private static void getFacebookIdInBackground() {
    Request.executeMeRequestAsync(ParseFacebookUtils.getSession(),
        new Request.GraphUserCallback() {
          @Override
          public void onCompleted(GraphUser user, Response response) {
            if (user != null) {
              ParseUser.getCurrentUser().put("fbId", user.getId());
              ParseUser.getCurrentUser().saveInBackground();
              // use this part to get details from the logged in
              // user
            }
          }
        });
  }

}