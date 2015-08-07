package com.musar.gui;
 


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
 
public class EmptyHomeFragment extends Fragment {
     
    public EmptyHomeFragment(){}
     
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
  
        View rootView = inflater.inflate(R.layout.home_empty, container, false);
        return rootView;
    }
    
}