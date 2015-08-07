package com.musar.youtubedownloader;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.musar.Database.AppActivity;
import com.musar.Database.Application;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class db_videos extends SQLiteOpenHelper{
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "videos";
 
 
    // Contacts Table Columns names
   
    private static final String TABLE_VIDEOS = "videos";
    private static final String TABLE_MUSIC = "music";
    private static final String TABLE_MUSIC_RECOMMENDED = "music_recommended";
    
    private static final String KEY_MUSIC="musicID";
    private static final String KEY_ARTIST="artist";
    private static final String PlayCount="playcount";
    
    private static final String KEY_MUSIC_recommended="music_recommendedID";
    private static final String KEY_ARTIST_recommended="artist_recommended";
    private static final String KEY_rate_recommended="rate_recommended";
    private static final String KEY_size_recommended="size_recommended";
    private static final String KEY_reject_counter="reject_counter";
    
    private static final String KEY_VIDEO = "videoID";
    private static final String KEY_TITLE = "title";
    private static final String KEY_LINK = "link";
    private static final String KEY_THUMBNAIL="thumbnail";
    private static final String KEY_size="size";
    private static final String KEY_status="status";
    
	public db_videos(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		 String CREATE_VIDEOS_TABLE = "CREATE TABLE " + TABLE_VIDEOS + "("
	                + KEY_VIDEO + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_TITLE + " TEXT,"
	                 + KEY_LINK + " TEXT,"+ KEY_THUMBNAIL + " TEXT,"+KEY_status + " TEXT,"+KEY_size+ " REAL"+")";
		 String CREATE_MUSIC_TABLE = "CREATE TABLE " + TABLE_MUSIC + "("
	                + KEY_MUSIC + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_ARTIST+ " TEXT,"+PlayCount+" INTEGER"+")";
		 
		 String CREATE_MUSIC_Recommended_TABLE = "CREATE TABLE " + TABLE_MUSIC_RECOMMENDED + "("
	                + KEY_MUSIC_recommended + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_ARTIST_recommended+ " TEXT,"+KEY_rate_recommended+ " REAL,"+KEY_size_recommended+ " REAL,"+KEY_reject_counter+" INTEGER"+")";
		 
		 db.execSQL(CREATE_VIDEOS_TABLE);
		 db.execSQL(CREATE_MUSIC_TABLE);
		 db.execSQL(CREATE_MUSIC_Recommended_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_VIDEOS);
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_MUSIC);
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_MUSIC_RECOMMENDED);
        // Create tables again
        onCreate(db);
	}
    // Adding new audio
    public void addaudio(music_info audio)
    {
    	 SQLiteDatabase db = this.getWritableDatabase();
    	 
         ContentValues values = new ContentValues();
        
         values.put(KEY_ARTIST, audio.getartist()); 
         values.put(PlayCount, audio.getPlayCount());
          System.out.println("Adding record");
         // Inserting Row
         db.insert(TABLE_MUSIC, null, values);
         db.close(); // Closing database connection
    }
    //add or update the audio of user modeling 
    public void addOrUpdateaudio(music_info audio) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = this.getReadableDatabase();
	if(audio.getartist()!=null){
        Cursor cursor = db.query(TABLE_MUSIC, null, KEY_ARTIST + "=?",
                new String[] {(audio.getartist())},  null, null, null);
        if (!cursor.moveToFirst() )
        {
        	addaudio(audio);
        }
        else
        {

            music_info music = new music_info();
            music.setaudio_id(Integer.parseInt(cursor.getString(0)));
            music.setartist((cursor.getString(1)));
            music.setPlayCount(Integer.parseInt(cursor.getString(2)));
            updateaudio(music);
        }
        db.close();
	}
	}
    //get the artist name from table music 
    public music_info getArtistByName(String artist_name) {
        SQLiteDatabase db = this.getReadableDatabase();
        if(artist_name!=null){
        Cursor cursor = db.query(TABLE_MUSIC, null, KEY_ARTIST + "=?",
        		new String[] { artist_name }, null, null, null, null);
        if (!cursor.moveToFirst() )
            return null;
 
        music_info artist = new music_info();
        artist.setaudio_id(Integer.parseInt(cursor.getString(0)));
        artist.setartist(cursor.getString(1));
        artist.setPlayCount(Integer.parseInt(cursor.getString(2)));
        
        return artist;
        }
        else return null;
    }
    //update the audio that the user is using
    public void updateaudio(music_info audio) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_MUSIC, audio.getaudio_id());
        values.put(KEY_ARTIST, audio.getartist());
        values.put(PlayCount, audio.getPlayCount());
        // updating row
        db.update(TABLE_MUSIC, values, KEY_MUSIC + " = ?",
                new String[] { String.valueOf(audio.getaudio_id()) });
      db.close();
    }
   // Getting audio count
    
    public int getaudioCount() {
    	SQLiteDatabase db = this.getWritableDatabase();
        String countQuery = "SELECT  * FROM " + TABLE_MUSIC;
        Cursor cursor = db.rawQuery(countQuery, null);
        // return count
        int count=cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }
    
    // Getting All videos
    public List<music_info> getALLaudio() {

        List<music_info> audio_list = new ArrayList<music_info>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_MUSIC;
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
            	music_info audio=new music_info();
                audio.setaudio_id(Integer.parseInt(cursor.getString(0)));
                audio.setartist(cursor.getString(1));
                audio.setPlayCount(Integer.parseInt(cursor.getString(2)));
                audio_list.add(audio);
          
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return audio_list;
    }
    //delete all music history
   public void delete_AllHistory()
   {
	List<music_info> audio_list = new ArrayList<music_info>();
	List<music_info>audios=new ArrayList<music_info>();
	SQLiteDatabase db = this.getWritableDatabase();
	  String selectQuery = "SELECT  * FROM " + TABLE_MUSIC;
	  Cursor cursor = db.rawQuery(selectQuery, null);
	  if(cursor!=null){
      // looping through all rows and adding to list
      if (cursor.moveToFirst()) {
          do {
          	
          	music_info audio=new music_info();
              audio.setaudio_id(Integer.parseInt(cursor.getString(0)));
              audio.setartist(cursor.getString(1));
              audio_list.add(audio);
          } while (cursor.moveToNext());
      }
      }
	  else
      {
      	db.close();
      }
	  cursor.close();
    	for(int i=0;i<audio_list.size();i++)
    	{
    		db.delete(TABLE_MUSIC, KEY_MUSIC + " = ?",
                    new String[] { String.valueOf(audio_list.get(i).getaudio_id()) });
    	}
    	db.close();
   }
   /////////////////recommended audio
   public void addrecommend(music_info audio)
   {
   	 SQLiteDatabase db = this.getWritableDatabase();
   	 
        ContentValues values = new ContentValues();
       
        values.put(KEY_ARTIST_recommended, audio.getartist()); 
        values.put(KEY_rate_recommended, audio.get_rate()); 
        values.put(KEY_size_recommended, audio.get_size());
        values.put(KEY_reject_counter, audio.get_reject());
        // Inserting Row
        db.insert(TABLE_MUSIC_RECOMMENDED, null, values);
        db.close(); // Closing database connection
   }
   //get the recommended count
   public int getrecommendCount() {
   	SQLiteDatabase db = this.getWritableDatabase();
       String countQuery = "SELECT  * FROM " + TABLE_MUSIC_RECOMMENDED;
       Cursor cursor = db.rawQuery(countQuery, null);
       // return count
       int count=cursor.getCount();
       cursor.close();
       db.close();
       return count;
   }
   //update the artist size the the artist storing to download the video
   public int update_arist_size(music_info artist) {
	   SQLiteDatabase db = this.getWritableDatabase();

       ContentValues values = new ContentValues();

       values.put(KEY_ARTIST_recommended, artist.getartist()); 
       values.put(KEY_rate_recommended, artist.get_rate()); 
       values.put(KEY_size_recommended, artist.get_size());
       values.put(KEY_reject_counter, artist.get_reject());
       // updating row
       int d=db.update(TABLE_MUSIC_RECOMMENDED, values, KEY_MUSIC_recommended+ " = ?",
               new String[] { String.valueOf(artist.getaudio_id()) });
       db.close();
       return d;
	   }
   //get all recommended audio
   public List<music_info> getALLrecommended() {
   	
       List<music_info> audio_list = new ArrayList<music_info>();
       // Select All Query
       String selectQuery = "SELECT  * FROM " + TABLE_MUSIC_RECOMMENDED;

       SQLiteDatabase db = this.getWritableDatabase();
       Cursor cursor = db.rawQuery(selectQuery, null);

       // looping through all rows and adding to list
       if (cursor.moveToFirst()) {
           do {
           	music_info audio=new music_info();
               audio.setaudio_id(Integer.parseInt(cursor.getString(0)));
               audio.setartist(cursor.getString(1));
               audio.set_rate(Double.parseDouble(cursor.getString(2)));
               audio.set_size(Double.parseDouble(cursor.getString(3)));
               audio.set_reject(Integer.parseInt(cursor.getString(4)));
            
               audio_list.add(audio);
           } while (cursor.moveToNext());
       }
       cursor.close();
       db.close();
       return audio_list;
   }
   //clear all recommended audio
   public void clear_recommended_all()
   {List<music_info>audios=new ArrayList<music_info>();
	
	audios=getALLrecommended();
	SQLiteDatabase db = this.getWritableDatabase();
	for(int i=0;i<audios.size();i++)
	{
		db.delete(TABLE_MUSIC_RECOMMENDED, KEY_MUSIC_recommended + " = ?",
               new String[] { String.valueOf(audios.get(i).getaudio_id()) });
	}
	db.close();
	   
   }
   //cleare all videos
   public void clear_video_all()
   {List<video_info>videos=new ArrayList<video_info>();
	
	videos=getAllvideos();
	SQLiteDatabase db = this.getWritableDatabase();
	for(int i=0;i<videos.size();i++)
	{
		db.delete(TABLE_VIDEOS, KEY_VIDEO + " = ?",
               new String[] { String.valueOf(videos.get(i).getVideoId()) });
	}
	db.close();
	   
   }
   /////////////////video
    public void addvideo(video_info video)
    {
    	 SQLiteDatabase db = this.getWritableDatabase();
    	 
         ContentValues values = new ContentValues();
         values.put(KEY_TITLE, video.getVideoTitle()); // title_video
         values.put(KEY_LINK, video.getVideoLink()); // link video(the directory of the saving video)
         values.put(KEY_THUMBNAIL, video.getVideoThumbnail()); // link video(the directory of the saving video)
         values.put(KEY_status, video.getStatus()); 
         values.put(KEY_size, video.getSize()); 
          
         // Inserting Row
         db.insert(TABLE_VIDEOS, null, values);
         db.close(); // Closing database connection
    }
    // Getting videos Count
    
    public int getvideosCount() {
    	SQLiteDatabase db = this.getWritableDatabase();
        String countQuery = "SELECT  * FROM " + TABLE_VIDEOS;
        Cursor cursor = db.rawQuery(countQuery, null);
        // return count
        int count=cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }
    //delete video
    public void deletevideo(int video_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_VIDEOS, KEY_VIDEO + " = ?",
                new String[] { String.valueOf(video_id) });
        System.out.println("delete_record");
        db.close();
    }
    
    // Updating single video   
    
    public int updateVideos(video_info video) {
        SQLiteDatabase db = this.getWritableDatabase();

        System.out.println("updating:D :D :D :D");
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, video.getVideoTitle());
        values.put(KEY_THUMBNAIL, video.getVideoThumbnail());
        values.put(KEY_LINK, video.getVideoLink());
        values.put(KEY_status, video.getStatus());
        values.put(KEY_size, video.getSize());
        // updating row
        int d=db.update(TABLE_VIDEOS, values, KEY_VIDEO+ " = ?",
                new String[] { String.valueOf(video.getVideoId()) });
        db.close();
        return d;
       
    }
    
    // Getting All videos
    public List<video_info> getAllvideos() {
    	System.out.println("get_all videos");
        List<video_info> appList = new ArrayList<video_info>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_VIDEOS;
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
            	video_info video=new video_info();
                video.setVideoid(Integer.parseInt(cursor.getString(0)));
                video.setVideoTitle(cursor.getString(1));
                video.setVideoLink(cursor.getString(2));
                video.setVideoThumbnail(cursor.getString(3));
                video.setStatus(cursor.getString(4));
                video.setSize(Float.parseFloat(cursor.getString(5)));
                appList.add(video);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return appList;
    }
    public int getVideo_id(String video_link) {
    	System.out.println(video_link);
        SQLiteDatabase db = this.getReadableDatabase();
 
        Cursor cursor = db.query(TABLE_VIDEOS, new String[] { KEY_VIDEO}, KEY_LINK + "=?",
                new String[] { video_link }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        if(cursor.getCount()!=0){
        int id =Integer.parseInt(cursor.getString(0));
        // return id
        System.out.println("id:"+id);
        cursor.close();
        db.close();
        return id;
        }
        else {
        	cursor.close();
        	db.close();
        	return  -1;
        	}
    }
    //get the staus size if music or youtube
    public float get_size_status(String status) {
        // Select All Query
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_VIDEOS,null, KEY_status + "=?",
                new String[] {status},null ,  null, null,null);
        // looping through all rows and adding to list
        float total_size=0;
        if (cursor.moveToFirst()) {
            do {
                total_size+=Double.parseDouble(cursor.getString(5));
               
            } while (cursor.moveToNext());
        }
        else
        {
        	db.close();
        	return total_size;
        }
        db.close();
       
        return total_size;
    }
    //get artist id from recommended music
    public music_info getartist_id(int artist_id) {
        SQLiteDatabase db = this.getReadableDatabase();
 
        Cursor cursor = db.query(TABLE_MUSIC_RECOMMENDED, null, KEY_MUSIC_recommended + "=?",
        		new String[] { String.valueOf(artist_id) }, null, null, null, null);
        if (!cursor.moveToFirst() )
            return null;
 
        music_info artist = new music_info();
        artist.setaudio_id(Integer.parseInt(cursor.getString(0)));
        artist.setartist(cursor.getString(1));
        artist.set_rate(Double.parseDouble(cursor.getString(2)));
        artist.set_size(Double.parseDouble(cursor.getString(3)));
        artist.set_reject(Integer.parseInt(cursor.getString(4)));
       
        return artist;
    }
}
