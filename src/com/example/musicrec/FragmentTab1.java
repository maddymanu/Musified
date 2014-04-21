package com.example.musicrec;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragment;
import com.parse.ParseUser;

public class FragmentTab1 extends SherlockFragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.tab_feed, container,
				false);
		
		ParseUser currentUser = ParseUser.getCurrentUser(); // used to get the
		Button logout = (Button) rootView.findViewById(R.id.logout_btn);
		logout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ParseUser.getCurrentUser();
				ParseUser.logOut();
				Intent intent = new Intent(getActivity(), LoginActivity.class);
				startActivity(intent);
				
			}
		});
		
		
		return rootView;
	}
	
	

}