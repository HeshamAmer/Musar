package musar;



import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.X509Certificate;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class AppRecommender {
	private List<user> userFriends = new ArrayList<user>();
	private Map<apps_recommender,List<user>> maping_users=new HashMap<apps_recommender,List<user>>();
	connecting c;
	boolean flag=false;
	public AppRecommender() throws ClassNotFoundException, SQLException {
		c=new connecting();
		
	}
	public Map<apps_recommender,List<user>> get_maping_users(){return maping_users;}
	/**
	 * @param A
	 *            Target user.
	 * @return A list of user friend to A
	 */
	public List<user> getFriends(user A) {
		
		int used_app=1;
		
		String sql = "SELECT contact_name,friend_id,image,_id from friends where user_id= '"+ A.get_id()+"' and used_app= "+used_app;
		String [][]result =  c.Conn(sql, false);
		System.out.println("friends length:"+result.length);
		A.set_user_friend(new ArrayList<user>());
		for (int i=0;i<result.length;i++) {
			String name=result[i][0];//contact name
			String friend_id=result[i][1];
			String image=result[i][2];
			String id=result[i][3];
			//get the type of my friend
			sql = "SELECT category from users where user_id= "+ friend_id;
			String [][]result_1 =  c.Conn(sql, false);
			String category_friend=result_1[0][0];
			user new_user=new user(name,category_friend,friend_id,image);
			System.out.println("friend:"+name+"id :"+id);
			A.get_friend().add(new_user);
		}
		// return A.userFriends;
		return A.get_friend();
	}

	


	// Dah cluster model based collaborative filtering
	public String getUserTypes(user TargetUser) {
		
		// Here i want to classify each user to a category based on the most used apps.
		Map<apps_recommender,Double> MUA = getMostUsedApps(TargetUser);
		Map<String, Integer> M = new TreeMap<String, Integer>();
		for(apps_recommender App:MUA.keySet())
		{
			System.out.println("Application Type :"+App.get_type());
			if (!M.containsKey(App.get_type())){
				M.put(App.get_type(), 1);
			} else if (M.containsKey(App.get_type())) {
				M.put(App.get_type(), M.get(App.get_type()) + 1);
			}
			
		}
		String Type = printMap(M,MUA); //a3la value , ya3ny a3la application name
		System.out.println("final type :"+Type);
		return Type;

	}

	public static String printMap(Map<String, Integer> M,Map MUA) {
		
		Iterator it = MUA.entrySet().iterator();
		String cluster=null;
		double Max=0;
		if(it.hasNext()){
			Map.Entry WantedPair=(Map.Entry) it.next();
			cluster=((apps_recommender)WantedPair.getKey()).get_type();
			System.out.println("cluster:"+cluster);
			Max=((double)WantedPair.getValue());
		while (it.hasNext()) {
			
			Map.Entry pairs = (Map.Entry) it.next();
			double integer=(double)pairs.getValue();
			System.out.println("duration:"+integer);
			if ((double)pairs.getValue()>Max){
				Max=(double) pairs.getValue();
				WantedPair=pairs;
				cluster=((apps_recommender)WantedPair.getKey()).get_type();
			}
			else if((double)pairs.getValue()==Max)
			{
				apps_recommender apps_recommender=(apps_recommender)pairs.getKey();
				apps_recommender apps_recommend=(apps_recommender)WantedPair.getKey();
				int repeating_1=0;
				int repeating_2=0;
				for(String app_1:M.keySet())
				{
					if(app_1.equalsIgnoreCase(apps_recommender.get_package()))
					{
						repeating_1=M.get(app_1);
					}
					else if(app_1.equalsIgnoreCase(apps_recommend.get_package()))
					{
						repeating_2=M.get(app_1);
					}
				}
				if(repeating_1>repeating_2){cluster=apps_recommender.get_type();}
				else cluster=apps_recommend.get_type();
			}

		}
		return cluster;
	}
		else return "unknown";//if the map if empty
	}
	/**
	 * @param A
	 *            : The user i'm getting the similarity for
	 * @param Friends
	 *            : The users's friend cycle
	 * @return A list of all users who are similar to A
	 */
	static public List<user> getSimilarities(user A, List<user> Friends) {
		// Ana hena 3andy around 10 users maslan , each of them has 1 category
		// (Profiled based on the most used apps ) ,
		// 3ayez ashoof el user elly m3aya dah , similar to which one of them
		// then return a list of the similar users out of the list of his
		// friends
		List<user> SimilarFriends = new ArrayList<user>();
		for (user B : Friends) {
			System.out.println("friend type:"+B.get_type());
			System.out.println("me type:"+A.get_type());
			if (A.get_type().equals(B.get_type())) {
				SimilarFriends.add(B);
			}
		}
		
		return SimilarFriends;
	}

	/**
	 * 
	 * @param A
	 */

	public Map<apps_recommender,Double> getMostUsedApps(user x) {
		//Query the database to return the most used apps for a certain user ordered by duration
		String sql = "SELECT app_name,category,duration from most_apps where most_apps.user_id= '"+ x.get_id()+"' ORDER BY duration";
		String [][] result =  c.Conn(sql, false);
		Map<apps_recommender,Double>MUA=new HashMap<apps_recommender,Double>();
		for(int i=0;i<result.length&&i<10;i++)//max 10 item for example why we need to make anding condition because if the user has apps <10
		{
			//System.out.println("app_name:"+i+"most used apps");
			String app_package=result[i][0];
			String category_app=result[i][1];
			Double duration=Double.parseDouble(result[i][2]);
			MUA.put(new apps_recommender(app_package,null,category_app),duration);//app name =null but package =app name
		}
		return MUA;
	}

	public List<apps_recommender> getInstalledApps(user x) {
		// Query the database to return the most used apps for a certain user
		String sql = "SELECT installed_app from installed_app where installed_app.user_id= '"+ x.get_id()+"'";
	    String [][]result =  c.Conn(sql, false);
	      
		List<apps_recommender> InstalledApps = new ArrayList<>();
		for(int i=0;i<result.length;i++)
		{
			InstalledApps.add(new apps_recommender(result[i][0]));
		}
		return InstalledApps;
	}

	public Map<apps_recommender,Double> recommend(most_apps data,user A, List<user> simFriends) throws NumberFormatException, Exception {
		Map<apps_recommender,Double> recommendedAppList = new HashMap<apps_recommender,Double>();
		// Hena ana 3ayez ageeb el Most used apps 3and each of SimFriend , check
		// if it is already at A
		// If not at A , add it to recommended list .
		 Map<apps_recommender,Double>apps_targetUser=new HashMap<apps_recommender,Double>();
		 apps_targetUser=getMostUsedApps(A);
		 List<apps_recommender> InstalledApps = getInstalledApps(A); 
		//if we have similar users 0 or we don't have friends
		 System.out.println("sim_friends:"+simFriends.size());
		if(simFriends.size()==0)
		{
			if(apps_targetUser.size()!=0)
			{//recommend with category of user
				RecommendData(data,A,InstalledApps,recommendedAppList, true);
			}
			else //recommender with rate
			{
			    RecommendData(data,A,InstalledApps,recommendedAppList, false);
			}
		}
		//////////////////////////////////using another similarity between users in same category//////////////////////////////////////
		else //there are trusted friends
		{
		 Map<user,Double>sim_list=new HashMap<user,Double>();
		  //calculate the similarities
		  sim_list=calculate_similarties_users(data,A,apps_targetUser,simFriends,InstalledApps,recommendedAppList);
		  //calculate the prediction items
		  if(flag==false)
		  {//if there are recommendations with friends we need to predict the items otherwise we don't need to predict them
			  for(apps_recommender app:recommendedAppList.keySet())
			  {
				  Double predict=prediction_item(app,A,sim_list);
				  recommendedAppList.put(app,predict);
			  }
		  }
		}
		//sorting the apps recommended :D
		recommendedAppList = sortByValue(recommendedAppList);

		return recommendedAppList;
	}
 private Map<user,Double>calculate_similarties_users(most_apps data,user A,Map<apps_recommender,Double>apps_targetUser,List<user>simFriends,List<apps_recommender>InstalledApps,Map<apps_recommender,Double> recommendedAppList) throws Exception
	{
		System.out.println("calculate the similarities");
		int userCount = 0;
		 Map<user,Double>sim_list=new HashMap<user,Double>();
		 List<String> setApps = new ArrayList<String>();
		for (user B : simFriends) {			
			  Map<apps_recommender,Double>apps_friend=new HashMap<apps_recommender,Double>();
			  apps_friend=getMostUsedApps(B);
			  Double sim=0.0;
			  sim=calcutate_similarities(apps_friend,apps_targetUser);
			  System.out.println("sim"+sim);
			  sim_list.put(B, sim);
			 
				for (apps_recommender App : apps_friend.keySet()) {
					String app_name=check_unknown(App.get_package());
					if(!app_name.equals("unknown")&&!check_last_recommend(app_name,data.get_user_recommended())){
						
						if (!has(InstalledApps, App)) {
						if (userCount < 2) {
							apps_recommender app_2=new apps_recommender(App.get_package(),app_name,A.get_type());
							if(!setApps.contains(app_name))
							{
							   recommendedAppList.put(app_2, 0.0);
							   setApps.add(app_name);
							}
							userCount = (userCount + 1) % 3;
							add_user_ToApp(app_2,B,maping_users);
							
						}
						if (userCount >= 2) {
							userCount = 0;
							break;// Jump to the next user 5las
						}
					}
				}
				}
			}
		// if the recommender app list=0 there is no apps from friends because I have all of them or my friends have not any apps to recommend
		System.out.println("recommended_app"+recommendedAppList.size());
		if(recommendedAppList.size()==0)
		{
			// if I have my apps I need recommender same category
			flag=true;
			if(apps_targetUser.size()!=0)
			{
				RecommendData(data,A,InstalledApps,recommendedAppList, true);
			}
			else //normal recommend
			{
				RecommendData(data,A,InstalledApps,recommendedAppList, false);
			}
		}
		return sim_list;
	}
 private void RecommendData(most_apps data,user A,List<apps_recommender>InstalledApps,Map<apps_recommender,Double> recommendedAppList,boolean flag) throws Exception
 {String myQuery23=null;
	 if(flag ==false)
	  myQuery23 = "SELECT rate,app_name,number_Downloads from apps_dataset ORDER BY number_Downloads";
	 else 
      myQuery23 = "SELECT rate,app_name,number_Downloads from apps_dataset where apps_dataset.category= '"+ A.get_type()+"' ORDER BY number_Downloads";
		
	 String [][] result =  c.Conn(myQuery23, false);
		int number_apps_recommended=0;
		Set<String> setApps = new HashSet<String>();

		for(int i=result.length-1;i>=0;i--)
		{
		  String app_name=check_unknown(result[i][1]);
		  if(!setApps.contains(app_name)&&!app_name.equals("unknown")&&!check_last_recommend(app_name,data.get_user_recommended()))
		  {
			apps_recommender app_1=new apps_recommender(result[i][1],null,A.get_type());//package name ok and app name=null
			if(!has(InstalledApps, app_1))
			{//it must be different app from his installed apps
				apps_recommender app=new apps_recommender(app_1.get_package(),app_name,A.get_type());
				System.out.println("apps recommended name:"+app.get_name()+" and package :"+app.get_package());
				recommendedAppList.put(app, Double.parseDouble(result[i][2]));
				user B=new user("unknown friend","unknown type","unknown id","unknown image");
				add_user_ToApp(app,B,maping_users);
				number_apps_recommended++;
				setApps.add(app_name);
			}
			if(number_apps_recommended==10)break;
		  }
		}
 }
 
 
	static private Double calcutate_similarities(Map<apps_recommender,Double>friends,Map<apps_recommender,Double>targetuser)
	{
		System.out.println("calculate similarities_2 fun");
		Double sim=0.0;
		double mean_target=calculate_mean(targetuser);
		double mean_friend=calculate_mean(friends);
		List<Double>rank_u=new ArrayList<Double>();//target user
		List<Double>rank_v=new ArrayList<Double>();//friend
		for(apps_recommender target :targetuser.keySet())
		{
			for(apps_recommender friend:friends.keySet())
			{
				if(target.get_package().equals(friend.get_package()))
				{
					rank_u.add(targetuser.get(target));
					rank_v.add(friends.get(friend));
					break;//if we insure that it will be unique we don't need break
				}
			}
		}
		double sum=0.0;
		double target_square=0.0;
		double friend_square=0.0;
		for(int i=0;i<rank_u.size();i++)
		{
			sum+=(rank_u.get(i)-mean_target)*(rank_v.get(i)-mean_friend);
			target_square=Math.pow((rank_u.get(i)-mean_target), 2.0);
			friend_square=Math.pow((rank_v.get(i)-mean_friend), 2.0);
		}
		if((target_square*friend_square)!=0)
		  sim=sum/(target_square*friend_square);
		return sim;
	}
 private double prediction_item(apps_recommender app,user A,Map<user,Double>sim)
	{
		List<user>friends=new ArrayList<user>();
		friends=A.get_similar();
		Map<apps_recommender,Double>apps_target=new HashMap<apps_recommender,Double>();
		apps_target=getMostUsedApps(A);
		Double mean_target=calculate_mean(apps_target);
		double predict=mean_target;
		double num=0.0;
		double sum=0.0;
		 for (user B : friends)
		 {
			 Map<apps_recommender,Double>apps_friend=new HashMap<apps_recommender,Double>();
			 apps_friend=getMostUsedApps(B);
			 double duration=0;
			 if(check_exist(app,apps_friend,duration))
			 {
				Double friend_mean=calculate_mean(apps_friend);
				Double similarity=get_sim(B,sim);
				num+=similarity+(duration-friend_mean);
				sum+=similarity;
			 }
		 }
		 if(sum!=0){predict+=num/sum;}
		 return predict;
	}
	static private boolean check_exist(apps_recommender app,Map<apps_recommender,Double>maping,double duration)
	{
		Iterator it = maping.entrySet().iterator();
		String cluster=null;
		if(it.hasNext()){
			Map.Entry WantedPair=(Map.Entry) it.next();
			if (((apps_recommender)WantedPair.getKey()).get_package().equals(app.get_package())){
				duration=(double)WantedPair.getValue();
				return true;
			}
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			if (((apps_recommender)pairs.getKey()).get_package().equals(app.get_package())){
				duration=(double)pairs.getValue();
				return true;
			}}
		}
		return false;
	}
	static private Double get_sim(user friend,Map<user,Double>maping)
	{
		for(user user_1:maping.keySet())
		{
			if(user_1.get_name().equals(friend.get_name()))
			{
				return maping.get(user_1);
			}
		}
		return 0.0;
	}
	static private double calculate_mean(Map<apps_recommender,Double>maping)
	{
		Iterator it = maping.entrySet().iterator();
		if(it.hasNext()){
		Map.Entry WantedPair=(Map.Entry) it.next();
		double mean=0.0;
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			mean+=(double)pairs.getValue();
		}
		return mean;
		}
		else return 0.0;
	}
	/**
	 * 
	 * @param L
	 * @param a
	 * @return true if L contains A , else return false
	 */
	static boolean has(List<apps_recommender> L, apps_recommender a) {
		for (int i = 0; i < L.size(); i++) {
			if (L.get(i).get_package().equals(a.get_package()))
				return true;
		}
		return false;

	}
	private static void add_user_ToApp(apps_recommender app,user B,Map<apps_recommender,List<user>>maping_users)
	{
		// the app is found just add new user
	
		List<user>user=new ArrayList<user>();
		if (check_exist_user(app,maping_users,user,B))
		{
			System.out.println("app exist:"+app.get_name()+"new user:"+B.get_name());
			
			//user.add(B);
			//maping_users.put(app, user);
		}
		else //new app
		{
			
			user.add(B);
			maping_users.put(app, user);
		}
	}
	static private boolean check_exist_user(apps_recommender app,Map<apps_recommender,List<user>>maping_user,List<user>user,user B)
	{
		
		Iterator it = maping_user.entrySet().iterator();
		if(it.hasNext()){
			Map.Entry WantedPair=(Map.Entry) it.next();
			
			if (app.get_package().equals(((apps_recommender)WantedPair.getKey()).get_package())){
				user=(List<user>)WantedPair.getValue();
				System.out.println("userssssssssssssssssssssss:"+user.size());
				((List<user>)WantedPair.getValue()).add(B);
				System.out.println("userssssssssssssssssssssss:"+user.size());
				return true;
			}
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();	
			
			if (app.get_package().equals(((apps_recommender)pairs.getKey()).get_package())){
				WantedPair=pairs;
				user=(List<user>)pairs.getValue();
				((List<user>)WantedPair.getValue()).add(B);
				System.out.println("userssssssssssssssssssss:"+user.size());
				return true;
			}

		}
		return false;
	}
	else return false;//if the map if empty
	
	}
	private String check_unknown(String app) throws Exception
	{
		HttpURLConnection url=new HttpURLConnection();
		String app_name=url.sendGet("http://api.ergsap.com/catapplus/catapplus_ind.php?catApplus_pn="+app,"app_name");
		return app_name;
	}
	private boolean check_last_recommend(String app_name,ArrayList<String>apps_recommended)
	{
		
		if(apps_recommended.contains(app_name)){System.out.println("check last recommend:"+app_name);return true;}
		return false;
	}
	private static Map sortByValue(Map unsortMap) {
		 
		List list = new LinkedList(unsortMap.entrySet());
 
		// sort list based on comparator
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
			}
		});
 
		// put sorted list into map again
                //LinkedHashMap make sure order in which keys were inserted
		Map sortedMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
 
	

	
}
