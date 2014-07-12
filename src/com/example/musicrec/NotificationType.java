package com.example.musicrec;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;


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

}
