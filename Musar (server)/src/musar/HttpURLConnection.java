package musar;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
 
public class HttpURLConnection {
 
	private final String USER_AGENT = "Mozilla/5.0";
 

 
	// HTTP GET request
	public String sendGet(String url,String type_request) throws Exception {
 
	
 
		URL obj = new URL(url);
		URLConnection con =  obj.openConnection();
 
		// optional default is GET
		((java.net.HttpURLConnection) con).setRequestMethod("GET");
 
		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);
 
		int responseCode = ((java.net.HttpURLConnection) con).getResponseCode();
		//System.out.println("\nSending 'GET' request to URL : " + url);
		//System.out.println("Response Code : " + responseCode);
		BufferedReader in =null;
 try{
		in= new BufferedReader(
		        new InputStreamReader(con.getInputStream()));}
 catch (Exception e) {return "unknown";}
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
 
		//print result
		//System.out.println(response.toString());
		 String response_json="{\"category\":"+response.toString()+"}";
		
		JSONObject object = new JSONObject(response_json);
		JSONArray results = object.getJSONArray("category");
		String return_request=null;
		
		for (int i=0;i<results.length();i++) {
			if(type_request=="category")
		  return_request=results.getJSONObject(i).getString("catApplus_cat");
			else {return_request=results.getJSONObject(i).getString("catApplus_name");}
		//  System.out.println(category);
			  }
        return return_request;
	}
 
 
}