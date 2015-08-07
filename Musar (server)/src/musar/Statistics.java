package musar;


/**
 * 
 * @author haron
 * Class Statistics for computing mathematical stats like mean and variance
 */
public class Statistics {
	private double[] data;
	private int size;
	
	public Statistics(double[] distances) {
        this.data = distances;
        size = distances.length;
    }   

    double getMean() {
        double sum = 0.0;
        for(double a : data)
            sum += a;
        return sum/size;
    }

    double getVariance() {
        double mean = getMean();
        double temp = 0;
        for(double a :data)
            temp += (mean-a)*(mean-a);
        return temp/size;
    }
}
