package com.musar.youtubedownloader;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.api.client.http.HttpResponse;
import com.musar.Database.store_data;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.widget.MediaController;
import android.widget.VideoView;

public class preload {
	private youtube_service mActivity;
    private YouTubePageStreamUriGetter jkYouTubeTask ;
    private String status="";
    private float video_size=0;
    store_data storing=new store_data();
	public YouTubePageStreamUriGetter get_asynctask(){return jkYouTubeTask;}
	 preload() {
		 jkYouTubeTask = (YouTubePageStreamUriGetter) new YouTubePageStreamUriGetter(); 
	    }
	 protected void getYouTubeVideos(youtube_service activity,String link,String title,String thumbnails,String token,String mail,String status)
	 {
	     //"https://www.youtube.com/watch?v=X_DutCd2tfg"
		 this.mActivity=activity;
	     jkYouTubeTask.execute(link,title,thumbnails,token,mail,status);
     }
	 ///////////////////these classes are very important to parsing the video file///////////////////////
	 class Meta {
		    public String num;
		    public String type;
		    public String ext;

		    Meta(String num, String ext, String type) {
		        this.num = num;
		        this.ext = ext;
		        this.type = type;
		    }
		}

		class Video {
		    public String ext = "";
		    public String type = "";
		    public String url = "";

		    Video(String ext, String type, String url) {
		        this.ext = ext;
		        this.type = type;
		        this.url = url;
		    }
		}
  /////////////////////////parsing the video file//////////////////////////////////////////////////////////
		public ArrayList<Video> getStreamingUrisFromYouTubePage(String ytUrl)
		        throws IOException {
		    if (ytUrl == null) {
		        return null;
		    }
		    int andIdx = ytUrl.indexOf('&');
		    if (andIdx >= 0) {
		        ytUrl = ytUrl.substring(0, andIdx);
		    }

		    // Get the HTML response
		    String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:8.0.1)";
		    HttpClient client = new DefaultHttpClient();
		    client.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
		            userAgent);
		    HttpGet request = new HttpGet(ytUrl);
		    org.apache.http.HttpResponse response = client.execute(request);
		    String html = "";
		    InputStream in = response.getEntity().getContent();
		    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		    StringBuilder str = new StringBuilder();
		    String line = null;
		    while ((line = reader.readLine()) != null) {
		        str.append(line.replace("\\u0026", "&"));
		    }
		    in.close();
		    html = str.toString();

		    // Parse the HTML response and extract the streaming URIs
		    if (html.contains("verify-age-thumb")) {
		        System.out.println("YouTube is asking for age verification. We can't handle that sorry.");
		        return null;
		    }

		    if (html.contains("das_captcha")) {
		       System.out.println("Captcha found, please try with different IP address.");
		        return null;
		    }

		    Pattern p = Pattern.compile("stream_map\": \"(.*?)?\"");
		    // Pattern p = Pattern.compile("/stream_map=(.[^&]*?)\"/");
		    Matcher m = p.matcher(html);
		    List<String> matches = new ArrayList<String>();
		    while (m.find()) {
		        matches.add(m.group());
		    }
            
		    if (matches.size() != 1) {
		        System.out.println("Found zero or too many stream maps.");
		        return null;
		    }

		    String urls[] = matches.get(0).split(",");
		    HashMap<String, String> foundArray = new HashMap<String, String>();
		    for (String ppUrl : urls) {
		        String url = URLDecoder.decode(ppUrl, "UTF-8");
		        Pattern p1 = Pattern.compile("itag=([0-9]+?)[&]");
		        Matcher m1 = p1.matcher(url);
		        String itag = null;
		        if (m1.find()) {
		            itag = m1.group(1);
		        }
		        Pattern p3 = Pattern.compile("url=(.*?)[&]");
		        Matcher m3 = p3.matcher(ppUrl);
		        String um = null;
		        if (m3.find()) {
		            um = m3.group(1);
		        }
		        if (itag != null  && um != null) {
		            foundArray.put(itag, URLDecoder.decode(um, "UTF-8"));
		            System.out.println("itag and um are not null");
		        }
		    }

		    if (foundArray.size() == 0) {
		        System.out.println("Couldn't find any URLs and corresponding signatures");
		        return null;
		    }

		    HashMap<String, Meta> typeMap = new HashMap<String, Meta>();
		    typeMap.put("13", new Meta("13", "3GP", "Low Quality - 176x144"));
		    typeMap.put("17", new Meta("17", "3GP", "Medium Quality - 176x144"));
		    typeMap.put("36", new Meta("36", "3GP", "High Quality - 320x240"));
		    typeMap.put("5", new Meta("5", "FLV", "Low Quality - 400x226"));
		    typeMap.put("6", new Meta("6", "FLV", "Medium Quality - 640x360"));
		    typeMap.put("34", new Meta("34", "FLV", "Medium Quality - 640x360"));
		    typeMap.put("35", new Meta("35", "FLV", "High Quality - 854x480"));
		    typeMap.put("43", new Meta("43", "WEBM", "Low Quality - 640x360"));
		    typeMap.put("44", new Meta("44", "WEBM", "Medium Quality - 854x480"));
		    typeMap.put("45", new Meta("45", "WEBM", "High Quality - 1280x720"));
		    typeMap.put("18", new Meta("18", "MP4", "Medium Quality - 480x360"));
		    typeMap.put("22", new Meta("22", "MP4", "High Quality - 1280x720"));
		    typeMap.put("37", new Meta("37", "MP4", "High Quality - 1920x1080"));
		    typeMap.put("33", new Meta("38", "MP4", "High Quality - 4096x230"));

		    ArrayList<Video> videos = new ArrayList<Video>();

		    for (String format : typeMap.keySet()) {
		        Meta meta = typeMap.get(format);

		        if (foundArray.containsKey(format)) {
		            Video newVideo = new Video(meta.ext, meta.type,
		                    foundArray.get(format));
		            videos.add(newVideo);
		           
		        }
		    }

		    return videos;
		}
		////////////////////////////////download the video after parsing/////////////////////////////////
		public class YouTubePageStreamUriGetter extends
		        AsyncTask<String, String, String> {
		    ProgressDialog progressDialog;
		    String title="";
		    String thumbnail="";
		    String token="";
		    String mail="";
		    String status="";
		    Long contentLength;
		    @Override
		    protected void onPreExecute() {
		        super.onPreExecute(); 
		    }

		    @SuppressLint("NewApi")
			@Override
		    protected String doInBackground(String... params) {
		        String url = params[0];
		        
		        this.title=params[1];
		        this.thumbnail=params[2];
		        this.token=params[3];
		        this.mail=params[4];
		        this.status=params[5];
		        String dirc_video="";
		        try {
		        	System.out.println("linkkkkkkk:"+url);
		            ArrayList<Video> videos = getStreamingUrisFromYouTubePage(url);
		            if (videos != null && !videos.isEmpty()) {
		                String retVidUrl = null;
		                for (Video video : videos) {
		                    if (video.ext.toLowerCase().contains("mp4")
		                            && video.type.toLowerCase().contains("medium")) {
		                        retVidUrl = video.url;
		                        break;
		                    }
		                }
		                if (retVidUrl == null) {
		                    for (Video video : videos) {
		                        if (video.ext.toLowerCase().contains("3gp")
		                                && video.type.toLowerCase().contains(
		                                        "medium")) {
		                            retVidUrl = video.url;
		                            break;

		                        }
		                    }
		                }
		                if (retVidUrl == null) {

		                    for (Video video : videos) {
		                        if (video.ext.toLowerCase().contains("mp4")
		                                && video.type.toLowerCase().contains("low")) {
		                            retVidUrl = video.url;
		                            break;

		                        }
		                    }
		                }
		                if (retVidUrl == null) {
		                    for (Video video : videos) {
		                        if (video.ext.toLowerCase().contains("3gp")
		                                && video.type.toLowerCase().contains("low")) {
		                            retVidUrl = video.url;
		                            break;
		                        }
		                    }
		                }
		            ///download the videos and image
		                	if(retVidUrl!=null)
		                	{
		                		File lroot=mActivity.getFilesDir();
		                		File file=new File(lroot+"/"+title+".jpg");
		                		if(!file.exists()){//check if we downloaded it before
		                		String dirc_image=download(thumbnail,".jpg",120,status);
		                		if(dirc_image!=null)
		                		{
		                		  this.thumbnail=dirc_image;
		                		  file=new File(lroot+"/"+title+".mp4");
		                		  if(!file.exists()){
		                		  dirc_video=download(retVidUrl,".mp4",600,status);
		                		  
		                		  if(dirc_video==null)
		                		  {
		                			  System.out.println("there is a problem happens with the video");
		                			  delete_file(lroot+"/"+title+".mp4");
		                			  delete_file(lroot+"/"+title+".jpg");
		                			  storing.saveData("temp_"+status+"_size",storing.getSavedData_float("temp_"+status+"_size", mActivity)+video_size, mActivity);
		                			  
		                			  return null;
		                		  }
		                		}
		                		  else return null;//we downloaded this file before
		                		}
		                		else 
		                		{System.out.println("there is a problem happens with the image");
		                			 delete_file(lroot+"/"+title+".jpg");
		                			 storing.saveData("temp_"+status+"_size",storing.getSavedData_float("temp_"+status+"_size", mActivity)+video_size, mActivity);
		                			  
		                			 return null;
		                		}
		                	}
		                		else return null;//we download this file before
		                	}
		                	else return null;
		            }
		            else return null;
		        } catch (Exception e) {
		            System.out.println("Couldn't get YouTube streaming URL"+e);
		            return null;
		        }
		   
		        if (!isCancelled())
		        return dirc_video;
		        else return null;
		    }
		    
		    @Override
		    protected void onPostExecute(String streamingUrl) {
		        super.onPostExecute(streamingUrl);
		      //response to the service to add it in db
		        video_info v=new video_info();
		        v.setVideoLink(streamingUrl);
		        v.setVideoTitle(this.title);
		        v.setVideoThumbnail(this.thumbnail);
		        v.setStatus(status);
		        v.setSize(video_size);
		        mActivity.response(v,this.token,this.mail);
		       
		    }
		    //////////////////////////delete file///////////////////////////////////////////////////
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
		    //////////////////////////download /////////////////////////////////////////////////////
		    @SuppressLint("NewApi")
			private String download(String url,String extension,int time,String status)
		    {
		    	try{
		    	   URL u = new URL(url);
                   HttpURLConnection c = (HttpURLConnection) u.openConnection();
                   c.setRequestMethod("GET");
                 //  c.setDoOutput(true);//el fun de 3amla moshkela fe el downloaaaaaaaaad gato el araf
                   c.connect();
                   contentLength = Long.parseLong(c.getHeaderField("Content-Length"));
                   System.out.println("content"+contentLength);
                   float contentLength_float=contentLength/(float)(1000*1000);//migabyte
                   float speed=((float)(mActivity.speed_wifi()))/(float)8;//convert mbps(migabit) to migabyte ps( is working well more than divide by 1000!)
                   float sec=contentLength_float/speed;//get the sec from m/m/s
                   System.out.println("timeeeeeeeeeeee :D:"+sec);
                   File lroot=mActivity.getFilesDir();
                   if(speed!=0&&sec!=0&&sec<time)// 
             	  {
                   if(check_size(contentLength,status)){
                   String x=lroot+"/"+title+extension;//the path
                   System.out.println("download file:"+x);
                   File file= new File(x);
                   file.setReadable(true, false);
                   FileOutputStream f_image=new FileOutputStream(file);
                   System.out.println("starting download");
	               InputStream in=c.getInputStream();
	               byte[] buffer=new byte[7000];
	               int sz = 0;
	              
	               //starting download
	             while ( ((sz = in.read(buffer)) > 0) ) {
	                    if (!isCancelled()){
	                     f_image.write(buffer,0, sz);
	                     //System.out.println("download");
	                    }
	                     else {System.out.println("cancellllllllllllllllllllllllllllllll");break;}
	                 }
	                  System.out.println("finish download ");
	                  f_image.close();
	                  if(isCancelled()){return null;}
	                  return x;
             	  }
                   else return null;//el file da mosh hayenfa3 a7amelo 3ashan el size beta3o kebeeeeer
                }
                else return null;
		    }
		    catch (MalformedURLException e) {
		          	  System.out.println("MalforedUrlException");
		          	  return null;
		                
		       } catch (IOException e) {
		          	  System.out.println("IOexeption");
		          	  return null;
		       }	
		    }
		    ///////////////////////////check size/////////////////////////////////////////
		    private boolean check_size(Long size,String status)
			{
		    	this.status=status;
		    	System.out.println("check the file size"+size);
				db_videos db= new db_videos(mActivity);
				boolean flag=false;
				float total_size=db.get_size_status(status);
				store_data storing =new store_data();
				float status_size=storing.getSavedData_float(status+"_size", mActivity);
				System.out.println("status is:"+status+" size:"+status_size);
				System.out.println(storing.getSavedData_float("temp_"+status+"_size", mActivity));
				if(size<status_size)
				{
					System.out.println("total size before ths file :"+total_size);
					System.out.println("total_size after this file :"+(total_size+size));
				   if((total_size+size)<=status_size)//tamaaaaaaaam 7amel ya ebny :D
				   	{
					   flag=true;
					   float size_temp=storing.getSavedData_float("temp_"+status+"_size", mActivity);
					   size_temp-=size;
					   storing.saveData("temp_"+status+"_size", size_temp, mActivity);
					   float total_size_all=storing.getSavedData_float("total_size", mActivity);
					   storing.saveData("total_size",total_size_all-size , mActivity);
					   video_size+=size;
					   if(status.equalsIgnoreCase("music"))
					   {
						  
						  int id= storing.getSavedData_int("artist_id", mActivity);
						  music_info artist=db.getartist_id(id);
						  System.out.println("artist :"+artist.getartist()+"artist size before :"+artist.get_size());
						  artist.set_size(artist.get_size()-size);//subtract the size of the artist
						  db.update_arist_size(artist);
						  music_info artist_2=db.getartist_id(id);
						  System.out.println("artist :"+artist_2.getartist()+"size:"+artist_2.get_size());
					   }
					   System.out.println("status size after resize :"+storing.getSavedData_float("temp_"+status+"_size", mActivity));
					   System.out.println("total size:"+storing.getSavedData_float("total_size", mActivity));
				   	}
				   else if(storing.getSavedData_float("temp_"+status+"_size", mActivity)>0)//hena ma3nah en day size yenfa3 bs el db heya elly malyana keda ana lazem amsa7 
				   {
					   while(storing.getSavedData_float("temp_"+status+"_size", mActivity)<size)//lazem amsa7 lee7ad ma el video size yekafy
					   {
						   List<video_info>video=db.getAllvideos();
						   if(video.size()>0)
						   {
							   int id=video.get(0).getVideoId();//deleting the first one
								System.out.println("delete record id :"+id);
								String video_link=video.get(0).getVideoLink();
								String image_link=video.get(0).getVideoThumbnail();
								delete_file(video_link);
								delete_file(image_link);
								db.deletevideo(id);
								float size_temp=storing.getSavedData_float("temp_"+status+"_size", mActivity);
								size_temp+=video.get(0).getSize();
								storing.saveData("temp_"+status+"_size", size_temp, mActivity);
								float total_size_all=storing.getSavedData_float("total_size", mActivity);
								storing.saveData("total_size",total_size_all+size , mActivity);
								   
						   }
					   }
					   float size_temp=storing.getSavedData_float("temp_"+status+"_size", mActivity);
					   size_temp-=size;
					   storing.saveData("temp_"+status+"_size", size_temp, mActivity);
					   float total_size_all=storing.getSavedData_float("total_size", mActivity);
					   storing.saveData("total_size",total_size_all-size , mActivity);
					   video_size+=size;
				   	   flag=true;
				       if(status.equalsIgnoreCase("music"))
					   {
						  int id= storing.getSavedData_int("artist_id", mActivity);
						  music_info artist=db.getartist_id(id);
						  artist.set_size(artist.get_size()-size);//subtract the size of the artist
						  db.update_arist_size(artist);
					   }
				   }
				}
				
				return flag;
			}
		}
		

}
