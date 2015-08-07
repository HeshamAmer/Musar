package com.musar.gui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.musar.Database.Application;
import com.musar.Database.DatabaseHandler;
import android.app.Fragment;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class LeastFragment extends Fragment {

	public LeastFragment() {
	}

	
	
	private RecentlyUsedAppAdapdter listadaptor = null;
	private PackageManager packageManager;
	private List<String> applist = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_most, container,
				false);

		// setContentView(R.layout.least_apps);
		List<String> recentTasks = new ArrayList<String>();
		packageManager = getActivity().getPackageManager();
		applist = checkForLaunchIntent(packageManager
				.getInstalledApplications(PackageManager.GET_META_DATA));
		recentTasks = getLeastUsedApps();
		listadaptor = new RecentlyUsedAppAdapdter(getActivity(),
				R.layout.snippet_list_row, recentTasks);
		ListView listview = (ListView) rootView.findViewById(R.id.listview);
		listview.setAdapter(listadaptor);
		return rootView;
	}

	public List<String> getLeastUsedApps() {
		
		DatabaseHandler db = new DatabaseHandler(getActivity());
		List<Application> topApps = db.getAllApps();
		topApps = db.getAllAppsSorting();
		if (topApps == null) {
			topApps = new ArrayList<Application>();
		}
		// ////////////get not used apps from the installed apps
		List<String> installed = applist;
		List<String> AllApps = NotUsed_apps(installed, topApps);
		Set<String> s = new HashSet<String>();
		for (int i = 0; i < topApps.size() && AllApps.size()<=20; i++) {
			if (!s.contains(topApps.get(i).getName())) {
				s.add(topApps.get(i).getName());
				AllApps.add(topApps.get(i).getName());
			}
		}
		return AllApps;
	}

	private List<String> NotUsed_apps(List<String> installed,
			List<Application> apps) {
		List<String> NotUsed = new ArrayList<String>();
		for (int j = 0; j < installed.size(); j++) {
			boolean flag = false;
			if(installed.get(j).equals("com.musar.gui"))
				continue;
			for (int i = 0; i < apps.size(); i++) {
				if (apps.get(i).getName().equals(installed.get(j))) {
					flag = true;
					break;
				}
			}
			if (!flag)// not used
			{
				NotUsed.add(installed.get(j));
			}
		}
		return NotUsed;
	}

	private List<String> checkForLaunchIntent(List<ApplicationInfo> list) {
		ArrayList<String> applist = new ArrayList<String>();
		
		for (ApplicationInfo info : list) {
			try {
				if (null != packageManager
						.getLaunchIntentForPackage(info.packageName)) {
					applist.add(info.packageName);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return applist;
	}
}