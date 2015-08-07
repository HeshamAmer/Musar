package musar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class Artist {
	private Collection < Pair<String, Integer> > tags;
	
	private String name;
	
	public void addTag(String tag, int count) {
		tags.add(new Pair<String, Integer>(tag, count));
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public int getTagCount(String tag) {
		for(Pair<String, Integer> p : tags) {
			if(p.getKey().equals(tag))
				return p.getValue();
		}
		return -1;
	}
	
	public List<String> getTopTags(int threshhold) {
		List<String> result = new ArrayList<String>();
		
		for(Pair<String, Integer> p : tags) {
			if(p.getValue() >= threshhold)
				result.add(p.getKey());
		}
		return result;
	}
}
