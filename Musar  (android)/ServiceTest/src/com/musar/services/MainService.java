package com.musar.services;

import java.util.Timer;
import java.util.TimerTask;


import com.musar.Database.store_data;
import com.musar.system.ServerCall;
import com.musar.youtubedownloader.music_track;
import com.musar.youtubedownloader.youtube_service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class MainService extends Service {

	/**
	 * @param args
	 */


	Boolean Revive = true;
	Boolean TrackerServiceAlive = false;
	Boolean LocationServiceAlive = false;
	Boolean LogsServiceAlive = false;
	Boolean ServerServiceAlive=false;
	Boolean uTubeServiceAlive=false;
	Boolean TrackerServiceRevive=false;
	Boolean LocationServiceRevive=false;
	Boolean LogsServiceRevive = false;
	Boolean ServerServiceRevive = false;
	Boolean uTubeServiceRevive = false;
	store_data storing=new store_data();
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		tickTime(6000);
		return Service.START_STICKY;

	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void ReviveLogs(){
		LogsServiceAlive = storing.getSavedData_bool("LogsServiceAlive",this);
		LogsServiceRevive = storing.getPrefSaveData("LogsServiceRevive",this);
		if (!LogsServiceAlive && LogsServiceRevive) {
			startService(new Intent(getApplicationContext(),
					LogsService.class));
		}
		if (LogsServiceAlive && !LogsServiceRevive) {
			storing.saveData("LogsServiceAlive",false,this);
			stopService(new Intent(getApplicationContext(),
					LogsService.class));
		}
	}
	private void reviveServices() {
		
	  //  System.out.println("Tracker service alive? : "+TrackerServiceAlive + " , Revive: " +TrackerServiceRevive);
			TrackerServiceRevive = storing.getPrefSaveData("TrackerServiceRevive",this);
			TrackerServiceAlive = storing.getSavedData_bool("TrackerServiceAlive",this);
			LocationServiceAlive = storing.getSavedData_bool("LocationServiceAlive",this);
			LocationServiceRevive = storing.getPrefSaveData("LocationServiceRevive",this);
			uTubeServiceRevive = storing.getSavedData_bool("uTubeServiceRevive",this);
			uTubeServiceAlive = storing.getSavedData_bool("uTubeServiceAlive",this);
			ServerServiceRevive = storing.getSavedData_bool("ServerServiceRevive",this);
			ServerServiceAlive = storing.getSavedData_bool("ServerServiceAlive",this);
		if (!TrackerServiceAlive && TrackerServiceRevive) {
			startService(new Intent(getApplicationContext(),
					TrackerService.class));
		}
		if(TrackerServiceAlive && !TrackerServiceRevive)
		{
			storing.saveData("TrackerServiceAlive",false,this);
			stopService(new Intent(getApplicationContext(),TrackerService.class));
		}
		//System.out.println("Location service alive? : "+LocationServiceAlive + " Revive: "+LocationServiceRevive);
		if (!LocationServiceAlive&&LocationServiceRevive) {
			startService(new Intent(getApplicationContext(),LocationService.class));
		}
		if(LocationServiceAlive && !LocationServiceRevive)
		{
			storing.saveData("LocationServiceAlive",false,this);
			stopService(new Intent(getApplicationContext(),LocationService.class));
		}
		
	    //Eman's work :D
		if (!uTubeServiceAlive&&uTubeServiceRevive) {
		  System.out.println("youtube is working");
		   //music
		   startService(new Intent(getApplicationContext(),music_track.class));
		}

		if(uTubeServiceAlive && !uTubeServiceRevive)
		{
			
			stopService(new Intent(getApplicationContext(),music_track.class));
		}
		if(uTubeServiceAlive && !uTubeServiceRevive)
		{
			stopService(new Intent(getApplicationContext(),youtube_service.class));
			stopService(new Intent(getApplicationContext(),music_track.class));
		}
	    ////////////////////////////Server/////////////////////////////////////////	
		//System.out.println("server service alive? : "+ServerServiceAlive + " Revive: "+ServerServiceRevive);
		//I don't need the tracking is working because we can recommend even if tracking has been stopped
		if (!ServerServiceAlive&&ServerServiceRevive ) {
			System.out.println("Server is working");
			startService(new Intent(getApplicationContext(),ServerCall.class));
		}
		if (ServerServiceAlive&&!ServerServiceRevive ) {
			
			stopService(new Intent(getApplicationContext(),ServerCall.class));
		}
		
	}

	private void tickTime(int TimeInterval) {
		TimerTask tasknew = new TimerTask() {
			@Override
			public void run() {
				//System.out.println("entered main service");
				reviveServices();
			}

		};
		TimerTask callsTask = new TimerTask(){

			@Override
			public void run() {
				
				ReviveLogs();
			}
		};
		Timer timer = new Timer();
		int initialDelay = 1000;
		timer.scheduleAtFixedRate(callsTask,initialDelay , TimeInterval);
		
		timer.scheduleAtFixedRate(tasknew, initialDelay, TimeInterval);
	}
	
	@Override
	public void onDestroy() {
	
		startService(new Intent(this, MainService.class));
		super.onDestroy();
	}
}
