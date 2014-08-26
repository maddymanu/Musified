package com.example.musicrec;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.manuelpeinado.multichoiceadapter.MultiChoiceBaseAdapter;
import com.parse.ParseUser;

/*
 * This is the custom list adapter that shows the list of friends to share
 * a song with.
 */
public class FriendPickerAdapter extends MultiChoiceBaseAdapter {
  
  //list of friends to show/select 
  private List<ParseUser> items;

  public FriendPickerAdapter(Bundle savedInstanceState, List<ParseUser> items) {
      super(savedInstanceState);
      this.items = items;
  }

  @Override
  public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
    //if the share button is clicked, send a notification to the selected users
    if (item.getItemId() == R.id.menu_share) {
      Toast.makeText(getContext(), "Share", Toast.LENGTH_SHORT).show();
      
      //Set of selected friends
      Set<Long> setOfIndeces = getCheckedItems();
      
      //converting the set to list so we can easily iterate over
      List<Long> listOfIndeces = new ArrayList(setOfIndeces);
      
      //Send notification to each friend. //DONT NEED A FOR LOOP
      //TODO
      for (int i=0 ; i < listOfIndeces.size() ; i++) {
        ParseUser friend = items.get(listOfIndeces.get(i).intValue());
        
        //Send notification to this friend.
        
      }
      
      
      return true;
    }
    Log.i("MENU" , " This tem was clicked " + item + getCheckedItemCount());
    
    getCheckedItemCount();
    return false;
  }
  /*
   * This creates the menu for the list activity
   */
  @Override
  public boolean onCreateActionMode(ActionMode mode, Menu menu) {
    MenuInflater inflater = mode.getMenuInflater();
    inflater.inflate(R.menu.my_action_mode, menu);
    return true;
  }

  @Override
  public boolean onPrepareActionMode(ActionMode arg0, Menu arg1) {
    return false;
  }
  
  
  @Override
  public int getCount() {
    return items.size();
  }

  @Override
  public Object getItem(int position) {
    return items.get(position);
  }

  @Override
  public long getItemId(int arg0) {
    return arg0;
  }
  
  /*
   * This method is used to view each list item.
   * Runs over each item in the list of friends.
   * s
   */
  @Override
  protected View getViewImpl(int position, View convertView, ViewGroup parent) {
    //if view is not available, create a new one.
    if (convertView == null) {
      int layout = R.layout.friend_picker_list_item;
      LayoutInflater inflater = LayoutInflater.from(getContext());
      convertView = inflater.inflate(layout, parent, false);
    }
    ViewGroup group = (ViewGroup) convertView;
    
    //add the name of each friend to a TextView
    String name = (String) ((ParseUser) getItem(position)).get("name");
    ((TextView) group.findViewById(R.id.friend_name)).setText(name);
    return group;
  }

}
