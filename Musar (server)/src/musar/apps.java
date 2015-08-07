package musar;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.mysql.jdbc.PreparedStatement;
import com.sun.jersey.core.util.Base64;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

// Plain old Java Object it does not extend as class or implements 
// an interface

// The class registers its methods for the HTTP GET request using the @GET annotation. 
// Using the @Produces annotation, it defines that it can deliver several MIME types,
// text, XML and HTML. 

// The browser requests per default the HTML MIME type.

//Sets the path to base URL + /hello
@Path("/apps")
public class apps {
	


  @POST
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes(MediaType.APPLICATION_JSON)

  public String get_userapps(String apps_string) throws Exception {
	  JSONObject object = new JSONObject(apps_string);
	  most_apps apps=new most_apps();
	  apps.set_id(object.getString("user"));
	  System.out.println(apps.get_id());
	  int size=object.getInt("size_activity");
	  System.out.println("size activity :"+size);
	  ArrayList<AppActivity>activity=new ArrayList<AppActivity>();
	  for(int i=0;i<size;i++)
	  {
		String app_name=object.getString("activity"+i);
		System.out.println("activity:"+app_name);
		String app_duration=object.getString("duration"+i);
		System.out.println("duration:"+app_duration);
		Integer du=Integer.parseInt(app_duration);
		AppActivity p=new AppActivity();
		p.setAppName(app_name);
		p.setDuration(du);
		activity.add(p);
	  }
	  int size_apps=object.getInt("size_apps");
	  ArrayList<String>apps_recommended=new ArrayList<String>();
	  for(int i=0;i<size_apps;i++)
	  {
		String app_name=object.getString("apps_lastRecommended"+i);
		apps_recommended.add(app_name);
	  }
	  apps.set_userActivity(activity);
	  apps.set_user_recommended(apps_recommended);
	  ////////////////////////////////////////////////////////////////////////////////////////////////
	  connecting c=new connecting();
	  String myQuery23 = "SELECT _id,app_name from most_apps where most_apps.user_id= '"+ apps.get_id()+"'";
	  String [][] result =  c.Conn(myQuery23, false);
	  ArrayList<String>result_apps=new ArrayList<String>();
	  for(int i=0;i<result.length;i++){result_apps.add(result[i][1]);}
	  //there is no user called this before so we need to insert his data
	  if (result.length == 0)
	  {
		   //insert
		  for(int i=0;i<apps.get_activity().size();i++){
			  HttpURLConnection url=new HttpURLConnection();
			  String category_app=url.sendGet("http://api.ergsap.com/catapplus/catapplus_ind.php?catApplus_pn="+ apps.get_activity().get(i)._appName,"category");
			  String Query = "INSERT INTO most_apps (user_id,app_name,category,duration) VALUES ('"+apps.get_id()+"','"+apps.get_activity().get(i)._appName+"','"+category_app+"',"+apps.get_activity().get(i)._duration+")"; 
			  String [][] result2 = c.Conn(Query, true);
		  }
	  }
	  else //found before 
	  {
		 for(int i=0;i<apps.get_activity().size();i++)
		 {
			 //this app is found before(update the duration)
			 if(result_apps.contains(apps.get_activity().get(i)._appName))
			 {
				 String sql = "UPDATE most_apps SET duration = "+apps.get_activity().get(i)._duration+ " WHERE user_id = '"+apps.get_id()+"'"+"and app_name= '"+apps.get_activity().get(i)._appName+"'";
				 result =  c.Conn(sql, true);
				 
			 }
			 //insert new app
			 else 
			 {
				HttpURLConnection url=new HttpURLConnection();
				String category_app=url.sendGet("http://api.ergsap.com/catapplus/catapplus_ind.php?catApplus_pn="+ apps.get_activity().get(i)._appName,"category");
				String Query = "INSERT INTO most_apps (user_id,app_name,category,duration) VALUES ('"+apps.get_id()+"','"+apps.get_activity().get(i)._appName+"','"+category_app+"','"+apps.get_activity().get(i)._duration+"')"; 
				String [][] result2 = c.Conn(Query, true);
				
			 }
		 }
	  }
	  ///////////////////////////////////now we can recommend :D////////////////////////////////////////////////////////
	  //first we need to categorize the user again
	  AppRecommender recommender=new AppRecommender();
	  user target_user=new user(apps.get_id(),"unknown");
	  String user_category=recommender.getUserTypes(target_user);
	  String sql = "UPDATE users SET category = '"+user_category+ "' WHERE user_id = '"+apps.get_id()+"'";
	  result =  c.Conn(sql, true);
	  target_user.set_type(user_category);
	  System.out.println("user_id:"+target_user.get_id());
	  //now we can recommend
	  target_user.set_user_friend(recommender.getFriends(target_user));
	  
	  target_user.set_similarfriend(recommender.getSimilarities(target_user,target_user.get_friend()));
	  Map<apps_recommender,Double> T=new HashMap<apps_recommender,Double>();
	  T = recommender.recommend(apps,target_user, target_user.get_similar());
	 
	  Map<apps_recommender,List<user>>maping_user=new HashMap<apps_recommender,List<user>>();
	  maping_user=recommender.get_maping_users();
	  //change the app package to app name normal
	  String return_result=jsonString(apps,T,maping_user);
	  return return_result; 
  }
  
public String jsonString(most_apps apps, Map<apps_recommender,Double> T, Map<apps_recommender,List<user>>maping_user) throws JSONException {
	JSONObject obj2 = new JSONObject(); 
	obj2.put("user", apps.get_id());
	obj2.put("size_map_recommended",T.size());
	obj2.put("size_maping_users", maping_user.size());
	Iterator it = T.entrySet().iterator();
	int i=0;
	while (it.hasNext()) {
		Map.Entry pairs = (Map.Entry) it.next();
		String name=((apps_recommender)pairs.getKey()).get_name();
		String package_name=((apps_recommender)pairs.getKey()).get_package();
		//String url=((apps_recommender)pairs.getKey()).get_url();
		name=name.replaceAll(" ", ",");//no white_spaces here
		obj2.put("recommended_app_name"+i, name);
		obj2.put("app_recommend_package"+i, package_name);
		obj2.put("app_recommended_rate"+i, ((Double)pairs.getValue()));
	//	obj2.put("url"+i, url);
		i++;
		}
	int j=0;
	Iterator it_2 = maping_user.entrySet().iterator();
	while (it_2.hasNext()) {
		Map.Entry pairs = (Map.Entry) it_2.next();
		String name=((apps_recommender)pairs.getKey()).get_name();
		String package_name=((apps_recommender)pairs.getKey()).get_package();
		name=name.replaceAll(" ", ",");
		obj2.put("recommended_app_name_user"+j,name );
		obj2.put("recommended_app_package_user"+j,package_name );
		List<user>user=new ArrayList<user>();
		user=((List<user>)pairs.getValue());
		obj2.put("size_users"+j, user.size());
		for(int x=0;x<user.size();x++)
		{
			String name_user=user.get(x).get_name();
			name_user=name_user.replaceAll(" ", ",");//no white spaces in jason here
			obj2.put("user_name"+j+"user_number"+x,name_user);
			obj2.put("user_image"+j+"user_number"+x, user.get(x).get_image());
		}
		j++;
		}
	return obj2.toString();
} 
private String check_unknown(String app) throws Exception
{
	HttpURLConnection url=new HttpURLConnection();
	String app_name=url.sendGet("http://api.ergsap.com/catapplus/catapplus_ind.php?catApplus_pn="+app,"app_name");
	System.out.println("check_unknown"+app_name);
	return app_name;
}
	
} 
