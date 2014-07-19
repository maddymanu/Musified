package com.example.musicrec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.model.GraphUser;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseQuery;
import com.parse.ParseUser;

//THIS CLASS SHOULD YOU FB FRIENDS FEED - MOVE THE UPLOAD PART TO WELCOME ACTIVITY

public class FragmentTab2 extends SherlockFragment {

  public static Boolean IS_RUNNING = false;
  Map<String, String> map = new HashMap<String, String>();

  Song currSong;
  int i = 0;

  ListView listview;
  List<GraphUser> friendListForInvites = null;
  CustomArrayAdapter adapter2;
  private String requestId;

  @SuppressWarnings("deprecation")
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.tab_feed, container, false);

    getFacebookIdInBackground();


    Button invBtn = (Button) rootView.findViewById(R.id.invite_btn);

    RequestAsyncTask r = Request.executeMyFriendsRequestAsync(
        ParseFacebookUtils.getSession(), new Request.GraphUserListCallback() {

          @SuppressWarnings("unchecked")
          @Override
          public void onCompleted(List<GraphUser> users, Response response) {
            if (users != null) {

              friendListForInvites = users;
              List<String> friendsList = new ArrayList<String>();

              for (GraphUser user : users) {
                friendsList.add(user.getId());
              }

              @SuppressWarnings("rawtypes")
              final ParseQuery<ParseUser> friendQuery = ParseQuery
                  .getUserQuery();
              friendQuery.whereContainedIn("fbId", friendsList);

              friendQuery.findInBackground(new FindCallback<ParseUser>() {

                public void done(List<ParseUser> friendUsers, ParseException e3) {

                  if (friendUsers.size() == 0) {
                    Log.i("Friend", "size0");
                  }

                  ParseQuery<Song> query = ParseQuery.getQuery("Song");
                  query.whereContainedIn("author", friendUsers);
                  query.addDescendingOrder("createdAt");
                  query.findInBackground(new FindCallback<Song>() {

                    @Override
                    public void done(final List<Song> songList, ParseException e) {

                      listview = (ListView) getActivity().getWindow()
                          .getDecorView().findViewById(R.id.listview);

                      adapter2 = new CustomArrayAdapter(getActivity(), songList);
                      listview.setAdapter(adapter2);
                      adapter2.notifyDataSetChanged();

                    }
                  });

                }

              });

            }

          }

        });

    invBtn.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        inviteFromFacebook(getActivity(), friendListForInvites);
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
              ParseUser.getCurrentUser().put("name", user.getName());
              ParseUser.getCurrentUser().saveInBackground();

            }
          }
        });
  }

  private void sendRequestDialog() {
    Bundle params = new Bundle();
    params.putString("message", "Learn how to make your Android apps social");

    WebDialog requestsDialog = (new WebDialog.RequestsDialogBuilder(
        getActivity(), Session.getActiveSession(), params))
        .setOnCompleteListener(new OnCompleteListener() {

          @Override
          public void onComplete(Bundle values, FacebookException error) {
            if (error != null) {
              if (error instanceof FacebookOperationCanceledException) {
                Toast.makeText(getActivity().getApplicationContext(),
                    "Request cancelled",

                    Toast.LENGTH_SHORT).show();
              } else {
                Toast.makeText(getActivity().getApplicationContext(),
                    "Network Error", Toast.LENGTH_SHORT).show();
              }
            } else {
              final String requestId = values.getString("request");
              if (requestId != null) {
                Toast.makeText(getActivity().getApplicationContext(),
                    "Request sent", Toast.LENGTH_SHORT).show();
              } else {
                Toast.makeText(getActivity().getApplicationContext(),
                    "Request cancelled", Toast.LENGTH_SHORT).show();
              }
            }
          }

        }).build();
    requestsDialog.show();
  }

  @SuppressWarnings("deprecation")
  private void inviteFromFacebook(Activity activity, List<GraphUser> list) {

    if (list == null || list.size() == 0)
      return;

    Bundle parameters = new Bundle();

    parameters.putString("message", "Use my app!");

    Facebook mFacebook = new Facebook("830750263621357");
    // Show dialog for invitation
    mFacebook.dialog(activity, "apprequests", parameters,
        new Facebook.DialogListener() {
          @Override
          public void onComplete(Bundle values) {
            // TODO Auto-generated method stub

          }

          @Override
          public void onCancel() {
            // TODO Auto-generated method stub

          }

          @Override
          public void onFacebookError(FacebookError e) {
            // TODO Auto-generated method stub

          }

          @Override
          public void onError(DialogError e) {
            // TODO Auto-generated method stub

          }
        });

  }

}