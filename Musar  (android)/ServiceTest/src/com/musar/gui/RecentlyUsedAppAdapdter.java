package com.musar.gui;



import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RecentlyUsedAppAdapdter extends ArrayAdapter<String> {
	private List<String> appsList = null;
	private Context context;

	public RecentlyUsedAppAdapdter(Context context, int textViewResourceId,
			List<String> appsList) {
		super(context, textViewResourceId, appsList);
		this.context = context;
		this.appsList = appsList;
		
	}
	
	@Override
	public int getCount() {
		return ((null != appsList) ? appsList.size() : 0);
	}

	@Override
	public String getItem(int position) {
		return ((null != appsList) ? appsList.get(position) : null);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	 private class ViewHolder {
         ImageView imageView;
         TextView app_name;
     }
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		if (null == view) {
			holder = new ViewHolder();
			LayoutInflater layoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = layoutInflater.inflate(R.layout.snippet_list_row, null);
			holder.imageView = (ImageView) view
                    .findViewById(R.id.app_icon);
            view.setTag(holder);
		}
		else {
            holder = (ViewHolder) view.getTag();
        }

		String data = appsList.get(position);
		if (null != data) {
			//TextView appName = (TextView) view.findViewById(R.id.app_name);
			holder.app_name = (TextView) view.findViewById(R.id.app_paackage);
			
			
			ApplicationInfo appInfo = null;
			PackageManager p=context.getPackageManager();
			try {
				appInfo = p.getApplicationInfo(data, 0);
				holder.app_name.setText(appInfo.loadLabel(p));
				Drawable d = context.getPackageManager().getApplicationIcon(appInfo);
				holder.imageView.setImageDrawable(d);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		return view;
	}
}
