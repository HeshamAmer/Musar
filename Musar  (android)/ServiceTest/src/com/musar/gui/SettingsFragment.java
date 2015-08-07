package com.musar.gui;

import org.json.JSONException;
import org.json.JSONObject;

import com.musar.Database.DatabaseHandler;
import com.musar.Database.store_data;
import com.musar.services.LocationService;
import com.musar.services.MainService;
import com.musar.services.RegisterActivity;
import com.musar.services.TrackerService;
import com.musar.system.ServerCall;
import com.musar.system.thread;
import com.musar.youtubedownloader.db_videos;
import com.musar.youtubedownloader.music_track;
import com.musar.youtubedownloader.youtube_service;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.widget.Toast;

public class SettingsFragment extends PreferenceFragment {
	store_data storing = new store_data();
	public static final int CONTACT_PICKER_RESULT = 1001;

	public SettingsFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Load the preferences from an XML resource
		addPreferencesFromResource(R.layout.fragment_settings);

		
		ListPreference lp = (ListPreference) findPreference("cacheSizeYouTube");
		lp.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				SharedPreferences myPreference = PreferenceManager
						.getDefaultSharedPreferences(getActivity()
								.getApplicationContext());
				String value = myPreference.getString("cacheSizeYouTube", "50");

				store_data storing = new store_data();
				if (value != null) {
					float total_size = Float.parseFloat(value);
					total_size = total_size * 1024 * 1024 * 8;
					storing.saveData("Total_sizeUser", total_size,
							getActivity().getApplicationContext());
					System.out.println("total sizeeeeeee:" + total_size);
				}
				return true;
			}
		});

		lp.setSummary(getResources().getString(R.string.setCacheSummary));

		Preference deleteAccountButton = (Preference) findPreference("deleteAccountButton");
		deleteAccountButton
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference arg0) {
						try {
							delete_my_account();
						} catch (JSONException e) {
							e.printStackTrace();
						}
						System.out.println("STUF DELTED");
						return true;
					}
				});

		final Preference selectBannedApps = (Preference) findPreference("selectBannedApps");
		selectBannedApps
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference arg0) {
						startActivity(new Intent(getActivity(),
								SelectBannedAppsActivity.class));
						System.out.println("select banned apps");
						return true;
					}
				});
		final CheckBoxPreference trackerService = (CheckBoxPreference) findPreference("TrackerServiceRevive");
		trackerService
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						selectBannedApps.setEnabled(trackerService.isChecked());
						return false;
					}
				});
		selectBannedApps.setEnabled(trackerService.isChecked());

		CheckBoxPreference youtube = (CheckBoxPreference) findPreference("uTubeRevive");
		youtube.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				if (newValue.toString().equals("true")) {
					get_the_token();
				} else {
					storing.saveData("uTubeServiceRevive", false, getActivity()
							.getApplicationContext());
				}
				return true;
			}
		});

		final Preference selectGuardian = (Preference) findPreference("selectGuardian");
		selectGuardian
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference arg0) {
						if (getSavedData("gNumber") != null) {
							System.out.println("select Guardian");
							new AlertDialog.Builder(getActivity())
									.setTitle("Change Number")
									.setMessage(
											"Are you sure you want to change the chosen number ("
													+ getSavedData("gName")
													+ ", "
													+ getSavedData("gNumber")
													+ ") ?")
									.setPositiveButton(
											android.R.string.yes,
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int which) {

													System.out
															.println("yes pressed");
													Intent contactPickerIntent = new Intent(
															Intent.ACTION_PICK,
															Contacts.CONTENT_URI);
													startActivityForResult(
															contactPickerIntent,
															CONTACT_PICKER_RESULT);
												}
											})
									.setNeutralButton(
											android.R.string.cancel,
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int which) {

													System.out
															.println("cancel pressed");
												}
											})
									.setIcon(android.R.drawable.ic_dialog_alert)
									.show();
						} else {
							// start contact picker for the first time
							Intent contactPickerIntent = new Intent(
									Intent.ACTION_PICK, Contacts.CONTENT_URI);
							startActivityForResult(contactPickerIntent,
									CONTACT_PICKER_RESULT);
						}
						return true;
					}
				});
		final CheckBoxPreference locationChange = (CheckBoxPreference) findPreference("LocationServiceRevive");
		locationChange
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						selectGuardian.setEnabled(locationChange.isChecked());
						return false;
					}
				});
		selectGuardian.setEnabled(locationChange.isChecked());
	}

	@SuppressWarnings("static-access")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == this.getActivity().RESULT_OK) {
			switch (requestCode) {
			case SettingsFragment.CONTACT_PICKER_RESULT:
				// handle contact results
				Uri result = data.getData();

				String number;
				String id = result.getLastPathSegment();
				// query for everything
				Cursor cursor = this
						.getActivity()
						.getContentResolver()
						.query(Phone.CONTENT_URI, null,
								Phone.CONTACT_ID + "=?", new String[] { id },
								null);

				int phoneID = cursor.getColumnIndex(Phone.DATA);
				int nameID = cursor.getColumnIndex(Phone.DISPLAY_NAME);
				String name;
				if (cursor.moveToFirst()) {
					number = cursor.getString(phoneID);
					name = cursor.getString(nameID);
				} else {
					number = null;
					name = null;
				}
				// let's just get the first email
				number = "tel:" + number;
				System.out.println(number + ",name=" + name);
				// et3.setText(number + ",name=" + name);
				if (name != null)
					saveData("gName", name);
				if (number != null)
					saveData("gNumber", number);
				break;
			}

		} else {
			// gracefully handle failure
			System.out.println("NOTHING CHOSEN");
			// et3.setText("NOTHING CHOSEN");
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void saveData(String key, String value) {
		SharedPreferences settings = this.getActivity().getSharedPreferences(
				"save", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public String getSavedData(String key) {
		SharedPreferences settings = this.getActivity().getSharedPreferences(
				"save", 0);
		return settings.getString(key, null);
	}

	// ///////////////////////////////////////////server////////////////////////////////////////////////////////
	private void delete_my_account() throws JSONException {
		String id = storing.getSavedData("id", getActivity().getApplicationContext());
		id = jsonString(id);
		Handler handler = new Handler();
		thread new_thread = new thread(id,"http://jbossews-musar.rhcloud.com/WorkingMusar11_7/rest/delete_account",getActivity().getApplicationContext(), handler);
		(new Thread(new_thread)).start();
		///reset the default
		SharedPreferences settings = getActivity().getApplicationContext().getSharedPreferences("save", 0);
		storing.saveData("ServerServiceRevive", false, getActivity().getApplicationContext());
		storing.saveData("uTubeServiceRevive", false, getActivity().getApplicationContext());
		CheckBoxPreference tracker = (CheckBoxPreference) findPreference("TrackerServiceRevive");
		tracker.setChecked(true);
		CheckBoxPreference missed = (CheckBoxPreference) findPreference("LogsServiceRevive");
		missed.setChecked(true);
		CheckBoxPreference location = (CheckBoxPreference) findPreference("LocationServiceRevive");
		location.setChecked(true);
		CheckBoxPreference youtube = (CheckBoxPreference) findPreference("uTubeRevive");
		youtube.setChecked(false);
		storing.saveData("gNumber", null, getActivity().getApplicationContext());
		storing.saveData("gName", null, getActivity().getApplicationContext());
		ListPreference lp = (ListPreference) findPreference("cacheSizeYouTube");
		lp.setValue("100");
		storing.saveData("Total_sizeUser", 100 * 1024 * 1024 * 8, getActivity().getApplicationContext());
		storing.saveData("first_time", false, getActivity().getApplicationContext());
		// ///////////////////////stop all services/////////////////////
		getActivity().getApplicationContext().stopService(new Intent(getActivity().getApplicationContext(),ServerCall.class));
		getActivity().getApplicationContext().stopService(new Intent(getActivity().getApplicationContext(),TrackerService.class));
		getActivity().getApplicationContext().stopService(new Intent(getActivity().getApplicationContext(),LocationService.class));
		getActivity().getApplicationContext().stopService(new Intent(getActivity().getApplicationContext(),music_track.class));
		getActivity().getApplicationContext().stopService(new Intent(getActivity().getApplicationContext(),youtube_service.class));
		getActivity().getApplicationContext().stopService(new Intent(getActivity().getApplicationContext(),MainService.class));
		// //////////////////delete all db //////////////////////////
		DatabaseHandler db = new DatabaseHandler(getActivity().getApplicationContext());
		db.clearMostApps();
		db.clearApps();
		db.clearBannedApps();
		db.clearRecommended();
		db.clearRecommendedUser();
		db.clearStartedActivity();
		db_videos db_2 = new db_videos(getActivity().getApplicationContext());
		db_2.clear_recommended_all();
		db_2.delete_AllHistory();
		db_2.clear_video_all();
		FragmentManager fragmentManager = getActivity().getFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.frame_container, new EmptyHomeFragment()).commit();
		// register again
		startActivity(new Intent(getActivity().getApplicationContext(),RegisterActivity.class));

	}

	public String jsonString(String user) throws JSONException {

		JSONObject obj2 = new JSONObject();
		obj2.put("user", user);
		return obj2.toString();
	}

	// //////////////////////////////////////////////get the authentication from the user////////////////
	void get_the_token() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
		prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
		final SharedPreferences.Editor editor = prefs.edit();
		String token_validate = prefs.getString("token", null);
		AccountManager.get(getActivity().getApplicationContext()).invalidateAuthToken("com.google", token_validate);
		final Account[] account = AccountManager.get(getActivity().getApplicationContext()).getAccounts();
		final Handler handler = new Handler();
		boolean flag = false;
		// check if there is any google account login is found
		for (int i = 0; i < account.length; i++) {
			if (account[i].type.equalsIgnoreCase("com.google")) {
				flag = true;
				break;
			}
		}
		if (flag == true) {
			AccountManager.get(getActivity().getApplicationContext())
					.getAuthTokenByFeatures("com.google",
							"oauth2:https://gdata.youtube.com", null,
							getActivity(), null, null,
							new AccountManagerCallback<Bundle>() {
								@Override
								public void run(
										AccountManagerFuture<Bundle> future) {
									try {
										Bundle bundle = future.getResult();
										String auth_token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
										String account = bundle.getString(AccountManager.KEY_ACCOUNT_NAME);
										saveData("mail_key", account);
										System.out.println("account:" + account);
										saveData("token_key", auth_token);
										System.out.println("token:"+ auth_token);
										editor.commit();
										// ///////////////save the boolean to be
										// true
										storing.saveData("uTubeServiceRevive",true,getActivity().getApplicationContext());
									} catch (Exception e) {
										System.out.println("youtube account error:"+ e.getClass().getSimpleName()+ ": " + e.getMessage());
										// Wrong with wifi
										if (getActivity() != null)
											Toast.makeText(getActivity().getApplicationContext(),"there is wrong with the authentication may be WIFI connection",Toast.LENGTH_LONG).show();
									}
								}
							}, handler);

		}
		// google account
		else if (getActivity() != null)
			Toast.makeText(getActivity().getApplicationContext(),"there is not any google account login ", Toast.LENGTH_LONG).show();
	}
}