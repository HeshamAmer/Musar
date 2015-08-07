package musar;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
@Path("/delete_account")
public class delete_account {

	  @POST
	  @Produces(MediaType.TEXT_HTML)
	  @Consumes(MediaType.APPLICATION_JSON)

	  public String delete_userdata(String user_id) throws IOException, ClassNotFoundException, SQLException, NoSuchAlgorithmException, NoSuchProviderException, JSONException {
		  JSONObject object = new JSONObject(user_id);
		  System.out.println("delete allllllllllllllllllllllllllllllllllllllllll");
		  user_id=object.getString("user");
		  connecting c=new connecting();
		  String myQuery23 ="DELETE FROM users WHERE users.user_id='"+user_id+"'";
		  String [][] result_2 =  c.Conn(myQuery23, true);
		  myQuery23 ="DELETE FROM friends WHERE friends.user_id='"+user_id+"'";
		  result_2 =  c.Conn(myQuery23, true);
		  myQuery23 ="DELETE FROM most_apps WHERE most_apps.user_id='"+user_id+"'";
		  result_2 =  c.Conn(myQuery23, true);
		  myQuery23 ="DELETE FROM installed_app WHERE installed_app.user_id='"+user_id+"'";
		  result_2 =  c.Conn(myQuery23, true);
		  return "ok";
	  }
}
