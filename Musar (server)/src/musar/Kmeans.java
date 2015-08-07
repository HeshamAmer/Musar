package musar;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Kmeans implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9210226599249925807L;

	/** Strategies to use for replacing an empty cluster. */
    public static enum EmptyClusterStrategy {
        /** Split the cluster with largest distance variance. */
        LARGEST_VARIANCE,

        /** Split the cluster with largest number of points. */
        LARGEST_POINTS_NUMBER,

        /** Create a cluster around the point farthest from its centroid. */
        FARTHEST_POINT
    }
    
    
    /** Random generator for choosing initial centers. */
    private final Random random;

    /** Selected strategy for empty clusters. */
    private final EmptyClusterStrategy emptyStrategy;
    
    public Kmeans(final Random random) {
        this(random, EmptyClusterStrategy.LARGEST_VARIANCE);
    }
    
    public Kmeans(final Random random, final EmptyClusterStrategy emptyStrategy) {
        this.random        = random;
        this.emptyStrategy = emptyStrategy;
    }
    
    
    public List<Cluster> cluster(final List<Vector> featureMatrix, final int k,
    								int numTrials, int maxIterationPerTrial) {
    	
    	// at first, we have not found any clusters list yet
    	List<Cluster> best = null;
    	double bestVarianceSum = Double.POSITIVE_INFINITY;
    	
    	for (int i = 0; i < numTrials; i++) {
    		List<Cluster> clusters = cluster(featureMatrix, k, maxIterationPerTrial);
    		
    		double varianceSum = 0.0;
    		
    		for(Cluster cluster : clusters) {
    			if (!cluster.getUsersScores().isEmpty()) {
    				Vector center = cluster.getCenter();
    				
    				double[] distances = new double[cluster.getUsersScores().size()];
    				int j = 0;
    				for(Vector vector : cluster.getUsersScores()) {
    					distances[j++] = center.distanceFrom(vector); 
    				}
    				
					Statistics stats = new Statistics(distances);
					varianceSum += stats.getVariance();
    			}
    		}
    		
    		if (varianceSum <= bestVarianceSum) {
    			best 			= clusters;
    			bestVarianceSum = varianceSum;
    		}
    	}
    	return best;
    }
    
    private List<Cluster> cluster(final List<Vector> points, final int k,
            final int maxIterations) {
    	
    	List<Cluster> clusters = chooseInitialCenters(points, k, random);
    	
    	
    	int[] assignments = new int[points.size()];
        assignPointsToClusters(clusters, points, assignments);
        
        for (int count = 0; count < maxIterations; count++) {
        	boolean emptyCluster = false;
        	List<Cluster> newClusters = new ArrayList<Cluster>();
        	
        	for (Cluster cluster : clusters) {
        		Vector newCenter = null;
        		
        		if (cluster.getUsersScores().isEmpty()) {
                    switch (emptyStrategy) {
                        case LARGEST_VARIANCE :
                            newCenter = getPointFromLargestVarianceCluster(clusters);
                            break;
                        case LARGEST_POINTS_NUMBER :
                            newCenter = getPointFromLargestNumberCluster(clusters);
                            break;
                        case FARTHEST_POINT :
                            newCenter = getFarthestPoint(clusters);
                            break;
                        default :
                        	
                    }
                    emptyCluster = true;
                } else {
                	newCenter = cluster.getCenter().centroidOf(cluster.getUsersScores());
                }
        		newClusters.add(new Cluster(newCenter));
        	}
        	
        	int changes = assignPointsToClusters(newClusters, points, assignments);
            clusters = newClusters;
            
            
            // if there were no more changes in the point-to-cluster assignment
            // and there are no empty clusters left, return the current clusters
            if (changes == 0 && !emptyCluster) {
                return clusters;
            }
        }
        return clusters;
    }

	private Vector getFarthestPoint(List<Cluster> clusters) {
		double maxDistance = Double.NEGATIVE_INFINITY;
        Cluster selectedCluster = null;
        int selectedPoint = -1;
        for (final Cluster cluster : clusters) {

            // get the farthest point
            final Vector center = cluster.getCenter();
            final List<Vector> points = cluster.getUsersScores();
            for (int i = 0; i < points.size(); ++i) {
                final double distance = points.get(i).distanceFrom(center);
                if (distance > maxDistance) {
                    maxDistance     = distance;
                    selectedCluster = cluster;
                    selectedPoint   = i;
                }
            }

        }

        // did we find at least one non-empty cluster ?
        if (selectedCluster == null) {
            
        }

        return selectedCluster.getUsersScores().remove(selectedPoint);
	}

	private Vector getPointFromLargestNumberCluster(List<Cluster> clusters) {
		int maxNumber = 0;
        Cluster selected = null;
        for (final Cluster cluster : clusters) {

            // get the number of points of the current cluster
            final int number = cluster.getUsersScores().size();

            // select the cluster with the largest number of points
            if (number > maxNumber) {
                maxNumber = number;
                selected = cluster;
            }

        }

        // did we find at least one non-empty cluster ?
        if (selected == null) {
            
        }

        // extract a random point from the cluster
        final List<Vector> selectedPoints = selected.getUsersScores();
        return selectedPoints.remove(random.nextInt(selectedPoints.size()));
	}

	private Vector getPointFromLargestVarianceCluster(Collection<Cluster> clusters) {
		double maxVariance = Double.NEGATIVE_INFINITY;
        Cluster selected = null;
        for (final Cluster cluster : clusters) {
        	if (!cluster.getUsersScores().isEmpty()) {

	            // compute the distance variance of the current cluster
	            final Vector center = cluster.getCenter();
	            double[] distances = new double[cluster.getUsersScores().size()];
	            int j = 0;
	            for (final Vector point : cluster.getUsersScores()) {
	            	distances[j++] = center.distanceFrom(point);
	            }
	            
	            Statistics stats = new Statistics(distances);
	            final double variance = stats.getVariance();
	
	            // select the cluster with the largest variance
	            if (variance > maxVariance) {
	                maxVariance = variance;
	                selected = cluster;
	            }
        	}
        }
        
        // did we find at least one non-empty cluster ?
        if (selected == null) {
            
        }

        // extract a random point from the cluster
        final List<Vector> selectedPoints = selected.getUsersScores();
        return selectedPoints.remove(random.nextInt(selectedPoints.size()));
	}

	private int assignPointsToClusters(List<Cluster> clusters,
			List<Vector> points, int[] assignments) {
		
		int assignedDifferently = 0;
		int pointIndex = 0;
		
		for (Vector v : points) {
			int clusterIndex = getNearestCluster(clusters, v);
			if (clusterIndex != assignments[pointIndex]) {
                assignedDifferently++;
            }

            Cluster cluster = clusters.get(clusterIndex);
            cluster.addVector(v);
            cluster.addUser(v.getUid());
            assignments[pointIndex++] = clusterIndex;
		}
		return assignedDifferently;
	}

	public int getNearestCluster(List<Cluster> clusters, Vector v) {
		double minDistance = Double.MAX_VALUE;
        int clusterIndex = 0;
        int minCluster = 0;
        for (final Cluster c : clusters) {
            final double distance = v.distanceFrom(c.getCenter());
            if (distance < minDistance) {
                minDistance = distance;
                minCluster = clusterIndex;
            }
            clusterIndex++;
        }
        return minCluster;

	}

	private List<Cluster> chooseInitialCenters(List<Vector> points, int k,
			Random random) {
		
		// Convert to list for indexed access. Make it unmodifiable, since removal of items
        // would screw up the logic of this method.
        final List<Vector> pointList = Collections.unmodifiableList(new ArrayList<Vector> (points));

        // The number of points in the list.
        final int numPoints = pointList.size();

        // Set the corresponding element in this array to indicate when
        // elements of pointList are no longer available.
        final boolean[] taken = new boolean[numPoints];

        // The resulting list of initial centers.
        final List<Cluster> resultSet = new ArrayList<Cluster>();

        // Choose one center uniformly at random from among the data points.
        final int firstPointIndex = random.nextInt(numPoints);

        final Vector firstPoint = pointList.get(firstPointIndex);

        resultSet.add(new Cluster(firstPoint));

        // Must mark it as taken
        taken[firstPointIndex] = true;

        // To keep track of the minimum distance squared of elements of
        // pointList to elements of resultSet.
        final double[] minDistSquared = new double[numPoints];

        // Initialize the elements.  Since the only point in resultSet is firstPoint,
        // this is very easy.
        for (int i = 0; i < numPoints; i++) {
            if (i != firstPointIndex) { // That point isn't considered
                double d = firstPoint.distanceFrom(pointList.get(i));
                minDistSquared[i] = d*d;
            }
        }

        while (resultSet.size() < k) {

            // Sum up the squared distances for the points in pointList not
            // already taken.
            double distSqSum = 0.0;

            for (int i = 0; i < numPoints; i++) {
                if (!taken[i]) {
                    distSqSum += minDistSquared[i];
                }
            }

            // Add one new data point as a center. Each point x is chosen with
            // probability proportional to D(x)2
            final double r = random.nextDouble() * distSqSum;

            // The index of the next point to be added to the resultSet.
            int nextPointIndex = -1;

            // Sum through the squared min distances again, stopping when
            // sum >= r.
            double sum = 0.0;
            for (int i = 0; i < numPoints; i++) {
                if (!taken[i]) {
                    sum += minDistSquared[i];
                    if (sum >= r) {
                        nextPointIndex = i;
                        break;
                    }
                }
            }

            // If it's not set to >= 0, the point wasn't found in the previous
            // for loop, probably because distances are extremely small.  Just pick
            // the last available point.
            if (nextPointIndex == -1) {
                for (int i = numPoints - 1; i >= 0; i--) {
                    if (!taken[i]) {
                        nextPointIndex = i;
                        break;
                    }
                }
            }

            // We found one.
            if (nextPointIndex >= 0) {

                final Vector p = pointList.get(nextPointIndex);

                resultSet.add(new Cluster(p));

                // Mark it as taken.
                taken[nextPointIndex] = true;

                if (resultSet.size() < k) {
                    // Now update elements of minDistSquared.  We only have to compute
                    // the distance to the new center to do this.
                    for (int j = 0; j < numPoints; j++) {
                        // Only have to worry about the points still not taken.
                        if (!taken[j]) {
                            double d = p.distanceFrom(pointList.get(j));
                            double d2 = d * d;
                            if (d2 < minDistSquared[j]) {
                                minDistSquared[j] = d2;
                            }
                        }
                    }
                }

            } else {
                // None found --
                // Break from the while loop to prevent
                // an infinite loop.
                break;
            }
        }

        return resultSet;
	}
            
            
}