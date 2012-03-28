package teme_ai.id3;

// TODO: Auto-generated Javadoc
/**
 * The Class AttributeValue.
 */
public class AttributeValue {
	
	/** The value discret. */
	private String valueDiscret;
	
	/** The value numeric. */
	private int valueNumeric;
	
	public String valueString;
	
	private boolean undefined = false;
	
	/** The type. */
	public int type ;
	
	
	/**
	 * Instantiates a new attribute value.
	 *
	 * @param val the val
	 */
	public AttributeValue(String val,Integer type){
		
		this.type = type;
		
		if(val.equals("?")){
			undefined = true;
		}
		
		if(type == Attribute.DISCRET){	
			this.valueDiscret = val;
		}
		else
		{
			if(!undefined)
				this.valueNumeric = Integer.parseInt(val);
		}
		//add and a string representation of the attribute
		this.valueString = val;
	}
	
	/**
	 * Checks if is undefined.
	 *
	 * @return true, if is undefined
	 */
	public boolean isUndefined(){
		return undefined;
	}
	
	/**
	 * Sets the value.
	 *
	 * @param val the new value
	 */
	public void setValue(String val){
		
		if(type == Attribute.DISCRET){	
			this.valueDiscret = val;
		}
		else
		{
			if(!undefined)
				this.valueNumeric = Integer.parseInt(val);
		}
		//add and a string representation of the attribute
		this.valueString = val;
	}
	
	/**
	 * Sets the value.
	 *
	 * @param val the new value
	 */
	public void setValue(Integer val){
		this.valueNumeric = val;
	}
	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public Object getValue(){
		if(this.type == Attribute.NUMERIC)
			return this.valueNumeric;
		else
			return this.valueDiscret;
	}
}
