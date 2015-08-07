package com.musar.gui;
 


import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.musar.Database.BannedApplication;
import com.musar.Database.DatabaseHandler;
import com.musar.services.LogsService;
import com.musar.services.LogsService.Call;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
 
public class CallsFragment extends Fragment {
     
    public CallsFragment(){}
     
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
  
        View rootView = inflater.inflate(R.layout.fragment_calls, container, false);
        
        
        final ListView listview = (ListView) rootView.findViewById(R.id.listview);
        final ArrayList<Call> list = new ArrayList<Call>();
        if(LogsService.ReminderList!=null)
        	list.addAll(LogsService.ReminderList);
        		
        
        
        MyCustomAdapter dataAdapter = new MyCustomAdapter(getActivity(), R.layout.apps_adapter,
				list);
        
        
        listview.setAdapter(dataAdapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

          @Override
          public void onItemClick(AdapterView<?> parent, final View view,
              int position, long id) {
            final Call item = (Call) parent.getItemAtPosition(position);
            String url = "tel:"+item.phNumber;
    		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
    		startActivity(intent);
          }

        });
        return rootView;
    }

	private class MyCustomAdapter extends ArrayAdapter<Call> {

		private ArrayList<Call> appList;

		public MyCustomAdapter(Context context, int textViewResourceId,
				ArrayList<Call> appList) {
			super(context, textViewResourceId, appList);
			this.appList = new ArrayList<Call>();
			this.appList.addAll(appList);
		}

		private class ViewHolder {
			TextView code;
			ImageView image;
			TextView name;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			Log.v("ConvertView", String.valueOf(position));

			if (convertView == null) {
				LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = vi.inflate(R.layout.calls_adapter, null);

				holder = new ViewHolder();
				holder.code = (TextView) convertView.findViewById(R.id.code);
				holder.image = (ImageView) convertView.findViewById(R.id.icon);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				convertView.setTag(holder);

				
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			Call call = appList.get(position);
			holder.code.setText(call.phNumber);
			holder.name.setText(getContactName(getActivity(), call.phNumber));
			//holder.date.setText(call.Date);
			holder.code.setTag(call);
			Bitmap bm = getFacebookPhoto(call.phNumber);
			Drawable d = new BitmapDrawable(getResources(),
					Bitmap.createScaledBitmap(bm, 100, 100, true));
			holder.image.setImageDrawable(d);
			return convertView;

		}

	}


	 public Bitmap getFacebookPhoto(String phoneNumber) {
		    Uri phoneUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
		    Uri photoUri = null;
		    ContentResolver cr = this.getActivity().getContentResolver();
		    Cursor contact = cr.query(phoneUri,
		            new String[] { ContactsContract.Contacts._ID }, null, null, null);

		    if (contact.moveToFirst()) {
		        long userId = contact.getLong(contact.getColumnIndex(ContactsContract.Contacts._ID));
		        photoUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, userId);

		    }
		    else {
		        Bitmap defaultPhoto = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_report_image);
		        return defaultPhoto;
		    }
		    if (photoUri != null) {
		        InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(
		                cr, photoUri);
		        if (input != null) {
		            return BitmapFactory.decodeStream(input);
		        }
		    } else {
		        Bitmap defaultPhoto = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_report_image);
		        return defaultPhoto;
		    }
		    Bitmap defaultPhoto = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_report_image);
		    return defaultPhoto;
		}
	 public static String getContactName(Context context, String phoneNumber) {
		    ContentResolver cr = context.getContentResolver();
		    Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
		    Cursor cursor = cr.query(uri, new String[]{PhoneLookup.DISPLAY_NAME}, null, null, null);
		    if (cursor == null) {
		        return null;
		    }
		    String contactName = null;
		    if(cursor.moveToFirst()) {
		        contactName = cursor.getString(cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME));
		    }

		    if(cursor != null && !cursor.isClosed()) {
		        cursor.close();
		    }

		    return contactName;
		}
}