package com.example.musicrec;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/*
 * Parse Class
 * Used to store info about a particular song.
 */

@ParseClassName("Song")
public class Song extends ParseObject{
	
	
	public Song() {
	    // A default constructor is required.
	  }
	
	public String getTitle() {
		return getString("title");
	}

	public void setTitle(String title) {
		put("title", title);
	}
	
	public int getLikes() {
    return getInt("likes");
  }

  public void setLikes(int l) {
    put("likes", l);
  }
	
	public String getArtist() {
		return getString("artist");
	}

	public void setArtist(String artist) {
		put("artist", artist);
	}

	
	public ParseUser getAuthor() {
		return getParseUser("author");
	}

	public void setAuthor(ParseUser user) {
		put("author", user);
	}
	
}
