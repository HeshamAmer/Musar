package musar;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.sql.SQLException;






import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.resource.factory.SmsFactory;
import com.twilio.sdk.resource.instance.Account;
import com.twilio.sdk.resource.instance.Sms;


// Plain old Java Object it does not extend as class or implements 
// an interface

// The class registers its methods for the HTTP GET request using the @GET annotation. 
// Using the @Produces annotation, it defines that it can deliver several MIME types,
// text, XML and HTML. 

// The browser requests per default the HTML MIME type.

//Sets the path to base URL + /hello
@Path("/token_register")
public class token_register {

  /**
   * From a password, a number of iterations and a salt,
   * returns the corresponding digest
   * @param iterationNb int The number of iterations of the algorithm
   * @param password String The password to encrypt
   * @param salt byte[] The salt
   * @return byte[] The digested password
   * @throws NoSuchAlgorithmException If the algorithm doesn't exist
   */
  
 
  @POST
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes(MediaType.APPLICATION_JSON)

  public String insert_userdata(String phone_number) throws Exception {
	  JSONObject object = new JSONObject(phone_number);
	  phone_number=object.getString("user");
	  SecureRandom random=null;
	  random = SecureRandom.getInstance("SHA1PRNG");
	  // Salt generation 64 bits long
	  byte[] bSalt = new byte[8];
	  random.nextBytes(bSalt);
	  hash hashing=new hash();
	  String sSalt = hashing.byteToBase64(bSalt);
	  ClockwerkAPI CWA=new ClockwerkAPI();
	  String newSalt = "";

		for (int j = 0; j < sSalt.length(); j++) {
			if (Character.isLetterOrDigit(sSalt.charAt(j))) {
				if (newSalt.length()<=3)
					newSalt += "" + sSalt.charAt(j);
				
			}
		}
	  CWA.SendSMS(phone_number,newSalt);
      return newSalt;
  }
  }