package algorithm;

import java.util.ArrayList;
import java.util.Stack;

import javax.swing.JProgressBar;

/**
 * class storing progress as double in [0, 1]
 * @author martin
 */
public class Progress 
{
	/**
	 * custom exception of progress class
	 * @author martin
	 */
	public static class ProgressException extends IllegalStateException
	{
		public ProgressException() {}
		
		public ProgressException (String message) { super (message); }
	}
	
	/**
	 * class encapsulating amount of increase
	 * can be split into even parts
	 * if a split part was not used, its value is added to the progress automatically,
	 * when unite is called, in order to reduce the amount of condition checking by the caller
	 * @author martin
	 */
	public class Increase implements Cloneable
	{
		/**
		 * returns copy of this increase
		 * having an empty parts stack
		 */
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
			if (!mCompletedStack.isEmpty())
				increasePerformed();
			mAmount /= parts;
			mPartStack.add (parts);
			mCompletedStack.push (parts);
		}
		
		/**
		 * merge split branch
		 */
		public void unite()
		{
			if (mPartStack.isEmpty())
				throw new IllegalStateException ("no previous split exists");
			
			int opsPending = mCompletedStack.peek();
			for (int cPending = 0; cPending < opsPending; ++cPending)	
				increase (this);
			mCompletedStack.pop();
			
			int top = mPartStack.pop();
			mAmount *= top;
		}
		
		private Increase (double init)
		{
			mAmount = init;
			mPartStack = new Stack<>();
			mCompletedStack = new Stack<>();
		}
		
		private void increasePerformed()
		{
			int top = mCompletedStack.pop();
			if (top <= 0)
				throw new ProgressException ("number of parts exhausted");
			mCompletedStack.push (--top);
		}
		
		private Stack<Integer> mPartStack;
		private Stack<Integer> mCompletedStack;
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
	 * @return progress in [0; 1]
	 */
	public double getProgress() { return mProg; }
	
	/**
	 * @param i increase to add to progress
	 */
	public void increase (Increase i)
	{
		/*if (!epsilonEquals (1.0, mProg + i.getAmount()) && mProg + i.getAmount() > 1.0)
			throw new IllegalStateException ("progress above 1.0 is not permissible");*/
		if (i.getAmount() == Double.NaN)
			throw new IllegalArgumentException ("NaN increase not permissible");
		
		i.increasePerformed();
		mProg += i.getAmount();
		
		double newBarVal = (mProgBar.getMaximum() - mProgBar.getMinimum()) * mProg;
		mProgBar.setValue ((int) newBarVal);
	}
	
	/**
	 * @param bar progress bar to set
	 * sets progress bar to be updated
	 */
	public void setProgressBar (JProgressBar bar)
	{
		mProgBar = bar;
	}
	
	//enable for multi threading private ArrayList<Increase> mIncreaseOps;
	private double mProg;
	private boolean mLock;
	
	private JProgressBar mProgBar;
}
