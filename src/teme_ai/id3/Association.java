package teme_ai.id3;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

/**
 * The Class Association.
 */
class Association{
	
	/** The ocurances. */
	public Integer ocurances;
	
	/** The classes. */
	public HashMap<String,Integer> classes;
	
	/**
	 * Instantiates a new association.
	 */
	public Association()
	{
		this.classes = new HashMap<String,Integer>();
	}
	
	/**
	 * Adds the class.
	 *
	 * @param newClass the new class
	 */
	public void addClass(String newClass){
		
		Integer val = classes.get(newClass);
		
		if(val == null)
			classes.put(newClass, 1);
		else
			classes.put(newClass,(val+1));
	}
	
	/**
	 * Gets the dominant class.
	 *
	 * @return the dominant class
	 */
	public String getDominantClass(){
		
		String dominantClass= "";
		int count = 0;
		if(classes.size()>0){
			Set<Entry<String,Integer>> set =classes.entrySet();
			
			Iterator<Entry<String,Integer>> it = set.iterator();
			
			while(it.hasNext()){
				Entry<String,Integer> entry = it.next();
				if(entry.getValue() > count){
					count = entry.getValue();
					dominantClass = entry.getKey();
				}
			}
			return dominantClass;
		}
			
		return null;
	}
}