package com.example.musicrec;

import java.util.Currency;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/*
 * This class is used to display the songs from a partiular Facebook user
 * 
 * 
 */
public class CurrFBUserWindow extends Activity {
  
  
  ListView listview; //listview for the songs
  CustomArrayAdapter adapter2; //custom adapter
  String FBID; //string that holds the id of the current user

  @SuppressWarnings({ "deprecation", "unchecked" })
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.tab_feed);
     
    //Getting the id of the user who was clicked on.
    Bundle extras = getIntent().getExtras();
    if (extras != null) {
      FBID = extras.getString("objectId");
    }
    
    //query to find the friend with the given id.
    @SuppressWarnings("rawtypes")
    final ParseQuery friendQuery = ParseQuery.getUserQuery();
    friendQuery.whereEqualTo("objectId", FBID);
    
    friendQuery.findInBackground(new FindCallback<ParseObject>() {
      
      //returns the list of users with the given id.
      @Override
      public void done(List<ParseObject> listOfUsers, ParseException arg1) {
         
        //check list to be 0 TODO
        //Takes the first user from the list
        ParseUser currUser = (ParseUser) listOfUsers.get(0);
        
        //Query to return all the songs from this user
        ParseQuery<Song> query = ParseQuery.getQuery("Song");
        query.whereEqualTo("author", currUser);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Song>() {

          @Override
          public void done(List<Song> songList, ParseException arg1) {
            //When the search is complete, sets the listview with the custom adapter.
            listview = (ListView) findViewById(R.id.listview);
            adapter2 = new CustomArrayAdapter(CurrFBUserWindow.this , songList);
            listview.setAdapter(adapter2);
          }
          
        });
        
        
      }
      
    });

  }

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
