package com.musar.gui;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.musar.Database.app_recommended;
import com.musar.Database.recommended_user;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class friends_recommendAdapter extends ArrayAdapter<recommended_user> {
  private final Context context;
  private final List<recommended_user> values;

  public friends_recommendAdapter(Context context, List<recommended_user> values) {
    super(context, R.layout.friends_recommendation, values);
    this.context = context;
    this.values = values;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View rowView = inflater.inflate(R.layout.friends_recommendation, parent, false);
    TextView textView = (TextView) rowView.findViewById(R.id.friend_name);
    ImageView imageView = (ImageView) rowView.findViewById(R.id.friends_image);
    if(values.get(position).get_user().equalsIgnoreCase("unknown friend"))//Musar recommend from training set
    {//lu 7aga gaya mn musar el mafrod yeb2a user wa7ed howa musar laken lu friends momken yeb2a kaza wa7ed
    	textView.setText("Musar");
    	imageView.setImageResource(R.drawable.ic_launcher);
    }
    else{//friends recommend
    	textView.setText(values.get(position).get_user());
    	String image = values.get(position).get_image();
    	Bitmap bitmap=queryContactImage(Integer.parseInt(image));
    	if (bitmap==null) {
    		imageView.setImageResource(R.drawable.unknown);
    	} else {
    	     imageView.setImageBitmap(bitmap);
    	}
    }
    return rowView;
  }
    
  private Bitmap queryContactImage(int imageDataRow) {
	    Cursor c = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, new String[] {
	        ContactsContract.CommonDataKinds.Photo.PHOTO
	    }, ContactsContract.Data._ID + "=?", new String[] {
	        Integer.toString(imageDataRow)
	    }, null);
	    byte[] imageBytes = null;
	    if (c != null) {
	        if (c.moveToFirst()) {
	            imageBytes = c.getBlob(0);
	        }
	        c.close();
	    }

	    if (imageBytes != null) {
	        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length); 
	    } else {
	        return null;
	    }
	}


} 