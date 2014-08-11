package com.example.musicrec;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FriendPicker extends Activity {

  FriendPickerAdapter adapter;
  List<String> items = Arrays.asList("Hello", "Second", "Bye", "BHbc",
      "3f3rf23", "ef23rf3rf");

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_friend_picker);
    
    String[] values = new String[] { "Android List View", 
        "Adapter implementation",
        "Simple List View In Android",
        "Create List View Android", 
        "Android Example", 
        "List View Source Code", 
        "List View Array Adapter", 
        "Android Example List View" 
       };
    
    ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
        android.R.layout.simple_list_item_1, android.R.id.text1, values);
    adapter = new FriendPickerAdapter(savedInstanceState, items);

    ListView listView = (ListView) findViewById(R.id.list_of_friends);
        
    
    adapter.setAdapterView(listView);

  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    adapter.save(outState);
  }
}
