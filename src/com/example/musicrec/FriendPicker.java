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

public class FriendPicker extends Activity {

  List<GraphUser> friendListForInvites = null;

  FriendPickerAdapter adapter;
  List<String> items = Arrays.asList("Hello", "Second", "Bye", "BHbc",
      "3f3rf23", "ef23rf3rf");

  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_friend_picker);

    final ListView listView = (ListView) findViewById(R.id.list_of_friends);

    // Get List of friends.
    @SuppressWarnings("deprecation")
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

                  // getting the list of friends complete here
                  if (friendUsers.size() == 0) {
                    Log.i("Friend", "size0");
                  }

                  // friendUsers is the list of friends here

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

}
