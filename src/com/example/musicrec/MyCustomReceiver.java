package com.example.musicrec;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
  
/*
 * This class is used to receive notifications on a device.
 */
public class MyCustomReceiver extends BroadcastReceiver {  
  //private static final String TAG = "MyCustomReceiver";  
  @Override  
  public void onReceive(Context context, Intent intent) {  
       try {  
            if (intent == null)  
            {  
                 //Log.d(TAG, "Receiver intent null");  
            }  
            else  
            {  
                 String action = intent.getAction();  
                 //Log.d(TAG, "got action " + action );  
                 if (action.equals("com.example.musicrec.UPDATE_STATUS"))  
                 {  
                      String channel = intent.getExtras().getString("com.parse.Channel");  
                      JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));  
                      //Log.d(TAG, "got action " + action + " on channel " + channel + " with:");  
                      Iterator itr = json.keys();  
                      while (itr.hasNext()) {  
                           String key = (String) itr.next();  

                      }  
                 }  
            }  
       } catch (JSONException e) {  
       }  
  }  
}
