package com.musar.gui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import com.musar.Database.BannedApplication;
import com.musar.Database.DatabaseHandler;
import com.musar.Database.store_data;
import com.musar.system.SimpleHttpClient;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SelectBannedAppsActivity extends Activity {

	MyCustomAdapter dataAdapter = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_apps);
		final DatabaseHandler db = new DatabaseHandler(this);
		// Generate list View from ArrayList
		displayListView(db);

		checkButtonClick(db);

	}

	private class App {
		ApplicationInfo ai;
		private boolean selected;

		public boolean isSelected() {
			return selected;
		}

		public void setSelected(boolean b) {
			selected = b;
		}

		public void setAi(ApplicationInfo ai) {
			this.ai = ai;
		};

	};

	private void displayListView(DatabaseHandler db) {

		// Array list of countries
		PackageManager pm = getPackageManager();
		@SuppressWarnings("static-access")
		List<ApplicationInfo> ApplicationsList = pm
				.getInstalledApplications(pm.GET_META_DATA);
		ArrayList<App> InstalledApps = new ArrayList<App>();

		List<BannedApplication> bannedApps = db.getAllBannedApps();
		for (ApplicationInfo ai : ApplicationsList) {
			if (((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0)
					|| ai.loadLabel(pm).toString().equalsIgnoreCase("musar")) {
				// System app
				continue;
			} else {
				App app = new App();
				app.setAi(ai);
				boolean check = false;
				for (BannedApplication ba : bannedApps) {
					if (ba.getName().equals(ai.packageName)) {
						app.setSelected(false);
						check = true;
						break;
					}
				}
				if (!check)
					app.setSelected(true);
				InstalledApps.add(app);
			}
		}

		// create an ArrayAdaptar from the String Array
		dataAdapter = new MyCustomAdapter(this, R.layout.apps_adapter,
				InstalledApps);
		ListView listView = (ListView) findViewById(R.id.listView1);
		// Assign adapter to ListView
		listView.setAdapter(dataAdapter);

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// When clicked, show a toast with the TextView text
				App app = (App) parent.getItemAtPosition(position);
				Toast.makeText(getApplicationContext(),
						"Clicked on Row: " + app.ai.packageName,
						Toast.LENGTH_LONG).show();
			}
		});

	}

	private class MyCustomAdapter extends ArrayAdapter<App> {

		private ArrayList<App> appList;

		public MyCustomAdapter(Context context, int textViewResourceId,
				ArrayList<App> appList) {
			super(context, textViewResourceId, appList);
			this.appList = new ArrayList<App>();
			this.appList.addAll(appList);
		}

		private class ViewHolder {
			TextView code;
			CheckBox name;
			ImageView image;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			Log.v("ConvertView", String.valueOf(position));

			if (convertView == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = vi.inflate(R.layout.apps_adapter, null);

				holder = new ViewHolder();
				holder.code = (TextView) convertView.findViewById(R.id.code);
				holder.name = (CheckBox) convertView
						.findViewById(R.id.checkBox1);
				holder.image = (ImageView) convertView.findViewById(R.id.icon);
				convertView.setTag(holder);

				holder.name.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						CheckBox cb = (CheckBox) v;
						App app = (App) v.getTag();
						// appInfo.
						Toast.makeText(
								getApplicationContext(),
								"Clicked on Checkbox: " + app.ai.packageName
										+ " is " + cb.isChecked(),
								Toast.LENGTH_LONG).show();
						app.setSelected(cb.isChecked());
					}
				});
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			App appi = appList.get(position);
			holder.code.setText(appi.ai.loadLabel(getPackageManager()));
			// holder.name.setText(appi.name);
			holder.name.setChecked(appi.isSelected());
			holder.name.setTag(appi);

			Drawable dr = appi.ai.loadIcon(getPackageManager());
			Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
			// Scale it to 50 x 50
			Drawable d = new BitmapDrawable(getResources(),
					Bitmap.createScaledBitmap(bitmap, 50, 50, true));

			holder.image.setImageDrawable(d);
			return convertView;

		}

	}

	//
	private void checkButtonClick(final DatabaseHandler db) {

		Button acceptButton = (Button) findViewById(R.id.button_accept);
		Button cancelButton = (Button) findViewById(R.id.button_cancel);
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		acceptButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// StringBuffer responseText = new StringBuffer();
				// responseText.append("The following were selected...\n");
				
				ArrayList<App> appList = dataAdapter.appList;
				for (int i = 0; i < appList.size(); i++) {
					App app = appList.get(i);
					if (!app.isSelected()) {
						BannedApplication ba = new BannedApplication();
						ba.setName(app.ai.packageName);
						db.addBannedApplication(ba);

					} else {
						BannedApplication ba = new BannedApplication();
						ba.setName(app.ai.packageName);
						db.deleteBannedApplication(ba);
					}
				}
				SendToServer server=new SendToServer();
				server.execute();
			
				finish();

				// Toast.makeText(getApplicationContext(),
				// responseText, Toast.LENGTH_LONG).show();

			}
		});

	}
	public String jsonString(List<BannedApplication> user) throws JSONException {
		store_data storing=new store_data();
		String id=storing.getSavedData("id", SelectBannedAppsActivity.this);
		JSONObject obj2 = new JSONObject(); 
		obj2.put("user", id);
		obj2.put("size_banned",user.size());
		for(int i=0;i<user.size();i++)
		{
			obj2.put("Banned"+i, user.get(i).getName());
		}
		return obj2.toString();
	}
	public class SendToServer extends  AsyncTask<Void,Void,Void>{
		@Override
		 protected Void doInBackground(Void... params) {
		 try { 
			DatabaseHandler db=new DatabaseHandler(SelectBannedAppsActivity.this);
			List<BannedApplication>bannedApplication=db.getAllBannedApps();
			StringEntity p= new StringEntity(jsonString(bannedApplication),"UTF-8");
			String response=SimpleHttpClient.executeHttpPost("http://jbossews-musar.rhcloud.com/WorkingMusar11_7/rest/deleteMost",p,SelectBannedAppsActivity.this); 
			String res = response.toString();
			
			System.out.println("doneeeeeeeeeeee");
			
	        finish();
		 }
		 catch (Exception e) {
			    e.printStackTrace();
			   }
			  return null;
			 }
	}
}