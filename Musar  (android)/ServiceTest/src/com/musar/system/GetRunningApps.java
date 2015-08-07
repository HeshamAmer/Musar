package com.musar.system;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


import com.musar.Database.AppActivity;
import com.musar.Database.Application;
import com.musar.Database.BannedApplication;
import com.musar.Database.DatabaseHandler;
import com.musar.Database.StartedActivity;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
/**
 * 
 * get the running apps 
 *
 */
public class GetRunningApps {

	DatabaseHandler db;
	PackageManager pm;

	public GetRunningApps(DatabaseHandler db, PackageManager pm) {
		this.db = db;
		this.pm = pm;
	}

	public void get_runningapps(Context main_activity) {
		ActivityManager am = (ActivityManager) main_activity
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = am.getRunningTasks(20);
		tasks=checkBanned(tasks);//check the banned apps
		// ///////////////////////////check the recent_tasks are done or not //////////////////
		
		List<StartedActivity> recentActivities = new ArrayList<StartedActivity>(); // not finished activity
		recentActivities = db.getAllStartedActivity();
		if (recentActivities != null && recentActivities.size() != 0) {
			for (int i = 0; i < recentActivities.size(); i++) {
				Application app = db.getApp(recentActivities.get(i).getAppID());// installed apps
				if (app != null&&checkIfNotSystemApp(app.getName())) {
					//System.out.println("Something is wrong here please check" + app.getName());
					check_done_apps(tasks, recentActivities.get(i), app.getName());
				}
				
			}
		}
		// ///////////////////////////check the running apps are previous or not// //////////////////
		for (int i = 0; i < tasks.size(); i++) {
			ComponentName topActivity = tasks.get(i).baseActivity;
			String topAPN = topActivity.getPackageName();
			if (!topAPN.equalsIgnoreCase("com.sec.android.app.launcher")&&!topAPN.equalsIgnoreCase("com.musar.gui")&& !topAPN.equalsIgnoreCase("com.example.servicetest")) {
			//	System.out.println(topAPN);
				check_previous_apps(topAPN);
			}
		}

	}
	//check if the app is new app or it is working in previous slot
	private void check_previous_apps(String running_app) {

		Application app = db.getApp(running_app);
		if (app == null) {
			if(checkIfNotSystemApp(running_app))
			{
				// not a system app and first time to be used
				app = new Application(running_app);
				db.addApp(app);
				app=db.getApp(running_app);// de 3ashan awel ma ba3mel insert ll new app fe new ablaha bey5aly insert be 0 ted5ol ll activity we heya fe el 7a2e2a 1 :D
			}
			else return;
		}
		
		StartedActivity activity = db.getStartedActivity(Integer.toString(app.getID()));
		if (activity != null) {
			activity.setDuration(activity.getDuration() + 1);
			app.setTime(app.getTime()+1);
			db.updateStartedActivity(activity);
			db.updateAppTime(app);//update app time "total time"
			
		} else// it wasn't here before so we will add it in the temp tasks
		{
			String time = Calendar.getInstance().getTime().toString(); 
			StartedActivity started = new StartedActivity(app.getID(), time, 0);
			db.addStartedActivity(started);

			//System.out.println("adding new one :" + app.getName());

		}
	}
//check if the app is finished or still working
	private void check_done_apps(List<RunningTaskInfo> running_apps,
			StartedActivity recent_tasks, String app_name) {
		boolean flag = false;
		for (int i = 0; i < running_apps.size(); i++) {
			ComponentName topActivity = running_apps.get(i).baseActivity;
			String topAPN = topActivity.getPackageName();
			if (!topAPN.equalsIgnoreCase("com.musar.gui")&&!topAPN.equalsIgnoreCase("com.sec.android.app.launcher")&& !topAPN.equalsIgnoreCase("com.example.servicetest")) {
				if (topAPN.equalsIgnoreCase(app_name)) {
					System.out.println("the app does not finish its task :"
							+ topAPN);
					flag = true;
					break;
				}
			}
		}
		if (flag == false) {
			// the app is done //database is here ISA :D
			AppActivity appActivity = new AppActivity(recent_tasks.getAppID(),recent_tasks.getTime(), recent_tasks.getDuration());
			db.addOrUpdateActivity(appActivity);
			//db.addActivity(appActivity); // finished activity( keda beye7sal add mareten !)
			db.deleteStartedActivity(recent_tasks);
			System.out.println("the app has finished el7 :D :" + app_name);

		}
	}
	//check the banned apps to delete it from the running apps
	private List<RunningTaskInfo> checkBanned(List<RunningTaskInfo> running_apps)
	{
		
		List<Integer> indexes=new ArrayList<Integer>();
		List<BannedApplication>banned=db.getAllBannedApps();
		
		for(int i=0;i<running_apps.size();i++)
		{
			for(int j=0;j<banned.size();j++)
			{
				ComponentName topActivity = running_apps.get(i).baseActivity;
				String topAPN = topActivity.getPackageName();
				if(banned.get(j).getName().equals(topAPN))
				{
					indexes.add(i);break;
				}
			}
		}
		//clear the running apps from the banned mosh 3ayez ye3mel remove :( el 7araka de 3'beya :(
		List<RunningTaskInfo> running=new ArrayList<RunningTaskInfo>();
	    for(int j=0;j<running_apps.size();j++)
		{
	    	if(!indexes.contains(j)){
			running.add(running_apps.get(j));
			
	    	}
		}
	    return running;
	}
	//check the app is not in the system apps
	boolean checkIfNotSystemApp(String packageName) {
		ApplicationInfo ai;
		try {
			ai = pm.getApplicationInfo(packageName, 0);
			if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
				//system app
				if(packageName.equalsIgnoreCase("com.google.android.youtube")||packageName.contains("music"))
				{return true;}
				return false;
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return true;
	}

}
