package com.example.musicrec;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragment;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class FragmentTab1 extends SherlockFragment {
	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.tab_feed, container, false);

		getFacebookIdInBackground();

		com.beardedhen.androidbootstrap.BootstrapButton btn = (com.beardedhen.androidbootstrap.BootstrapButton) rootView
				.findViewById(R.id.logout_btn);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ParseUser.getCurrentUser();
				ParseUser.logOut();
				Intent intent = new Intent(getActivity(), LoginActivity.class);
				startActivity(intent);

			}
		});

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/* User List Friends */

		Request.executeMyFriendsRequestAsync(ParseFacebookUtils.getSession(),
				new Request.GraphUserListCallback() {

					@SuppressWarnings("unchecked")
					@Override
					public void onCompleted(List<GraphUser> users,
							Response response) {
						if (users != null) {
							List<String> friendsList = new ArrayList<String>();
							for (GraphUser user : users) {
								friendsList.add(user.getId());
							}

							// Construct a ParseUser query that will find
							// friends whose
							// facebook IDs are contained in the current user's
							// friend list.
							@SuppressWarnings("rawtypes")
							ParseQuery friendQuery = ParseQuery.getUserQuery();
							friendQuery.whereContainedIn("fbId", friendsList);

							// findObjects will return a list of ParseUsers that
							// are friends with
							// the current user
							try {
								@SuppressWarnings("unused")
								List<ParseObject> friendUsers = friendQuery
										.find();
								Log.d("MyApp",
										"Size is=  " + friendUsers.size());
								Log.d("MyApp", "Name is=  "
										+ friendUsers.get(0).get("fbId"));
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				});

		return rootView;
	}

	@SuppressWarnings("deprecation")
	private static void getFacebookIdInBackground() {
		Request.executeMeRequestAsync(ParseFacebookUtils.getSession(),
				new Request.GraphUserCallback() {
					@Override
					public void onCompleted(GraphUser user, Response response) {
						if (user != null) {
							ParseUser.getCurrentUser()
									.put("fbId", user.getId());
							ParseUser.getCurrentUser().saveInBackground();
							// use this part to get details from the logged in
							// user
						}
					}
				});
	}

}