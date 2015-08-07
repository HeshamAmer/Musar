package com.musar.Database;

import java.util.ArrayList;
import java.util.List;
/**
 * 
 * refresh the user data with user id.installed Apps,contacts,images of friends
 *
 */
public class refresh_data {
	String user_id;
	List<String>installedApps=new ArrayList<String>();
	ArrayList<Contact>contacts=new ArrayList<Contact>();
	ArrayList<Integer>images=new ArrayList<Integer>();
	public refresh_data(String user_id,List<String> installed,ArrayList<Contact>contacts,ArrayList<Integer>images) 
	{
		this.user_id=user_id;
		this.installedApps=installed;
		this.contacts=contacts;
		this.images=images;
	}
	public void set_id(String user_id){this.user_id=user_id;}
    public void set_installedApps(ArrayList<String>installedApps){this.installedApps=installedApps;}
	public void set_contacts(ArrayList<Contact>contacts){this.contacts=contacts;}
	public void set_images(ArrayList<Integer>images){this.images=images;}
	
	
	public String get_id(){return this.user_id;}
	public List<String>get_installed(){return this.installedApps;}
	public ArrayList<Contact>get_contact(){return this.contacts;}
	public ArrayList<Integer>get_images(){return this.images;}
}
