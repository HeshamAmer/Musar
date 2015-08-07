package com.musar.gui;

import java.util.ArrayList;

import com.musar.services.MainService;
import com.musar.services.RegisterActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends Activity {

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	public static int frag=-1;
	// nav drawer title
	

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		if (!getSavedData("first_time")) {
			// first time
			System.out.println("Started Register page");
			startActivity(new Intent(getApplicationContext(),
					RegisterActivity.class));
			// saveData("registered", true);
		}
		PreferenceManager.setDefaultValues(this, R.layout.fragment_settings, true);
		System.out.println("Started Newsfeed");
		startService(new Intent(this, MainService.class));
		mTitle = getTitle();

		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		// nav drawer icons from resources
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		navDrawerItems = new ArrayList<NavDrawerItem>();

		// adding nav drawer items to array
		// Home
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons
				.getResourceId(0, -1)));
		//  youtube
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons
				.getResourceId(1, -1)));
		//calls
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons
				.getResourceId(2, -1)));
		
		//most
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3],
		 navMenuIcons.getResourceId(3, -1)));
		// // least
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4],
		navMenuIcons.getResourceId(4, -1)));
		// Communities, Will add a counter here
		// navDrawerItems.add(new NavDrawerItem(navMenuTitles[3],
		// navMenuIcons.getResourceId(3, -1), true, "22"));
		// // Pages
		// navDrawerItems.add(new NavDrawerItem(navMenuTitles[4],
		// navMenuIcons.getResourceId(4, -1)));
		// // What's hot, We will add a counter here
		// navDrawerItems.add(new NavDrawerItem(navMenuTitles[5],
		// navMenuIcons.getResourceId(5, -1), true, "50+"));
		//
		//
		// Recycle the typed array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapter);

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, // nav menu toggle icon
				R.string.list_name, // nav drawer open - description for
									// accessibility
				R.string.list_name // nav drawer close - description for
									// accessibility
		){
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(R.string.list_name);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			if(getSavedData("first_time"))
			{
					displayView(0);
			}
			else
			{
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, new EmptyHomeFragment()).commit();
			}
		}

	}

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			if(getSavedData("first_time"))
			{
					displayView(position);
			}
			else
			{

				startActivity(new Intent(getApplicationContext(),
						RegisterActivity.class));
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/***
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	private void displayView(int position) {
		 frag=position;
		// update the main content by replacing fragments
		Fragment fragment = null;
		switch (position) {
		case 0:
			fragment = new HomeFragment();
			break;
		case 1:
			fragment = new YoutubeFragment();
			break;
		case 2:
			fragment = new CallsFragment();
			break;
		case 3:
			fragment = new MostFragment();
			break;
		case 4:
			fragment = new LeastFragment();
			break;
		// case 5:
		// fragment = new WhatsHotFragment();
		// break;
		//
		default:
			break;
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(navMenuTitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	public void openSettingsWindow(MenuItem item) {
		System.out.println("started el main activity from settings");
		// startActivity(new Intent(this,
		// com.example.servicetest.MainActivity.class));
		if(getSavedData("first_time"))
		{
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.frame_container, new SettingsFragment()).commit();
		}
		else
		{

			startActivity(new Intent(getApplicationContext(),
					RegisterActivity.class));
		}

	}
	public void RefreshFragment(MenuItem item) {
		System.out.println("refresh the fragment number:"+frag);
		displayView(frag);
	}
	public void openAboutUsWindow(MenuItem item) {
		new AlertDialog.Builder(this)
	    .setTitle("About")
	    .setMessage("MUSAR is a Graduation Project for Cairo University Faculty of engineering Team.\n" +
	    		"MUSAR is a Multi User Secretary and Recommender. It works as a recommender for new apps from your contacts that are similar to your taste.\n And recommend new music similar to your taste.\n" +
	    		"It also works as a personal secretary that tells you your most/least used apps and your missed calls.\n " +
	    		"It also reminds you to call or message a number when you change your location.")
	    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	        	
	        }
	     })
	    .setIcon(R.drawable.about)
	    .show();
	}
	public void openContactUsWindow(MenuItem item){
		new AlertDialog.Builder(this)
	    .setTitle("Contact Us")
	    .setMessage(
	    		"Ahmed Talaat: +2(010)07208646\n" +
	    		"Eman Mostafa: +2(011)24085889\n" +
	    		"Haron Shihab: +2(012)20693863\n" +
	    		"Hesham Hamdy: +2(012)22344324")
	    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	        	
	        }
	     })
	    .setIcon(R.drawable.contact)
	    .show();
	}

	public void saveData(String key, boolean value) {
		SharedPreferences settings = getSharedPreferences("save", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public boolean getSavedData(String key) {
		SharedPreferences settings = getSharedPreferences("save", 0);
		return settings.getBoolean(key, false);
	}

}