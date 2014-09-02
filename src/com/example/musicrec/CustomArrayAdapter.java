package com.example.musicrec;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.echonest.api.v4.Artist;
import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Image;
import com.echonest.api.v4.SongParams;
import com.google.android.youtube.player.YouTubeIntents;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SendCallback;


/*
 * This class is used for the custom list view.
 * holds the image, buttons for youtube, rdio and spotify 
 * and also links to the users profiles.
 * 
 */


@SuppressLint("NewApi")
public class CustomArrayAdapter extends ArrayAdapter<Song> {
  
  //stores the current context
  private final Context context;
  
  //stores the songlist for the listview
  private final List<Song> songArrayList;
  
  //stores the count for the "likes"
  private int count = 0;
  
  //Stores the artist of the API search song
  Artist currEchoArtist;
  
  //used to search for strong details
  private EchoNestAPI en;
  
  //Stores the image of the searched API song.
  Image currEchoArtistImage;
  
  //Stores the song from the API
  com.echonest.api.v4.Song song;
  
  //store the urls for the song/imag
  String url;
  String url2 = "";
  
  //store the artistname, songname of each song.
  String artistName = "";
  String songName = "";
  
  /*
   * Used to create a new adapter with the gievn details
   */
  public CustomArrayAdapter(Context context, List<Song> songList) {

    super(context, R.layout.single_song_item, songList);

    
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
        .permitAll().build();
    StrictMode.setThreadPolicy(policy);
    
    //initializes the API
    en = new EchoNestAPI("FUS98WPLXFNIHZHHG");
    
    //sets the context
    this.context = context;
    
    //sets the songlist
    this.songArrayList = songList;

  }
  
  /*
   * Sets the view for each list item.
   * 
   */
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {

    // 1. Create inflater
    LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    // 2. Get rowView from inflater
    View rowView = inflater.inflate(R.layout.single_song_item, parent, false);

    // 3. Get the two text view from the rowView
    TextView titleView = (TextView) rowView.findViewById(R.id.title);
    TextView artistView = (TextView) rowView.findViewById(R.id.artist);

    // Getting the buttons for all 3 playing services
    Button spotBtn = (Button) rowView.findViewById(R.id.spotifySearch);
    Button rdioBtn = (Button) rowView.findViewById(R.id.rdioSearch);
    Button youtubeBtn = (Button) rowView.findViewById(R.id.youtubeSearch);
    
    //getting the share button.
    Button shareButton = (Button) rowView.findViewById(R.id.sendSong);
    
    //getting the textviews for likes
    final TextView likeButtonTV = (TextView) rowView
        .findViewById(R.id.likeButton);
    final TextView heartTV = (TextView) rowView.findViewById(R.id.heartTV);
    
    //getting the imageviews for the song and the user
    final ImageView profileImage = (ImageView) rowView
        .findViewById(R.id.profileImage);
    final ImageView artistImage = (ImageView) rowView
        .findViewById(R.id.artistImage);

    // gets the song from the list with the curr position
    final Song currSong = (Song) songArrayList.get(position);
    
    // formats the title of the song
    String formattedTitle = currSong.get("title").toString().substring(0, 1)
        .toUpperCase()
        + currSong.get("title").toString().substring(1).toLowerCase();
    //asigning songname
    songName = formattedTitle;
    
    //setting the songname
    titleView.setText(formattedTitle);
    
    //settign the artistname
    artistView.setText(currSong.get("artist").toString());
    
    //storing the artist name
    artistName = currSong.get("artist").toString();
    
    //gets the number of likes for this song
    count = currSong.getLikes();
    
    //sets the like count
    likeButtonTV.setText("" + count);
    
    //gets the currently logged in user
    final ParseUser currUser = ParseUser.getCurrentUser();
    
    //button click listener for the like button
    heartTV.setOnClickListener(new View.OnClickListener() {

      @SuppressWarnings("static-access")
      @Override
      public void onClick(View v) {
        //updates the song count by 1
        count = currSong.getLikes();
        count++;
        currSong.setLikes(count);
        heartTV.setClickable(false);
        likeButtonTV.setText("" + count);
        currSong.saveInBackground();

        /*
         * This part is used to send a like notification to the song user
         */
        
        //creates a query for the notification assigning song user as the receiver
        ParseQuery<ParseInstallation> userQuery = ParseInstallation.getQuery();
        userQuery.whereContains("user", currSong.getAuthor().getUsername());
        
        //create a new NotificationType 
        NotificationType newNotificationType = new NotificationType();
        
        //sets the from user as the logged in user
        newNotificationType.setFromUser(currUser);
        
        //sets the receiver user, the type of the notification, status as new, and the song details
        newNotificationType.setToUser(currSong.getAuthor());
        newNotificationType.setType("Like");
        newNotificationType.setStatus("new");
        newNotificationType.setSong(currSong);
        newNotificationType.saveInBackground();
        
        
        //create a new notification for the receiving user
        try {
          JSONObject data = new JSONObject(
              "{\"action\": \"com.example.musicrec.UPDATE_STATUS\",\"name\": \"Vaughn\",\"newsItem\": \"Man bites dog\"}");
          data.put("data", "My string");
          
          //assigns song details to the notification
          data.put("action", "com.example.musicrec.UPDATE_STATUS");
          data.put("songartist", currSong.get("artist").toString());
          data.put("songtitle", currSong.get("title").toString());
          data.put("title", "Musify");
          data.put("alert", currUser.get("name") + " liked your Song!");
          
          //pushes the pushnotification to the server
          ParsePush push = new ParsePush();
          push.setQuery(userQuery);
          push.setData(data);
          //sends the data in background.
          push.sendDataInBackground(data, userQuery);

        } catch (JSONException e1) {
          e1.printStackTrace();
        }

      }
    });
    
    /*
     * Spotify button listener.
     * Opens spotify if installed with the song, otherwise opens play store.
     */
    spotBtn.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setAction(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
        intent.setComponent(new ComponentName("com.spotify.mobile.android.ui",
            "com.spotify.mobile.android.ui.Launcher"));
        intent.putExtra(SearchManager.QUERY, currSong.getTitle() + " "
            + currSong.getArtist());
        try {
          context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
          Toast.makeText(context, "You must first install Spotify",
              Toast.LENGTH_LONG).show();
          Intent i = new Intent(Intent.ACTION_VIEW, Uri
              .parse("market://details?id=com.spotify.mobile.android.ui"));
          context.startActivity(i);
        }

      }
    });
    
    /*
     * Rdia button Listener.
     * Opens Rdio app if installed, otherwise opens play store.
     */
    rdioBtn.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        boolean isRdioAvail = isRdioAvailable(context);
        if (isRdioAvail) {
          url = new String("rdio://search/" + currSong.getTitle() + "%20"
              + currSong.getArtist());
        } else {
          url = "https://play.google.com/store/apps/details?id=com.rdio.android.ui";
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);

      }
    });
    
    /*
     * Opens the friendlist to share the song with them.
     */
    shareButton.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        Intent shareList = new Intent(context, FriendPicker.class);
        context.startActivity(shareList);

      }
    });
    
    /*
     * youtube button listener.
     * Opens youtube with the song search.
     */
    youtubeBtn.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        Intent intent = YouTubeIntents.createSearchIntent(context,
            currSong.getTitle() + " " + currSong.getArtist());
        context.startActivity(intent);

      }
    });
    
    //Sets the heart icon 
    Typeface font = Typeface.createFromAsset(context.getAssets(),
        "fontawesome-webfont.ttf");
    heartTV.setTypeface(font);

    /* for facebook Image */
    AsyncTask<Void, Void, Bitmap> t = new AsyncTask<Void, Void, Bitmap>() {
      protected Bitmap doInBackground(Void... p) {
        Bitmap bm = null;
        
        //getting the user for the song.
        final ParseUser songUser = currSong.getAuthor();
        try {
          songUser.fetchIfNeeded();
        } catch (ParseException e) {
          e.printStackTrace();
        }
        //getting the facebook identifier or the user.
        final String userFacebookId = songUser.get("fbId").toString();

        try {
          
          //getting his profile image
          String url = String.format("https://graph.facebook.com/%s/picture",
              userFacebookId);

          InputStream is = new URL(url).openStream();
          bm = BitmapFactory.decodeStream(is);

          // rounding off corners
          bm = getRoundedCornerBitmap(bm, 35f);

          is.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
        
        //returning the image
        return bm;
      }

      protected void onPostExecute(Bitmap bm) {
        
        //setting the profile image.
        profileImage.setImageBitmap(bm);
        profileImage.setOnClickListener(new View.OnClickListener() {
          
          //opens the user profile if the user image is clicked 
          @Override
          public void onClick(View v) {
            // ActionBar actionBar = context.getActivity().getActionBar();
            // Open new window with fbid and get feed again.
            ParseUser songUser = currSong.getAuthor();
            //Log.i("USER", songUser.get("fbId").toString());
            // pass in fbId.
            
            //opening window for current song user
            Intent currFBUserWindow = new Intent(context,
                CurrFBUserWindow.class);
            currFBUserWindow.putExtra("objectId", songUser.getObjectId()
                .toString());
            context.startActivity(currFBUserWindow);

          }
        });

      }
    };
    t.execute();

    /* for song image */
    AsyncTask<Void, Void, Bitmap> artistImageTask = new AsyncTask<Void, Void, Bitmap>() {
      protected Bitmap doInBackground(Void... p) {
        url = null;
        Bitmap bm = null;
        
        //passing in the song parameters to look for song metadata.
        SongParams p1 = new SongParams();
        p1.setArtist(currSong.get("artist").toString());
        p1.setTitle(currSong.get("title").toString());
        p1.includeTracks(); // the album art is in the track data
        p1.setLimit(true); // only return songs that have track data
        p1.addIDSpace("7digital-US");

        // fix this
        // echonest search for songs not working
        try {
          
          //Searching for songs and selecting the first one.
          List<com.echonest.api.v4.Song> songs = en.searchSongs(p1);
          if (songs.size() != 0) {
            song = songs.get(0);
            try {
              url = song.getString("tracks[0].release_image");
            } catch (IndexOutOfBoundsException e) {

            }
          } 
          //if no songs were found, search for the artist instead.
          else {
            //
            SongParams p2 = new SongParams();
            p2.setTitle(currSong.get("title").toString());
            p2.includeTracks(); // the album art is in the track data
            p2.setLimit(true); // only return songs that have track data
            p2.addIDSpace("7digital-US");
            
            //get the list of artists and show the first one.
            List<com.echonest.api.v4.Song> songsWithoutArtists = en
                .searchSongs(p2);
            if (songsWithoutArtists.size() != 0) {
              song = songsWithoutArtists.get(0);
              try {
                url = song.getString("tracks[0].release_image");
              } catch (IndexOutOfBoundsException e) {

              }
            }
          }
        } catch (EchoNestException e) {
          e.printStackTrace();
        }
        try {
          
          //downloading the image from the url.
          if (url != null) {
            InputStream is = new URL(url).openStream();

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            BitmapFactory.decodeStream(is, null, options);

            options.inSampleSize = calculateInSampleSize(options, 300, 300);
            options.inJustDecodeBounds = false;
            is.close();

            is = new URL(url).openStream();
            bm = BitmapFactory.decodeStream(is, null, options);

            int width = bm.getWidth();
            int height = bm.getHeight();
            int newWidth = 300;
            int newHeight = 300;

            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;

            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            
            //creating a bitmap and rounding off corners.
            bm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
            bm = getRoundedCornerBitmap(bm, 12f);
          }

        } catch (IOException e) {
          e.printStackTrace();
        }
        return bm;
      }

      protected void onPostExecute(Bitmap bm) {

        /* SET THE ARTIST IMAGE VIEW */
        // check if not null.
        artistImage.setImageBitmap(bm);
        //Log.i("SONG INFO", " " + url);
        
        //if the image is clicked, open that songs individual window.
        artistImage.setOnClickListener(new View.OnClickListener() {
          
          // opens the window for the current song.
          @Override
          public void onClick(View v) {
            Intent currSongWindow = new Intent(context, CurrSongWindow.class);
            currSongWindow.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            currSongWindow.putExtra("ARTIST", currSong.getArtist());
            currSongWindow.putExtra("TITLE", currSong.getTitle());
            context.startActivity(currSongWindow);

          }
        });

      }
    };
    artistImageTask.execute();

    return rowView;
  }
  
  /*
   * function to round off corners of a bitmap.
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
  
  //calculates the size of the input image.
  private static int calculateInSampleSize(BitmapFactory.Options options,
      int reqWidth, int reqHeight) {

    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

      final int heightRatio = Math.round((float) height / (float) reqHeight);
      final int widthRatio = Math.round((float) width / (float) reqWidth);

      inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
    }
    return inSampleSize;
  }
  
  //checks if rdio app id available on the device.
  public boolean isRdioAvailable(Context context) {
    PackageManager packageManager = context.getPackageManager();
    Intent intent = packageManager
        .getLaunchIntentForPackage("com.rdio.android.ui");

    if (intent == null) {
      // Check if the Brazil version of Rdio is installed
      intent = packageManager
          .getLaunchIntentForPackage("com.rdio.oi.android.ui");
    }

    return intent != null;
  }

}
