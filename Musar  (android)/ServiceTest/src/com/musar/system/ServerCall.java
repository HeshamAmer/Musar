package com.musar.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.musar.Database.*;
import com.musar.services.ContactsManager;
import com.musar.youtubedownloader.db_videos;
import com.musar.youtubedownloader.music_info;
/**
 * 
 * service for call the server in time intervals
 *
 */
public class ServerCall extends Service {
	private Handler handler;
	private PackageManager packageManager = null;
	private List<String> applist = null;
	private boolean installedAppsWorking = false;
	store_data storing = new store_data();

	@Override
	public void onCreate() {

	}

	@Override
	public void onDestroy() {
		storing.saveData("ServerServiceAlive", false, this);
		System.out.println("destroooooooooooooooooooooooooooooooooy server");
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		storing.saveData("ServerServiceAlive", true, this);
		System.out.println("da5al el serveer");
		// ///////// send data periodically after establish data when starting account
	
		handler = new Handler();
		tickTime(24 * 60 * 60 * 1000, "refresh");
		tickTime(1 * 60 * 60 * 1000, "apps");
	    tickTime(24*60*60*1000,"music");//kol youm

		return Service.START_STICKY;

	}

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	// //////////////////////////////////////////////////////////get the data from db//////////////////////////////
	public List<String> getApplist() {
		while (!installedAppsWorking || applist == null);

		return applist;
	}
	//get the top used activities
	public List<AppActivity> getTopActivity(Context context) {
		DatabaseHandler db = new DatabaseHandler(context);
		List<AppActivity> topActivities = new ArrayList<AppActivity>();
		System.out.println("da5al sorting");
		// topActivities=db.getAllActivity();
		topActivities = db.getAllActivityTodaySortedDecDuration();
		if (topActivities == null) {
			topActivities = new ArrayList<AppActivity>();
		}
		return topActivities;

	}
//get the installed apps
	private List<String> checkForLaunchIntent(List<ApplicationInfo> list) {
		ArrayList<String> applist = new ArrayList<String>();
		installedAppsWorking = true;
		for (ApplicationInfo info : list) {
			try {
				if (null != packageManager
						.getLaunchIntentForPackage(info.packageName)) {
					applist.add(info.packageName);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		installedAppsWorking = false;
		return applist;
	}

	private List<String> get_apps() {
		// get recommended list to be sent to server and there we will deal with this
		DatabaseHandler db = new DatabaseHandler(ServerCall.this);
		List<app_recommended> app_recommended = db.getAllApps_recommended();
		List<String> apps_name = new ArrayList<String>();
		for (int i = 0; i < app_recommended.size(); i++) {
			apps_name.add(app_recommended.get(i).get_name());
		}
		return apps_name;
	}

	// /////////////////////////////////////////////////// server request////////////////////////////////////////
	public void getApplicationRecommendations() {
		try {
			System.out.println("apsssssssssssssssssssss");
			List<AppActivity> app_activity = new ArrayList<AppActivity>();
			app_activity = getTopActivity(ServerCall.this
					.getApplicationContext());
			String user_id = storing.getSavedData("id", this);

			List<String> apps_name_recommended = get_apps();
			most_apps apps = new most_apps(user_id, app_activity,apps_name_recommended);
			String data = jsonString(apps);
			thread new_thread = new thread(data,"http://jbossews-musar.rhcloud.com/WorkingMusar11_7/rest/apps",ServerCall.this, handler);
			(new Thread(new_thread)).start();

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	public void refresh_data() throws Exception {
		System.out.println("refreshhhhhhhhhhhhhhh");
		// /////////////////normalize the least apps used/////////////////////////////////
		DatabaseHandler db = new DatabaseHandler(ServerCall.this.getApplicationContext());
		List<Application> topApps = db.getAllApps();
		// first calculate mean then standard deviation

		normalize(topApps);
		// ///////////////////////server call/////////////////////////////////////////////
		packageManager = ServerCall.this.getPackageManager();
		applist = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
		List<String> installed = applist;
		ContactsManager contact = new ContactsManager(ServerCall.this);
		ArrayList<Contact> contacts = contact.GetContacts(ServerCall.this);
		ArrayList<Integer> images_friend = contact.images;
		String user_id = storing.getSavedData("id", this);
		// ////////////////////////////////////////////////////////////////
		refresh_data data = new refresh_data(user_id, installed, contacts,images_friend);
		String refresh = jsonString(data);
		thread new_thread = new thread(refresh,"http://jbossews-musar.rhcloud.com/WorkingMusar11_7/rest/refresh",ServerCall.this, handler);
		(new Thread(new_thread)).start();
	}

	public void getMusicRecommendations() throws JSONException {
		db_videos db = new db_videos(ServerCall.this);
		List<music_info> music = db.getALLaudio();
		String music_string = jsonString(music);
		thread new_thread = new thread(music_string,
				"http://jbossews-musar.rhcloud.com/WorkingMusar11_7/rest/music_recommend",
				ServerCall.this, handler);
		(new Thread(new_thread)).start();

	}

	// ////////////////////////////////////////////////json String/////////////////////////////////////////////////
	@SuppressLint("NewApi")
	public String jsonString(List<music_info> music) throws JSONException {
		JSONObject obj2 = new JSONObject();
		obj2.put("size_artists", music.size());
		for (int i = 0; i < music.size(); i++) {
			obj2.put("artist" + i, music.get(i).getartist());
			obj2.put("playcount" + i, music.get(i).getPlayCount());
		}
		return obj2.toString();
	}

	@SuppressLint("NewApi")
	public String jsonString(refresh_data user) throws JSONException {
		JSONObject obj2 = new JSONObject();
		obj2.put("user", user.get_id());
		obj2.put("size_contact", user.get_contact().size());
		obj2.put("size_installed", user.get_installed().size());
		for (int i = 0; i < user.get_contact().size(); i++) {
			obj2.put("contact_name" + i, user.get_contact().get(i).getName());
			obj2.put("contact_phone" + i, user.get_contact().get(i)
					.getPhoneNumber());
		}
		for (int i = 0; i < user.get_installed().size(); i++) {
			obj2.put("installed_name" + i, user.get_installed().get(i));
		}
		for (int i = 0; i < user.get_images().size(); i++) {
			obj2.put("image_friend" + i, user.get_images().get(i));
		}
		return obj2.toString();
	}

	@SuppressLint("NewApi")
	public String jsonString(most_apps user) throws JSONException {

		JSONObject obj2 = new JSONObject();
		obj2.put("user", user.get_id());
		obj2.put("size_activity", user.get_activity().size());
		for (int i = 0; i < user.get_activity().size(); i++) {
			obj2.put("activity" + i, user.get_activity().get(i).getAppName());
			obj2.put("duration" + i, user.get_activity().get(i).getDuration());
		}
		obj2.put("size_apps", user.get_apps_recommended().size());
		for (int i = 0; i < user.get_apps_recommended().size(); i++) {
			obj2.put("apps_lastRecommended" + i, user.get_apps_recommended()
					.get(i));
		}
		return obj2.toString();
	}

	// ////////////////////////////////////////////////////////////tick the time//////////////////////////////////
	private void tickTime(int TimeInterval, final String passing_fun) {
		TimerTask tasknew = new TimerTask() {
			@Override
			public void run() {
				// System.out.println("serverrrrrrrrrrr alive :"+storing.getSavedData_bool("ServerServiceAlive",
				// ServerCall.this));
				if (storing.getSavedData_bool("ServerServiceAlive",
						ServerCall.this)) {
					if (passing_fun.equalsIgnoreCase("apps")) {
						getApplicationRecommendations();
					} else if (passing_fun.equalsIgnoreCase("refresh")) {
						try {
							refresh_data();
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						try {
							getMusicRecommendations();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
			}

		};
		Timer timer = new Timer();
		// int timeInMS = 1800000;// 30 minutes in milli-seconds.
		int initialDelay = 0;
		if (passing_fun.equalsIgnoreCase("apps"))
			initialDelay = 60000*2;
		else if (passing_fun.equalsIgnoreCase("refresh"))
			initialDelay = 60000;
		else
			initialDelay = 60000*3;
		timer.scheduleAtFixedRate(tasknew, initialDelay, TimeInterval);
	}

	// /////////////////////////////////////////////////normalize fun/////////////////////////////////////////////////////////////////////
	private void normalize(List<Application> apps) {
		DatabaseHandler db = new DatabaseHandler(
				ServerCall.this.getApplicationContext());
		Map<Double, Double> maping = new HashMap<Double, Double>();
		maping = calculate_mean_standard(apps);
		Double mean = get_key(maping);
		Double standard = maping.get(mean);
		for (int i = 0; i < apps.size(); i++) {
			Double normalize = apps.get(i).getTime();
			if (standard != 0)
				normalize = (apps.get(i).getTime() - mean) / standard;
			apps.get(i).setTime(normalize);
			// update database apps table
			db.updateAppTime(apps.get(i));
		}
	}

	private Double get_key(Map<Double, Double> maping) {
		for (Double key : maping.keySet()) {
			return key;
		}
		return null;
	}

	private Map<Double, Double> calculate_mean_standard(List<Application> apps) {
		double sum = 0;
		for (int i = 0; i < apps.size(); i++) {
			sum += apps.get(i).getTime();
		}
		System.out.println("sum" + sum);
		Double mean = sum / apps.size();
		sum = 0;
		for (int i = 0; i < apps.size(); i++) {
			sum += Math.pow(apps.get(i).getTime() - mean, 2.0);
		}
		Double standard = sum / apps.size();
		standard = Math.sqrt(standard);
		Map<Double, Double> maping = new HashMap<Double, Double>();
		maping.put(mean, standard);
		return maping;
	}

}
