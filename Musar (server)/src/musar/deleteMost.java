package musar;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@Path("/deleteMost")
public class deleteMost {

	  @POST
	  @Produces(MediaType.TEXT_HTML)
	  @Consumes(MediaType.APPLICATION_JSON)

	  public String deleteMost_data(String user_id) throws IOException, ClassNotFoundException, SQLException, NoSuchAlgorithmException, NoSuchProviderException, JSONException {

		  JSONObject object = new JSONObject(user_id);
		  ArrayList<String> mostBanned=new ArrayList<String>();
          user_id=object.getString("user");
		  int size=object.getInt("size_banned");
		  for(int i=0;i<size;i++)
		  {
			 mostBanned.add(object.getString("Banned"+i));
			 System.out.println("most bannedd:"+mostBanned.get(i));
		  }
		 
		  connecting c=new connecting();
		  for(int i=0;i<size;i++){

    		  String myQuery23 ="DELETE FROM most_apps WHERE most_apps.app_name='"+mostBanned.get(i)+"'and user_id= '"+user_id+"'";
			  String [][] result_2 =  c.Conn(myQuery23, true);
		  }
		 
		  return "ok";
	  }
}