package com.musar.Database;
/**
 * 
 * recommended trusted friends with user name,user image,id and app_id
 *
 */
public class recommended_user {
	String user_name;
	String user_image;
	int id;
	int app_ID;
	public recommended_user(){}
	public recommended_user(String user_name,String user_image,int app_ID)
	{
		this.user_name=user_name;
		this.user_image=user_image;
		this.app_ID=app_ID;
	}
	public void set_APP_id(int app_id){this.app_ID=app_id;}
	public void set_user(String user_name){this.user_name=user_name;}
	public void set_id(int id){this.id=id;}
	public void set_image(String image){this.user_image=image;}
	
	public String get_user(){return this.user_name;}
	public String get_image(){return user_image;}
	public int get_id(){return id;}
	public int get_App_ID(){return app_ID;}
}
