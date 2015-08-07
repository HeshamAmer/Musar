package com.musar.services;

import java.util.Timer;
import java.util.TimerTask;

import com.musar.Database.DatabaseHandler;
import com.musar.system.GetRunningApps;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

public class TrackerService extends Service {

	@Override
	public void onCreate() {

	}

	Boolean TrackerServiceAlive;
	Boolean TrackerServiceRevive;
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		 saveData("TrackerServiceAlive", true);
		System.out.println("Started TrackerService");
		TrackerServiceAlive = getSavedData1("TrackerServiceAlive");
		TrackerServiceRevive = getPrefSaveData("TrackerServiceRevive");
		reloadApps(5000);
		return Service.START_STICKY;
	}

	private void track() {
		DatabaseHandler db = new DatabaseHandler(this);
		final GetRunningApps AppHandler = new GetRunningApps(db,
				this.getPackageManager());
		AppHandler.get_runningapps(getApplicationContext());
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		saveData("TrackerServiceAlive", false);
		super.onDestroy();
	}

	private void reloadApps(int TimeInterval) {
		TimerTask tasknew = new TimerTask() {
			@Override
			public void run() {
				if (TrackerServiceRevive) {// IF this line shows trouble , Remove it to line 81
					track();
		//			System.out.println("TimeInterval in TrackerServiceAlive have passed");
					TrackerServiceRevive = getPrefSaveData("TrackerServiceRevive");
				}

			}

		};
		Timer timer = new Timer();
		// int timeInMS = 1800000;// 30 minutes in milli-seconds.
		int initialDelay = 1000;
		timer.scheduleAtFixedRate(tasknew, initialDelay, TimeInterval);

	}
	private boolean getPrefSaveData(String key)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		// then you use
		return prefs.getBoolean(key, true);	
	}

	public boolean getSavedData1(String key) {
		SharedPreferences settings = getApplicationContext()
				.getSharedPreferences("save", 0);
		return settings.getBoolean(key, false);
	}

	public void saveData(String key, boolean value) {
		SharedPreferences settings = getApplicationContext()
				.getSharedPreferences("save", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

}