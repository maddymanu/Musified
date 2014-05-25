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

@SuppressLint("NewApi")
public class CustomArrayAdapter extends ArrayAdapter<Song> {

  private final Context context;

  private final List<Song> songArrayList;
  private int count = 0;
  Artist currEchoArtist;
  private EchoNestAPI en;
  Image currEchoArtistImage;
  com.echonest.api.v4.Song song;
  String url;

  public CustomArrayAdapter(Context context, List<Song> songList) {

    super(context, R.layout.single_song_item, songList);

    /* TEMP SOL */
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
        .permitAll().build();
    StrictMode.setThreadPolicy(policy);
    en = new EchoNestAPI("FUS98WPLXFNIHZHHG");

    this.context = context;
    this.songArrayList = songList;

    // JSONObject data = new
    // JSONObject("{\"action\": \"com.example.UPDATE_STATUS\""});
  }

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

    final TextView likeButtonTV = (TextView) rowView
        .findViewById(R.id.likeButton);
    final TextView heartTV = (TextView) rowView.findViewById(R.id.heartTV);

    final ImageView profileImage = (ImageView) rowView
        .findViewById(R.id.profileImage);

    final ImageView artistImage = (ImageView) rowView
        .findViewById(R.id.artistImage);

    // 4. Set the text for textView
    final Song currSong = (Song) songArrayList.get(position);
    // currSong.get
    String formattedTitle = currSong.get("title").toString().substring(0, 1)
        .toUpperCase()
        + currSong.get("title").toString().substring(1).toLowerCase();
    titleView.setText(formattedTitle);
    artistView.setText(currSong.get("artist").toString());
    count = currSong.getLikes();
    likeButtonTV.setText("" + count);
    final ParseUser currUser = ParseUser.getCurrentUser();

    heartTV.setOnClickListener(new View.OnClickListener() {

      @SuppressWarnings("static-access")
      @Override
      public void onClick(View v) {
        Log.i("Liked!", "Should only show once for 1 song!");
        count = currSong.getLikes();
        count++;
        currSong.setLikes(count);
        heartTV.setClickable(false);
        likeButtonTV.setText("" + count);
        currSong.saveInBackground();

        ParseQuery<ParseInstallation> userQuery = ParseInstallation.getQuery();
        userQuery.whereContains("user", currSong.getAuthor().getUsername());

        try {
           JSONObject data = new JSONObject(
           "{\"action\": \"com.example.musicrec.UPDATE_STATUS\",\"name\": \"Vaughn\",\"newsItem\": \"Man bites dog\"}");
           data.put("data", "My string"); //works
//          JSONObject data;
//          data = new JSONObject();
          data.put("action", "com.example.musicrec.UPDATE_STATUS");
          data.put("songartist", currSong.get("artist").toString());
          data.put("songtitle", currSong.get("title").toString());
          data.put("title", "Musify");
          data.put("alert", currUser.get("name") + " liked your Song!");
          
          ParsePush push = new ParsePush();
          push.setQuery(userQuery);
          push.setData(data);
         // push.setMessage(currUser.get("name") + " liked your Song!");
          push.sendDataInBackground(data, userQuery);

//          push.sendInBackground(new SendCallback() {
//
//            @Override
//            public void done(ParseException arg0) {
//              Log.i("Liked!", "Notified");
//
//            }
//          });
        } catch (JSONException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }

        // JSONObject obj;
        // obj = new JSONObject();
        // try {
        // obj.put("alert", "erwerwe");
        // obj.put("action", "com.example.musicrec.UPDATE_STATUS");
        // obj.put("customdata", "My string");
        //
        //
        //
        //
        // ParsePush push = new ParsePush();
        // push.setQuery(userQuery);
        // push.setData(obj);
        // push.setMessage(currUser.get("name") + " liked your Song!");
        //
        // push.sendInBackground(new SendCallback() {
        //
        // @Override
        // public void done(ParseException arg0) {
        // Log.i("Liked!", "Notified");
        //
        // }
        // });
        // } catch (JSONException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }

        // JSONObject data = new JSONObject("{\"action\":
        // \"com.example.UPDATE_STATUS\",
        // \"name\": \"Vaughn\",
        // \"newsItem\": \"Man bites dog\""}));

      }
    });

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

    youtubeBtn.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        Intent intent = YouTubeIntents.createSearchIntent(context,
            currSong.getTitle() + " " + currSong.getArtist());
        context.startActivity(intent);

      }
    });

    Typeface font = Typeface.createFromAsset(context.getAssets(),
        "fontawesome-webfont.ttf");
    heartTV.setTypeface(font);

    /* for facebook Image */
    AsyncTask<Void, Void, Bitmap> t = new AsyncTask<Void, Void, Bitmap>() {
      protected Bitmap doInBackground(Void... p) {
        Bitmap bm = null;

        final ParseUser songUser = currSong.getAuthor();

        try {
          songUser.fetchIfNeeded();
        } catch (ParseException e) {
          e.printStackTrace();
        }
        final String userFacebookId = songUser.get("fbId").toString();

        try {

          String url = String.format("https://graph.facebook.com/%s/picture",
              userFacebookId);

          InputStream is = new URL(url).openStream();
          bm = BitmapFactory.decodeStream(is);

          // testing
          bm = getRoundedCornerBitmap(bm, 35f);

          // bis.close();
          is.close();
        } catch (IOException e) {
          e.printStackTrace();
        }

        return bm;
      }

      protected void onPostExecute(Bitmap bm) {

        profileImage.setImageBitmap(bm);
        profileImage.setOnClickListener(new View.OnClickListener() {

          @Override
          public void onClick(View v) {
            // ActionBar actionBar = context.getActivity().getActionBar();
            // Open new window with fbid and get feed again.
            ParseUser songUser = currSong.getAuthor();
            Log.i("USER", songUser.get("fbId").toString());
            // pass in fbId.

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
        Bitmap bm = null;
        SongParams p1 = new SongParams();
        p1.setArtist(currSong.get("artist").toString());
        p1.setTitle(currSong.get("title").toString());
        p1.includeTracks(); // the album art is in the track data
        p1.setLimit(true); // only return songs that have track data
        p1.addIDSpace("7digital-US");

        try {

          List<com.echonest.api.v4.Song> songs = en.searchSongs(p1);
          if (songs.size() > 0) {
            song = songs.get(0);
            url = song.getString("tracks[0].release_image");
          }
        } catch (EchoNestException e) {
          e.printStackTrace();
        }
        try {

          // /String url = currEchoArtistImage.getURL();
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

          bm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
          bm = getRoundedCornerBitmap(bm, 12f);

        } catch (IOException e) {
          e.printStackTrace();
        }
        return bm;
      }

      protected void onPostExecute(Bitmap bm) {

        /* SET THE ARTIST IMAGE VIEW */
        artistImage.setImageBitmap(bm);
        artistImage.setOnClickListener(new View.OnClickListener() {

          @Override
          public void onClick(View v) {
            Intent currSongWindow = new Intent(context, CurrSongWindow.class);
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
