package musar;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.sql.SQLException;






import java.util.Arrays;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.codehaus.jettison.json.JSONObject;

import com.sun.jersey.core.util.Base64;


// Plain old Java Object it does not extend as class or implements 
// an interface

// The class registers its methods for the HTTP GET request using the @GET annotation. 
// Using the @Produces annotation, it defines that it can deliver several MIME types,
// text, XML and HTML. 

// The browser requests per default the HTML MIME type.

//Sets the path to base URL + /hello
@Path("/register")
public class register {

  /**
   * From a password, a number of iterations and a salt,
   * returns the corresponding digest
   * @param iterationNb int The number of iterations of the algorithm
   * @param password String The password to encrypt
   * @param salt byte[] The salt
   * @return byte[] The digested password
   * @throws NoSuchAlgorithmException If the algorithm doesn't exist
   */
  
  String id=null;	
  @POST
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes(MediaType.APPLICATION_JSON)
 
  public String insert_userdata(String phone_number) throws Exception {
	  JSONObject object = new JSONObject(phone_number);
	  phone_number=object.getString("user");
	  SecureRandom random=null;
		  iaik.security.provider.IAIK.addAsProvider(true);

			try {
				random = SecureRandom.getInstance("SHA1PRNG");
			} catch (NoSuchAlgorithmException e) {
			
				e.printStackTrace();
			}
		      // Salt generation 64 bits long
		      byte[] bSalt = new byte[8];
		      random.nextBytes(bSalt);
		      hash hashing=new hash();
		      byte[] bDigest= hashing.getHash(100,phone_number,bSalt);
		  	  String sDigest = hashing.byteToBase64(bDigest);
	          String sSalt = hashing.byteToBase64(bSalt);
	        
		 connecting c=new connecting();
		  //check if this number was here before refuse the registration 
		  if(!check_exist(c,phone_number,hashing))
		  {
			  
			  String Query = "INSERT INTO users (phone_number,salt,category) VALUES ('"+sDigest+"','"+sSalt+"','"+"unknown"+"')"; 
	          String [][] result2 = c.Conn(Query, true);
	         //we need to change it after register wall
			  String myQuery23 = "SELECT user_id,phone_number,salt from users where phone_number= '"+ sDigest+"'";
	          String [][] result =  c.Conn(myQuery23, false);
	          System.out.println(result[0][0]);
	          return result[0][0];
	      }
		  System.out.println(id);
		  return id;
         

	

  }
private boolean check_exist(connecting c,String phone_number,hash hashing) throws Exception
{
	 String myQuery23 = "SELECT user_id,phone_number,salt from users";
	  String [][] result =  c.Conn(myQuery23, false);
	  for(int i=0;i<result.length;i++)
	  {
		 
		  String salt=result[i][2];
		  byte[]bsalt=hashing.base64ToByte(salt);
		  byte[]bdigest=hashing.base64ToByte(result[i][1]);
		  byte[]bDigest= hashing.getHash(100,phone_number,bsalt);
		  if( Arrays.equals(bDigest, bdigest))
		  {
			  System.out.println("la2etoooooooooooooooooooooooooooooooooo");
			  id=result[i][0];
			  return true;
		  }
	  }
	  return false;
}
} 
