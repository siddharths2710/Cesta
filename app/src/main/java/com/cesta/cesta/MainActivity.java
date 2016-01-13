package com.cesta.cesta;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

	/*@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
			}
		});
	}*/

	public static final String PREF = "com.cesta.cesta.PREF";
	public static final int REQUEST_CODE_GOOGLE_SIGN_IN = 0x1;
	public static final int REQUEST_CODE_GOOGLE_SIGN_OUT = 0x2;
	public static final int REQUEST_CODE_FB_LOG_IN = 0x3;
	public static final int REQUEST_CODE_FB_LOG_OUT = 0x4;
	public static final String ACCOUNT_FILE = "account";
	public static final String SIGNED_IN = "signedIn";
	private static final String TAG = "MainActivity";

	//private Fragment fragment;
	private Button fbBtn;
	private View googleBtn;

	private CallbackManager callbackManager;
	private List<String> permissions = Arrays.asList("public_profile", "user_friends", "email", "user_location");

	private FacebookCallback<LoginResult> callback;
	private Account account;
	private boolean fbLoginDone = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FacebookSdk.sdkInitialize(this);

		//setContentView(R.layout.frag_container);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		/*fragment = new SignUpFragment();

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(R.id.fragmentContainer, fragment);
		transaction.commit();*/

		SharedPreferences p = getSharedPreferences(PREF, Context.MODE_PRIVATE);

		if (p.contains(SIGNED_IN) && p.getBoolean(SIGNED_IN, false)) {
			Intent intent = new Intent(this, MapsActivity.class);
			Serializable s = null;
			try {
				FileInputStream fis = openFileInput(ACCOUNT_FILE);
				ObjectInputStream is = new ObjectInputStream(fis);
				s = (Serializable) is.readObject();
				is.close();
				fis.close();
			} catch (Exception e) {
				Log.d(TAG, "Hello");
			}
			intent.putExtra("ac", s);
			startActivity(intent);
			finish();
		}

		/*callbackManager = CallbackManager.Factory.create();
		callback = new FacebookCallback<LoginResult>() {
			private ProfileTracker mProfileTracker;

			@Override
			public void onSuccess(final LoginResult loginResult) {
				//AccessToken accessToken = loginResult.getAccessToken();
				//textDetails = (TextView) findViewById(R.id.status);
				Log.d(TAG, "onSuccess: loginResult = " + loginResult);
				Profile profile = Profile.getCurrentProfile();
				account = new Account(Account.Type.FaceBook);

				if (profile == null) {
					mProfileTracker = new ProfileTracker() {
						@Override
						protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
							//fblogin(profile2);
							Log.d(TAG, "onCurrentProfileChanged: got profile " + profile2);
							mProfileTracker.stopTracking();

							account.setName(profile2.getName());
							account.downloadImage(getApplicationContext(),
									profile2.getProfilePictureUri(400, 400).getPath());

							*//*if (account.getEmail() != null) {
								fbLoginDone = true;
								saveAndOpenMap(account);
							} else
								Log.d(TAG, "onCurrentProfileChanged: Waiting for email.");*//*
							startGraph(loginResult);
						}
					};
					mProfileTracker.startTracking();
				} else {
					Log.d(TAG, "onSuccess: got profile directly");
					String full_name = profile.getName();
					Log.d(TAG, full_name);

					account.setName(full_name);
					account.downloadImage(getApplicationContext(),
							profile.getProfilePictureUri(400, 400).getPath());
					startGraph(loginResult);
				}
			}

			private void startGraph(LoginResult loginResult) {
				GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
						new GraphRequest.GraphJSONObjectCallback() {
							@Override
							public void onCompleted(JSONObject object, GraphResponse response) {
								// Application code
								Log.v("LoginActivity", response.toString());
								try {
									JSONObject jsonObject = response.getJSONObject();
									Log.d(TAG, "jsonObj = " + jsonObject.toString());
									String email = jsonObject.getString("email");

									Toast.makeText(MainActivity.this, "Email : " + email, Toast
											.LENGTH_SHORT).show();
									Log.d(TAG, "onCompleted: Email : " + email);
									account.setEmail(email);
									account.setGender(jsonObject.getString("gender"));
									*//*if (account.getName() != null) {
										fbLoginDone = true;
									}*//*
									fbLoginDone = true;
									saveAndOpenMap(account);
								} catch (JSONException e) {
									e.printStackTrace();
									Toast.makeText(MainActivity.this, "Error getting email..", Toast
											.LENGTH_SHORT).show();
								}
							}
						});
				Bundle parameters = new Bundle();
				parameters.putString("fields", "id,name,email,gender,birthday");
				request.setParameters(parameters);
				request.executeAsync();
			}

			private void saveAndOpenMap(@NonNull Account account) {
				Intent intent = new Intent(MainActivity.this, MapsActivity.class);
				intent.putExtra("ac", account);

				try {
					FileOutputStream fout = openFileOutput("account", Context.MODE_PRIVATE);
					ObjectOutputStream oos = new ObjectOutputStream(fout);
					oos.writeObject(account);
					fout.close();
					oos.close();
					Log.d(TAG, "Account saved.");
				} catch (IOException e) {
					e.printStackTrace();
				}

				SharedPreferences p = getSharedPreferences(PREF, Context.MODE_PRIVATE);
				SharedPreferences.Editor edit = p.edit();
				edit.putBoolean(SIGNED_IN, true);
				edit.apply();

				startActivity(intent);
				finish();
			}

			@Override
			public void onCancel() {
				Toast.makeText(MainActivity.this, "Canceled by user.", Toast.LENGTH_SHORT).show();
				Log.w(TAG, "onCancel: FB login canceled by User.");
				//setResult(RESULT_CANCELED);
				//finish();
			}

			@Override
			public void onError(FacebookException error) {
				Toast.makeText(MainActivity.this, "Error couldn't log you in.", Toast.LENGTH_SHORT)
						.show();
				Log.e(TAG, "onError: FB login error", error);
			}
		};*/

		googleBtn = findViewById(R.id.sign_in);
		googleBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this, GoogleSignActivity.class);
				i.setAction(GoogleSignActivity.SIGN_IN);
				startActivityForResult(i, REQUEST_CODE_GOOGLE_SIGN_IN);
			}
		});

		findViewById(R.id.sign_out).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(MainActivity.this, GoogleSignActivity.class);
				i.setAction(GoogleSignActivity.SIGN_OUT);
				startActivityForResult(i, REQUEST_CODE_GOOGLE_SIGN_OUT);
			}
		});

		fbBtn = (Button) findViewById(R.id.login_button);
		fbBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//loginButton.registerCallback(callbackManager, callback);
				Intent i = new Intent(MainActivity.this, FBLoginActivity.class);
				i.setAction(FBLoginActivity.LOG_IN);
				startActivityForResult(i, REQUEST_CODE_FB_LOG_IN);
			}
		});
		/*fbBtn.setReadPermissions(permissions);
		fbBtn.registerCallback(callbackManager, callback);*/

		findViewById(R.id.log_out).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(MainActivity.this, FBLoginActivity.class);
				i.setAction(FBLoginActivity.LOG_OUT);
				startActivityForResult(i, REQUEST_CODE_FB_LOG_OUT);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//fragment.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_GOOGLE_SIGN_IN) {
			if (resultCode == RESULT_OK) {
				Intent intent = new Intent(this, MapsActivity.class);
				Serializable account = data.getSerializableExtra("Ac");
				intent.putExtra("ac", account);

				try {
					FileOutputStream fout = openFileOutput("account", Context.MODE_PRIVATE);
					ObjectOutputStream oos = new ObjectOutputStream(fout);
					oos.writeObject(account);
					fout.close();
					oos.close();
					Log.d(TAG, "Account saved.");
				} catch (IOException e) {
					e.printStackTrace();
				}

				SharedPreferences p = getSharedPreferences(PREF, Context.MODE_PRIVATE);
				SharedPreferences.Editor edit = p.edit();
				edit.putBoolean(SIGNED_IN, true);
				edit.apply();

				startActivity(intent);
				finish();
			} else {
				Toast.makeText(this, "Failed to login.", Toast.LENGTH_SHORT).show();
				SharedPreferences p = getSharedPreferences(PREF, Context.MODE_PRIVATE);
				SharedPreferences.Editor edit = p.edit();
				edit.putBoolean(SIGNED_IN, false);
				edit.apply();
			}
		} else if (requestCode == REQUEST_CODE_GOOGLE_SIGN_OUT) {
			if (resultCode == RESULT_OK) {
				Toast.makeText(this, "Log out successful.", Toast.LENGTH_SHORT).show();
				Log.d(TAG, "onActivityResult: Log out successful.");
				Snackbar.make(googleBtn, "Log out successful", Snackbar.LENGTH_SHORT).show();
			}
		} else if (requestCode == REQUEST_CODE_FB_LOG_IN) {
			if (resultCode == RESULT_OK) {
				Intent intent = new Intent(this, MapsActivity.class);
				Serializable account = data.getSerializableExtra("Ac");
				intent.putExtra("ac", account);

				try {
					FileOutputStream fout = openFileOutput("account", Context.MODE_PRIVATE);
					ObjectOutputStream oos = new ObjectOutputStream(fout);
					oos.writeObject(account);
					fout.close();
					oos.close();
					Log.d(TAG, "Account saved.");
				} catch (IOException e) {
					e.printStackTrace();
				}

				SharedPreferences p = getSharedPreferences(PREF, Context.MODE_PRIVATE);
				SharedPreferences.Editor edit = p.edit();
				edit.putBoolean(SIGNED_IN, true);
				edit.apply();

				startActivity(intent);
				finish();
			} else {
				Toast.makeText(this, "Failed to login.", Toast.LENGTH_SHORT).show();
				SharedPreferences p = getSharedPreferences(PREF, Context.MODE_PRIVATE);
				SharedPreferences.Editor edit = p.edit();
				edit.putBoolean(SIGNED_IN, false);
				edit.apply();
			}
		} else if (requestCode == REQUEST_CODE_FB_LOG_OUT) {
			if (resultCode == RESULT_OK) {
				Toast.makeText(this, "Log out successful.", Toast.LENGTH_SHORT).show();
				Log.d(TAG, "onActivityResult: Log out successful.");
				Snackbar.make(fbBtn, "Log out successful", Snackbar.LENGTH_SHORT).show();
			}
		}
		//callbackManager.onActivityResult(requestCode, resultCode, data); //TODO: uncomment
	}

	/*public void changeFragment(Fragment fragment) {
		this.fragment = fragment;

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragmentContainer, fragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}*/

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/*@Override
	protected void onPause() {
		super.onPause();

		AppEventsLogger.deactivateApp(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		AppEventsLogger.activateApp(this);
	}*/
}
