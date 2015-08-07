package musar;



import java.io.Serializable;

/**
 * 
 * @author haron pc
 * Class Pair represents a pair of objects
 * @param <K> Key (first value in the pair)
 * @param <V> Value (second value in the pair)
 */
public class Pair<K extends Comparable <K>, V extends Comparable<V> > implements Comparable< Pair<K, V> >, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3123258186177735732L;
	private K key;
	private V value;
	
	public Pair (K key, V value) {
		this.key = key;
		this.value = value;
	}
	
	public K getKey() {
		return key;
	}
	
	public V getValue() {
		return value;
	}
	
	@Override
	public int compareTo(Pair<K, V> that) {
		int cmp = this.getValue().compareTo(that.getValue());
		return cmp;
	}
}
