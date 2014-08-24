package com.example.musicrec;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.model.GraphUser;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/*
 * This class provides the main "frame" for the app.
 * It provides the top 2 fragment-tabs for different views.
 */

public class Welcome extends SherlockFragmentActivity {
  
  
  //Store the top 2 tabs
  ActionBar.Tab Tab1, Tab2;
  
  //Store the fragment tabs for each view.
  Fragment fragmentTab1 = new FragmentTab1();
  Fragment fragmentTab2 = new FragmentTab2();
  final Context context = this;
  List<GraphUser> friendListForInvites = null;

  @SuppressWarnings("deprecation")
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View sliderView = inflater.inflate(R.layout.slider_list, null);
    ListView sliderList = (ListView) sliderView
        .findViewById(R.id.optionsListView);

    RequestAsyncTask r = Request.executeMyFriendsRequestAsync(
        ParseFacebookUtils.getSession(), new Request.GraphUserListCallback() {

          @SuppressWarnings("unchecked")
          @Override
          public void onCompleted(List<GraphUser> users, Response response) {
            if (users != null) {

              friendListForInvites = users;

            }

          }

        });
    
    String[] items2 = new String[] { "Notifications", "Settings", "Send",
        "Logout", "Invite!" };
    ArrayAdapter<String> sliderListViewAdapter = new ArrayAdapter<String>(
        context, android.R.layout.simple_list_item_1, android.R.id.text1,
        items2);
    sliderList.setAdapter(sliderListViewAdapter);

    ActionBar actionBar = getSupportActionBar();

    // Hide Actionbar Icon
    actionBar.setDisplayShowHomeEnabled(false);

    // Hide Actionbar Title
    actionBar.setDisplayShowTitleEnabled(true);

    // Create Actionbar Tabs
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

    // Create the SlidingMenu Drawer
    SlidingMenu menu = new SlidingMenu(this);
    menu.setMode(SlidingMenu.LEFT);
    menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
    menu.setFadeDegree(0.35f);
    menu.setBehindOffset(300);
    menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
    menu.setMenu(sliderView);

    sliderList.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position,
          long id) {
        // Open Different Intents

        // Open Notification Menu
        if (position == 0) {
          Intent intent = new Intent(Welcome.this, NotificationTray.class);
          startActivity(intent);
        }
        // Logout
        else if (position == 3) {
          ParseUser.getCurrentUser();
          ParseUser.logOut();
          Intent intent = new Intent(Welcome.this, LoginActivity.class);
          startActivity(intent);
        }
        // Invite more friends
        else if (position == 4) {
          inviteFromFacebook(Welcome.this, friendListForInvites);
        }
      }
    });

    // Set Tab Icon and Titles
    Tab1 = actionBar.newTab().setText("Friends");
    // Tab1.setCustomView(View v);
    Tab2 = actionBar.newTab().setText("Own");

    // Set Tab Listeners
    Tab1.setTabListener(new TabListener(fragmentTab2));
    Tab2.setTabListener(new TabListener(fragmentTab1));

    // Add tabs to actionbar
    actionBar.addTab(Tab1);
    actionBar.addTab(Tab2);

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getSupportMenuInflater();
    inflater.inflate(R.menu.main, menu);

    return super.onCreateOptionsMenu(menu);
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
