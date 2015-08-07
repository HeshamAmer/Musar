package com.musar.youtubedownloader;



import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;


import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.api.services.youtube.YouTubeScopes;
import com.musar.Database.store_data;




import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.Toast;
import android.content.Context;
public class youtube_service extends Service{

	//private int counter_videos;
	protected GetSuggestedVideos get_recommended;
	private SharedPreferences prefs_1 ;
	private String token;
	private String mail;
	Intent toService = new Intent();
	store_data storing=new store_data();
	Handler h = new Handler();
	//private int variable_counter=2;
	public GetSuggestedVideos get_GetSuggestvideossTask(){return get_recommended;}
	///////////////////get the responding from the preload class to save it in db and restart again///////
	public void response(video_info link,String token,String mail){
		System.out.println("response:"+storing.getSavedData_int("artist_count", this));
		SharedPreferences.Editor editor = prefs_1.edit();
		this.token=token;
		storing.saveData("token_key",token,this);
		this.mail=mail;
		storing.saveData("mail_key",mail,this);
        storing.saveData("uTubeServiceAlive",true,this);
		db_videos db= new db_videos(this);
		
	    //the size is 1 all the time and the reload will be after every video ... then there will be another reload after all videos(4)
		{
			if(link.getVideoLink()!=null)
			{
			String link_string=link.getVideoLink();
			System.out.println(link_string);
			video_info v=new video_info();
			v.setVideoLink(link.getVideoLink());
			v.setVideoTitle(link.getVideoTitle());
			v.setVideoThumbnail(link.getVideoThumbnail());
			v.setStatus(link.getStatus());
			v.setSize(link.getSize());
			db.addvideo(v);//adding the videos in db
		}
		///wrong with downloading video
		else {System.out.println("there is something wrong with reading video and preloading the video :( ");}
		}
		//////////////////////////////////normal reload///////////////////////////////////////////////////////
		if(storing.getSavedData_float("total_size", this)>0)
		{
			reloadvideo(60000*20);//reload after 1 video lazem yeb2a fe timer keda hamsa7 kolo wara ba3d 3ala el fady
		}
	}
  	/////get the message from get suggested videos then stop the service because the error was wrong account and the user must select new account from the activity
	public void set_message(String message)
	{
		Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
	    stopSelf();
		
	}
	///error with the json we need to reload again
	///it is different from response function because response from preload not get suggested videos
	public void reload_JsonError()
	{
		Thread myThread = new Thread(new Runnable(){
			@Override
			public void run() {
				reloadvideo(60000*20);//reload again after 1 day 
			}

		});
		myThread.start();
	}
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println("begining:D");
		prefs_1=PreferenceManager.getDefaultSharedPreferences(this);
		//check if the service is uTubeServiceAliveed from the system --->the intent is null so we need to start it manually again
		//it is a problem if you need the intent object to get variables from activity to use them here
		if(intent!=null)	
		{
		Bundle b = new Bundle();
		b=intent.getExtras();
		token= b.getString("key");
		storing.saveData("token_key",token,this);// token store
		mail= b.getString("account_key");
        storing.saveData("mail_key",mail,this);// mail store
        storing.saveData("uTubeServiceAlive",true,this);
		System.out.println(mail);
		System.out.println(token);
        System.out.println("calling the Asynch task");
        //calling get suggested videos after check if the wifi and battery percent are good
        broadcast_conditions= new WifiReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(broadcast_conditions, intentFilter);  
		}
		
		else {
			System.out.println("handling the intent is null");
			reloadvideo(60000*20);//because the service sometimes breaks suddenly I don't know why and we need to reload it
		     }
			return Service.START_STICKY;
	}
	public void reloadvideo(int TimeInterval) {
		Runnable r = new Runnable()  {
			@Override
			public void run() {
				if(storing.getSavedData_bool("uTubeServiceAlive",youtube_service.this)==true){
				//start service with the mail,token
				System.out.println("reloading");
				toService.setClass(youtube_service.this, youtube_service.class);
				Bundle b = new Bundle();
				token=storing.getSavedData("token_key",youtube_service.this);
				mail=storing.getSavedData("mail_key",youtube_service.this);
				b.putString("account_key",mail);
		        b.putString("key", token);  
		        toService.putExtras(b);
				startService(toService);
				}
			}

		};
		
		h.postDelayed(r,TimeInterval ); // <-- the "1000" is the delay time in miliseconds. 

	}
	private WifiReceiver broadcast_conditions;
	
    public class WifiReceiver extends BroadcastReceiver{
       @Override
       public void onReceive(Context context, Intent intent)
           {
           if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {

        	    NetworkInfo nwInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        	    if(NetworkInfo.State.CONNECTED.equals(nwInfo.getState())){//This implies the WiFi connection is through
        	        System.out.println("Wifi is on :)");
                    get_recommended=(GetSuggestedVideos)new GetSuggestedVideos(youtube_service.this,token,mail);
             		get_recommended.execute();
        	    }
        	    else 
        	    { 
        	    	//set wifi off
        	    	System.out.println("Wifi is off :(");
        	    }
        	}
           }
       
       
       }
  
	 //////////////////get the speed of the wifi////////////////////////////////////////////////////////////////////
	 public int speed_wifi()
	 {
		 WifiManager mainWifi;
        mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = mainWifi.getConnectionInfo();
        int speed=0;
        if(wifiInfo.getBSSID()!=null){
        speed=wifiInfo.getLinkSpeed();
        }
        return speed;
	 }
	 ///////////////////get the battery charge percentage /////////////////////////////////////////////
	 public int battery_percent()
	 {
		  Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
	      int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
          int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
          int percent = (level*100)/scale;
          return percent;
	 }
	 ////////////////////////////uTubeServiceAlive all synch tasks and the service//////////////////////////
	 @Override
	  public void onDestroy() {
		    System.out.println("destroooooooooooy");
		    storing.saveData("uTubeServiceAlive",false,this);
	        boolean uTubeServiceAlive_reload=storing.getSavedData_bool("uTubeServiceAlive",this);
			System.out.println(uTubeServiceAlive_reload);
		    if(get_recommended!=null)//cancel preload and get suggested videos 
		     {
			    get_recommended.get_preload().get_asynctask().cancel(true);
		        get_recommended.cancel(true);
		  
		     }
		 unregisterReceiver(broadcast_conditions);
		
	  }
	
}
