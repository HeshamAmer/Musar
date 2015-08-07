/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.musar.youtubedownloader;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.musar.Database.store_data;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public  class GetSuggestedVideos extends AsyncTask<Void, Void,ArrayList<video_info>>{
    private youtube_service mActivity;
    private String token = null;
    private String mail=null;
    boolean flag=false;
    private SharedPreferences prefs_reference ;
    private preload p;
    private String status=null;
    store_data storing=new store_data();
    public preload get_preload(){return p;}
    public ArrayList<video_info>titles=new ArrayList<video_info>();

    GetSuggestedVideos(youtube_service activity,String token,String mail) {
        this.mActivity = activity;
        this.token=token;
        this.mail=mail;
        p=new preload();
        
    }
    /////////////////////////////refresh the token///////////////////////////////////////////////////
    @SuppressWarnings("deprecation")
	private String  refresh_auth()
    {
    prefs_reference=PreferenceManager.getDefaultSharedPreferences(mActivity);
    final SharedPreferences.Editor editor = prefs_reference.edit();
	final String token_validate_1=prefs_reference.getString("token_refresh", token);
	AccountManager.get(mActivity).invalidateAuthToken("com.google",token_validate_1 );
	Account []account=AccountManager.get(mActivity).getAccounts();
    Account real_account = null;
   //check if there is any google account login is found
    for(int i=0;i<account.length;i++){
    	if(account[i].name.equalsIgnoreCase(this.mail))
    		{
    			real_account=account[i];
    			flag=true;
    			break;
    		}
    	}
    	if(flag==true){
    		String token_validate=prefs_reference.getString("token_refresh", token);
            AccountManager.get(mActivity).getAuthToken(real_account, "oauth2:https://gdata.youtube.com", false, new AccountManagerCallback<Bundle>() {
            @Override
	            public void run(AccountManagerFuture<Bundle> future) {
	                try {
	                	 Bundle bundle = future.getResult();
	                    String auth_token = bundle
	                            .getString(AccountManager.KEY_AUTHTOKEN);
	                    editor.putString("token_refresh", auth_token);
	                    editor.commit();
	                    System.out.println("refresh token :"+auth_token);
	                	String account_name=bundle.getString(AccountManager.KEY_ACCOUNT_NAME);
	                } catch (Exception e) {
	                   
	                	System.out.println("exception refresh the wifi connection");
	                }
	            }
	        }, null);
        ;   
    	token_validate=prefs_reference.getString("token_refresh", token);
    	return token_validate;
    	
    	}
    	else return null;
    }
    @Override
    protected ArrayList<video_info> doInBackground(Void... params) {
    	
        String link="";
 	      try {
 	    	//get the list of videos 
 	    	int percent=mActivity.battery_percent();
 	    	if(percent>20) 
 	    	{//check the battery is low or high and reload again if it is low
 	    	
 	    	titles=fetchNameFromProfileServer();
 	        if(titles!=null)
 	        {
 	          if(titles.size()==0){System.out.println("it must reload because of json error");}
 	          //pass the videos to preload
 	         // for(int i=0;i<titles.size();i++)
 	          //count int will work:D -1 3ashan ana f eel awel bazawed wa7ed :D
 	          int count=storing.getSavedData_int("artist_count", mActivity);
 	          if(count>0&&titles.size()>0)
 	           {
 	        	  System.out.println("we will download :"+titles.get(count-1).getVideoLink()+"count:"+count);
 	              link="http://www.youtube.com/watch?v="+titles.get(count-1).getVideoLink();
 	              if(!isCancelled())
 	              {
 	                p.getYouTubeVideos(mActivity,link,titles.get(count-1).getVideoTitle(),titles.get(count-1).getVideoThumbnail(),token,mail,status);
 	              }
 	           }
 	        }
 	        
 	    	}
 	        } catch (IOException ex) {
 	        	System.out.println("there is error with the connection I think we will reload again");
 	        	
 	        } 
      
      return titles;
    }
    @Override
    protected void onPostExecute(ArrayList<video_info> streamingUrl) {
        super.onPostExecute(streamingUrl);
        //check if the error with connection wifi or token key
        if(streamingUrl==null&&storing.getSavedData_bool("finish_music", mActivity)==true)
        {mActivity.reloadvideo(60000*20);}
        
        else if(streamingUrl==null&&storing.getSavedData_bool("finish", mActivity)==false)
        {
        	String message="there is error with your google account and youtube account please check your accounts";
            mActivity.set_message(message);
        }
        //check if the error json error or battery is low-->we need to reload again
        //the reload with json error or battery percent <20
        else if(streamingUrl!=null&&streamingUrl.size()==0){mActivity.reload_JsonError();}
    }
   
 //////////////////get the videos from the token key/////////////////////////////////////////////////////////
    private ArrayList<video_info> fetchNameFromProfileServer() throws IOException {
    	
    	token=refresh_auth();
        if (token == null) {
          System.out.println("error in account again! ");
          return null;
        }
        System.out.println("tokeeeeeeeeeeen:"+token);
        
        float music_size=storing.getSavedData_float("temp_music_size",mActivity );
        float youtube_size=storing.getSavedData_float("temp_youtube_size",mActivity );
        System.out.println("music size:"+music_size);
        URL url=null;
        int artist_number=storing.getSavedData_int("artist_number", mActivity);
        db_videos db=new db_videos(mActivity);
        List<music_info>artists=db.getALLrecommended();
        System.out.println("artist_number:"+artist_number+"sizeeeeeee:"+artists.size());
        if(music_size>0&&artist_number<artists.size())//music must finish first
        {
        	status="music";
        	storing.saveData("finish", false, mActivity);//3ashan lu 7asal null mn dol sa3etha ye print el mesage elly fe el post
        	//because the count must be max 25 :D and we can't get them each video alone we must get them in the first time :D
        	String artist_name=artists.get(artist_number).getartist().replace(" ", "");
    		int count_int=storing.getSavedData_int("artist_count", mActivity);
    		if(artists.get(artist_number).get_size()>0&&count_int<3)//it is threshold 
        	 {
        		url=new URL("http://gdata.youtube.com/feeds/api/videos?q="+artist_name+"&v=2&alt=json");
        		storing.saveData("artist_id", artists.get(artist_number).getaudio_id(), mActivity);
        		storing.saveData("artist_count",count_int+1, mActivity);
           	 }
        	else if(artist_number<artists.size()-1) //lazem asafar 3ashan ana ray7a le artist tany 
        	{
        		storing.saveData("artist_count", 1, mActivity);
        		artist_number+=1;
        		storing.saveData("artist_number", artist_number, mActivity);
        		artist_name=artists.get(artist_number).getartist().replace(" ", "");
        		url=new URL("http://gdata.youtube.com/feeds/api/videos?q="+artist_name+"&v=2&alt=json");
        		storing.saveData("artist_id", artists.get(artist_number).getaudio_id(), mActivity);
      	     }
        	else {storing.saveData("artist_number", artist_number+1, mActivity);
        	System.out.println("5alasnaaa artistsssssssssssssssss");
        	storing.saveData("finish_music", true, mActivity);return null;}
        }
        else if (youtube_size>0){//now YouTube recommendations  is working
        	storing.saveData("finish", false, mActivity);//3ashan lu 7asal null mn dol sa3etha ye print el mesage elly fe el post
        	url = new URL("https://gdata.youtube.com/feeds/api/users/default/recommendations?v=2&access_token=" 
        		+ token  + "&key=AI39si7e5qcDPgdfLp79qcrKgnD8qhbRpsAZHQiSviw_GX-oSpG6-oj6wL3AMnNDIpv3YQ4Iu0mgasI4zVTILtJi98tjevYKMg" +"&max-results=1"+"&alt=json");
        status="youtube";
        storing.saveData("artist_count", 1, mActivity);
        }
        else {System.out.println("5alasnaaaaaaaa youtube we music kamaaaaaaan");storing.saveData("finish", true, mActivity);return null;}//da lu kolo 5alas lazem ye return 3ashan 2a2olo eny 5alast we mat7amelsh tany 
        
        HttpURLConnection con = (HttpURLConnection) url.openConnection();       
        int sc = con.getResponseCode();
        //it is correct token
        
        if (sc == 200) {
        	
          InputStream is = con.getInputStream();
          String response = readResponse(is);
          if(response!=null){
          ArrayList<video_info> titles = getrecommending(response);
          is.close();
          return titles;
          }
          else 
        	  {//reload again
        	    ArrayList<video_info> titles=new ArrayList<video_info>();
        	    return titles;
        	  }
        } else if (sc == 401) {//error with the token --?it not similar
        	System.out.println("401");
            return null;
        } else if(sc==400){
        	System.out.println("el artist 3'alat aw el link 3'alat");
        	 ArrayList<video_info> titles=new ArrayList<video_info>();
     	    return titles;
        }
        else {return null;}
    }
    //read the response from the input stream
    private static String readResponse(InputStream is) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] data = new byte[2048];
        int len = 0;
        try {
			while ((len = is.read(data, 0, data.length)) >= 0) {
			    bos.write(data, 0, len);
			}
		} catch (IOException e) {
			
			e.printStackTrace();
			return null;
		}
        try {
			return new String(bos.toByteArray(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
			return null;
		}
    }
   //get the videos from the recommended videos
    private ArrayList<video_info> getrecommending(String response) {
		
    	ArrayList<video_info>titles=new ArrayList<video_info>();
    	try {
			JSONObject object = new JSONObject(response);
			JSONObject feedObject = object.getJSONObject("feed");
			
			JSONArray channelArray = feedObject.getJSONArray("entry");
			
			for (int i=0; i< channelArray.length(); i++){
				JSONObject media_group=channelArray.getJSONObject(i).optJSONObject("media$group");
				if(media_group!=null){
				String video_link="";
				String title="";
				String thumbnails="";
				//check the xml file of the recommended videos to get the correct title and so on 
				//if there is something wrong we need to get another video
				if(media_group.getJSONArray("media$content")!=null&&media_group.getJSONArray("media$content").getJSONObject(0)!=null)
			         video_link=media_group.getJSONArray("media$content").getJSONObject(0).getString("url");
				else {System.out.println("media content is null");break;}
				if(media_group.getJSONObject("media$title")!=null)
				     title=media_group.getJSONObject("media$title").getString("$t");
				else {System.out.println("media title is null");break;}
				if(media_group.getJSONArray("media$thumbnail")!=null&&media_group.getJSONArray("media$thumbnail").getJSONObject(0)!=null)
				   thumbnails= media_group.getJSONArray("media$thumbnail").getJSONObject(0).getString("url");
				else {System.out.println("thumbnails is null");break;}
				int index_1=video_link.indexOf("/v/");
				int index_2=video_link.indexOf("?");
				video_info video=new video_info();
				if(index_1!=-1&index_2!=-1&(index_2-(index_1+3))>=0)
				  video.setVideoLink(video_link.substring(index_1+3, index_2));
				else {System.out.println("the id of the video is not found!");break;}
				video.setVideoTitle(title);
				video.setVideoThumbnail(thumbnails);
				//insert the video in the titles array
				titles.add(video);
				}
				else {System.out.println("group media is null");}
			}
			
		} catch (JSONException e) {
			
			System.out.println("error in JSONException");
			
		}
    	
    	
		return titles;
	}
}
