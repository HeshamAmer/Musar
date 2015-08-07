package com.musar.system;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;




import com.musar.Database.AppActivity;
import com.musar.Database.DatabaseHandler;
import com.musar.Database.app_recommended;
import com.musar.Database.recommended_user;
import com.musar.Database.store_data;
import com.musar.youtubedownloader.db_videos;
import com.musar.youtubedownloader.music_info;
import com.musar.youtubedownloader.youtube_service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
/**
 * 
 * this thread for all requests to the server
 * very important note :we need data from the server after its response in get music and get apps
 *
 */
public class thread implements Runnable{
String data=null;
String url=null;
Context context=null;
Handler handler=null;
String response=null;
Boolean uTubeServiceRevive = false;
Boolean uTubeServiceAlive=false;
private float total_size=50331648;//TODO:it 100 migabit we will get it from gui it is the default
store_data storing=new store_data();
public String get_response(){return response;}
public thread(String data,String url,Context context,Handler handler)
{
	this.data=data;
	this.url=url;
	this.context=context;
	this.handler=handler;

}
	@Override
	public void run(){
	StringEntity p=null;
	try {
		p = new StringEntity(data,"UTF-8");
	} catch (UnsupportedEncodingException e) {
		e.printStackTrace();
	}
	try {
		response=SimpleHttpClient.executeHttpPost(url,p,context);
		//get results from apps request and clear db
		if(url.contains("apps"))
		 {
			String res = response.toString();
			String data_recommend = res.replaceAll("\\s+", "");
			get_data(data_recommend);
			//clear data base activity
			//before deleting the db we need to get the ratio between music and youtube
			DatabaseHandler db=new DatabaseHandler(context);
		    AppActivity music=db.getActivity("com.sec.android.app.music");
			AppActivity youtube=db.getActivity("com.google.android.youtube");//youtubeeeeeeee de 3'alat
		    float ratio=1;//hagarab 7aga TODO
			//if the youtube =0 so the ratio will be 0 if music =0 ratio=1 else there is normal ratio
			if(music!=null&&music.getDuration()!=0)
			{
			  System.out.println("music time:"+music.getDuration());
			  if(youtube!=null)
				ratio=youtube.getDuration()/music.getDuration();
			  else ratio=0;
		    }
			System.out.println("appsssssss ratio");
			storing.saveData("ratio",ratio,context);
			db.clearMostApps();
		}
	    if(url.contains("music_recommend"))
		{
			total_size=storing.getSavedData_float("Total_sizeUser", context);
			if(total_size==0){total_size=50331648;}//100 miga bit da mosh sa7 TODOs
			storing.saveData("total_size",total_size,context);
			float ratio=storing.getSavedData_float("ratio", context);
			float youtube_size=ratio*total_size;
			float music_size=total_size-youtube_size;
			System.out.println("youtube ratio:"+youtube_size);
			System.out.println("music ratio:"+music_size);
			storing.saveData("youtube_size",youtube_size,context);
			storing.saveData("music_size",music_size,context);
			storing.saveData("temp_youtube_size", youtube_size, context);
			storing.saveData("temp_music_size", music_size, context);
			////get the data 
			String res = response.toString();
			String data_recommend = res.replaceAll("\\s+", "");
			get_music(data_recommend);
			///clear the music db
			db_videos db=new db_videos(context);
			db.delete_AllHistory();
			storing.saveData("artist_count", 0, context);//it is threshold for each artist videos
			storing.saveData("artist_number", 0, context);
			////call preload functionssssssssss ba2a :D
			uTubeServiceRevive=storing.getSavedData_bool("uTubeServiceRevive", context);
			System.out.println("youtube go!:"+uTubeServiceRevive);
			if (uTubeServiceRevive) {
				System.out.println("youtube is in threading");
				Bundle b = new Bundle();				 
				String token=storing.getSavedData("token_key",context);
				String account=storing.getSavedData("mail_key",context);
				System.out.println(account);
				b.putString("key",token);
				b.putString("account_key", account);
				Intent mServiceIntent = new Intent();
			    mServiceIntent.setClass(context.getApplicationContext(), youtube_service.class);
				mServiceIntent.putExtras(b);
				context.getApplicationContext().startService(mServiceIntent);
			 }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		}
	/*
	 * get the music information after recommendations
	 */
	private void get_music(String music) throws JSONException
	{
		//divide size fairly 
		store_data store=new store_data();
		double music_size=store.getSavedData_float("music_size", context);
		JSONObject object = new JSONObject(music);
		int size_recommended=object.getInt("size_music");
		db_videos db=new db_videos(context);
		db.clear_recommended_all();
		ArrayList<String>artist=new ArrayList<String>();
		ArrayList<Double>rates=new ArrayList<Double>();
		for(int i=0;i<size_recommended;i++)
		{
			String artist_object=object.getString("music_artist"+i);
			artist.add(artist_object);
			artist.set(i, artist.get(i).replaceAll(",", " "));
			rates.add(object.getDouble("artist_rate"+i));
			
		}
		double sum_rates=0;
		for(int i=0;i<size_recommended;i++)
		{
			sum_rates+=rates.get(i);
		}
		for (int i=0;i<size_recommended;i++)
		{
			music_info audio=new music_info();
			
			audio.setartist(artist.get(i));
			audio.set_rate(rates.get(i));
			if(sum_rates>0)
			{
				audio.set_size(music_size*audio.get_rate()/sum_rates);
			}
			audio.set_reject(0);
			db.addrecommend(audio);
		}
		if(size_recommended==0)//reset the default that the YouTube has the full size 
		{
			storing.saveData("youtube_size",total_size,context);
			storing.saveData("music_size",(float)0,context);
			storing.saveData("temp_youtube_size",total_size, context);
			storing.saveData("temp_music_size", (float)0, context);
			
		}
	}
	/*
	 * get the recommended apps
	 */
	private void get_data(String data) throws JSONException, ClientProtocolException, IOException, URISyntaxException
	{
		JSONObject object = new JSONObject(data);
		int size_map_recommended=object.getInt("size_map_recommended");
		DatabaseHandler db=new DatabaseHandler(context);
		for(int i=0;i<size_map_recommended;i++)
		 {
			String name_app=object.getString("recommended_app_name"+i);
			name_app=name_app.replaceAll(",", " ");
			Double rate=object.getDouble("app_recommended_rate"+i);
			String package_name=object.getString("app_recommend_package"+i);
			//http request for app icon
			String urlIcon=http_Icon(package_name);
			//urlIcon="";
			if(urlIcon==null)urlIcon="";
			
			List<app_recommended>apps=db.getAllApps_recommended();
			System.out.println("size apps :D:"+apps.size());
			//users
			if(apps.size()>=20){
				int id=apps.get(0).get_id();//delete the first entry all the time
				String deletedIcon=apps.get(0).getIcon();
				delete_file(deletedIcon);
				db.deleteApp_recommended(id);
				db.deleteUser_recommended(id);//delete all users with application id
				System.out.println("after deleteeeeeeeeeee recommended:"+db.getAllApps_recommended().size());
			}
			app_recommended app=new app_recommended(name_app,rate,package_name,urlIcon);
			db.add_recommended_app(app);
			int new_id=db.getAppRecommended(app.get_name());
			System.out.println("new idddd :"+new_id+"name:"+app.get_name());
			//user
			int size_users=object.getInt("size_users"+i);
			for(int j=0;j<size_users;j++)
			{
				String user_name=object.getString("user_name"+i+"user_number"+j);
				user_name=user_name.replaceAll(",", " ");
				String user_image=object.getString("user_image"+i+"user_number"+j);
				recommended_user app_user=new recommended_user(user_name,user_image,new_id);
				db.add_recommended_user(app_user);
			}
	
		 }
	}
	private String http_Icon(String package_name) 
	{
		String icon="";
		String url="https://42matters.com/api/1/apps/lookup.json?p="+package_name+"&access_token=27dd5b7e58b037d6abe7289cd14f14575de75550";
		HttpClient httpclient = new DefaultHttpClient();
		URI r=null;
		try {
			r = new URI(url);
		} catch (URISyntaxException e1) {
			return null;
		}
	    HttpResponse response=null;
		try {
			response = httpclient.execute(new HttpGet(r));
		} catch (ClientProtocolException e1) {
			return null;
		} catch (IOException e1) {
			return null;
		}
	    StatusLine statusLine = response.getStatusLine();
	    try{
	    if(statusLine.getStatusCode() == HttpStatus.SC_OK){
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        response.getEntity().writeTo(out);
	        out.close();
	        String responseString = out.toString();
	        JSONObject obj2;
			try {
				obj2 = new JSONObject(responseString);
				icon=obj2.getString("icon");
			} catch (JSONException e) {
				
				return null;
			} 
	        
	    } else{
	        //Closes the connection.
	    	System.out.println("da5al else");
	        response.getEntity().getContent().close();
	        return null;
	    }
	    }
	    catch(IOException e){e.printStackTrace();return null;}
	    String path="";
	    try{
	    System.out.println(icon);
	    URL u = new URL(icon);
        HttpURLConnection c = (HttpURLConnection) u.openConnection();
        c.setRequestMethod("GET");
      //  c.setDoOutput(true);//el fun de 3amla moshkela fe el downloaaaaaaaaad gato el araf
        c.connect();
	    File lroot=context.getFilesDir();
	    path=lroot+"/"+package_name+".jpg";//the path
	    File file= new File(path);
        file.setReadable(true, false);
        FileOutputStream f_image=new FileOutputStream(file);
        
        InputStream in=c.getInputStream();
        byte[] buffer=new byte[7000];
        int sz = 0;
        while ( ((sz = in.read(buffer)) > 0) ) {
              f_image.write(buffer,0, sz);
          }

        f_image.close();
	    }
	    catch(IOException e)
	    {e.printStackTrace();return null;}
	 
		return path;
	}
	/*
	 * delete the file
	 */
	 private void delete_file(String file_name)
	    {
	    	System.out.println("file_name"+file_name);
	    	 File file=new File(file_name);
	    	 if(file.exists())
	    	 {
	    		 file.delete();
	    	    System.out.println("Deleting is complete");
	    	 }
	    }
}

