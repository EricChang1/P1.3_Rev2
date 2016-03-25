package models;

/**
 * class modeling a ratio of two integers
 * @author martin
 */
public class Ratio 
{
	/**
	 * @param num numerator of ratio
	 * @param denom denominator of ratio
	 */
	public Ratio (long num, long denom)
	{
		mNum = num;
		mDenom = denom;
	}
	
	/**
	 * @param x number within range of double type
	 * @return product of x and the ratio
	 */
	public double apply (Number x)
	{
		return x.doubleValue() * getRatio();
	}
	
	public double getRatio() { return (double) (mNum / mDenom); }
	
	private long mNum, mDenom;
}
