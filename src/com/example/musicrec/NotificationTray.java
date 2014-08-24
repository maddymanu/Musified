package com.example.musicrec;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/*
 * Class to show the notification list to the user.
 * Deletes old notifications after seen.
 */
public class NotificationTray extends Activity {
  
  //To store the listview and a custom adapter
  ListView listview;
  NotificationsAdapter adapter;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_notification_tray);
    
//    //
//    TextView titleView = (TextView) findViewById(R.id.title);
//    titleView.setText("Hey");
//


    //TODO
    // CHANGE to whereEqualto toUser = currUser.
    ParseUser currUser = ParseUser.getCurrentUser();
    
    //creates a ParseQuery to get all notifications for the curruser.
    ParseQuery<NotificationType> query = ParseQuery
        .getQuery("NotificationType");
    query.whereEqualTo("fromUser", currUser);
    query.whereEqualTo("status", "new");
    query.addDescendingOrder("createdAt");
    
    //looks for notifications in background.
    query.findInBackground(new FindCallback<NotificationType>() {

      @Override
      public void done(List<NotificationType> notificationList,
          ParseException arg1) {
        //assigns the listview
        listview = (ListView) findViewById(R.id.notificationListView);
        
        //creates a new adapter with the given notification list
        adapter = new NotificationsAdapter(NotificationTray.this
            .getApplicationContext(), 5, notificationList);
        
        //sets the adapter
        listview.setAdapter(adapter);
        
        //changes the status of each notification to old.
        for (NotificationType currNoti : notificationList) {
          currNoti.setStatus("old");
          currNoti.saveInBackground();
        }
        
      }

    });

  }

}

/*
 * Class to act as a custom adapter for the notification listview
 */

@SuppressLint("NewApi")
class NotificationsAdapter extends ArrayAdapter<NotificationType> {
  
  //stores the list of notifications 
  public List<NotificationType> notificationsArrayList;
  
  //stores the context
  private final Context c;
  
  //constructor. assigns the context and the list of notifications.
  public NotificationsAdapter(Context applicationContext, int i,
      List<NotificationType> notificationList) {
    // TODO Auto-generated constructor stub
    super(applicationContext, i, notificationList);
    this.c = applicationContext;
    this.notificationsArrayList = notificationList;
  }
  
  //sets the view for each notification.
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    
    //gets the layout inflater.
    LayoutInflater inflater = (LayoutInflater) c
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    // Get rowView from inflater
    if (convertView == null) {
      convertView = inflater.inflate(R.layout.single_notification_item, null);
    }
    
    //titleview stores the title of the song
    final TextView titleView = (TextView) convertView.findViewById(R.id.title);
    
    //imagview stores the profile image of the author of the notification.
    final ImageView profileImage = (ImageView) convertView
        .findViewById(R.id.profImageView);
    
    //trying to get the current notification at this position.
    NotificationType currNotification = null;
    try {
      currNotification = (NotificationType) notificationsArrayList
          .get(position);

    } catch (NullPointerException e) {
      e.printStackTrace();
    }
    
    //gets the author of the current notification
    final ParseUser fromUser = currNotification.getFromUser();
    
    //gets the song of the notification
    final Song currSong = currNotification.getSong();

    // For facebook profile image
    AsyncTask<Void, Void, Bitmap> t = new AsyncTask<Void, Void, Bitmap>() {
      protected Bitmap doInBackground(Void... p) {
        
        //create a null bitmap
        Bitmap bm = null;
        
        //fetches the user and the song if needed
        try {
          fromUser.fetchIfNeeded();
          if (currSong != null) {
            currSong.fetchIfNeeded();
          }

        } catch (ParseException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        
        //gets the id of the author of the notification.
        final String userFacebookId = fromUser.get("fbId").toString();
        
        //gets the profile image of the author
        try {
          
          //url that has the image
          String url = String.format("https://graph.facebook.com/%s/picture",
              userFacebookId);
          
          //downloading the image
          InputStream is = new URL(url).openStream();
          bm = BitmapFactory.decodeStream(is);

          // rounding the corners of the image
          bm = getRoundedCornerBitmap(bm, 35f);

          // bis.close();
          is.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
        
        //returning the bitmap
        return bm;
      }

      protected void onPostExecute(Bitmap bm) {
        
        //store the song info
        final String songTitle = currSong.getTitle();
        final String artistTitle = currSong.getArtist();
        
        //setting the title of the notification.
        titleView.setText(fromUser.get("name") + " Likes your Song "
            + songTitle);
        titleView.setOnClickListener(new View.OnClickListener() {
          
          //if notificatio is clicked, it takes you to that particular song.
          @Override
          public void onClick(final View v) {
            Intent currSongWindow = new Intent(c, CurrSongWindow.class);
            currSongWindow.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            currSongWindow.putExtra("ARTIST", artistTitle);
            currSongWindow.putExtra("TITLE", songTitle);
            c.startActivity(currSongWindow);

          }
        });
        //sets the imageview wih the profile image
        profileImage.setImageBitmap(bm);
        
        //sets the listsner for the profile image
        profileImage.setOnClickListener(new View.OnClickListener() {

          @Override
          public void onClick(View v) {
            // ActionBar actionBar = context.getActivity().getActionBar();
            // Open new window with fbid and get feed again.
            
            //opens the window for this particular facebook id.
            Intent currFBUserWindow = new Intent(c, CurrFBUserWindow.class);
            currFBUserWindow.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            currFBUserWindow.putExtra("objectId", fromUser.getObjectId()
                .toString());
            c.startActivity(currFBUserWindow);

          }
        });

      }
    };
    t.execute();

    // TODO try smooth scrolling.
    // try {
    // fromUser.fetchIfNeeded();
    // currSong.fetchIfNeeded();
    // } catch (ParseException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    //
    // titleView.setText(fromUser.get("name") + "Likes your Song " +
    // currSong.getTitle());

    return convertView;
  }
  
  /*
   * Function to round off the corners of a bitmap.
   */
  public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float rnd) {
    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
        Config.ARGB_8888);
    Canvas canvas = new Canvas(output);

    final int color = 0xff424242;
    final Paint paint = new Paint();
    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
    final RectF rectF = new RectF(rect);
    final float roundPx = rnd;

    paint.setAntiAlias(true);
    canvas.drawARGB(0, 0, 0, 0);
    paint.setColor(color);
    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
    canvas.drawBitmap(bitmap, rect, rect, paint);

    return output;
  }

}
