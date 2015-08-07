package com.musar.youtubedownloader;

import com.musar.Database.store_data;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
/**
 * 
 * music track the user modeling
 *
 */
public class music_track extends Service {
	 store_data storing=new store_data();
	public static final String SERVICECMD = "com.android.music.musicservicecommand";
	@Override
	public IBinder onBind(Intent arg0) {
		
		return null;
	}
	 @Override
	    public int onStartCommand(Intent intent, int flags, int startId) {
		 storing.saveData("uTubeServiceAlive",true,this);
		
		    IntentFilter iF = new IntentFilter();
	        iF.addAction("com.android.music.metachanged");
	        //iF.addAction("com.android.music.playstatechanged");
	        iF.addAction("com.android.music.playbackcomplete");
	        iF.addAction("com.android.music.queuechanged");
	       
	        registerReceiver(mReceiver, iF);
		 return Service.START_STICKY;
	    }
	 @Override
	  public void onDestroy() 
	 {
		 storing.saveData("uTubeServiceAlive",false,this);
		 unregisterReceiver(mReceiver);
	 }
	 public BroadcastReceiver mReceiver=new  BroadcastReceiver(){

		    @Override
		    public void onReceive(Context context, Intent intent) {
		        String action = intent.getAction();
		        String cmd = intent.getStringExtra("command");
		        String artist = intent.getStringExtra("artist");
		        String album = intent.getStringExtra("album");
		        String track = intent.getStringExtra("track");
		        Log.v("System.out", artist + ":" + album + ":" + track);
		        Toast.makeText(context, track, Toast.LENGTH_SHORT).show();
		        db_videos db= new db_videos(music_track.this);
		        music_info music=db.getArtistByName(artist);
		        if(music==null)
		        {
		        	music=new music_info();
		        	music.setartist(artist);
		        	music.setPlayCount(1);
		        }
		        else
		        {
		        	music.setPlayCount(music.getPlayCount()+1);
		        }
		        System.out.println(music.getPlayCount());
		        db.addOrUpdateaudio(music);
		    }
		};

}
