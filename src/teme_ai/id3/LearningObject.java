package teme_ai.id3;

import java.util.ArrayList;

/**
 * The Class LearningObject.
 */
public class LearningObject {

	/** The values. */
	public ArrayList<AttributeValue> values;
	
	/** The class value. */
	public ClassCategory classValue;
	
	/**
	 * Instantiates a new learning object.
	 *
	 * @param values the values
	 */
	public LearningObject(ArrayList<AttributeValue> values,ClassCategory classValue){
		this.values = values;
		this.classValue = classValue;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String result = "";
		for(int j=0;j<values.size();j++)
			result += values.get(j).valueString + " ";
		
		result += classValue.name;
		
		return result;
	}
}
