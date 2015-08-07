package com.musar.services;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.musar.gui.MainActivity;
import com.musar.gui.R;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

@SuppressLint({ "InlinedApi", "HandlerLeak" })
public class LocationService extends Service {

	Boolean Revive = false;
	Boolean LocationServiceAlive = false;
	Boolean LocationServiceRevive = false;
	static int notificationID=0;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		LocationServiceAlive = true;
		saveData("LocationServiceAlive", true);
		System.out.println("started locservice");
		LocationServiceRevive = getPrefSaveData("LocationServiceRevive");
		
		reloadApps(10*60*1000);

		// reloadApps();

		return Service.START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}


	private void reloadApps(int TimeInterval) {
		
		TimerTask tasknew = new TimerTask() {
			@Override
            public void run() {
				if(Looper.myLooper()==null)
					Looper.prepare();

                Handler innerHandler= new Handler() {
                    @Override
                    public void handleMessage(Message message) {
                        locationChange();
                    }

                    @Override
                    public void dispatchMessage(Message message) {
                        handleMessage(message);
                    }
                };
                if(!getPrefSaveData("LocationServiceRevive"))
                {
                	Looper.myLooper().quit();
                	stopSelf();
                }
                else
                {
                Message message = innerHandler.obtainMessage();
                innerHandler.dispatchMessage(message);
                }
            }

		};
		Timer timer = new Timer();
		
		int initialDelay = 1000;
		timer.scheduleAtFixedRate(tasknew, initialDelay, TimeInterval);

	}

	@Override
	public void onDestroy() {
		// TODO To be implemented to revive the service again
		saveData("LocationServiceAlive", false);
		super.onDestroy();
	}

	public void locationChange() {

		double[] dd = getLocation();
		if (dd != null) {

			dd[0] = Math.floor(dd[0] * 10000) / 10000;
			dd[1] = Math.floor(dd[1] * 10000) / 10000;
			//System.out.println("Location = " + dd[0] + "," + dd[1]);
			if (detectLocationChange((float) dd[0], (float) dd[1])) {
				// location changed send the message or make the call
				List<Address> ad = getAddress(dd[1], dd[0]);
				// check here if it's a different location
				System.out.println("Trying to find location");
				if (ad != null) {
					String address = ad.get(0).getAddressLine(0);
					String city = ad.get(0).getAddressLine(1);
					String country = ad.get(0).getAddressLine(2);
					String fullAddress = address + ", " + city + ", " + country;
					// et2.setText(fullAddress);
					System.out.println(fullAddress);
					String message = "I'm at " + fullAddress
							+ " sent from home -Rayes";
					System.out.println(message);
					//Looper.prepare();
					Toast.makeText(getApplicationContext(),
							"Location Changed Bro gratz", Toast.LENGTH_LONG)
							.show();

					//alert with notification
					NotificationCompat.Builder normal = buildNormal("Musar detected your location change",fullAddress);
					NotificationCompat.InboxStyle big = new NotificationCompat.InboxStyle(
				            normal);
					big.addLine("Reminder to notify Your Guardian");
					if(getSavedDataString("gNumber")!=null)
						big.addLine(getSavedDataString("gName")+" "+getSavedDataString("gNumber").substring(4));
					NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
					manager.notify(++notificationID, big.build());
					

				} else {
					// et2.setText("No address found");
					System.out.println("No Address Found");
				}

			}

			// dd[0]=getSavedData("longitude");
			// dd[1]=getSavedData("latitude");

		}
	}
	private NotificationCompat.Builder buildNormal(CharSequence pTitle,String fullAddress) {
	    NotificationCompat.Builder builder = new NotificationCompat.Builder(
	            this);
		
		Intent in = new Intent(this,MainActivity.class);
		PendingIntent pMainIntent = PendingIntent.getActivity(this, 0,
				in, 0);
		
		if(getSavedDataString("gNumber")!=null)	
		{
			String url = getSavedDataString("gNumber");
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
			PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
			
			//Intent smsIntent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:" 
			//+ Uri.encode(getSavedDataString("gNumber").substring(4))));
			//intent.setData());
			//startActivity(intent);
			Intent smsIntent = new Intent(Intent.ACTION_VIEW);
			//smsIntent.setType("vnd.android-dir/mms-sms");
			smsIntent.putExtra("address", getSavedDataString("gNumber").substring(4));
			smsIntent.putExtra("sms_body","I'm at " + fullAddress);
			smsIntent.setData(Uri.parse("smsto:" 
			+ Uri.encode(getSavedDataString("gNumber").substring(4))));
			PendingIntent psmsIntent = PendingIntent.getActivity(this, 0,
					smsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			
			builder.addAction(android.R.drawable.ic_menu_call, "Call", pIntent);
			builder.addAction(android.R.drawable.sym_action_email, "Send SMS", psmsIntent);
		}
		else
		{
			builder.addAction(0, "Choose Guardian", pMainIntent);
		}
	    builder.setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL);
	    // set the shown date
	    builder.setWhen(System.currentTimeMillis());
	    // the title of the notification
	    builder.setContentTitle(pTitle);
	    // set the text for pre API 16 devices
	    builder.setContentText(pTitle);
	    // set the action for clicking the notification
	    
	    builder.setContentIntent(pMainIntent);
	    // set the notifications icon
	    builder.setSmallIcon(R.drawable.musar);
	    //builder.setSound(android.)
	    // set the small ticker text which runs in the tray for a few seconds
	    builder.setTicker("Location Change Alert");
	    // set the priority for API 16 devices
	    //builder.setVibrate(pattern)
	    builder.setPriority(Notification.PRIORITY_DEFAULT);
	    return builder;
	}

	public double[] getLocation() {
		double[] location = new double[2];
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		String bestProvider = lm.getBestProvider(new Criteria(), true);
		
		Location l = lm.getLastKnownLocation(bestProvider);
		if (l == null)
			return null;
		double longitude = l.getLongitude();
		double latitude = l.getLatitude();
		location[0] = longitude;
		location[1] = latitude;
		return location;
	}

	public void saveData(String key, float value) {
		SharedPreferences settings = getApplicationContext()
				.getSharedPreferences("save", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putFloat(key, value);
		editor.commit();
	}

	private boolean getPrefSaveData(String key) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		// then you use
		return prefs.getBoolean(key, true);
	}

	
	public String getSavedDataString(String key)
	{
		SharedPreferences settings = getApplicationContext()
				.getSharedPreferences("save", 0);
		return settings.getString(key, null);
	}
	public float getSavedData(String key) {
		SharedPreferences settings = getApplicationContext()
				.getSharedPreferences("save", 0);
		return settings.getFloat(key, -1);
	}

	public boolean getSavedData1(String key) {
		SharedPreferences settings = getApplicationContext()
				.getSharedPreferences("save", 0);
		return settings.getBoolean(key, false);
	}

	public boolean detectLocationChange(float longitude, float latitude) {
		float oldLongitude = getSavedData("longitude");
		float oldLatitude = getSavedData("latitude");
		float olderLongitude = getSavedData("oldLongitude");
		float olderLatitude = getSavedData("oldLatitude");
		saveData("longitude", (float) longitude);
		saveData("latitude", (float) latitude);
		saveData("oldLongitude", oldLongitude);
		saveData("oldLatitude", oldLatitude);
		if (oldLongitude == -1 || olderLatitude == -1 || olderLatitude == -1
				|| olderLongitude == -1)
			return false; // still not initialized
		if (longitude == oldLongitude && latitude == oldLatitude // stable
																	// location
				&& longitude != olderLongitude && latitude != olderLatitude) // changed
																				// location
			return true;
		else
			return false;
	}

	public List<Address> getAddress(double latitude, double longitude) {
		try {
			Geocoder geocoder;
			List<Address> addresses;
			geocoder = new Geocoder(this);
			if (latitude != 0 || longitude != 0) {
				addresses = geocoder.getFromLocation(latitude, longitude, 1);

				// Log.d("TAG",
				// "address = "+address+", city ="+city+", country = "+country
				// );
				return addresses;
			} else {
				// Toast.makeText(this, "latitude and longitude are null",
				// Toast.LENGTH_LONG).show();
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void saveData(String key, boolean value) {
		SharedPreferences settings = getApplicationContext()
				.getSharedPreferences("save", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

}
