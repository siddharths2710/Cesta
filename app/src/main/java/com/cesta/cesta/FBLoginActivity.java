package com.cesta.cesta;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class FBLoginActivity extends AppCompatActivity {

    public static final int RESULT_CODE_LOGGED_IN = 0x10;
    public static final int RESULT_CODE_LOGGED_OUT = 0x11;
    public static final String LOGGED = "Log";

    public static final String LOG_IN = "Login";
    public static final String LOG_OUT = "Logout";

    private static final String TAG = "FBLoginActivity";

    private CallbackManager callbackManager;
    private List<String> permissions = Arrays.asList("public_profile", "user_friends", "email", "user_location");

    private FacebookCallback<LoginResult> callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fblogin);

        callbackManager = CallbackManager.Factory.create();
        callback = new FacebookCallback<LoginResult>() {
            private ProfileTracker mProfileTracker;

            @Override
            public void onSuccess(final LoginResult loginResult) {
                //AccessToken accessToken = loginResult.getAccessToken();
                //textDetails = (TextView) findViewById(R.id.status);
                Log.d(TAG, "onSuccess: loginResult = " + loginResult);
                Profile profile = Profile.getCurrentProfile();
                //final Intent i = new Intent();
                final Account a = new Account(Account.Type.FaceBook);

                if (profile == null) {
                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                            //fblogin(profile2);
                            Log.d(TAG, "onCurrentProfileChanged: got profile " + profile2);
                            mProfileTracker.stopTracking();

                            a.setName(profile2.getName());
                            a.downloadImage(getApplicationContext(),
                                    profile2.getProfilePictureUri(400, 400).getPath());

							/*i.putExtra("Ac", a);
                            i.putExtra(LOGGED, RESULT_CODE_LOGGED_IN);
							if (a.getEmail() != null) {
								setResult(RESULT_OK, i);
								finish();
							} else
								Log.d(TAG, "onCurrentProfileChanged: Waiting for email.");*/
                            startGraph(loginResult, a);
                        }
                    };
                    mProfileTracker.startTracking();
                } else {
                    Log.d(TAG, "onSuccess: got profile directly");
                    String full_name = profile.getName();
                    Log.d(TAG, full_name);
                    //fblogin(profile);

                    a.setName(full_name);
                    a.downloadImage(getApplicationContext(),
                            profile.getProfilePictureUri(400, 400).getPath());

					/*i.putExtra("Ac", a);
					i.putExtra(LOGGED, RESULT_CODE_LOGGED_IN);*/
                    startGraph(loginResult, a);
                }
            }

            private void startGraph(LoginResult loginResult, final Account account) {
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

                                    Toast.makeText(FBLoginActivity.this, "Email : " + email, Toast
                                            .LENGTH_SHORT).show();
                                    Log.d(TAG, "onCompleted: Email : " + email);
                                    account.setEmail(email);
                                    account.setGender(jsonObject.getString("gender"));
									/*if (account.getName() != null) {
										fbLoginDone = true;
									}*/
                                    Intent i = new Intent();
                                    i.putExtra("Ac", account);
                                    setResult(RESULT_OK, i);
                                    finish();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(FBLoginActivity.this, "Error getting email..", Toast
                                            .LENGTH_SHORT).show();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(FBLoginActivity.this, "Canceled by user.", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "onCancel: FB login canceled by User.");
                setResult(RESULT_CANCELED);
                finish();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(FBLoginActivity.this, "Error couldn't log you in.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onError: FB login error", error);
                setResult(RESULT_CANCELED);
                finish();
            }
        };

        LoginManager.getInstance().registerCallback(callbackManager, callback);

        Intent i = getIntent();
        if (i == null || i.getAction() == null) {
            Log.w(TAG, "i is null || action is null");
        } else if (i.getAction().equals(LOG_IN)) {
            Log.d(TAG, "Facebook logging in...");
            //onSignInClicked();
            //loginButton.registerCallback(callbackManager, callback);
			/*LoginManager loginManager = LoginManager.getInstance();
			loginManager.registerCallback(callbackManager, callback);*/
            Log.d(TAG, "onCreate: Callback registered. Logging in.");
            LoginManager.getInstance().logInWithReadPermissions(this, permissions);
            Log.d(TAG, "onCreate: Logged In");
        } else if (i.getAction().equals(LOG_OUT)) {
            Log.d(TAG, "Facebook logging out...");
            //onSignOutClicked();
            //loginButton.registerCallback(callbackManager, callback);
            LoginManager loginManager = LoginManager.getInstance();
            loginManager.registerCallback(callbackManager, callback);
            loginManager.logOut();
            setResult(RESULT_OK);
            finish();
            Log.d(TAG, "onCreate: FB log out successful.");
		/*} else if (i.getAction().equals(GET_DATA)) {
			Log.d(TAG, "Facebook getting data");
			LoginManager.getInstance().logInWithReadPermissions(this, permissions);*/
        } else
            Log.w(TAG, "Unknown");
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Deprecated
    public void registerListener(final LoginButton button) {
        button.setReadPermissions(permissions);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.registerCallback(callbackManager, callback);
				/*Intent i = new Intent(FBLoginActivity.this, FBLoginActivity.class);
				i.setAction(LOG_IN);
				startActivity(i);*/
            }
        });
    }
}
