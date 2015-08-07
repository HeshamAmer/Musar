package musar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


















//import com.sun.org.apache.xml.internal.serialize.Serializer;

import de.umass.lastfm.*;

@SuppressWarnings("unused")
/**
 * Class MusicRecommendation
 * Trains data from lastfm website http://www.dtic.upf.edu/~ocelma/MusicRecommendationDataset/
 * Clusters users based on their listening similarities using kmeans clustering
 * Saves trained data and clusters in files, to save time of re-training the data
 * @author haron pc
 *
 */
public class MusicRecommendation implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4949651356951632449L;

	//DB object, handles connections to database
	private transient static Db db;
	
	//last fm API key
	private transient static final String API_KEY = "b5a2e3f9804e904aa98f777f8b8ac9ae";
	
	//feature tags
	private static List<String> tags;
	
	//Map : artist -> list of tags associated to this artist
	private static Map< String, Collection< Pair<String, Integer> > > artistInfo;
	
	//List of users used in training 
	private static List<Integer> users;
	
	//Map : user -> list of user's listening history
	private static Map<Integer, Collection< Pair <String, Integer> > > listeningHistory;
	
	//Kmeans object to do clustering
	private static Kmeans kmeans;
	
	//K is the number of desired clusters
	private transient static int K;
	
	//feature Matrix : Users as rows, tags as columns
	//featureMatrix[i][j] = score of user i to tag j
	private static List<Vector> featureMatrix;
	
	//list of clusters retreived from kmeans clustering
	private static List<Cluster> clusters;
	
	//instance of this class
	private static MusicRecommendation instance = null;
	
	//logging
	private static Logger logger;
	
	
	/**
	 * Private Constructor to guard against instantiations
	 */
	private MusicRecommendation() {
		logger = Logger.getLogger("MyLog");
		FileHandler fh;
		
		try {  

	        // This block configure the logger with handler and formatter  
	        fh = new FileHandler("Logfile.log");  
	        logger.addHandler(fh);
	        SimpleFormatter formatter = new SimpleFormatter();  
	        fh.setFormatter(formatter);   

	    } catch (SecurityException e) {  
	        e.printStackTrace();  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    }  

	}
	
	/**
	 * initializes all objects needed, trains the data, does the clustering
	 * and saves the clusters into a file
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public static void init()  {
		K = 50;
		//Caller.getInstance().setUserAgent("tst");
		//Caller.getInstance().setDebugMode(false);
		
		db = new Db();
		tags = new ArrayList<String>();
		artistInfo = new HashMap<String, Collection< Pair<String, Integer> >>();
		listeningHistory = new HashMap <Integer, Collection < Pair<String, Integer> > >();
		users = new ArrayList<Integer>();
		featureMatrix = new ArrayList<Vector>();
		
		long lStartTime = System.nanoTime();
		train();
		long lEndTime = System.nanoTime();
		
		
		Random random = new Random(System.currentTimeMillis());
		kmeans = new Kmeans(random);
		
		System.out.println("Clusterting ..");
		
		
		//clusters = kmeans.cluster(featureMatrix, K, 10, 128);
		
		
		
		long difference = lEndTime - lStartTime;
		
		System.out.println("Elapsed milliseconds: " + difference/1000000);
		//saveClusters("SavedClusters");
		clusters = loadClusters("SavedClusters");
	}
	
	/**
	 * saves clusters into a file
	 * @param filename the filename to save the clusters in
	 */
	public static void saveClusters(String filename) {
		
		System.out.println("Saving Clusters...");
		File file = new File(filename);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			
			for (int i = 0; i < K; i++) {
				
				Cluster cluster = clusters.get(i);
				List<Integer> usersInCluster = cluster.getUsers();
				int size = usersInCluster.size();
				
				writer.write(i + " " + size + "\n");
				
				Vector center = cluster.getCenter();
				double[] values = center.getVector();
				writer.write("" + values[0]);
				for (int j = 1; j < tags.size(); j++) {
					writer.write(" " + values[j]);
				}
				writer.write("\n");
				
				for(Integer user : usersInCluster) {
					writer.write(user + "\n");
				}
			}
			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Calculates a Vector representing the user score
	 * @param uid the user id
	 * @return user score as a vector
	 */
	private static Vector userScore(int uid) {
		Collection<Pair<String, Integer>> listeningHistory = MusicRecommendation.listeningHistory
				.get(uid);

		// calculate total number of plays of that user
		int totalplays = 0;
		for (Pair<String, Integer> pair : listeningHistory)
			totalplays += pair.getValue();

		double[] featureScores = new double[tags.size()];
		for (Pair<String, Integer> pair : listeningHistory) {
			String artist = pair.getKey();
			int playCount = pair.getValue();
			Collection<Pair<String, Integer>> artistTags = artistInfo
					.get(artist);
			if (artistTags == null)
				continue;

			double nlizedPlayCount = 1.0 * playCount / totalplays;

			int maxTagCount = getMaxTagCount(artistTags);
			
			if (totalplays == 0)
				logger.info("gayTotal plays");

			if (maxTagCount == 0)
				logger.info("gaymaxTagCount");
			
			for (int j = 0; j < tags.size(); j++) {
				String tag = tags.get(j);
				
				double nlizedTagCount;
				
				if (maxTagCount == 0)
					nlizedTagCount = 0;
				else
					nlizedTagCount = 1.0
						* getTagCount(artist, tag, artistTags) / maxTagCount;

				// matrix[i][j] += 0.5*nlizedPlayCount + 0.5*nlizedTagCount;
				featureScores[j] += 0.5 * nlizedPlayCount + 0.5
						* nlizedTagCount;
			}
		}
		
		/*
		for(int i = 0; i < tags.size(); i++) {
			if (featureScores[i] == 0) continue;
			featureScores[i] = 1 + Math.log10(featureScores[i]);
		}*/
		
		return new Vector(featureScores);
	}
	
	/**
	 * loads clusters from a given file
	 * @param filename filename to load clusters from
	 * @return the loaded clusters
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public static List<Cluster> loadClusters(String filename)  {
		System.out.println("Loading Clusters...");
		
		List<Cluster> result = new ArrayList<Cluster>();
		connecting connect = null;
		try {
			connect = new connecting();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String myQuery23 = "SELECT * from savedcluster limit 10100";
		String [][] result_db =  connect.Conn(myQuery23, false);
		int j=0;
		 while(j<result_db.length){
			String[] splitted =result_db[j][0].split(" ");j++;
			int index = Integer.parseInt(splitted[0]);
			int size = Integer.parseInt(splitted[1]);	
			double[] values = new double[tags.size()];
			String center = result_db[j][0];j++;
			String[] splittedCenterValues = center.split(" ");
			for (int k = 0; k < splittedCenterValues.length; k++)
				values[k] = Double.parseDouble(splittedCenterValues[k]);
			Vector centerVector = new Vector(values);
				Cluster c = new Cluster(centerVector);
				for (int i = 0; i < size; i++) {
					int uid = Integer.parseInt(result_db[j][0]);j++;
					c.addUser(uid);
				}
				result.add(c);
			}
		return result;
	}
	
	/**
	 * Singleton design pattern
	 * @return a MusicRecommendation Instance
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public static MusicRecommendation getInstance()  {
		if(instance == null) {
			instance = new MusicRecommendation();
			init();
		}
		return instance;
	}
	
	protected Object readResolve()  {
		return getInstance();
	}
	
	/**
	 * recommended artists to user
	 * @param artists artists in user listening history
	 * @param k how many artists should be in the recommendations
	 * @return a list of recommended artists
	 */
	public List<Pair<String,Double>> recommend(Collection <Pair <String,Integer>> history, int totalplays, int k) {
		
		double[] featureVector = new double[tags.size()];
		
		Collection <String> tagsInHistory = new HashSet<String>();
		Set <String> artistInHistory = new HashSet<String>();
		
		for(Pair<String,Integer> p : history) {
			String artist = p.getKey();
			Collection <Pair<String, Integer>> artistTags = artistInfo.get(artist);
			artistInHistory.add(artist);
			
			if(artistTags == null) continue;
			
			for(Pair<String,Integer> tag : artistTags) {
				if(tag.getValue() >= 100)
					tagsInHistory.add(tag.getKey());
			}
		}
		
		for(Pair<String,Integer> p : history) {
			String artist = p.getKey();
			int playCount = p.getValue();
			Collection< Pair<String, Integer> > artistTags = artistInfo.get(artist);
			if(artistTags == null) //?
				continue;
			
			double nlizedPlayCount = 1.0*(playCount+1) / (totalplays + artistInfo.size());
			int maxTagCount = getMaxTagCount(artistTags);
			
			for(int i = 0; i < featureVector.length; i++) {
				String tag = tags.get(i);
			
				double nlizedTagCount = 1.0*getTagCount(artist, tag, artistTags) / maxTagCount;
				
				featureVector[i] += 0.5*nlizedPlayCount + 0.5*nlizedTagCount;
			}
		}
		
		Vector userScore = new Vector(featureVector);

		int cluster = kmeans.getNearestCluster(clusters, userScore);
		
		List<Integer> usersInCluster = clusters.get(cluster).getUsers();
		//List<Integer> usersInCluster = getUsersInCluster(cluster);
		Set<String> commonArtists = getCommonArtists(usersInCluster, tagsInHistory);
		List< Pair<String, Double> > scores = new ArrayList<Pair<String,Double>>();
		Map<String, Double> artistScore = new HashMap<String, Double>();
		
		for(String artist : commonArtists) {
			artistScore.put(artist, 0.0);
		}
		
		//rank
		for(Integer user : usersInCluster) {
			
			Collection< Pair<String,Integer> > listeningHistory = MusicRecommendation.listeningHistory.get(user);
			
			int totalPlays = 0;
			for(Pair<String,Integer> pair : listeningHistory)
				totalPlays += pair.getValue();
			 
			 
			for(String artist : commonArtists) {
				int playcount = getPlayCountByUserforArtist(user, artist);
				if (playcount == 0) continue;
				double oldScore = artistScore.get(artist);
				oldScore += 1.0*playcount / totalPlays;
				artistScore.put(artist, oldScore);
			}
		}
		
		Iterator<Entry<String, Double>> it = artistScore.entrySet().iterator();
		
		while(it.hasNext()) {
			Map.Entry<String, Double> pairs = (Map.Entry<String, Double>) it.next();
			if(!artistInHistory.contains(pairs.getKey()))
				scores.add(new Pair<String, Double>(pairs.getKey(), pairs.getValue()));
		}
		
		return scores;
	}
	
	/**
	 * gets the play count of a user for an artist
	 * @param user user id
	 * @param artist the artist name
	 * @return play count value
	 */
	private int getPlayCountByUserforArtist(int user, String artist) {
		Collection <Pair<String, Integer>> listeningHistory = MusicRecommendation.listeningHistory.get(user);
		
		for(Pair<String, Integer> record : listeningHistory) {
			if(artist.equals(record.getKey()))
				return record.getValue();
		}
		return 0;
	}
	
	/**
	 * retrieves a list of top artists for a specified user
	 * @param limit a limit to the number of top artists retrieved
	 * @param uid the user id
	 * @return a list of top artists
	 */
	private static List< Pair<String, Integer> > getTopArtists(int limit, int uid) {
		List < Pair<String, Integer> > result = new ArrayList<Pair<String,Integer>>();
		
		Collection < Pair <String, Integer> > history = listeningHistory.get(uid);
		
		
		for (Pair <String, Integer> record : history) {
			if(limit == 0) break;
			result.add(record);
			limit--;
		}
		return result;
	}
	
	
	/**
	 * retrieves a list of common artists that share common tags 
	 * between a group of users
	 * @param users a list of users to select common artists for
	 * @param tags a collection of tags to filter common artists
	 * @return a list of common artists
	 */
	private Set<String> getCommonArtists(List<Integer> users, Collection<String> tags) {
		Set<String> commonArtists = new HashSet<String>();
		
		for (Integer user : users) {
			List< Pair<String, Integer> > userArtists = getTopArtists(10, user);
			for (Pair<String, Integer> pair : userArtists) {
				String artist = pair.getKey();
				Collection < Pair<String, Integer> > artistTags = artistInfo.get(artist);
				if(artistTags == null) continue;
				
				boolean ok = false;
				for(Pair<String, Integer> p : artistTags) {
					if(tags.contains(p.getKey()) && p.getValue() >= 100) {
						ok = true;
						break;
					}
				}
				
				if(ok)
					commonArtists.add(artist);
			}
		}
		
		return commonArtists;
	}
	
	/**
	 * loads feature tags from a file
	 * @param filename the filename to load feature tags from
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public static void loadFeatureTags(String filename)  {
		
			connecting connect = null;
			try {
				connect = new connecting();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String myQuery23 = "SELECT * from significantlyusedtags";
			String [][] result =  connect.Conn(myQuery23, false);
			for(int i=0;i<result.length;i++) {
				tags.add(result[i][0]);
			}
	}
	
	/**
	 * loads feature matrix from a given file
	 * @param filename the file name to load from
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	private static void loadFeatureMatrixFromFile(String filename)  {
		
		connecting connect = null;
		try {
			connect = new connecting();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String myQuery23 = "SELECT * from savedfeaturematrix";
		String [][] result =  connect.Conn(myQuery23, false);
		String dimenstions = result[0][0];
			//int usersLength = Integer.parseInt(dimenstions.split(" ")[0]);
			//int featuresLength = Integer.parseInt(dimenstions.split(" ")[1]);
			
		double totalAvg = 0.0;
		for (int i = 1; i < users.size(); i++) {
			System.out.println("Reading line : " + i);
			String line = result[i][0];
			String[] Stringvalues = line.split(" ");
			
			double avg = 0.0;
			double[] values = new double[tags.size()];
			
			for (int j = 0; j < tags.size(); j++) {
				values[j] = Double.parseDouble(Stringvalues[j]);
				avg += values[j];
			}
			avg /= tags.size();
			totalAvg += avg;
			//System.out.println("avg = " + avg);
			Vector v = new Vector(values);
			v.setUid(users.get(i));
			featureMatrix.add(v);
		}
		System.out.println("Total avg = " + totalAvg/users.size());
		
	}
	
	/**
	 * saves the feature matrix to a file
	 * @param filename the file name to save in
	 */
	private static void dumpFeatureMatrix(String filename) {
		File file = new File(filename);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			
			
			writer.write(users.size() + " " + tags.size() + "\n");
			for (int i = 0; i < featureMatrix.size(); i++) {
				
				StringBuilder sc = new StringBuilder();
				sc.append(featureMatrix.get(i).getVector()[0]);
				for (int j = 1; j < tags.size(); j++) {
					sc.append(" " + featureMatrix.get(i).getVector()[j]);
				}
				sc.append("\n");
				writer.write(sc.toString());
			}
			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * loads all the needed user history from the database
	 * into the listeningHistory map
	 */
	private static void loadUsersHistory() {
		
		System.out.println("Retreiving Users history from database");
		for(int i=0;i<users.size();i++) {
			listeningHistory.put(users.get(i), new ArrayList<Pair<String, Integer>>());
		}
		
		
		for(Integer user : users) {
			List< Pair<String, Integer> > history = db.getTopArtists(-1, user);
			
			for(Pair<String, Integer> record : history) {
				listeningHistory.get(user).add(record);
			}
		}
	}
	
	/**
	 * trains the data, loads data from database into containers
	 * saves the trained data into files
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	private static void train()  {
		getSimilarUsers("United States", 10000);
		//getAllUsers();
		//Set<String> commonArtists = getCommonArtists(users);
		System.out.println("getSimilarUsers done ..");
		
		loadUsersHistory();
		System.out.println("loadUsersHistory done ..");
		
		//dumpAllTags(users);
		loadAllTags("allTags");
		System.out.println("loadAllTags done ..");
		
		//tags = calculateFeatureTags(commonArtists);
		loadFeatureTags("significantlyUsedTags");
		System.out.println("loadFeatureTags done ..");
		
		
		//buildFeatureMatrix(tags);
		//System.out.println("FreatureMatrix has been built successfully");
		
		
		//dumpFeatureMatrix("SavedFeatureMatrix");
		//System.out.println("FreatureMatrix has been dumped successfully");
		loadFeatureMatrixFromFile("SavedFeatureMatrix");
		System.out.println("FreatureMatrix loaded successfully from file");
	}
	
	/**
	 * retrieves a list of common artists between users
	 * @param users a list of users 
	 * @return a set of common artists between those users
	 */
	private Set<String> getCommonArtists(List<Integer> users) {
		Set<String> commonArtists = new HashSet<String>();
		
		for (Integer user : users) {
			List< Pair<String, Integer> > userArtists = db.getTopArtists(10, user);
			for (Pair<String, Integer> pair : userArtists)
				commonArtists.add(pair.getKey());
		}
		
		return commonArtists;
	}
	
	
	/**
	 * loads artist-usertag information from a file
	 * @param filename the file name to load from
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	private static void loadAllTags(String filename)  {
		artistInfo = db.getAllTags();
	}
	
	/**
	 * computes all artist tags in the given users listening history
	 * calls the lastfm api (Artist.gettoptags) to get the artist tags
	 * @param users a list of users to compute the tags of the artists in their
	 * listening history
	 */
	private static void dumpAllTags(List<Integer> users) {
		Set<String> commonArtists = new HashSet<String>();
		
		File file = new File("allTags");
		
		File errorFile = new File("Errors");
		BufferedWriter errorLog = null;
		BufferedWriter writer = null;
		
		try {
			errorLog = new BufferedWriter(new FileWriter(errorFile));
			writer = new BufferedWriter(new FileWriter(file));
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		int i = 0;
		for (Integer user : users) {
			List< Pair<String, Integer> > userArtists = db.getTopArtists(-1, user);
			for (Pair<String, Integer> pair : userArtists) {
				String artist = pair.getKey();
				if(commonArtists.add(artist)) {
					Collection<Tag> tags = null;
					try {
						tags = de.umass.lastfm.Artist.getTopTags(artist, API_KEY);
					} catch (Exception e) {
						try {
							errorLog.write("Problem with artist : " + artist);
						} catch (IOException ee) {
							ee.printStackTrace();
						}
						continue;
					}
					System.out.println("Done with #"+i);
					for(Tag tag : tags) {
						try {
							writer.write(artist + "\t" + tag.getName() + "\t" + tag.getCount() + "\n");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					i = i+1;
				}
			}
		}
	}
	
	/**
	 * calculates the feature tags to be used in our training
	 * @param artists a collection of artist names(String) to get tags from
	 * @return a list of features
	 */
	private List<String> calculateFeatureTags(Collection<String> artists) {
		List<String> result = new ArrayList<String>();
		
		Set<String> significantlyUsedTags = new HashSet<String>();
		int i = 0;
		
		File file = new File("significantlyUsedTags");
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		for(String artist : artists) {
			
			Collection<Pair<String, Integer>> tags = null;
			try {
				tags = artistInfo.get(artist);
				if (tags == null)
					continue;
			} catch (Exception e) {
				System.out.println("Problem with artist : " + artist);
				continue;
			}
			System.out.println("Done with #"+i);
			for(Pair<String, Integer> tag : tags) {
				if(tag.getValue() >= 100) {
					significantlyUsedTags.add(tag.getKey());
				}
			}
			i = i+1;
		}

		
		for(String tag : significantlyUsedTags) { 
			result.add(tag);
			try {
				writer.write(tag + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * retrieves a list of users in a certain country
	 * @param country the country common between the users
	 * @param limit a limit to the number of users retrieved
	 */
	private static void getSimilarUsers(String country, int limit) {
		List<Integer> result = db.getUsersInCountry(country, limit);
		
		for(Integer i : result)
			users.add(i);
	}
	
	/**
	 * returns a tag count of the artist 
	 * @param artist the artist name
	 * @param t tag
	 * @param tags a collection of tags to search in
	 * @return the tag count value
	 */
	private static int getTagCount(String artist, String t, Collection<Pair<String, Integer>> tags) {
			
		for(Pair<String, Integer> tag : tags) {
			if(tag.getKey().equals(t)) {
				return tag.getValue();
			}
		}
		return 0;
	}
	
	/**
	 * computes the maximum tag in a Collection of tags
	 * @param tags the collection of tags to search in
	 * @return the maximum tag count
	 */
	private static int getMaxTagCount(Collection<Pair<String, Integer>> tags) {
		
		int max = 0;
		for(Pair<String, Integer> tag : tags) {
			int count = tag.getValue();
			max = Math.max(count, max);
		}
		return max;
	}
	
	/**
	 * builds up a feature matrix: rows as users, columns as features
	 * matrix[i][j] = user i score for feature j
	 * @param tags the list of tags (columns)
	 */
	private static void buildFeatureMatrix(List<String> tags) {
		
		System.out.println("Building feature Matrix ..");
		 int usersLength = users.size();
		 
		 for(int i = 0; i < usersLength; i++) {
			 int user = users.get(i);
			 Vector v = MusicRecommendation.userScore(user);
			 v.setUid(user);
			 featureMatrix.add(v);
			 System.out.println("Done with user : " + i);
		 }
	}
	
	public static void main (String args[]) throws ClassNotFoundException, SQLException {
		
		
		MusicRecommendation mr = MusicRecommendation.getInstance();

		List<Pair<String, Integer>> history = new ArrayList<Pair<String, Integer>>();
		history.add(new Pair<String, Integer>("led zepplin", 0));
		history.add(new Pair<String, Integer>("pink floyd", 0));
		history.add(new Pair<String, Integer>("iron maiden", 0));
		history.add(new Pair<String, Integer>("Ed Sheeran", 0));
		history.add(new Pair<String, Integer>("katy perry", 0));
		
		
		List<Pair<String, Double>> recommendedArtists = mr.recommend(history, 0, 5);
		
		Collections.sort(recommendedArtists);
		
		System.out.println("-------------------------------------");
		for(Pair<String, Double> r : recommendedArtists) {
			System.out.println(r.getKey() + " " + r.getValue());
		}
	}
	
}
