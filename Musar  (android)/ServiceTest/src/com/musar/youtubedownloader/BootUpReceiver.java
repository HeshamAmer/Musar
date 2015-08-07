package com.musar.youtubedownloader;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import com.musar.Database.Contact;
import com.musar.Database.refresh_data;
import com.musar.Database.store_data;
import com.musar.services.ContactsManager;
import com.musar.services.LocationService;
import com.musar.services.TrackerService;
import com.musar.system.ServerCall;
import com.musar.system.SimpleHttpClient;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class BootUpReceiver extends BroadcastReceiver {
	Boolean TrackerServiceAlive = false;
	Boolean LocationServiceAlive = false;
	Boolean LogsServiceAlive = false;
	Boolean uTubeServiceRevive = false;
	Boolean uTubeServiceAlive= false;
	Boolean serverServiceRevive = false;
	Boolean serverServiceAlive= false;
	Boolean TrackerServiceRevive=false;
	Boolean LocationServiceRevive=false;
	 @Override
	 public void onReceive(final Context context, final Intent bootintent) {
	  //start service again after the power is up
	  Toast.makeText(context,"Musar is back after restart your mobile",Toast.LENGTH_LONG).show();
	  SharedPreferences prefs ;
	  store_data storing=new store_data();
	  prefs=PreferenceManager.getDefaultSharedPreferences(context);
  	  String token = storing.getSavedData("token_key",context); //default is null
  	  String account=storing.getSavedData("mail_key",context);//default is null
  	  //start other service
  	  TrackerServiceRevive = storing.getPrefSaveData("TrackerServiceRevive",context);
  	  TrackerServiceAlive = storing.getSavedData_bool("TrackerServiceAlive",context);
  	  LocationServiceAlive = storing.getSavedData_bool("LocationServiceAlive",context);
  	  LocationServiceRevive = storing.getPrefSaveData("LocationServiceRevive",context);
  	  uTubeServiceRevive = storing.getSavedData_bool("uTubeServiceRevive",context);
  	  uTubeServiceAlive = storing.getSavedData_bool("uTubeServiceAlive",context);
	  serverServiceAlive = storing.getSavedData_bool("ServerServiceRevive",context);
	  serverServiceRevive = storing.getSavedData_bool("ServerServiceAlive",context);
	 
	  //start youtube service
  	  if(uTubeServiceRevive)
  	  {
	  	  Bundle b = new Bundle(); 
	  	  Intent mServiceIntent = new Intent();
	  	  mServiceIntent.setClass(context, youtube_service.class);
	  	  b.putString("key",token);
	  	  System.out.println(token);
	  	  Toast.makeText(context,token,Toast.LENGTH_LONG).show();
	      b.putString("account_key", account);
	      System.out.println(account);
	      mServiceIntent.putExtras(b);
	      //context.startService(mServiceIntent);
	      //music
	      Intent tracker_service = new Intent();
		  tracker_service.setClass(context, music_track.class);
		  context.startService(tracker_service);
	  }
  	
	  if ( TrackerServiceRevive) {
		  Intent tracker_service = new Intent();
		  tracker_service.setClass(context, TrackerService.class);
		  context.startService(tracker_service);
		}
		System.out.println("Location service alive? : "+LocationServiceAlive + " Revive: "+LocationServiceRevive);
		if (LocationServiceRevive) {
			 Intent location_service = new Intent();
			 location_service.setClass(context, TrackerService.class);
			 context.startService(location_service);
		}
        Toast.makeText(context,"server service alive? : "+serverServiceAlive + " Revive: "+serverServiceRevive,Toast.LENGTH_LONG).show();
		if (serverServiceRevive) {
			Toast.makeText(context,"Sever",Toast.LENGTH_LONG).show();
			 Intent server_service = new Intent();
			 server_service.setClass(context, ServerCall.class);
			 context.startService(server_service);
		}
	    
	 }
	

	}