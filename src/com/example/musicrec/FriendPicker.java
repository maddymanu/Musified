package com.example.musicrec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/*
 * This class is the activity that is called when a share button is clicked.
 * It populates a list of friends the user has and sends them to FriendPickerAdapter.
 */
public class FriendPicker extends Activity {
  
  //list to store the list of friends
  List<GraphUser> friendListForInvites = null;
  
  //custom list adapter 
  FriendPickerAdapter adapter;

  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_friend_picker);
    
    //assigning the listview
    final ListView listView = (ListView) findViewById(R.id.list_of_friends);

    // Get List of friends.
    @SuppressWarnings("deprecation")
    RequestAsyncTask r = Request.executeMyFriendsRequestAsync(
        ParseFacebookUtils.getSession(), new Request.GraphUserListCallback() {

          @SuppressWarnings("unchecked")
          @Override
          public void onCompleted(List<GraphUser> users, Response response) {
            
            //if this user has friends
            if (users != null) {
              
              //add all friends to a separate list.
              friendListForInvites = users;
              List<String> friendsList = new ArrayList<String>();

              for (GraphUser user : users) {
                friendsList.add(user.getId());
              }
              
              //get a list of ParseUsers for these facebook friends
              @SuppressWarnings("rawtypes")
              final ParseQuery<ParseUser> friendQuery = ParseQuery
                  .getUserQuery();
              friendQuery.whereContainedIn("fbId", friendsList);

              friendQuery.findInBackground(new FindCallback<ParseUser>() {

                public void done(List<ParseUser> friendUsers, ParseException e3) {

                  // getting the list of friends complete here
                  if (friendUsers.size() == 0) {
                    //Log.i("Friend", "size0");
                  }

                  // friendUsers is the list of friends here
                  
                  //create a new custom adapter for FriendPicker
                  adapter = new FriendPickerAdapter(savedInstanceState,
                      friendUsers);
                  
                  adapter.setAdapterView(listView);

                }

              });

            }

          }

        });

  }
  
  @Override
  protected void onSaveInstanceState(Bundle outState) {
    adapter.save(outState);
  }



}
