package com.musar.youtubedownloader;
/**
 * 
 * video info with title,thumbnail.link,time id,size,status(music or youtube)
 *
 */
public class video_info {
	String video_title;
	String video_thumbnail;
	String video_link;
	String video_time;
	int video_id;
	float size;
	String status;
	public void setVideoTitle(String video_title){this.video_title=video_title;}
	public void setVideoThumbnail(String video_thumbnail){this.video_thumbnail=video_thumbnail;}
	public void setVideoLink(String video_link){this.video_link=video_link;}
	public void setVideoTime(String video_time){this.video_time=video_time;}
	public void setVideoid(int video_id){this.video_id=video_id;}
	public void setStatus(String video_status){this.status=video_status;}
	public void setSize(float video_size){this.size=video_size;}
	
	public String getVideoTitle(){return this.video_title;}
	public String getVideoThumbnail(){return this.video_thumbnail;}
	public String getVideoLink(){return this.video_link;}
	public String getVideoTime(){return this.video_time;}
	public int getVideoId(){return this.video_id;}
	public String getStatus(){return this.status;}
	public float getSize(){return this.size;}
}
