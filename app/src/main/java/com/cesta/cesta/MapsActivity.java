package com.cesta.cesta;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";
    private static final int MY_LOCATION_REQUEST_CODE = 0x21;
    //private static final int MY_LOCATION_REQUEST_CODE2 = 0x22;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    //private GoogleApiClient mGoogleApiClient;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private CharSequence drawerTitle = "Map";
    private CharSequence title = "Drawer";
    private LatLng loc;
    private boolean markerFlag;
    GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {

            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            loc = new LatLng(latitude, longitude);
            if (markerFlag) {
                Marker mMarker = mMap.addMarker(new MarkerOptions().position(loc).title("GET RIDES FROM HERE"));

                mMap.addCircle(new CircleOptions().center(loc).radius(50).fillColor(Color.TRANSPARENT));
                markerFlag = false;
            }
            if (mMap != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 18.0f));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);

        Toolbar actionBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(actionBar);
        getSupportActionBar().show();
        /*getSupportActionBar().show();
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);*/

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mMap = mapFragment.getMap();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, actionBar,
                R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(title);
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getSupportActionBar().setTitle(drawerTitle);
                supportInvalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);

        ((NavigationView) findViewById(R.id.nav_view)).setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        FragmentTransaction transaction;
                        switch (menuItem.getItemId()) {
                            case R.id.map_item:
                                transaction = getSupportFragmentManager()
                                        .beginTransaction();

                                // Replace whatever is in the fragment_container view with this fragment,
                                // and add the transaction to the back stack if needed
                                transaction.replace(R.id.map, mapFragment);
                                transaction.addToBackStack(null);

                                // Commit the transaction
                                transaction.commit();

                                // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                        .findFragmentById(R.id.map);
                                mapFragment.getMapAsync(MapsActivity.this);

                                mMap = mapFragment.getMap();
                                break;
                            case R.id.profile_item:
                                // TODO: add profile.
                                /*transaction = getSupportFragmentManager()
										.beginTransaction();

								// Replace whatever is in the fragment_container view with this fragment,
								// and add the transaction to the back stack if needed
								transaction.replace(R.id.map, new ProfileFrag());
								transaction.addToBackStack(null);

								// Commit the transaction
								transaction.commit();*/
                                break;
                        }
                        drawerLayout.closeDrawers();
                        return true;
                    }
                }
        );

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogBox();
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
            Log.d(TAG, "Asking permissions...");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_LOCATION_REQUEST_CODE);
        }
    }

    private void showDialogBox() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        View dialog = View.inflate(this, R.layout.dialog_layout, null);
        //b.setCustomTitle(dialog);
        b.setView(dialog);

        AppCompatSpinner spinner = (AppCompatSpinner) dialog.findViewById(R.id.seat_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.seats, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        b.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MapsActivity.this, "Contents saved (Not really).", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Contents saved.");
            }
        });
        b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MapsActivity.this, "Canceled.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Canceled.");
            }
        });
        b.show();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(myLocationChangeListener);  //For current location

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
		mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
		mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            } else {
                // Permission was denied. Display an error message.
                Log.w(TAG, "Location Permission denied.");
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

	/* Facebook App Tracking
    @Override
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