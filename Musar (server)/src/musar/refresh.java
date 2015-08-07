package musar;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
@Path("/refresh")
public class refresh {
	private refresh_data data=new refresh_data();
  
  public void get_userData(String data_string) throws JSONException
  {
	  JSONObject object = new JSONObject(data_string);
	  
	  data.set_id(object.getString("user"));
	  int size_contact=object.getInt("size_contact");
	  ArrayList<Contact>contact=new ArrayList<Contact>();
	  
	  for(int i=0;i<size_contact;i++)
	  {
		  Contact c=new Contact();
		  c.setName(object.getString("contact_name"+i));
		  c.setPhoneNumber(object.getString("contact_phone"+i));
		  c.setImage(object.getString("image_friend"+i));
		  contact.add(c);
	  }
	  data.set_contacts(contact);
	  int size_installed=object.getInt("size_installed");
	  ArrayList<String>installed=new ArrayList<String>();
	  for(int i=0;i<size_installed;i++)
	  {
		  installed.add(object.getString("installed_name"+i));
	  }
	  data.set_installedApps(installed);
  }
  @POST
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes(MediaType.APPLICATION_JSON)
  public String get_userdata(String data_string) throws Exception {
	  connecting c=new connecting();
	  get_userData(data_string);
	  hash hashing=new hash();
	  iaik.security.provider.IAIK.addAsProvider(true);
////////////////////////////////////installed apps///////////////////////////////////////////////////////////////////////////////////
      String myQuery23 = "SELECT user_id from installed_app where installed_app.user_id= '"+ data.get_id()+"'";
      String [][]result =  c.Conn(myQuery23, false);
      if(result.length==0)//insert
        {
    	  	//insert
    	  	for(int i=0;i<data.get_installed().size();i++){
    	  		String Query = "INSERT INTO installed_app (user_id,installed_app) VALUES ('"+data.get_id()+"','"+data.get_installed().get(i)+"')"; 
    	  		String [][] result2 = c.Conn(Query, true);
    	  		System.out.println("insert installed apps first time");
    	  	}
        }
      else //check if there is change in installed apps
      {
    	  update_installed_apps(c,data);
      }
      

//////////////////////////////////////contacts///////////////////////////////////////////////////////////////////////////////////
      myQuery23 = "SELECT user_id from friends where friends.user_id= '"+ data.get_id()+"'";
      result =  c.Conn(myQuery23, false);
      if(result.length==0)//insert
      	{
    	  	//insert
    	  	//very important note: friends table have friends numbers only not the user also so in recommender algo we need to get the true used app friends then search about them in users
    	  	for(int i=0;i<data.get_contact().size();i++){
    	  		insert_contact(c,data,i,hashing);
    	  	}
      	}
      else //check if there is change in contacts
      	{
    	  	update_contacts(c,data,hashing);
      	}

//////////////////////////////////normalize the most used apps/////////////////////////////////////////////////////////////////
    //first calculate mean then standard deviation
			 System.out.println("normalize the most used apps");
             Map<Double,Double>maping=new HashMap<Double,Double>();
			 maping =calculate_mean_standard(c,data);
			 Double mean=get_key_2(maping);
			 Double standard=maping.get(mean);
			 normalize(c,data,standard,mean);

	 return data.get_id();
 }
  ///////////////////////////////////////////////////apps db/////////////////////////////////////////////////////////////////////
  private void normalize(connecting c,refresh_data data,Double standard,Double mean)
  {
	  String myQuery23 = "SELECT duration,app_name from most_apps where most_apps.user_id= '"+ data.get_id()+"'";
      String [][]result =  c.Conn(myQuery23, false);
      for(int i=0;i<result.length;i++)
      {
    	  Double normalize=Double.parseDouble(result[i][0]);
    	  if(standard!=0)
    		  normalize=(Double.parseDouble(result[i][0])-mean)/standard;
    	  if(normalize<0)//delete this app it is not the most used apps now !
    	  {
    		  System.out.println("delete the least used apps from db");
    		  myQuery23 ="DELETE FROM most_apps WHERE most_apps.app_name='"+result[i][1]+"'and user_id= '"+data.get_id()+"'";
 			 String [][] result_2 =  c.Conn(myQuery23, true);
    	  }
    	  else
    	  {
    		  System.out.println("update the most used apps with normalization");
    		  String sql = "UPDATE most_apps SET duration = "+normalize+ " WHERE user_id = '"+data.get_id()+"'and app_name= '"+result[i][1]+"'";
		     result =  c.Conn(sql, true);
    	  }
      }
  }
  
  
  private Map<Double,Double> calculate_mean_standard(connecting c,refresh_data data)
  {
	  String myQuery23 = "SELECT duration from most_apps where most_apps.user_id= '"+ data.get_id()+"'";
      String [][]result =  c.Conn(myQuery23, false);
      double sum=0;
      for(int i=0;i<result.length;i++)
      {
    	  sum+=Double.parseDouble(result[i][0]);
      }
      System.out.println("sum"+sum);
      Double mean=sum/result.length;
      sum=0;
      for(int i=0;i<result.length;i++)
      {
    	  sum+=Math.pow(Double.parseDouble(result[i][0])-mean, 2.0);
      }
      Double standard=sum/result.length;
      standard=Math.sqrt(standard);
      Map<Double,Double>maping =new HashMap<Double,Double>();
      System.out.println("mean"+mean);
      System.out.println("standrd"+standard);
      maping.put(mean, standard);
      return maping;
  }
  

  private Double get_key_2(Map<Double,Double>maping)
  {
	  for(Double key:maping.keySet())
	  {
		  return key;
	  }
	  return null;
  }
  private String get_key(Map<String,String>maping)
  {
	  for ( String key : maping.keySet() ) {
		    return key;
		}
	  return null;

  }
  /////////////////////////////////////////////////////////////friends db////////////////////////////////////////////////////////////////////
 private Map<String,String> get_phone_hash(String phone_contact,hash hashing) throws NoSuchAlgorithmException, UnsupportedEncodingException, NoSuchProviderException
 {
	 // Salt generation 64 bits long
	 SecureRandom random=null;
	 random = SecureRandom.getInstance("SHA1PRNG");
     byte[] bSalt = new byte[8];
     random.nextBytes(bSalt);
     byte[] bDigest= hashing.getHash(100,phone_contact,bSalt);
 	 String sDigest = hashing.byteToBase64(bDigest);
     String sSalt = hashing.byteToBase64(bSalt);
     Map <String,String >maping=new HashMap<String,String>();
     maping.put(sDigest, sSalt);
     return maping;
 }
 private String check_phone_exist(connecting c,String phone_contact,hash hashing) throws Exception
 {
	 
	  String myQuery23 = "SELECT phone_number,salt,user_id from users";
	  String [][] result =  c.Conn(myQuery23, false);
	  for(int i=0;i<result.length;i++)
	  {
		  
		  String digest = result[i][0];
	      String salt = result[i][1];
	      byte[] bDigest = hashing.base64ToByte(digest);
	      byte[] bSalt = hashing.base64ToByte(salt);
	      // Compute the new DIGEST
	      byte[] proposedDigest = hashing.getHash(100, phone_contact, bSalt);
	      if( Arrays.equals(proposedDigest, bDigest))
	      {System.out.println("doneeeeeeeeeeeeeeeeeeee:"+result[i][2]);
	      String user_id=result[i][2];
	         return user_id;
	      }
	      
	  }
	  return null;

 }
 private boolean check_phone_old(String phone_hashing,connecting c,refresh_data data,String salt,String name,String image,hash hashing) throws Exception
 {
	 byte[] bDigest = hashing.base64ToByte(phone_hashing);
	 byte[] bSalt = hashing.base64ToByte(salt);
	 for (int i=0;i<data.get_contact().size();i++)
	 {
		  // Compute the new DIGEST
	      byte[] proposedDigest = hashing.getHash(100, data.get_contact().get(i)._phone_number, bSalt);
	      if( Arrays.equals(proposedDigest, bDigest))
	      {
	    	//the number is found in db and contact check if the used app has changed "this check is important if the contact changed from 0 to 1"
	    	 String friend_id=check_phone_exist(c,data.get_contact().get(i)._phone_number,hashing);
	    	if(friend_id!=null)
	  		{
	  			System.out.println("update the used_app status");
	  			int used_app=1;
	  			String sql = "UPDATE friends SET used_app = "+used_app+ " , friend_id = '"+friend_id+"'WHERE user_id = '"+data.get_id()+"'"+"and contact_phone= '"+phone_hashing+"'";
				String [][]result =  c.Conn(sql, true);
				
	  		}
	  		//check if the name has changed
	  		if(!name.equals(data.get_contact().get(i)._name))
	  		{
	  			System.out.println("update the contact name"+data.get_contact().get(i)._name);
	  			String sql = "UPDATE friends SET contact_name = '"+data.get_contact().get(i)._name+"' WHERE user_id = '"+data.get_id()+"'and contact_phone='"+phone_hashing+"'";
	  		    String [][]result =  c.Conn(sql, true);
	  		}
	  	    //check if the image has changed
	  		if(!image.equals(data.get_contact().get(i)._image))
	  		{
	  			System.out.println("update the contact image"+data.get_contact().get(i)._image);
	  			String sql = "UPDATE friends SET image = '"+data.get_contact().get(i)._image+"' WHERE user_id = '"+data.get_id()+"'and contact_phone='"+phone_hashing+"'";
	  		    String [][]result =  c.Conn(sql, true);
	  		}
	  		return true;
	      }
	 }
	 return false;
 }
 private boolean check_phone_new(String contact_phone,hash hashing,connecting c,refresh_data data,ArrayList<String>result_phone,ArrayList<String>result_salt) throws Exception
 {
	 
	 for(int i=0;i<result_phone.size();i++)
	 {
		 byte[] bDigest = hashing.base64ToByte(result_phone.get(i));
		 byte[] bSalt = hashing.base64ToByte(result_salt.get(i));
		 byte[] proposedDigest = hashing.getHash(100, contact_phone, bSalt);
		 if( Arrays.equals(proposedDigest, bDigest)){return true;}
	 }
	 return false;
 }
 
 private void update_contacts(connecting c,refresh_data data,hash hashing) throws Exception
 {
	 
	 System.out.println("contact updateeeeeeeeeeeeeeeeeee");
	 String myQuery23 = "SELECT contact_phone,salt,contact_name,image from friends where friends.user_id= '"+ data.get_id()+"'";
	 String [][]result =  c.Conn(myQuery23, false);
	 ArrayList<String>result_phone=new ArrayList<String>();
	 ArrayList<String>result_salt=new ArrayList<String>();
	 for(int i=0;i<result.length;i++)
	 {
		 result_phone.add(result[i][0]);
		 result_salt.add(result[i][1]);
	 }
	 //check the database
	for(int i=0;i<result.length;i++)
	 {
		 //the contacts is old in db (this contact is not found in user's mobile now)
		if(!check_phone_old(result[i][0],c,data,result[i][1],result[i][2],result[i][3],hashing))
		{
			 System.out.println("deleteeeeeeeeeeeee contact");
			 myQuery23 ="DELETE FROM friends WHERE friends.contact_phone='"+result[i][0]+"'and user_id= '"+data.get_id()+"'";
			 String [][] result_2 =  c.Conn(myQuery23, true);
		}
		
	 }
	//check the user data now
	for(int i=0;i<data.get_contact().size();i++)
	  {
		//check if user's contacts have changed and there are some contacts are new 
		if(!check_phone_new(data.get_contact().get(i)._phone_number,hashing,c,data,result_phone,result_salt))
		{
			System.out.println("new contact is not in db");
			insert_contact(c,data,i,hashing);
		}
		
	  }
	 
 }
 private void insert_contact(connecting c,refresh_data data,int i,hash hashing) throws Exception
 {
	  System.out.println("insert new contacts");
	  int used_app=0;
	  String friend_id=null;
	  friend_id=check_phone_exist(c,data.get_contact().get(i)._phone_number,hashing);
	  if(friend_id!=null)
	  {
		 used_app=1;
	  }
	  Map<String,String>maping=new HashMap<String,String>();
	  maping=get_phone_hash(data.get_contact().get(i)._phone_number,hashing);
      String phone=get_key(maping);
	  String salt1=maping.get(phone);
	  String image=data.get_contact().get(i)._image;
	  String Query = "INSERT INTO friends (user_id,contact_phone,salt,contact_name,used_app,friend_id,image) VALUES ('"+data.get_id()+"','"+phone+"','"+salt1+"','"+data.get_contact().get(i)._name+"',"+used_app+",'"+friend_id+"','"+image+"')"; 
	  String [][] result2 = c.Conn(Query, true);
	 
 }
 ////////////////////////////////////////////////////////////installed db//////////////////////////////////////////////////////////////
 private void update_installed_apps(connecting c,refresh_data data)
  {
	 System.out.println("installed updateeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
	 String myQuery23 = "SELECT installed_app from installed_app where installed_app.user_id= '"+ data.get_id()+"'";
	 String [][]result =  c.Conn(myQuery23, false);
	 ArrayList<String>result_array=new ArrayList<String>();
	 for(int i=0;i<result.length;i++)
	 {
		 result_array.add(result[i][0]);
	 }
	 //check the database
	 for(int i=0;i<result.length;i++)
	 {
		 //the installed app is old in db (this app is not found in user's mobile now)
		 if(!data.get_installed().contains(result[i][0]))
		 {
			 System.out.println("deleteeeeeeeeeeeeeeeee installed app");
			 myQuery23 ="DELETE FROM installed_app WHERE installed_app.installed_app='"+result[i][0]+"'and user_id= '"+data.get_id()+"'";
			 String [][] result_2 =  c.Conn(myQuery23, true);
			 //delete from the most used apps
			 myQuery23 ="DELETE FROM most_apps WHERE most_apps.app_name='"+result[i][0]+"'and user_id= '"+data.get_id()+"'";
			 result_2 =  c.Conn(myQuery23, true);
		 }
		 
	 }
	 //check the user data now
	 for(int i=0;i<data.get_installed().size();i++)
	 {
		 //check if user's apps have changed and there are some apps are new 
		 if(!result_array.contains(data.get_installed().get(i)))
		 {
			 System.out.println("insert new installed app");
			 String Query = "INSERT INTO installed_app (user_id,installed_app) VALUES ('"+data.get_id()+"','"+data.get_installed().get(i)+"')"; 
			 String [][] result2 = c.Conn(Query, true);
		 }
	 }

  }

} 
