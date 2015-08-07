package com.musar.gui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.musar.Database.Application;
import com.musar.Database.DatabaseHandler;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class MostFragment extends Fragment {

	public MostFragment() {
	}

	private RecentlyUsedAppAdapdter listadaptor = null;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_most, container,
				false);

		// setContentView(R.layout.least_apps);
		
		List<String> recentTasks = new ArrayList<String>();
		recentTasks = getMostUsedApps();
		listadaptor = new RecentlyUsedAppAdapdter(getActivity(),
				R.layout.snippet_list_row, recentTasks);
		ListView listview = (ListView) rootView.findViewById(R.id.listview);
		listview.setAdapter(listadaptor);
		return rootView;
	}

	public List<String> getMostUsedApps() {
		DatabaseHandler db = new DatabaseHandler(getActivity());
		List<Application> topApps = db.getAllApps();
		topApps = db.getAllAppsSorting();
		// Collections.reverse(topApps);
		if (topApps == null) {
			topApps = new ArrayList<Application>();
		}
		List<String> topAppsNames = new ArrayList<String>();
		Set<String> s = new HashSet<String>();
		int last = 0;
		if (topApps.size() > 20)
			last = topApps.size() - 20;
		for (int i = topApps.size() - 1; i >= last; i--) {
			if (!s.contains(topApps.get(i).getName())) {
				s.add(topApps.get(i).getName());
				topAppsNames.add(topApps.get(i).getName());
			}
		}
		return topAppsNames;
	}
}