package musar;



import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class Db handles all connections to database, creates queries, open connections
 * any data retrieved from the database is through this class
 * @author haron pc
 *
 */
public class Db {
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	
	private static final String DB_URL = "jdbc:mysql://mysql-env-8861173.j.layershift.co.uk/MUSAR";
	
	
	private static final String USER = "admin881Rhs7";
	private static final String PASS = "9JDlLn1rh4l6";
	
	/**
	 * Establishes a connection to the database
	 * @return the established connection or null if establishment failed
	 */
	public Connection establishConnection() {
		Connection conn = null;
		
		try {
			
			//System.out.println("Connecting to database...");
			Class.forName(JDBC_DRIVER);
			
			conn = DriverManager.getConnection(DB_URL,USER,PASS);
			
			return conn;
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * get the top k artists along with their play counts 
	 * from the user listening history
	 * @param k desired number of artists
	 * @param uid user id
	 */
	public List< Pair<String, Integer> > getTopArtists(int k, int uid) {
		
		List< Pair<String, Integer> > topArtists = new ArrayList<Pair<String,Integer>>();
		Connection conn = establishConnection();
		if(conn == null) {
			System.out.println("Unable to establish connection");
			return null;
		}
		
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			
			String sql = null;
			if (k != -1)
				sql = "SELECT artist, playcount FROM playcounts WHERE uid = " + uid + " LIMIT " + k;
			else
				sql = "SELECT artist, playcount FROM playcounts WHERE uid = " + uid;
			
			//System.out.println(sql);
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				String artist = rs.getString("artist");
				int playCount = rs.getInt("playcount");
				topArtists.add(new Pair<String, Integer>(artist, playCount));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				stmt.close();
				conn.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		
		return topArtists;
	}
	
	/**
	 * get a list of users in country, query is limited by limit 
	 * @param country
	 * @param limit
	 * @return
	 */
	public List<Integer> getUsersInCountry(String country, int limit) {
		List<Integer> users = new ArrayList<Integer>();
		Connection conn = establishConnection();
		if(conn == null) {
			System.out.println("Unable to establish connection");
			return null;
		}
		
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			String sql = "SELECT uid FROM profiles WHERE country = '" + country + "'";
			rs = stmt.executeQuery(sql);
			int i = 0;
			while(rs.next() && i < limit) {
				int uid = rs.getInt("uid");
				users.add(uid);
				i++;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				stmt.close();
				conn.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		
		return users;
	}
	
	public Map< String, Collection< Pair<String, Integer> > > getAllTags() {
		Connection conn = establishConnection();
		Map< String, Collection< Pair<String, Integer> > > artistInfo = new HashMap<String, Collection<Pair<String,Integer>>>();
		if(conn == null) {
			System.out.println("Unable to establish connection");
			return null;
		}
		
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			String sql = "SELECT * FROM alltags";
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				String artist = rs.getString("artist");
				String tag = rs.getString("tag");
				int tagCount = rs.getInt("tagcount");
				
				Pair<String, Integer> p = new Pair<String, Integer>(tag, tagCount);
				if(artistInfo.containsKey(artist))
					artistInfo.get(artist).add(p);
				else {
					Collection < Pair<String, Integer> > c = new ArrayList <Pair <String, Integer>>();
					c.add(p);
					artistInfo.put(artist, c);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				stmt.close();
				conn.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return artistInfo;
	}
	
	public static void main(String[] args) {
		Db db = new Db();
		List< Pair <String, Integer> > l = db.getTopArtists(10, 0);
		for (int i = 0; i < l.size(); i++)
			System.out.println(l.get(i).getKey()+ " " + l.get(i).getValue());
	}
}
