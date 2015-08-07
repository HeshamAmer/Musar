package com.musar.Database;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
 
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
 /**
  * 
  * database of application used , the last used activities ,the starting used activities ,recommended Apps and the trusted friends that recommend this apps and the banned applications
  *
  */
public class DatabaseHandler extends SQLiteOpenHelper {
 
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "Applications";
 
 
    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    
    private static final String TABLE_APPS = "apps";
    private static final String KEY_TYPE = "Type";
    private static final String TOTAL_TIME="totalTime";

    private static final String TABLE_ACTIVTY = "activity";
    private static final String KEY_APPID = "appID";
    private static final String KEY_TIME = "time";
    private static final String KEY_DURATION = "duration";
    
    private static final String TABLE_RECOMMENDED = "recommended_apps";
    private static final String KEY_RECOMMENDED_NAME = "recommended_name";
    private static final String KEY_RECOMMENDED_RATE = "recommended_rate";
    private static final String KEY_RECOMMENDED_PACKAGE = "recommended_package";
    private static final String KEY_RECOMMENDED_ICON = "recommended_icon";
    private static final String KEY_RECOMMENDED_ID = "recommended_ID";
    
    
    private static final String TABLE_RECOMMENDED_USER = "recommended_usersTABLE";
    private static final String KEY_RECOMMENDED_USER = "recommended_user";
    private static final String KEY_RECOMMENDED_USER_IMAGE= "recommended_user_image";
    private static final String KEY_RECOMMENDEDuser_ID = "recommended_user_id";
    private static final String KEY_RECOMMEND_ID="recommend_id";
    
    private static final String TABLE_STARTED_ACTIVTY = "started";
 
    private static final String TABLE_BANNED_APPLICATION = "banned_apps";
    
    public DatabaseHandler(Context context) {
    	
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
       
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        
        String CREATE_APPS_TABLE = "CREATE TABLE " + TABLE_APPS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME + " TEXT,"
                +KEY_TYPE + " TEXT," +TOTAL_TIME + " REAL"+ ")";
        String CREATE_ACTIVITY_TABLE = "CREATE TABLE " + TABLE_ACTIVTY + "("
        		+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_APPID +" INTEGER, "
        		+ KEY_TIME + " DATETIME, " + KEY_DURATION +" INTEGER, "
        		+"FOREIGN KEY (" + KEY_APPID + ") REFERENCES "+TABLE_APPS+" ("
        		+ KEY_ID + ")" 
        		+ ");";
        String CREATE_STARTED_ACTIVITY_TABLE = "CREATE TABLE " + TABLE_STARTED_ACTIVTY+ "("
        		+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_APPID +" INTEGER, "
        		+ KEY_TIME + " DATETIME, " + KEY_DURATION +" INTEGER, "
        		+"FOREIGN KEY (" + KEY_APPID + ") REFERENCES "+TABLE_APPS+" ("
        		+ KEY_ID + ")" 
        		+ ");";
        String CREATE_RECOMMENDED_TABLE = "CREATE TABLE " + TABLE_RECOMMENDED + "("
                + KEY_RECOMMENDED_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_RECOMMENDED_NAME + " TEXT,"
                + KEY_RECOMMENDED_RATE + " DOUBLE," + KEY_RECOMMENDED_PACKAGE + " TEXT," + KEY_RECOMMENDED_ICON + " TEXT"+")";
        
        String CREATE_RECOMMENDED_USER_TABLE = "CREATE TABLE " + TABLE_RECOMMENDED_USER + "("
                + KEY_RECOMMENDEDuser_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +KEY_RECOMMEND_ID + " INTEGER,"
                + KEY_RECOMMENDED_USER + " TEXT," +KEY_RECOMMENDED_USER_IMAGE + " TEXT,"
                + "FOREIGN KEY (" + KEY_RECOMMEND_ID + ") REFERENCES "+TABLE_RECOMMENDED+" ("
        		+ KEY_RECOMMENDED_ID + ")" + ")";
        
        String CREATE_BANNED_APPLICATION_TABLE = "CREATE TABLE " + TABLE_BANNED_APPLICATION + "("
        		+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME + " TEXT" + ")";
       

        db.execSQL(CREATE_APPS_TABLE);
        db.execSQL(CREATE_ACTIVITY_TABLE);
        db.execSQL(CREATE_STARTED_ACTIVITY_TABLE);
        db.execSQL(CREATE_RECOMMENDED_TABLE);
        db.execSQL(CREATE_RECOMMENDED_USER_TABLE);
        db.execSQL(CREATE_BANNED_APPLICATION_TABLE);
    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVTY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STARTED_ACTIVTY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECOMMENDED);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECOMMENDED_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BANNED_APPLICATION);
        
        // Create tables again
        onCreate(db);
    }
 
   /**
    * Adding new banned application
    * @param bannedApp
    */
    public void addBannedApplication(BannedApplication bannedApp)
    {
    	 SQLiteDatabase db = this.getWritableDatabase();
    	 
         ContentValues values = new ContentValues();
         
         
         values.put(KEY_NAME, bannedApp.getName());
  
         // Inserting Row
         db.insert(TABLE_BANNED_APPLICATION, null, values);
         db.close(); // Closing database connection
    }
   
   /**
    * 
    * @param app
    * adding new application in application table
    */
    public void addApp(Application app)
    {
    	 SQLiteDatabase db = this.getWritableDatabase();
    	 
         ContentValues values = new ContentValues();
         values.put(KEY_NAME, app.getName()); 
         values.put(KEY_TYPE, app.getType()); 
         values.put(TOTAL_TIME, app.getTime()); 
         // Inserting Row
         db.insert(TABLE_APPS, null, values);
         db.close(); // Closing database connection
    }
    /**
     * 
     * @param app
     * adding new recommended App
     */
    public void add_recommended_app(app_recommended app)
    {
    	 SQLiteDatabase db = this.getWritableDatabase();
    	 
         ContentValues values = new ContentValues();
         values.put(KEY_RECOMMENDED_NAME, app.get_name()); 
         values.put(KEY_RECOMMENDED_RATE, app.get_rate()); 
         values.put(KEY_RECOMMENDED_PACKAGE, app.get_package());
         values.put(KEY_RECOMMENDED_ICON, app.getIcon());
         // Inserting Row
         db.insert(TABLE_RECOMMENDED, null, values);
         db.close(); // Closing database connection
    }
    /**
     * 
     * @param user
     * adding the trusted friends to the recommended app
     * 
     */
    public void add_recommended_user(recommended_user user)
    {
    	 SQLiteDatabase db = this.getWritableDatabase();
    	 
         ContentValues values = new ContentValues();
         values.put(KEY_RECOMMENDED_USER, user.get_user()); 
         values.put(KEY_RECOMMENDED_USER_IMAGE, user.get_image()); 
         values.put(KEY_RECOMMEND_ID, user.get_App_ID());
         // Inserting Row
         db.insert(TABLE_RECOMMENDED_USER, null, values);
         db.close(); // Closing database connection
    }
    /**
     * 
     * @param activity
     * adding new activity to activity table
     */
    public void addActivity(AppActivity activity)
    {
    	 SQLiteDatabase db = this.getWritableDatabase();
    	 
         ContentValues values = new ContentValues();
         
        
         values.put(KEY_APPID, activity.getAppID());
         values.put(KEY_DURATION, activity.getDuration()); 
         values.put(KEY_TIME, activity.getTime()); 
  
         // Inserting Row
         db.insert(TABLE_ACTIVTY, null, values);
         db.close(); // Closing database connection
    }
    /**
     * 
     * @param activity
     * adding started activity
     */
    public void addStartedActivity(StartedActivity activity)
    {
    	 SQLiteDatabase db = this.getWritableDatabase();
    	 
         ContentValues values = new ContentValues();
         
         values.put(KEY_APPID, activity.getAppID());
         values.put(KEY_DURATION, activity.getDuration()); 
         values.put(KEY_TIME, activity.getTime()); 
  
         // Inserting Row
         db.insert(TABLE_STARTED_ACTIVTY, null, values);
         db.close(); // Closing database connection
    }
    
   /**
    * 
    * @param id
    * @return the app with this id
    */
    public Application getApp(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
 
        Cursor cursor = db.query(TABLE_APPS, new String[] { KEY_ID,
                KEY_NAME, KEY_TYPE,TOTAL_TIME }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (!cursor.moveToFirst() )
            return null;
        Application app = new Application(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2),Double.parseDouble(cursor.getString(3)));
        // return contact
        return app;
    }
    /**
     * 
     * @param name
     * @return app with this name
     */
    public Application getApp(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
 
        Cursor cursor = db.query(TABLE_APPS, new String[] { KEY_ID,
                KEY_NAME, KEY_TYPE,TOTAL_TIME }, KEY_NAME + "=?",
                new String[] { name }, null, null, null, null);
        if (!cursor.moveToFirst() )
        	return null;
          
        Application app = new Application(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2),Double.parseDouble(cursor.getString(3)));
     
        db.close();
        return app;
    }
    /**
     * 
     * @param name
     * @return get the app recommended with this name
     */
    public int getAppRecommended(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
 
        Cursor cursor = db.query(TABLE_RECOMMENDED, new String[] { KEY_RECOMMENDED_ID }, KEY_RECOMMENDED_NAME + "=?",
                new String[] { name }, null, null, null, null);
        if (!cursor.moveToFirst() )
        	return 0;
        
        int id=Integer.parseInt(cursor.getString(0));

        db.close();
        return id;
    }
    /**
     * 
     * @param id
     * @return get the arecommnded app with this name
     */
    public app_recommended getAppRecommended(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
 
        Cursor cursor = db.query(TABLE_RECOMMENDED, new String[] { KEY_RECOMMEND_ID,KEY_RECOMMENDED_NAME,KEY_RECOMMENDED_RATE,KEY_RECOMMENDED_PACKAGE,KEY_RECOMMENDED_ICON }, KEY_RECOMMEND_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (!cursor.moveToFirst() )
        	return null;
   
        app_recommended app=new app_recommended(cursor.getString(1),Double.parseDouble(cursor.getString(2)),cursor.getString(3),cursor.getString(4));
        
        db.close();
        return app;
    }
    /**
     * 
     * @param appID
     * @return return the starting activity with the appID from application table
     */
    public StartedActivity getStartedActivity(String appID) {
        SQLiteDatabase db = this.getReadableDatabase();
 
        Cursor cursor = db.query(TABLE_STARTED_ACTIVTY, new String[] { KEY_ID,
                KEY_APPID, KEY_TIME, KEY_DURATION}, KEY_APPID + "=?",
                new String[] { appID }, null, null, null, null);
        if(!cursor.moveToFirst())
        	return null;
	         
	        StartedActivity app = new StartedActivity(Integer.parseInt(cursor.getString(0)),
	        		Integer.parseInt(cursor.getString(1)), cursor.getString(2)
	        		,Integer.parseInt(cursor.getString(3)));
	        return app;
        
    }
   /**
    * 
    * @param appName
    * @return get app activity from the app name
    */
    public AppActivity getActivity(String appName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_APPS, new String[] { KEY_ID,
        		KEY_NAME}, KEY_NAME + "=?",
                new String[] { appName }, null, null, null, null);
        if(cursor.moveToFirst()){
        	 Cursor cursor_1 = db.query(TABLE_ACTIVTY, new String[] { KEY_ID,
                     KEY_APPID, KEY_TIME, KEY_DURATION}, KEY_APPID + "=?",
                     new String[] {String.valueOf(Integer.parseInt(cursor.getString(0)))}, null, null, null, null);
        	 if(!cursor_1.moveToFirst())
             	return null;
     	    
     	        AppActivity app = new AppActivity(Integer.parseInt(cursor_1.getString(0)),
     	        		Integer.parseInt(cursor_1.getString(1)), cursor_1.getString(2)
     	        		,Integer.parseInt(cursor_1.getString(3)));
     	  
     	        return app;
        }
        else return null;
    }
    /**
     * 
     * @return all recommended apps
     */
    public List<app_recommended> getAllApps_recommended() {
        List<app_recommended> appList = new ArrayList<app_recommended>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_RECOMMENDED;
      
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                app_recommended app = new app_recommended();
                app.set_id(Integer.parseInt(cursor.getString(0)));
                app.set_name(cursor.getString(1));
                app.set_rate(Double.parseDouble(cursor.getString(2)));
                app.set_app_Package(cursor.getString(3));
                app.setIconURL(cursor.getString(4));
               
                appList.add(app);
            } while (cursor.moveToNext());
        }
        db.close();
       
        return appList;
    }
   /**
    * 
    * @return all trusted friends 
    */
    public List<recommended_user> getAlluser_recommended() {
        List<recommended_user> appList = new ArrayList<recommended_user>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_RECOMMENDED_USER;
        
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                recommended_user app = new recommended_user();
                
                app.set_id(Integer.parseInt(cursor.getString(0)));
                app.set_APP_id(Integer.parseInt(cursor.getString(1)));
                app.set_user(cursor.getString(2));
                app.set_image(cursor.getString(3));

                appList.add(app);
            } while (cursor.moveToNext());
        }
        db.close();
        
        return appList;
    }
    /**
     * 
     * @param id
     * @return get all trusted friends from app name
     */
    public List<recommended_user> get_usersFromApp(int id) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECOMMENDED_USER, new String[] { KEY_RECOMMENDEDuser_ID,
        		KEY_RECOMMEND_ID, KEY_RECOMMENDED_USER, KEY_RECOMMENDED_USER_IMAGE}, KEY_RECOMMEND_ID + "=?",
                     new String[] {String.valueOf(id)}, null, null, null, null);
        List<recommended_user>app_recommended=new ArrayList<recommended_user>();
        if (cursor.moveToFirst()) {
            do {
                recommended_user app = new recommended_user();
                app.set_id(Integer.parseInt(cursor.getString(0)));
                app.set_APP_id(Integer.parseInt(cursor.getString(1)));
                app.set_user(cursor.getString(2));
                app.set_image((cursor.getString(3)));
              
                app_recommended.add(app);
            } while (cursor.moveToNext());
        }
        return app_recommended;
    }
    
    /**
     * 
     * @return all Apps from application table
     */
    public List<Application> getAllApps() {
        List<Application> appList = new ArrayList<Application>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_APPS;
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Application app = new Application();
                app.setID(Integer.parseInt(cursor.getString(0)));
                app.setName(cursor.getString(1));
                app.setType(cursor.getString(2));
                app.setTime(Double.parseDouble(cursor.getString(3)));
              
                appList.add(app);
            } while (cursor.moveToNext());
        }
        db.close();
      
        return appList;
    }
    /**
     * 
     * @return get all activities from activities table
     */
    public List<AppActivity> getAllActivity() {
        List<AppActivity> actList = new ArrayList<AppActivity>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ACTIVTY;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if(cursor!=null){
        if (cursor.moveToFirst()) {
            do {
                AppActivity act = new AppActivity();
                act.setID(Integer.parseInt(cursor.getString(0)));
                act.setAppID(Integer.parseInt(cursor.getString(1)));
                act.setTime(cursor.getString(2));
                act.setDuration(Integer.parseInt(cursor.getString(3)));
                
                String appName = getApp(act.getAppID()).getName();
                act.setAppName(appName);
                actList.add(act);
            } while (cursor.moveToNext());
        }
        }
        else
        {
        	db.close();
        	return null;
        }
        db.close();
       
        return actList;
    }
    /**
     * 
     * @return get all sorting activities  
     */
    public List<AppActivity> getAllActivityTodaySortedDecDuration() {
        List<AppActivity> actList = new ArrayList<AppActivity>();
        // Select All Query
        SQLiteDatabase db = this.getWritableDatabase();
        Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE,0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);
		
		Calendar tom = today;
		tom.add(Calendar.DAY_OF_YEAR, 1);
		Cursor cursor = db.query(TABLE_ACTIVTY, null, null,null ,  null, null, KEY_DURATION + " DESC");
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                AppActivity act = new AppActivity();
                act.setID(Integer.parseInt(cursor.getString(0)));
                act.setAppID(Integer.parseInt(cursor.getString(1)));
                act.setTime(cursor.getString(2));
                act.setDuration(Integer.parseInt(cursor.getString(3)));
                String appName = getApp(act.getAppID()).getName();
                act.setAppName(appName);
                actList.add(act);
            } while (cursor.moveToNext());
        }
        else
        {
        	db.close();
        	return null;
        }
        db.close();
       
        return actList;
    }
    /**
     * 
     * @return get all sorting applications 
     */
    public List<Application> getAllAppsSorting() {
        List<Application> appList = new ArrayList<Application>();
        // Select All Query
        SQLiteDatabase db = this.getWritableDatabase();
        
        Cursor cursor = db.query(TABLE_APPS, null,null,null,  null, null, TOTAL_TIME + " ASC");
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Application app = new Application();
                app.setID(Integer.parseInt(cursor.getString(0)));
                app.setName(cursor.getString(1));
                app.setType(cursor.getString(2));
                app.setTime(Double.parseDouble(cursor.getString(3)));
               
                appList.add(app);
            } while (cursor.moveToNext());
        }
        else
        {
        	db.close();
        	return null;
        }
        db.close();
       
        return appList;
    }
    /**
     * 
     * @return get all starting activities
     */
    public List<StartedActivity> getAllStartedActivity() {
        List<StartedActivity> actList = new ArrayList<StartedActivity>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_STARTED_ACTIVTY;
        
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                StartedActivity act = new StartedActivity();
                act.setID(Integer.parseInt(cursor.getString(0)));
                act.setAppID(Integer.parseInt(cursor.getString(1)));
                act.setTime(cursor.getString(2));
                act.setDuration(Integer.parseInt(cursor.getString(3)));
                actList.add(act);
            } while (cursor.moveToNext());
        }
        db.close();
      
        return actList;
    }
    /**
     * get all banned apps
     * @return
     */
    public List<BannedApplication> getAllBannedApps() {
        List<BannedApplication> appList = new ArrayList<BannedApplication>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_BANNED_APPLICATION;
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
            	BannedApplication app = new BannedApplication();
                app.setId(Integer.parseInt(cursor.getString(0)));
                app.setName(cursor.getString(1));
              
                appList.add(app);
            } while (cursor.moveToNext());
        }
        db.close();
      
        return appList;
    }
    /**
     *  Updating single application   
     * @param app
     * @return
     */
    
    public int updateApplication(Application app) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, app.getName());
        values.put(KEY_TYPE, app.getType());
 
        // updating row
        return db.update(TABLE_APPS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(app.getID()) });
    }
    /**
     * update the starting activities
     * @param activity
     */
    public void updateStartedActivity(StartedActivity activity) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_APPID, activity.getAppID());
        values.put(KEY_TIME, activity.getTime());
        values.put(KEY_DURATION, activity.getDuration());
 
        // updating row
        db.update(TABLE_STARTED_ACTIVTY, values, KEY_ID + " = ?",
                new String[] { String.valueOf(activity.getID()) });
        db.close();
        
    }
    /**
     * update the app time
     * @param app
     */
    public void updateAppTime(Application app) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, app.getName());
        values.put(KEY_TYPE, app.getType());
        values.put(TOTAL_TIME, app.getTime());
 
        // updating row
        db.update(TABLE_APPS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(app.getID()) });
        db.close();
    }
    /**
     * update the activity
     * @param activity
     */
    public void updateActivity(AppActivity activity) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_APPID, activity.getAppID());
        values.put(KEY_TIME, activity.getTime());
        values.put(KEY_DURATION, activity.getDuration());
 
        // updating row
        db.update(TABLE_ACTIVTY, values, KEY_ID + " = ?",
                new String[] { String.valueOf(activity.getID()) });
        db.close();
    }
   
    
   /**
    * clear the activities
    */
    public void clearMostApps()
    {
    
    	List<AppActivity>activities=new ArrayList<AppActivity>();
    	
    	activities=getAllActivity();
    	SQLiteDatabase db = this.getWritableDatabase();
    	for(int i=0;i<activities.size();i++)
    	{
    		db.delete(TABLE_ACTIVTY, KEY_ID + " = ?",
                    new String[] { String.valueOf(activities.get(i).getID()) });
    	}
    	db.close();
    }
    /**
     * clear the starting activity
     */
    public void clearStartedActivity()
    {
    
    	List<StartedActivity>activities=new ArrayList<StartedActivity>();
    	
    	activities=getAllStartedActivity();
    	SQLiteDatabase db = this.getWritableDatabase();
    	for(int i=0;i<activities.size();i++)
    	{
    		db.delete(TABLE_STARTED_ACTIVTY, KEY_ID + " = ?",
                    new String[] { String.valueOf(activities.get(i).getID()) });
    	}
    	db.close();
    }
    /**
     * clear the apps 
     */
    public void clearApps()
    {
    
    	List<Application>apps=new ArrayList<Application>();
    	
    	apps=getAllApps();
    	SQLiteDatabase db = this.getWritableDatabase();
    	for(int i=0;i<apps.size();i++)
    	{
    		db.delete(TABLE_APPS, KEY_ID + " = ?",
                    new String[] { String.valueOf(apps.get(i).getID()) });
    	}
    	db.close();
    }
    /**
     * clear the recommended apps
     */
    public void clearRecommended()
    {
    
    	List<app_recommended>activities=new ArrayList<app_recommended>();
    	
    	activities=getAllApps_recommended();
    	SQLiteDatabase db = this.getWritableDatabase();
    	for(int i=0;i<activities.size();i++)
    	{
    		db.delete(TABLE_RECOMMENDED, KEY_RECOMMENDED_ID + " = ?",
                    new String[] { String.valueOf(activities.get(i).get_id()) });
    	}
    	db.close();
    }
    /**
     * clear the recommended trusted friends
     */
    public void clearRecommendedUser()
    {
    
    	List<recommended_user>activities=new ArrayList<recommended_user>();
    	
    	activities=getAlluser_recommended();
    	SQLiteDatabase db = this.getWritableDatabase();
    	for(int i=0;i<activities.size();i++)
    	{
    		db.delete(TABLE_RECOMMENDED_USER, KEY_RECOMMENDEDuser_ID + " = ?",
                    new String[] { String.valueOf(activities.get(i).get_id()) });
    	}
    	db.close();
    }
    /**
     * clear banned APPs
     */
    public void clearBannedApps()
    {
    
    	List<BannedApplication>activities=new ArrayList<BannedApplication>();
    	
    	activities=getAllBannedApps();
    	SQLiteDatabase db = this.getWritableDatabase();
    	for(int i=0;i<activities.size();i++)
    	{
    		db.delete(TABLE_BANNED_APPLICATION, KEY_ID + " = ?",
                    new String[] { String.valueOf(activities.get(i).getId()) });
    	}
    	db.close();
    }
    /**
     * delete the row from the starting activities
     * @param activity
     */
    public void deleteStartedActivity(StartedActivity activity) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_STARTED_ACTIVTY, KEY_ID + " = ?",
                new String[] { String.valueOf(activity.getID()) });
        db.close();
    }
    /**
     * delete row from recommended app
     * @param id_app
     */
    public void deleteApp_recommended(int id_app) {
        SQLiteDatabase db = this.getWritableDatabase();
   
        db.delete(TABLE_RECOMMENDED, KEY_RECOMMENDED_ID + " = ?",
                new String[] { String.valueOf(id_app) });
        db.close();
    }
    /**
     * delete the user that recommends the app
     * @param id_app
     */
    public void deleteUser_recommended(int id_app) {
    	
        SQLiteDatabase db = this.getWritableDatabase();
  
        db.delete(TABLE_RECOMMENDED_USER, KEY_RECOMMEND_ID + " = ?",
                new String[] { String.valueOf(id_app) });
        db.close();
    }
    /**
     * delete the row of application
     * @param app
     */
    public void deleteApplication(Application app) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_APPS, KEY_ID + " = ?",
                new String[] { String.valueOf(app.getID()) });
        db.close();
    }
    /***
     * delete the banned application
     * @param app
     */
    public void deleteBannedApplication(BannedApplication app) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BANNED_APPLICATION, KEY_NAME + " = ?",
                new String[] { app.getName() });
        db.close();
    }
  /**
   * get the app counts
   * @return
   */
    public int getAppsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_APPS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
 
        return cursor.getCount();
    }
     /**
      * add or update the activity
      * @param appActivity
      */
	public void addOrUpdateActivity(AppActivity appActivity) {
		
		SQLiteDatabase db = this.getReadableDatabase();
		Application app = getApp(appActivity.getAppID());
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE,0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);
		
		Calendar tom = Calendar.getInstance();
		tom.add(Calendar.DAY_OF_YEAR, 1);
		tom.set(Calendar.HOUR_OF_DAY, 0);
		tom.set(Calendar.MINUTE,0);
		tom.set(Calendar.SECOND, 0);
		tom.set(Calendar.MILLISECOND, 0);
	
        Cursor cursor = db.query(TABLE_ACTIVTY, null, KEY_APPID + "=?",
                new String[] {Integer.toString(app.getID())},  null, null, null);
        if (!cursor.moveToFirst() )
        {
        	addActivity(appActivity);
        }
        else
        {
        	AppActivity act = new AppActivity();
            act.setID(Integer.parseInt(cursor.getString(0)));
            act.setAppID(Integer.parseInt(cursor.getString(1)));
            act.setTime(cursor.getString(2));
            act.setDuration(Integer.parseInt(cursor.getString(3))+appActivity.getDuration());
            updateActivity(act);
        }
	}
	
}