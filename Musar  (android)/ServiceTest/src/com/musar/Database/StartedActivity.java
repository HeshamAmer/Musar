package com.musar.Database;
/**
 * 
 * Started activities with _id,app_id,time and duration
 *
 */
public class StartedActivity {

	int _id;
	int _appID;
	String _time;
	int _duration;
	
	public StartedActivity(int _id, int _appID, String _time, int _duration) {
		super();
		this._id = _id;
		this._appID = _appID;
		this._time = _time;
		this._duration = _duration;
	}
	public StartedActivity(int _appID, String _time, int _duration) {
		super();
		this._appID = _appID;
		this._time = _time;
		this._duration = _duration;
	}
	
	public StartedActivity(int _appID, String _time) {
		super();
		this._appID = _appID;
		this._time = _time;
	}

	public StartedActivity(int _appID) {
		super();
		this._appID = _appID;
	}

	public StartedActivity() {
		super();
	}

	
	public int getID() {
		return _id;
	}
	public void setID(int _id) {
		this._id = _id;
	}
	public int getAppID() {
		return _appID;
	}
	public void setAppID(int _appID) {
		this._appID = _appID;
	}
	public String getTime() {
		return _time;
	}
	public void setTime(String _time) {
		this._time = _time;
	}
	public int getDuration() {
		return _duration;
	}
	public void setDuration(int _duration) {
		this._duration = _duration;
	}
}
