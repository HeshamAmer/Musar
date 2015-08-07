package com.musar.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.IBinder;
import android.provider.CallLog;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class LogsService extends Service {

	/**
	 * @param args
	 */
	List<Call> MissedCallsList;
	List<Call> OutCallsList;
	List<Call> InCallsList;
	static public List<Call> ReminderList;
	Boolean LogsServiceAlive;

	public static void main(String[] args) {

	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	public class Call {
		public String phNumber;
		public String Date;

		Call() {
			phNumber = "This number havn't been assigned yet";
			Date = "This Date havn't been assigned yet";
		}
		Call(String S1){
			phNumber=S1;
		}
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//System.out.println("Started logsservice2");
		saveData("LogsServiceAlive", true);
		try {
		
			callsReminder();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		stopSelf();
		return Service.START_NOT_STICKY;

	}
	@Override
	public void onDestroy() {
		saveData("LogsServiceAlive", false);
		super.onDestroy();
	}

	private void init() {
		MissedCallsList = new ArrayList<Call>();
		OutCallsList = new ArrayList<Call>();
		InCallsList = new ArrayList<Call>();
		ReminderList = new ArrayList<Call>();
	}

	private void callsReminder() throws ParseException {

		init();
		/**
		 * 
		 * EditText et1 = (EditText) this.findViewById(R.id.editText1); Logs
		 * format Phone Number:--- Mobinil Call Type : --- Incoming call Date :
		 * --- Mon Feb 24 00:02:52 EET 2014
		 */

		StringBuffer sb = new StringBuffer();
		ContentResolver cr = this.getContentResolver();

		Cursor managedCursor = cr.query(CallLog.Calls.CONTENT_URI, null, null,
				null, null);

		int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
		int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
		int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);

		sb.append("Call Details :");
	
		while (managedCursor.moveToNext()) {
			Call C = new Call();
			String phNumber = managedCursor.getString(number);
			String callType = managedCursor.getString(type);
			String callDate = managedCursor.getString(date);
			Date callDayTime = new Date(Long.valueOf(callDate));
			int dircode = Integer.parseInt(callType);
			switch (dircode) {
			case CallLog.Calls.OUTGOING_TYPE:
				C.Date = callDayTime.toString();
				C.phNumber = phNumber;
				OutCallsList.add(C);

				break;

			case CallLog.Calls.INCOMING_TYPE:
				C.Date = callDayTime.toString();
				C.phNumber = phNumber;
				InCallsList.add(C);
				break;

			case CallLog.Calls.MISSED_TYPE:
				C.Date = callDayTime.toString();
				C.phNumber = phNumber;
				MissedCallsList.add(C);
				
				break;
			}
			/*
			 * sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- "
			 * + dir + " \nCall Date:--- " + callDayTime);
			 * sb.append("\n----------------------------------");
			 */
		}
		managedCursor.close();
		SimpleDateFormat sf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
		for (Call S : MissedCallsList) {
			Date MissedCallDate = sf.parse(S.Date);
			Calendar MissedCallCal = Calendar.getInstance();
			MissedCallCal.setTime(MissedCallDate);
			Date Today = new Date();
			Calendar TodayCal = Calendar.getInstance();
			TodayCal.setTime(Today);
			Calendar OutCallCal = Calendar.getInstance();
			if (has(OutCallsList, S)) {
				int index = getIndexOf(OutCallsList, S);
				Date OutCallDate = sf.parse(OutCallsList.get(index).Date);
				OutCallCal = Calendar.getInstance();
				OutCallCal.setTime(OutCallDate);

				// If a missed call showed after out call , and in the sameday.
				if (MissedCallCal.after(OutCallCal)
						&& sameday(MissedCallCal, TodayCal)) {
					ReminderList.add(S);

				}
				//continue;
			}
			else if (has(InCallsList, S)) {
				int index = getIndexOf(InCallsList, S);
				Date InCallDate = sf.parse(InCallsList.get(index).Date);
				Calendar InCallCal = Calendar.getInstance();
				InCallCal.setTime(InCallDate);
				
				if (MissedCallCal.before(InCallCal)) {
					if (sameday(MissedCallCal, TodayCal) )
						ReminderList.add(S);
				}
			}

			// Missed call saree7a
			
		}
		Set<String> set = new HashSet<String>();
		for (Call S : ReminderList)
			set.add(S.phNumber);
		ReminderList.clear();
		for (String S : set) {
			Log.e("Musar", S);
			ReminderList.add(new Call(S));
		}
		//System.out.println("list = " + ReminderList);

	}

	private boolean sameday(Calendar c1, Calendar c2) {
		return (Math.abs(c1.get(Calendar.DAY_OF_YEAR)
				- c2.get(Calendar.DAY_OF_YEAR)) < 2
				&& c1
					.get(Calendar.YEAR) == c2.get(Calendar.YEAR));
	}

	public boolean getSavedData1(String key) {
		SharedPreferences settings = getApplicationContext()
				.getSharedPreferences("save", 0);
		return settings.getBoolean(key, false);
	}

	public void saveData(String key, boolean value) {
		SharedPreferences settings = getApplicationContext()
				.getSharedPreferences("save", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	private boolean has(List<Call> L, Call S) {
		for (int i = 0; i < L.size(); i++) {
			if (L.get(i).phNumber.equals(S.phNumber)) {

				return true;
			}
		}
		return false;
	}

	private int getIndexOf(List<Call> L, Call S) {
		int i = 0;
		for (i = 0; i < L.size(); i++) {
			if (L.get(i).phNumber.equals(S.phNumber)) {
				return i;
			}
		}
		// TODO: check this condition
		return i;
	}

}
