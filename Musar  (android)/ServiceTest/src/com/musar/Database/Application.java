package com.musar.Database;
/**
 * 
 * @param args
 * _id of the app in application table
 * name of the app
 * type of app(category)
 * time of usng this app
 *
 */

public class Application {

	int _id;
	String _name;
	String _type = "NONE";
	double _Time = 0;
	public Application(){};
	
	public Application(int id,String name,String type,double time)
	{
		this._id = id;
		this._name = name;
		this._type = type;
		this._Time=time;
	}
	
	public Application(String name,String type)
	{
		this._name = name;
		this._type = type;
	}
	
	public Application(String name)
	{
		this._name = name;
	}

	public int getID() {
		return _id;
	}

	public void setID(int _id) {
		this._id = _id;
	}

	public String getName() {
		return _name;
	}

	public void setName(String _name) {
		this._name = _name;
	}
	public double getTime() {
		return this._Time;
	}
	
	public void setTime(double Time) {
		this._Time = Time;
	}
	
	public String getType() {
		return _type;
	}

	public void setType(String _type) {
		this._type = _type;
	}
	
	
}
