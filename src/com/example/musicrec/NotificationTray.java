package com.example.musicrec;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class NotificationTray extends Activity {
  
  ListView listview;
  NotificationsAdapter adapter;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_notification_tray);
    
    TextView titleView = (TextView)findViewById(R.id.title);
    titleView.setText("Hey");

    // add listview images etc.
    // set status to old.

    // only get new status notifications
    // TODO
    
    ParseUser currUser = ParseUser.getCurrentUser();
    ParseQuery<NotificationType> query = ParseQuery.getQuery("NotificationType");
    query.whereEqualTo("fromUser", currUser);
    query.addDescendingOrder("createdAt");
    query.findInBackground(new FindCallback<NotificationType>() {

      @Override
      public void done(List<NotificationType> notificationList, ParseException arg1) {
        listview = (ListView) findViewById(R.id.notificationListView);
        Log.i("NOTI" , "size is= " + notificationList.size());
        adapter = new NotificationsAdapter(NotificationTray.this.getApplicationContext(), 5 , notificationList);
        listview.setAdapter(adapter);
      }
      
    });

  }

}

class NotificationsAdapter extends ArrayAdapter<NotificationType> {
  
  public List<NotificationType> notificationsArrayList;
  private final Context c;
  

  
  public NotificationsAdapter(Context applicationContext, int i,
      List<NotificationType> notificationList) {
    // TODO Auto-generated constructor stub
    super(applicationContext, i , notificationList);
    this.c = applicationContext;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) c
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    // 2. Get rowView from inflater
    View rowView = inflater.inflate(R.layout.single_notification_item, parent, false);
    
    TextView titleView = (TextView) rowView.findViewById(R.id.title);
    //NotificationType currNotification = (NotificationType) notificationsArrayList.get(position);
    titleView.setText("jhBHDBjwedn2lel");
    
    
    return rowView;
  }


}








