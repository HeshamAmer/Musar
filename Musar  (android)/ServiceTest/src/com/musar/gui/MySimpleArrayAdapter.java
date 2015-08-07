package com.musar.gui;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.musar.Database.app_recommended;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 
 * the recommended apps GUI
 *
 */
public class MySimpleArrayAdapter extends ArrayAdapter<app_recommended> {
  private final Context context;
  private final List<app_recommended> values;

  public MySimpleArrayAdapter(Context context, List<app_recommended> values) {
    super(context, R.layout.newsfeed_adapter, values);
    this.context = context;
    this.values = values;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View rowView = inflater.inflate(R.layout.newsfeed_adapter, parent, false);
    TextView textView = (TextView) rowView.findViewById(R.id.firstLine);
    ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
    textView.setText(values.get(position).get_name());
    // change the icon for Windows and iPhone
    String image = values.get(position).getIcon();
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inSampleSize = 1;
  
    if(!image.equals(""))
    {
    	Bitmap bm = BitmapFactory.decodeFile(image, options);
    	imageView.setImageBitmap(bm);
    }
    else {imageView.setImageResource(R.drawable.ic_launcher);}
   
    return rowView;
  }
} 