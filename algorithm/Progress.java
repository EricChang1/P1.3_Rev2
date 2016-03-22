package algorithm;

import java.util.ArrayList;

/**
 * class storing progress as double in [0, 1]
 * @author martin
 */
public class Progress 
{
	
	/**
	 * class encapsulating amount of increase
	 * can be split into even parts
	 * @author martin
	 */
	public static class Increase implements Cloneable
	{
		public Increase (double init)
		{
			mAmount = init;
		}
		
		public Increase clone() { return new Increase (getAmount()); }
		
		/**
		 * @return amount of increase stored
		 */
		public double getAmount() { return mAmount; }
		
		/**
		 * @param parts number of parts to split amount into
		 */
		public void split (int parts)
		{
			mAmount /= mAmount;
		}
		
		/**
		 * @param ratio ratio of current amount to store as new amount
		 */
		public void split (double ratio)
		{
			assert (ratio > 0 && ratio <= 1);
			mAmount *= ratio;
		}
		
		private double mAmount;
	}
	
	/**
	 * @param d1 a double
	 * @param d2 another double
	 * @return true if d1 > d2 - e && d1 < d2 + e
	 */
	public static boolean epsilonEquals (double d1, double d2)
	{
		return (d1 > d2 - EPSILON && d1 < d2 + EPSILON);
	}
	
	public static double EPSILON = 0.00001;
	
	/**
	 * initializes progress to start: 0.0
	 */
	public Progress()
	{
		mLock = false;
		mProg = 0.0;
	}
	
	/**
	 * @return increase object which would increase this progress to the maximum
	 */
	public Increase getRemainingIncrease()
	{
		return new Increase (1 - mProg);
	}
	
	/**
	 * @param i increase to add to progress
	 */
	public void increase (Increase i)
	{
		if (!epsilonEquals (1.0, mProg + i.getAmount()) && mProg + i.getAmount() > 1.0)
			throw new IllegalStateException ("progress above 1.0 is not permissible");
		mProg += i.getAmount();
	}
	
	//enable for multi threading private ArrayList<Increase> mIncreaseOps;
	private double mProg;
	private boolean mLock;
}
