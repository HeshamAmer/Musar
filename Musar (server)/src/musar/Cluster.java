package musar;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Cluster implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8965657842634500992L;

	/** users score in this cluster**/
	private final List<Vector> featureVectors;
	
	/** users in this cluster**/
	private final List<Integer> users;
	
	/** Center vector of this cluster **/
	private final Vector center;
	
	/**
     * Build a cluster centered at a specified vector.
     * @param center the vector which is to be the center of this cluster
     */
	public Cluster(Vector center) {
		this.center = center;
		featureVectors = new ArrayList<Vector>();
		users = new ArrayList<Integer>();
	}
	
	/**
     * Add a user score to this cluster.
     * @param score vector to add
     */
	public void addVector(Vector vector) {
		featureVectors.add(vector);
	}
	
	/**
     * Add a user to this cluster.
     * @param user to add
     */
	public void addUser(int uid) {
		users.add(uid);
	}
	
	/**
     * Get the users scores contained in the cluster.
     * @return users scores contained in the cluster
     */
	public List<Vector> getUsersScores() {
		return featureVectors;
	}
	
	/**
     * Get the users contained in the cluster.
     * @return users contained in the cluster
     */
	public List<Integer> getUsers() {
		return users;
	}
	
	/**
     * Get the vector chosen to be the center of this cluster.
     * @return chosen cluster center
     */
	public Vector getCenter() {
		return center;
	}
}
