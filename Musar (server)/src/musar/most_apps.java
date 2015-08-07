package musar;

import java.util.ArrayList;
import java.util.List;

public class most_apps {
	String user_id;
	List<AppActivity>user_Activities=new ArrayList<AppActivity>();
	ArrayList<String>user_recommended=new ArrayList<String>();
	public most_apps(){}
	public most_apps(String user_id,List<AppActivity> app_activity,List<String>user_recommened) 
	{
		this.user_id=user_id;
		this.user_Activities=app_activity;
		this.user_recommended=user_recommended;
	}
	public void set_id(String user_id){this.user_id=user_id;}
	public void set_userActivity(ArrayList<AppActivity>user_Activities){this.user_Activities=user_Activities;}
	public String get_id(){return this.user_id;}
	public List<AppActivity>get_activity(){return this.user_Activities;}
	public void set_user_recommended(ArrayList<String>user_recommended){this.user_recommended=user_recommended;}
	public ArrayList<String> get_user_recommended(){return this.user_recommended;}
	
}
