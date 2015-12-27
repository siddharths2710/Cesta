package com.cesta.cesta;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class GoogleSignActivity extends AppCompatActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final int RESULT_CODE_LOGGED_IN = 0x10;
    public static final int RESULT_CODE_LOGGED_OUT = 0x11;

    public static final String PHOTO_URL = "photoUrl";
    public static final String EMAIL = "email";
    public static final String NAME = "name";

    public static final String PREF = "com.pheah.pheah_android.PREF";
    public static final String LOGGED = "Log";
    public static final String SIGN_IN = "Login";
    public static final String SIGN_OUT = "Signout";
    public static final String DISCONNECT = "Disconnect";
    private static final String TAG = "GoogleSignActivity";
    /* RequestCode for resolutions involving sign-in */
    private static final int RC_SIGN_IN = 1;
    /* RequestCode for resolutions to get GET_ACCOUNTS permission on M */
    private static final int RC_PERM_GET_ACCOUNTS = 2;
    /* Keys for persisting instance variables in savedInstanceState */
    private static final String KEY_IS_RESOLVING = "is_resolving";
    private static final String KEY_SHOULD_RESOLVE = "should_resolve";
    /* Client for accessing Google APIs */
    private GoogleApiClient mGoogleApiClient;
    // [START resolution_variables]
    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;
    // [END resolution_variables]
    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;

    private boolean startMap = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_sign);

        try {
            getSupportActionBar().hide();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        // Restore from saved instance state
        // [START restore_saved_instance_state]
        if (savedInstanceState != null) {
            mIsResolving = savedInstanceState.getBoolean(KEY_IS_RESOLVING);
            mShouldResolve = savedInstanceState.getBoolean(KEY_SHOULD_RESOLVE);
            //startMap = savedInstanceState.getBoolean(ARG_SIGN_UP, true);
        }
        // [END restore_saved_instance_state]

        // Set up button click listeners
        /*findViewById(R.id.sign_in).setOnClickListener(this);
        findViewById(R.id.sign_out).setOnClickListener(this);
        findViewById(R.id.disconnect_button).setOnClickListener(this);
        findViewById(R.id.map_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(this, UserActivity.class)); //TODO: Make Fragment.
                //((MainActivity) this).openMap(new MapFragment());
            }
        });

        // Large sign-in
        ((SignInButton) findViewById(R.id.sign_in)).setSize(SignInButton.SIZE_WIDE);

        // Start with sign-in button disabled until sign-in either succeeds or fails
        findViewById(R.id.sign_in).setEnabled(false);

        // Set up view instances
        //mStatus = (TextView) findViewById(R.id.status);
        */

        // [START create_google_api_client]
        // Build GoogleApiClient with access to basic profile
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.EMAIL))
                .build();
        // [END create_google_api_client]

        Intent i = getIntent();
        if (i == null || i.getAction() == null) {
            Log.w(TAG, "i is null");
        } else if (i.getAction().equals(SIGN_IN)) {
            onSignInClicked();
            Log.d(TAG, "Google signing in...");
        } else if (i.getAction().equals(SIGN_OUT)) {
            onSignOutClicked();
            Log.d(TAG, "Google signing out...");
        } else if (i.getAction().equals(DISCONNECT)) {
            onDisconnectClicked();
            Log.d(TAG, "Google Disconnecting...");
        } else
            Log.w(TAG, "Unknown");
    }

    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            if (currentPerson != null) {
                // Show signed-in user's name
                String name = currentPerson.getDisplayName();
                //mStatus.setText(name);//getString(R.string.signed_in_fmt, name));
                Log.d(TAG, "Signed in: name");

                // Show users' email address (which requires GET_ACCOUNTS permission)
                if (checkAccountsPermission()) {
                    String currentAccount = Plus.AccountApi.getAccountName(mGoogleApiClient);
                    //mStatus.setText(mStatus.getText() + currentAccount);
                    Log.d(TAG, "Signed in email : " + currentAccount);
                }
            } else {
                // If getCurrentPerson returns null there is generally some error with the
                // configuration of the application (invalid Client ID, Plus API not enabled, etc).
                Log.w(TAG, "Null person");
                //mStatus.setText("Null person");
            }

            /*// Set button visibility
            findViewById(R.id.sign_in).setVisibility(View.GONE);
            //findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out).setVisibility(View.VISIBLE);
            findViewById(R.id.disconnect_button).setVisibility(View.VISIBLE);*/
        } /*else {
            // Show signed-out message and clear email field
            //mStatus.setText("Please Sign in");
            //((TextView) findViewById(R.id.email)).setText("");

            // Set button visibility
            findViewById(R.id.sign_in).setEnabled(true);
            findViewById(R.id.sign_in).setVisibility(View.VISIBLE);
            //findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
            findViewById(R.id.sign_out).setVisibility(View.GONE);
            findViewById(R.id.disconnect_button).setVisibility(View.GONE);
        }*/
    }

    /**
     * Check if we have the GET_ACCOUNTS permission and request it if we do not.
     *
     * @return true if we have the permission, false if we do not.
     */
    private boolean checkAccountsPermission() {
        final String perm = Manifest.permission.GET_ACCOUNTS;
        int permissionCheck = ContextCompat.checkSelfPermission(this, perm);
        Log.d(TAG, "checkAccountsPermission()");
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            // We have the permission
            Log.d(TAG, "checkAccountsPermission(): granted");
            //showSignedInUI();
            return true;
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
            // Need to show permission rationale, display   a snackbar and then request
            // the permission again when the snackbar is dismissed.
            /*Snackbar.make(findViewById(R.id.main_layout),
                    R.string.contacts_permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
					.setAction(android.R.string.ok, new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							// Request the permission again.
							ActivityCompat.requestPermissions(this,
									new String[]{perm},
									RC_PERM_GET_ACCOUNTS);
						}
					}).show();*/
            Log.d(TAG, "checkAccountsPermission(): asking..");
            ActivityCompat.requestPermissions(this,
                    new String[]{perm},
                    RC_PERM_GET_ACCOUNTS);
            return false;
        } else {
            // No explanation needed, we can request the permission.
            Log.d(TAG, "checkAccountsPermission(): asking...");
            ActivityCompat.requestPermissions(this,
                    new String[]{perm},
                    RC_PERM_GET_ACCOUNTS);
            return false;
        }
        /*else {
			Toast.makeText(this, "Please grant all the required permissions", Toast.LENGTH_SHORT).show();
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

    private void showSignedInUI() {
        updateUI(true);

        if (checkAccountsPermission()) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            Log.d(TAG, "p = " + currentPerson + ", m = " + startMap);
            if (currentPerson != null) {
                SharedPreferences p = getSharedPreferences(PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor e = p.edit();
                e.putString(NAME, currentPerson.getDisplayName());
                e.putString(EMAIL, Plus.AccountApi.getAccountName(mGoogleApiClient));
                e.putString(PHOTO_URL, currentPerson.getImage().getUrl());
                e.apply();
                /*if (startMap) {
                    *//*startActivity(new Intent(this, UserActivity.class)); //TODO: Make Fragment.
                    finish();*//*
                    //((MainActivity) this).openMap(new MapFragment());
                }*/
                Intent i = new Intent();
                final Account a = new Account(Account.Type.Google);
                a.setName(currentPerson.getDisplayName());
                a.setEmail(Plus.AccountApi.getAccountName(mGoogleApiClient));

                new AsyncTask<String, Void, Bitmap>() {

                    @Override
                    protected Bitmap doInBackground(String... params) {

                        try {
                            URL url = new URL(params[0]);
                            InputStream in = url.openStream();
                            return BitmapFactory.decodeStream(in);
                        } catch (Exception e) {
                            Log.d(TAG, "Error getting image");
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        ContextWrapper cw = new ContextWrapper(getApplicationContext());
                        // path to /data/data/yourapp/app_data/imageDir
                        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                        // Create imageDir
                        File mypath = new File(directory, "profile.png");

                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(mypath);
                            // Use the compress method on the BitMap object to write image to the OutputStream
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                fos.close();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                        String path = directory.getAbsolutePath();
                        a.setImagePath(path);
                    }
                }.execute(currentPerson.getImage().getUrl());

                i.putExtra("Ac", a);
                i.putExtra(LOGGED, RESULT_CODE_LOGGED_IN);
                setResult(RESULT_OK, i);
                finish();
            }
        }
    }

    private void showSignedOutUI() {
        SharedPreferences sh = getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sh.edit();
        editor.remove(NAME);
        editor.remove(EMAIL);
        editor.remove(PHOTO_URL);
        editor.apply();
        updateUI(false);
        Intent i = new Intent();
        i.putExtra(LOGGED, RESULT_CODE_LOGGED_OUT);
        setResult(RESULT_OK, i);
        finish();
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
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
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
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, RC_SIGN_IN,
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
                Toast.makeText(this, errorString, Toast.LENGTH_SHORT).show();

                mShouldResolve = false;
                showSignedOutUI();
            }
        }
    }

    /*// [START on_click]
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
    // [END on_click]*/

    // [START on_sign_in_clicked]
    private void onSignInClicked() {
        // User clicked the sign-in button, so begin the sign-in process and automatically
        // attempt to resolve any errors that occur.
        mShouldResolve = true;
        mGoogleApiClient.connect();
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
