package com.musar.youtubedownloader;

import java.util.ArrayList;

import com.musar.gui.R;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

    public class adapter_image extends ArrayAdapter<video_info>  {
        private LayoutInflater mInflater;
        int layoutResourceId;    
        Context context;
        //images name
        ArrayList<video_info>image_path=new ArrayList<video_info>();
        
        public adapter_image(Context context, int layoutResourceId,ArrayList<video_info>data) {
            super(context, layoutResourceId,data);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.image_path = data;
            
            mInflater  = (LayoutInflater) context
                    .getSystemService(context.LAYOUT_INFLATER_SERVICE);
        }


       public void set_images(ArrayList<video_info>images)
       {
    	   image_path=images;
       }
       public ArrayList<video_info> get_image(){return image_path;}
      

        public long getItemId(int position) {
            return position;
        }

        /*private view holder class*/
        private class ViewHolder {
            ImageView imageView;
            TextView image_title;
           
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
         
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(layoutResourceId, null);
                
                holder.imageView = (ImageView) convertView
                        .findViewById(R.id.icon);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            String image = image_path.get(position).getVideoThumbnail();
            String title=image_path.get(position).getVideoTitle();
            
            if (null != image) {
    			holder.image_title = (TextView) convertView.findViewById(R.id.image_title);
    			holder.image_title.setText(title);
    			// display the image on the screen
                BitmapFactory.Options options = new BitmapFactory.Options();
    			 options.inSampleSize = 1;
    			 
                Bitmap bm = BitmapFactory.decodeFile(image, options);
    	        holder.imageView.setImageBitmap(bm);
    	       
                
            }
            
            return convertView;
        }

		
	
    }