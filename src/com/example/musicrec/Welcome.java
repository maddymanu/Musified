package com.example.musicrec;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

//Is basically the Tab Holder for different tabs

public class Welcome extends SherlockFragmentActivity {

  ActionBar.Tab Tab1, Tab2;
  Fragment fragmentTab1 = new FragmentTab1();
  Fragment fragmentTab2 = new FragmentTab2();

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

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
    menu.setMenu(R.layout.activity_login_facebook);

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
}
