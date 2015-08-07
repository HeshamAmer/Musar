package com.musar.gui;
 


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.musar.Database.store_data;
import com.musar.youtubedownloader.adapter_image;
import com.musar.youtubedownloader.db_videos;
import com.musar.youtubedownloader.video_info;
import com.musar.youtubedownloader.video_play;



import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
 
public class YoutubeFragment extends Fragment {
	 private db_videos db;
	    adapter_image adapter;
	    ArrayList<video_info>images_path=new ArrayList<video_info>();
	    ArrayList<String>videos_path=new ArrayList<String>();
	    ArrayList<String>title_path=new ArrayList<String>();
	    ArrayList<String>status=new ArrayList<String>();
	    List<video_info>videos=new ArrayList<video_info>();
	    store_data storing=new store_data();
    public YoutubeFragment(){}
     
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
  
    View rootView = inflater.inflate(R.layout.switch_on, container, false);
    db = new db_videos(getActivity().getApplicationContext());
	retrieve_videos();  
     ////////////////////////////////////////////////////////////////////////////////////////////////
     //display the array of images
    ListView listview = (ListView)  rootView.findViewById(R.id.list);
    adapter = new adapter_image(rootView.getContext(), R.layout.images, images_path);
   listview.setAdapter(adapter);
    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, final View view,
            int position, long id) {
        	final CharSequence[] items = {"watch the video","Delete the video" };
  		  AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
  		  builder.setTitle(title_path.get(position));
  		  final int position_2=position;
  		  builder.setItems(items, new DialogInterface.OnClickListener() {
  		   public void onClick(DialogInterface dialog, int item) {
  		    switch (item) {
  		    case 0:
                   Intent intent1 = new Intent(); 
  				 intent1.setClass(getActivity().getApplicationContext(), video_play.class);
  		         Bundle b = new Bundle();
  		         b.putString("key", videos_path.get(position_2));
  		         intent1.putExtras(b);
  		         startActivity(intent1);
  		     break;
  		    case 1:
  		    	delete_file(videos_path.get(position_2));
  		    	delete_file(images_path.get(position_2).getVideoThumbnail());
  		    	int id=videos.get(position_2).getVideoId();//deleting 
  		    	float size=videos.get(position_2).getSize();
  		    	storing.saveData("temp_"+status.get(position_2)+"_size",storing.getSavedData_float("temp_"+status.get(position_2)+"_size",getActivity().getApplicationContext())+size,getActivity().getApplicationContext());
  		    	db.deletevideo(id);
  		    	//refreshhhhhh 
  		    	Fragment currentFragment = new YoutubeFragment();
  		        FragmentManager fragTransaction = getActivity().getFragmentManager();
  		        
  		        fragTransaction.beginTransaction().replace(R.id.frame_container, currentFragment).commit();

  		     break;
  		   
  		    default:
  		     break;
  		    }  
  		    }
  		  });
  		  AlertDialog alert = builder.create();
  		  alert.show();
          
        }

      });
////////////////////////////////////////////////////////////////////////////////////////////////
//else waiting to preload some videos
    return rootView;
    }
	//retrieve all videos///////////////////////////////////////////////////////////////////////////////
 private void retrieve_videos()
 {
	  videos=db.getAllvideos();
		//////////////////////loading images from db :D
		if(videos.size()>0)
		{
			for(int i=0;i<videos.size();i++){
				{
					{
					System.out.println("the directory:"+videos.get(i).getVideoLink());
					images_path.add(videos.get(i));
					videos_path.add(videos.get(i).getVideoLink());
					title_path.add(videos.get(i).getVideoTitle());
					status.add(videos.get(i).getStatus());
					}
				}
			}
		}	
 }
	 //////////////////////////delete file///////////////////////////////////////////////////
    private void delete_file(String file_name)
    {
    	System.out.println("file_name"+file_name);
    	 File file=new File(file_name);
    	 if(file.exists())
    	 {
    		 file.delete();
 
			System.out.println("Deleting is complete");}
    }
    
}

