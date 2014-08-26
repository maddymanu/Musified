package com.example.musicrec;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

/*
 * This class is the facebook Login Activity
 */
public class LoginActivity extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_facebook);

	}

	/*
	 * Decides which activity to start based on whether or not the user is present.
	 */
	public void loginUsingFacebook(View v) {
		ParseFacebookUtils.logIn(this, new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException err) {
				if (user == null) {
					Log.d("MyApp",
							"Uh oh. The user cancelled the Facebook login.");
				} else if (user.isNew()) {
					Log.d("MyApp",
							"User signed up and logged in through Facebook!");
					Intent intent = new Intent(LoginActivity.this, Welcome.class);
					startActivity(intent);
				} else {
					Log.d("MyApp", "User logged in through Facebook!");
					Intent intent = new Intent(LoginActivity.this, Welcome.class);
					startActivity(intent);
				}
			}
		});
	}


	/*
	 * This is the calling activity that asks Facbook to authenticate the user.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}
}
