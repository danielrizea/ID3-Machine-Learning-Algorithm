package teme_ai.id3;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class Attribute.
 */
public class Attribute {

	/** The DISCRET. */
	public static int DISCRET = 0;
	
	/** The NUMERIC. */
	public static int NUMERIC= 1;
		
	/** The name. */
	public String name;
	
	/** The type. */
	public int type;
	
	/** The pos in learning set. */
	public int posInLearningSet;

	
	
	/** The values for discrete attributes. */
	public ArrayList<String> discretValues;

	/** The numeric interval bounds for numeric attributes. */
	public ArrayList<Interval> numericIntervalBounds;
	
	/**
	 * Instantiates a new attribute.
	 *
	 * @param name the name
	 * @param posInLearningSet the pos in learning set
	 */
	public Attribute(String name, int posInLearningSet){
		
		discretValues = new ArrayList<String>();
		this.numericIntervalBounds = new ArrayList<Interval>();
		this.name = name;
		this.posInLearningSet = posInLearningSet;
	}
	
}
