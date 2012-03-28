package teme_ai.id3;

import java.util.ArrayList;

/**
 * The Class Nod.
 */
public class Nod {

	
	/** The neighbours. */
	public ArrayList<Nod> childs;
	
	/** The tranzitions. */
	public ArrayList<String> tranzitions = null;
	
	//can decide is leaf node or not
	/** The attribute. */
	public Attribute attribute = null;
	
	/** The class value. */
	public String classValue= null;
	
	/** The numeric tranzitions used to pint rules but with numeric discretized attributes. */
	public ArrayList<Interval> numericTranzitions = null;
	
	
	/**
	 * Instantiates a new nod.
	 */
	public Nod(){
		this.childs = new ArrayList<Nod>();
	}
}
