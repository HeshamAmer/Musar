package musar;



import java.io.Serializable;
import java.util.Collection;



public class Vector implements Serializable {
	
	/**
	 * Class Vector, a simple vector contains the user score for the features
	 * along with the user id
	 * 
	 */
	private static final long serialVersionUID = -2588442933749737266L;

	private int uid;
	
	private final double[] vector;
	
	public Vector(double[] v) {
		this.vector = v;
		uid = -1;
	}
	
	public void setUid(int uid) {
		this.uid = uid;
	}
	
	public int getUid() {
		return uid;
	}
	
	/**
	 * Calculates the centroid of a list of vectors
	 * @param vectors to calculate their centroid
	 * @return the centroid vector
	 */
	public Vector centroidOf(Collection<Vector> vectors) {
		double[] centroid = new double[vector.length];
		
		for (Vector vector : vectors) {
			for (int i = 0; i < centroid.length; i++) {
				centroid[i] += vector.getVector()[i];
			}
		}
		for (int i = 0; i < centroid.length; i++)
			centroid[i] /= vectors.size();
		
		return new Vector(centroid);
	}
	
	public double[] getVector() {
		return vector;
	}
	
	/**
	 * calculates the eucledian distance to another vector
	 * @param v the vector to calculate the distance to
	 * @return the distance value
	 */
	public double distanceFrom(Vector v) {
		double s = 0;
        for (int d = 0; d < vector.length; d++) {
            s += Math.pow(Math.abs(vector[d] - v.getVector()[d]), 2);
        }
        return Math.sqrt(s);
	}
}
