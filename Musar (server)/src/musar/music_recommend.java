package musar;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;



@Path("/music_recommend")
public class music_recommend {
	
	  // This method is called if TEXT_PLAIN is request
	  @GET
	  @Produces(MediaType.TEXT_PLAIN)
	  public String sayPlainTextHello() throws NoSuchAlgorithmException, UnsupportedEncodingException, NoSuchProviderException {
		 
		  ArrayList<Pair<String, Integer>> history=new ArrayList<Pair<String, Integer>>();
		 
		  history.add(new Pair<String, Integer>("linkin park",2));
		  //haron's work
		  MusicRecommendation mr =MusicRecommendation.getInstance();
		  return "result:";
	  }
	  @POST
	  @Produces(MediaType.TEXT_PLAIN)
	  @Consumes(MediaType.APPLICATION_JSON)
	  public String get_music(String music_string) throws JSONException, ClassNotFoundException, SQLException 
	  {
		  JSONObject object = new JSONObject(music_string);
		  ArrayList<Pair<String, Integer>> history=new ArrayList<Pair<String, Integer>>();
		  int size=object.getInt("size_artists");
		  for(int i=0;i<size;i++)
		  {
			  history.add(new Pair<String, Integer>(object.getString("artist"+i), object.getInt("playcount"+i)));
			  System.out.println(history.get(i).getKey()+"total_play:"+history.get(i).getValue());
		  }
		  //haron's work
		  MusicRecommendation mr =MusicRecommendation.getInstance();
		  List<Pair<String, Double>> recommendedArtists = mr.recommend(history, 0, 5);
		  System.out.println("-------------------------------------");
		  for(Pair<String, Double> r : recommendedArtists) {
					System.out.println(r.getKey() + " " + r.getValue());
				}
		  List<Score> scores = new ArrayList<Score>();
		  for(int i=0;i<recommendedArtists.size();i++)
		  {
			  Score score=new Score(recommendedArtists.get(i).getKey(),recommendedArtists.get(i).getValue());
			  System.out.println("score:"+score.name);
			  scores.add(score);
		  }
		  Collections.sort(scores);
		  if(scores.size()!=0)
		  {
			  if(scores.size()<=5) scores=scores.subList(0, scores.size());
			  else scores=scores.subList(0, 5);
		  }
		  return jsonString(scores);

		  
	  }
	  public String jsonString(List<Score> music) throws JSONException
	  {
		  JSONObject obj2 = new JSONObject(); 
		  obj2.put("size_music",music.size());
		  for(int i=0;i<music.size();i++)
		  {
			 // String artist=music.get(i).getartist().replaceAll(" ", ",");//no white spaces in jason here
			  String artist=music.get(i).name.replaceAll(" ", ",");
			  obj2.put("music_artist"+i, artist);
			  obj2.put("artist_rate"+i,music.get(i).score);
		  }
		  return obj2.toString();
	  }
	  class Score implements Comparable<Score> {
		    double score;
		    String name;

		    public Score(String name, double score) {
		        this.score = score;
		        this.name = name;
		    }

		    @Override
		    public int compareTo(Score o) {
		        return score > o.score ? -1 : score < o.score ? 1 : 0;
		    }
		}

}
