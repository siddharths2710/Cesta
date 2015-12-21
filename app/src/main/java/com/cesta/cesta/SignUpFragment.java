package com.cesta.cesta;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class SignUpFragment extends Fragment implements
		View.OnClickListener,
		ActivityCompat.OnRequestPermissionsResultCallback,
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener {

	public static final String ARG_SIGN_UP = "sign_up";

    private static final String TAG = "pheah.SignUpFragment";

	/* RequestCode for resolutions involving sign-in */
	private static final int RC_SIGN_IN = 1;

	/* RequestCode for resolutions to get GET_ACCOUNTS permission on M */
	private static final int RC_PERM_GET_ACCOUNTS = 2;

	/* Keys for persisting instance variables in savedInstanceState */
	private static final String KEY_IS_RESOLVING = "is_resolving";
	private static final String KEY_SHOULD_RESOLVE = "should_resolve";
    public static final String PHOTO_URL = "photoUrl";
    public static final String EMAIL = "email";
    public static final String NAME = "name";

    /* Client for accessing Google APIs */
	private GoogleApiClient mGoogleApiClient;

	/* View to display current status (signed-in, signed-out, disconnected, etc) */
	private TextView mStatus;

	// [START resolution_variables]
	/* Is there a ConnectionResult resolution in progress? */
	private boolean mIsResolving = false;

    /* Should we automatically resolve ConnectionResults when possible? */
	private boolean mShouldResolve = false;
	// [END resolution_variables]

	private boolean startMap = true;


    private TextView textDetails;

    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private List<String> permissions = Arrays.asList("public_profile", "user_friends", "email", "user_location");
    private Profile profile;

    private String facebook_id;
    private String f_name;
    private String m_name;
    private String l_name;
    private String full_name;
    private String profile_image;
    private String email_id;
    private String gender;

    private FacebookCallback<LoginResult> callback= new FacebookCallback<LoginResult>() {
        private ProfileTracker mProfileTracker;

        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();
            textDetails = (TextView) getActivity().findViewById(R.id.status);
            profile = Profile.getCurrentProfile();

            if (profile == null) {
                mProfileTracker = new ProfileTracker() {
                    @Override
                    protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                        fblogin(profile2);
                        mProfileTracker.stopTracking();
                    }
                };
                mProfileTracker.startTracking();
            } else {
                facebook_id=profile.getId();
                f_name=profile.getFirstName();
                m_name=profile.getMiddleName();
                l_name=profile.getLastName();
                full_name=profile.getName();
                profile_image=profile.getProfilePictureUri(400, 400).toString();
                Log.d("Pheah", full_name);
                fblogin(profile);
            }

           /*GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            try {
                                email_id = object.getString("email");
                                gender = object.getString("gender");
                                //Start new activity or use this info in your project.
                                Intent i = new Intent();
                                i.putExtra("type", "facebook");
                                i.putExtra("facebook_id", facebook_id);
                                i.putExtra("f_name", f_name);
                                i.putExtra("m_name", m_name);
                                i.putExtra("l_name", l_name);
                                i.putExtra("full_name", full_name);
                                i.putExtra("profile_image", profile_image);
                                i.putExtra("email_id", email_id);
                                i.putExtra("gender", gender);

                                startActivity(i);
                                finish();
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                //  e.printStackTrace();
                            }

                        }

                    });

            request.executeAsync();*/

            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {
                            // Application code
                            Log.v("LoginActivity", response.toString());
                            try {
                                JSONObject jsonObject = response.getJSONObject();
                                Log.d(TAG, "jsonObj = " + jsonObject.toString());
                                String email = jsonObject.getString("email");

                                SharedPreferences p = getActivity().getSharedPreferences(PREF, Context.MODE_PRIVATE);
                                SharedPreferences.Editor e = p.edit();
                                e.putString("email", email);
                                e.apply();

                                Toast.makeText(getContext(), "Email : " + email, Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getContext(), "Error getting email..", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender, birthday");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {
            //textDetails.setText("Error");
            Toast.makeText(getContext(), "Error!!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(FacebookException error) {
            //textDetails.setText("Error");
            Toast.makeText(getContext(), "Error!!", Toast.LENGTH_SHORT).show();
        }
    };

	public SignUpFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.layout_signup, container, false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        //FacebookSdk.sdkInitialize(getContext());
        //callbackManager=CallbackManager.Factory.create();

		Bundle arguments = getArguments();
		if (arguments!= null)
			startMap = arguments.getBoolean(ARG_SIGN_UP, true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

        /*FacebookSdk.sdkInitialize(getContext());
        callbackManager=CallbackManager.Factory.create();*/
        callbackManager=CallbackManager.Factory.create();

        // Restore from saved instance state
		// [START restore_saved_instance_state]
		if (savedInstanceState != null) {
			mIsResolving = savedInstanceState.getBoolean(KEY_IS_RESOLVING);
			mShouldResolve = savedInstanceState.getBoolean(KEY_SHOULD_RESOLVE);
			//startMap = savedInstanceState.getBoolean(ARG_SIGN_UP, true);
		}
		// [END restore_saved_instance_state]

		// Set up button click listeners
		getActivity().findViewById(R.id.sign_in).setOnClickListener(this);
		getActivity().findViewById(R.id.sign_out).setOnClickListener(this);
		getActivity().findViewById(R.id.disconnect_button).setOnClickListener(this);
		getActivity().findViewById(R.id.map_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(getActivity(), MapActivity.class)); //TODO: Make Fragment.
                //((MainActivity) getActivity()).openMap(new MapFragment());
            }
        });

		// Large sign-in
		((SignInButton) getActivity().findViewById(R.id.sign_in)).setSize(SignInButton.SIZE_WIDE);

		// Start with sign-in button disabled until sign-in either succeeds or fails
		getActivity().findViewById(R.id.sign_in).setEnabled(false);

		// Set up view instances
		mStatus = (TextView) getActivity().findViewById(R.id.status);

        loginButton=(LoginButton) getActivity().findViewById(R.id.login_button);
        loginButton.setReadPermissions(permissions);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.registerCallback(callbackManager, callback);
            }
        });

		// [START create_google_api_client]
		// Build GoogleApiClient with access to basic profile
		mGoogleApiClient = new GoogleApiClient.Builder(getContext())
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(Plus.API)
				.addScope(new Scope(Scopes.PROFILE))
				.addScope(new Scope(Scopes.EMAIL))
				.build();
		// [END create_google_api_client]
	}

	private void updateUI(boolean isSignedIn) {
		if (isSignedIn) {
			Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
			if (currentPerson != null) {
				// Show signed-in user's name
				String name = currentPerson.getDisplayName();
				mStatus.setText(name);//getString(R.string.signed_in_fmt, name));

				// Show users' email address (which requires GET_ACCOUNTS permission)
				if (checkAccountsPermission()) {
					String currentAccount = Plus.AccountApi.getAccountName(mGoogleApiClient);
					mStatus.setText(mStatus.getText() + currentAccount);
				}
			} else {
				// If getCurrentPerson returns null there is generally some error with the
				// configuration of the application (invalid Client ID, Plus API not enabled, etc).
				Log.w(TAG, "Null person");
				mStatus.setText("Null person");
			}

			// Set button visibility
			getActivity().findViewById(R.id.sign_in).setVisibility(View.GONE);
			//getActivity().findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
			getActivity().findViewById(R.id.sign_out).setVisibility(View.VISIBLE);
			getActivity().findViewById(R.id.disconnect_button).setVisibility(View.VISIBLE);
		} else {
			// Show signed-out message and clear email field
			mStatus.setText("Please Sign in");
			//((TextView) getActivity().findViewById(R.id.email)).setText("");

			// Set button visibility
			getActivity().findViewById(R.id.sign_in).setEnabled(true);
			getActivity().findViewById(R.id.sign_in).setVisibility(View.VISIBLE);
			//getActivity().findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
			getActivity().findViewById(R.id.sign_out).setVisibility(View.GONE);
			getActivity().findViewById(R.id.disconnect_button).setVisibility(View.GONE);
		}
	}

	/**
	 * Check if we have the GET_ACCOUNTS permission and request it if we do not.
	 * @return true if we have the permission, false if we do not.
	 */
	private boolean checkAccountsPermission() {
		final String perm = Manifest.permission.GET_ACCOUNTS;
		int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), perm);
        Log.d(TAG, "checkAccountsPermission()");
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
			// We have the permission
            Log.d(TAG, "checkAccountsPermission(): granted");
            //showSignedInUI();
            return true;
		} else if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), perm)) {
			// Need to show permission rationale, display a snackbar and then request
			// the permission again when the snackbar is dismissed.
			/*Snackbar.make(getActivity().findViewById(R.id.main_layout),
                    R.string.contacts_permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
					.setAction(android.R.string.ok, new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							// Request the permission again.
							ActivityCompat.requestPermissions(getActivity(),
									new String[]{perm},
									RC_PERM_GET_ACCOUNTS);
						}
					}).show();*/
            Log.d(TAG, "checkAccountsPermission(): asking..");
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{perm},
                    RC_PERM_GET_ACCOUNTS);
			return false;
		} else {
			// No explanation needed, we can request the permission.
            Log.d(TAG, "checkAccountsPermission(): asking...");
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{perm},
                    RC_PERM_GET_ACCOUNTS);
			return false;
		}
		/*else {
			Toast.makeText(getActivity(), "Please grant all the required permissions", Toast.LENGTH_SHORT).show();
			return false;
		}*/
	}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult:" + requestCode);
        if (requestCode == RC_PERM_GET_ACCOUNTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showSignedInUI();
            } else {
                Log.d(TAG, "GET_ACCOUNTS Permission Denied.");
            }
        }
    }

	public static final String PREF = "com.pheah.pheah_android.PREF";

	private void showSignedInUI() {
		updateUI(true);

        if (checkAccountsPermission()) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            Log.d(TAG, "p = " + currentPerson + ", m = " + startMap);
            if (currentPerson != null) {
                SharedPreferences p = getActivity().getSharedPreferences(PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor e = p.edit();
                e.putString(NAME, currentPerson.getDisplayName());
                e.putString(EMAIL, Plus.AccountApi.getAccountName(mGoogleApiClient));
                e.putString(PHOTO_URL, currentPerson.getImage().getUrl());
                e.apply();
                if (startMap) {
                    startActivity(new Intent(getActivity(), MapActivity.class)); //TODO: Make Fragment.
                    getActivity().finish();
                    //((MainActivity) getActivity()).openMap(new MapFragment());
                }
            }
        }
	}

    private void fblogin(Profile p) {
        SharedPreferences sh = getActivity().getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sh.edit();
        e.putString(NAME, p.getName());
        //e.putString("email", p.getId());
        e.putString(PHOTO_URL, p.getProfilePictureUri(400, 400).toString());
        e.apply();
        if (startMap) {
            startActivity(new Intent(getActivity(), MapActivity.class)); //TODO: Make Fragment.
            getActivity().finish();
        }
    }

	private void showSignedOutUI() {
        SharedPreferences sh = getActivity().getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sh.edit();
        editor.remove(NAME);
        editor.remove(EMAIL);
        editor.remove(PHOTO_URL);
        editor.apply();
        updateUI(false);
	}

	// [START on_start_on_stop]
	@Override
	public void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}
    @Override
	public void onStop() {
		super.onStop();
		mGoogleApiClient.disconnect();
	}

	// [END on_start_on_stop]
    // [START on_save_instance_state]
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(KEY_IS_RESOLVING, mIsResolving);
		outState.putBoolean(KEY_SHOULD_RESOLVE, mShouldResolve);
	}

	// [END on_save_instance_state]
    // [START on_activity_result]
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);

		if (requestCode == RC_SIGN_IN) {
			// If the error resolution was not successful we should not resolve further.
			if (resultCode != Activity.RESULT_OK) {
				mShouldResolve = false;
			}

			mIsResolving = false;
			mGoogleApiClient.connect();
		} else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

	// [END on_activity_result]

	// [START on_connected]
	@Override
	public void onConnected(Bundle bundle) {
		// onConnected indicates that an account was selected on the device, that the selected
		// account has granted any requested permissions to our app and that we were able to
		// establish a service connection to Google Play services.
		Log.d(TAG, "onConnected:" + bundle);
		mShouldResolve = false;

		// Show the signed-in UI
		showSignedInUI();
	}
	// [END on_connected]

	@Override
	public void onConnectionSuspended(int i) {
		// The connection to Google Play services was lost. The GoogleApiClient will automatically
		// attempt to re-connect. Any UI elements that depend on connection to Google APIs should
		// be hidden or disabled until onConnected is called again.
		Log.w(TAG, "onConnectionSuspended:" + i);
	}

	// [START on_connection_failed]
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		// Could not connect to Google Play Services.  The user needs to select an account,
		// grant permissions or resolve an error in order to sign in. Refer to the javadoc for
		// ConnectionResult to see possible error codes.
		Log.d(TAG, "onConnectionFailed:" + connectionResult);

		if (!mIsResolving && mShouldResolve) {
			Log.d(TAG, "inside first if");
			if (connectionResult.hasResolution()) {
				try {
					connectionResult.startResolutionForResult(getActivity(), RC_SIGN_IN);
					mIsResolving = true;
					Log.d(TAG, "inside try and second if");
				} catch (IntentSender.SendIntentException e) {
					Log.e(TAG, "Could not resolve ConnectionResult.", e);
					mIsResolving = false;
					mGoogleApiClient.connect();
				}
			} else {
				// Could not resolve the connection result, show the user an
				// error dialog.
				Log.d(TAG, "Error dialog.");
				showErrorDialog(connectionResult);
			}
		} else {
			Log.d(TAG, "Sign out ui");
			// Show the signed-out UI
			showSignedOutUI();
		}
	}
	// [END on_connection_failed]

	private void showErrorDialog(ConnectionResult connectionResult) {
		GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
		int resultCode = apiAvailability.isGooglePlayServicesAvailable(getActivity());

		if (resultCode != ConnectionResult.SUCCESS) {
			if (apiAvailability.isUserResolvableError(resultCode)) {
				apiAvailability.getErrorDialog(getActivity(), resultCode, RC_SIGN_IN,
						new DialogInterface.OnCancelListener() {
							@Override
							public void onCancel(DialogInterface dialog) {
								mShouldResolve = false;
								showSignedOutUI();
							}
						}).show();
			} else {
				Log.w(TAG, "Google Play Services Error:" + connectionResult);
				String errorString = apiAvailability.getErrorString(resultCode);
				Toast.makeText(getActivity(), errorString, Toast.LENGTH_SHORT).show();

				mShouldResolve = false;
				showSignedOutUI();
			}
		}
	}

	// [START on_click]
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.sign_in:
				onSignInClicked();
				break;
			case R.id.sign_out:
				onSignOutClicked();
				break;
			case R.id.disconnect_button:
				onDisconnectClicked();
				break;
		}
	}
	// [END on_click]

	// [START on_sign_in_clicked]
	private void onSignInClicked() {
		// User clicked the sign-in button, so begin the sign-in process and automatically
		// attempt to resolve any errors that occur.
		mShouldResolve = true;
		mGoogleApiClient.connect();

		// Show a message to the user that we are signing in.
		mStatus.setText("Signing in");
	}
	// [END on_sign_in_clicked]

	// [START on_sign_out_clicked]
	private void onSignOutClicked() {
		// Clear the default account so that GoogleApiClient will not automatically
		// connect in the future.
		if (mGoogleApiClient.isConnected()) {
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			mGoogleApiClient.disconnect();
		}

		showSignedOutUI();
	}
	// [END on_sign_out_clicked]

	// [START on_disconnect_clicked]
	private void onDisconnectClicked() {
		// Revoke all granted permissions and clear the default account.  The user will have
		// to pass the consent screen to sign in again.
		if (mGoogleApiClient.isConnected()) {
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient);
			mGoogleApiClient.disconnect();
		}

		showSignedOutUI();
	}
	// [END on_disconnect_clicked]
}
