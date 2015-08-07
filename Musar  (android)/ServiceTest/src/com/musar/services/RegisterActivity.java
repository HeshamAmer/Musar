package com.musar.services;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import com.musar.Database.store_data;
import com.musar.gui.R;
import com.musar.services.ConfirmationActivity.register;
import com.musar.system.SimpleHttpClient;

import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class RegisterActivity extends Activity {
	Spinner spinner1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_activity);
		final EditText mEdit;
		Button   mButton;
		mButton = (Button)findViewById(R.id.button1);
		mEdit   = (EditText)findViewById(R.id.editText1);
		addListenerOnSpinnerItemSelection();
		mButton.setOnClickListener(
			        new View.OnClickListener()
			        {
			            public void onClick(View view)
			            {
			                Log.v("EditText", mEdit.getText().toString()+String.valueOf(spinner1.getSelectedItem()));
			                try {
			                	int index=String.valueOf(spinner1.getSelectedItem()).indexOf("+");
			                	String code =String.valueOf(spinner1.getSelectedItem()).substring(index);
			                	System.out.println("code"+code);
			                	saveData("phone_number",mEdit.getText().toString());
			                	SendToServer server=new SendToServer(code+mEdit.getText().toString());
								server.execute();
							} catch (Exception e) {
								e.printStackTrace();
							}
			             }
			        });
	}
	public void addListenerOnSpinnerItemSelection() {
		spinner1 = (Spinner) findViewById(R.id.spinner1);
		//spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());
	  }


	@SuppressLint("NewApi")
	public String jsonString(String user) throws JSONException {
		
		JSONObject obj2 = new JSONObject(); 
		  obj2.put("user", user);
		return obj2.toString();
	}
	public void saveData(String key, String value) {
		SharedPreferences settings = getApplicationContext()
				.getSharedPreferences("save", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	public class SendToServer extends  AsyncTask<Void,Void,Void>{
		String phNumber=null;
		SendToServer(String phNumber){this.phNumber=phNumber;}
		@Override
		 protected Void doInBackground(Void... params) {
		 try {  String phone_number=phNumber;
			StringEntity p= new StringEntity(jsonString(phone_number),"UTF-8");				
			//TODO
			
			 store_data storing =new store_data();
			  storing.saveData("first_time",true,getApplicationContext());
			  storing.saveData("ServerServiceRevive", true,getApplicationContext());
			//  register register_object=new register();
			//  register_object.execute();
			
			//String token = res.replaceAll("\\s+", "");
			SharedPreferences settings = getApplicationContext().getSharedPreferences("save", 0);
			SharedPreferences.Editor editor = settings.edit();
			//editor.putString("token", token);
			editor.commit();
			System.out.println("doneeeeeeeeeeee");
			//startActivity(new Intent(getApplicationContext(),ConfirmationActivity.class));
	        finish();
		 }
		 catch (Exception e) {
			    e.printStackTrace();
			   }
			  return null;
			 }
		}
	

}
