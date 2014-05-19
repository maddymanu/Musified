package com.example.musicrec;

import java.util.ArrayList;
import java.util.Date;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.google.android.youtube.player.YouTubeIntents;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

//THIS CLASS SHOULD YOU FB FRIENDS FEED - MOVE THE UPLOAD PART TO WELCOME ACTIVITY

public class FragmentTab2 extends SherlockFragment {

  public static Boolean IS_RUNNING = false;
  Map<String, String> map = new HashMap<String, String>();

  Song currSong;
  int i = 0;

  ListView listview;
  CustomArrayAdapter adapter2;

  @SuppressWarnings("deprecation")
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.tab_feed, container, false);

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

    RequestAsyncTask r = Request.executeMyFriendsRequestAsync(
        ParseFacebookUtils.getSession(), new Request.GraphUserListCallback() {

          @SuppressWarnings("unchecked")
          @Override
          public void onCompleted(List<GraphUser> users, Response response) {
            if (users != null) {

              List<String> friendsList = new ArrayList<String>();
              final long startTime = System.currentTimeMillis();
              
              
              for (GraphUser user : users) {
                friendsList.add(user.getId());
              }
              

              @SuppressWarnings("rawtypes")
              final ParseQuery<ParseUser> friendQuery = ParseQuery
                  .getUserQuery();
              friendQuery.whereContainedIn("fbId", friendsList);

              friendQuery.findInBackground(new FindCallback<ParseUser>() {

                public void done(List<ParseUser> friendUsers, ParseException e3) {

                  ParseQuery<Song> query = ParseQuery.getQuery("Song");
                  query.whereContainedIn("author", friendUsers);
                  query.addDescendingOrder("createdAt");
                  query.findInBackground(new FindCallback<Song>() {

                    @Override
                    public void done(final List<Song> songList,
                        ParseException e) {


                      listview = (ListView) getActivity().getWindow()
                          .getDecorView().findViewById(R.id.listview);
                      

                      adapter2 = new CustomArrayAdapter(getActivity(),
                          songList);
                      listview.setAdapter(adapter2);
                      adapter2.notifyDataSetChanged();
                      
                      long endTime   = System.currentTimeMillis();
                      long totalTime = endTime - startTime;
                      Log.i("TIMEc" , " " + totalTime);

                      listview
                          .setOnItemClickListener(new OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent,
                                View view, int position, long id) {
                              Intent currSongWindow = new Intent(getActivity(),
                                  CurrSongWindow.class);
                              currSongWindow.putExtra("ARTIST", songList
                                  .get(position).getArtist());
                              currSongWindow.putExtra("TITLE", songList
                                  .get(position).getTitle());
                              startActivity(currSongWindow);

                            }
                          });

                    } // here were done getting the songList for each
                      // followed.

                  });

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

        // CHANGE THIS TO ACTUALLY UPLOAD A SONG
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

            }
          }
        });
  }

}