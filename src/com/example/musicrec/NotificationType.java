package com.example.musicrec;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/*
 * Parse Class.
 * used to store info about a single notification.
 */

@ParseClassName("NotificationType")
public class NotificationType extends ParseObject {

  public NotificationType() {
    // TODO Auto-generated constructor stub
  }

  public ParseUser getFromUser() {
    return getParseUser("fromUser");
  }

  public void setFromUser(ParseUser user) {
    put("fromUser", user);
  }

  public ParseUser getToUser() {
    return getParseUser("toUser");
  }

  public void setToUser(ParseUser user) {
    put("toUser", user);
  }

  public String getType() {
    return getString("type");
  }

  public void setType(String t) {
    put("type", t);
  }

  public Song getSong() {
    return (Song) get("Song");
  }

  public void setSong(Song song) {
    put("Song", song);
  }

  public String getStatus() {
    return getString("status");
  }

  public void setStatus(String s) {
    put("status", s);
  }

}
