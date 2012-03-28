package teme_ai.id3;

/**
 * The Class Interval.
 * A value is in this interval if and only if lowBound < val <=highBound
 */
public class Interval {

	public int lowBound;
	public int highBound;
	
	/**
	 * Instantiates a new interval.
	 *
	 * @param lowBound the low bound
	 * @param highBound the high bound
	 */
	public Interval(int lowBound, int highBound){
		this.lowBound = lowBound;
		this.highBound = highBound;
	}
	
	
	/**
	 * Instantiates a new interval.
	 */
	public Interval(){
		
	}
	
	
	/**
	 * Checks if is value in interval.
	 *
	 * @param val the val
	 * @return true, if is value in interval
	 */
	public boolean isValueInInterval(int val){
		if(lowBound< val && val<=highBound)
			return true;
		
		return false;
	}
}
