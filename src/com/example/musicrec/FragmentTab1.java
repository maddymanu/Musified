package com.example.musicrec;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.parse.ParseUser;

public class FragmentTab1 extends SherlockFragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.tab_feed, container,
				false);
		
		ParseUser currentUser = ParseUser.getCurrentUser(); // used to get the
		// current user
		
		
		
		
		
		return rootView;
	}

}