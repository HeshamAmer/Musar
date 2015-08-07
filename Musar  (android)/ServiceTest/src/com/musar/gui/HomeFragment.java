package com.musar.gui;
 


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import com.musar.Database.DatabaseHandler;
import com.musar.Database.app_recommended;
import com.musar.Database.recommended_user;
import com.musar.youtubedownloader.video_play;


import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
 
public class HomeFragment extends Fragment {
	List<app_recommended> list = new ArrayList<app_recommended>();
	ArrayList<app_recommended> list_highest=new ArrayList<app_recommended>();
    public HomeFragment(){}
     
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
  
        final View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        final ListView listview = (ListView) rootView.findViewById(R.id.listview);
        DatabaseHandler db=new DatabaseHandler(getActivity().getApplicationContext());
        list=db.getAllApps_recommended();
        
        for(int i=list.size()-1;i>=0;i--)
        {
        	list_highest.add(list.get(i));
        }
        //we need to recommend from the highest prediction or highest rate to the smallest
        final MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(rootView.getContext(),list_highest);
        listview.setAdapter(adapter);
        
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

          @Override
          public void onItemClick(AdapterView<?> parent, final View view,int position, long id) {
        	  final CharSequence[] items = {"Information about App","Who recommends this App" };
      		  AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      		  builder.setTitle(list_highest.get(position).get_name());
      		  final int position_2=position;
      		  builder.setItems(items, new DialogInterface.OnClickListener() {
      		   public void onClick(DialogInterface dialog, int item) {
      		    switch (item) {
      		    case 0:
      		    	String link="https://play.google.com/store/apps/details?id="+list_highest.get(position_2).get_package();
      		        Uri uri = Uri.parse(link);
      			    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
      			    startActivity(intent);
      		     break;
      		    case 1:
      		    	 AlertDialog.Builder builder_2 = new AlertDialog.Builder(getActivity());
      		    	 builder_2.setTitle(list_highest.get(position_2).get_name());
      		    	 DatabaseHandler db=new DatabaseHandler(getActivity().getApplicationContext());
      				 List<recommended_user>list=db.get_usersFromApp(list_highest.get(position_2).get_id());
      		    	 final friends_recommendAdapter adapter = new friends_recommendAdapter(getActivity().getApplicationContext(),list);
      		    	 builder_2.setAdapter(adapter,new DialogInterface.OnClickListener() {

                         @Override
                         public void onClick(DialogInterface dialog, int which) {
                            
                         }
                     });
             builder_2.show();
      		    	 //Intent intent1 = new Intent(); 
      				 //intent1.setClass(getActivity().getApplicationContext(), FriendsRecommendations.class);
      				 //Bundle b = new Bundle();
      		         //b.putInt("id", list.get(position_2).get_id());
      		         //b.putString("app_name", list.get(position_2).get_name());
      		         //b.putString("icon", list.get(position_2).getIcon());
      		         //intent1.putExtras(b);
      				 //startActivity(intent1);
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
        return rootView;
    }
    
}