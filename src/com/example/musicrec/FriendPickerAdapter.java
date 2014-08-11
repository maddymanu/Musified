package com.example.musicrec;

import java.util.List;

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

public class FriendPickerAdapter extends MultiChoiceBaseAdapter {

  private List<String> items;

  public FriendPickerAdapter(Bundle savedInstanceState, List<String> items) {
      super(savedInstanceState);
      this.items = items;
      Log.i("Adapter!" , "Entering the aapter atleeast");
  }

  @Override
  public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
    if (item.getItemId() == R.id.menu_share) {
      Toast.makeText(getContext(), "Share", Toast.LENGTH_SHORT).show();
      return true;
    }
    return false;
  }

  @Override
  public boolean onCreateActionMode(ActionMode mode, Menu menu) {
    Log.i("Adapter!" , "Entering onCreateAction");
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
    // TODO Auto-generated method stub
    return arg0;
  }

  @Override
  protected View getViewImpl(int position, View convertView, ViewGroup parent) {
    Log.i("Adapter!" , "Entering getViewImpl");
    if (convertView == null) {
      int layout = R.layout.friend_picker_list_item;
      LayoutInflater inflater = LayoutInflater.from(getContext());
      convertView = inflater.inflate(layout, parent, false);
    }
    ViewGroup group = (ViewGroup) convertView;
    String str = (String) getItem(position);
    ((TextView) group.findViewById(R.id.friend_name)).setText(str);
    // ((TextView)group.findViewById(android.R.id.text2)).setText(building.height);
    return group;
  }

}
