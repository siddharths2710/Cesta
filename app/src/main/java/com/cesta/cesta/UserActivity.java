package com.cesta.cesta;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class UserActivity extends AppCompatActivity {

    private String[] sideBarArray;
    private DrawerLayout drawerLayout;
    private ListView sideBarListView;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);

        sideBarArray = getResources().getStringArray(R.array.frag_list);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        sideBarListView = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        sideBarListView.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, sideBarArray));
        // Set the list's click listener
        sideBarListView.setOnItemClickListener(new DrawerItemClickListener());

        mTitle = mDrawerTitle = getTitle();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                null, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        drawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        selectItem(0);
    }

    /**
     * Swaps fragments in the main content view
     */
    public void selectItem(int position) {
        Fragment fragment = null;
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position) {
            case 0:
                fragment = new UserFragment();
                // Insert the fragment by replacing any existing fragment
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .commit();
                break;
            case 1:
                // Create a new fragment and specify the planet to show based on position
                fragment = new SignUpFragment();
                Bundle args = new Bundle();
                args.putBoolean(SignUpFragment.ARG_SIGN_UP, false);
                fragment.setArguments(args);

                // Insert the fragment by replacing any existing fragment
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .commit();
                break;
        }

        // Highlight the selected item, update the title, and close the drawer
        sideBarListView.setItemChecked(position, true);
        setTitle(sideBarArray[position]);
        drawerLayout.closeDrawer(sideBarListView);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        //boolean drawerOpen = drawerLayout.isDrawerOpen(sideBarListView);
        //menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }
}
