package com.musar.Database;

import java.util.ArrayList;
import java.util.List;
/**
 * most used apps with user id from the server,user used activities,last recommended Apps
 * @author Eman_mostafa
 *
 */
public class most_apps {
	String user_id;
	List<AppActivity>user_Activities=new ArrayList<AppActivity>();
	List<String>apps_recommended=new ArrayList<String>();
	public most_apps(String user_id,List<AppActivity> app_activity,List<String>apps_recommended) 
	{
		this.user_id=user_id;
		this.user_Activities=app_activity;
		this.apps_recommended=apps_recommended;
		
	}
	public void set_id(String user_id){this.user_id=user_id;}
	public void set_userActivity(ArrayList<AppActivity>user_Activities){this.user_Activities=user_Activities;}
	public String get_id(){return this.user_id;}
	public List<AppActivity>get_activity(){return this.user_Activities;}
	public void set_apps_recommended(ArrayList<String>apps_recommended){this.apps_recommended=apps_recommended;}
	public List<String> get_apps_recommended(){return this.apps_recommended;}
	
}
