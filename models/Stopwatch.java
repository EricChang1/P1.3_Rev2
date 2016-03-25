package models;

/**
 * class to compute elapsed time
 * @author martin
 */
public class Stopwatch 
{
	public static class Seconds extends Ratio
	{
		public Seconds() { super (1, 1000); }
	}
	
	public static class Minutes extends Ratio
	{
		public Minutes() { super (1, 60000); }
	}
	
	public static class Hours extends Ratio
	{
		public Hours() { super (1, 3600000); }
	}
	
	
	public class StopwatchException extends IllegalStateException
	{
		public StopwatchException (String message) { super (message); }
	}
	
	
	/**
	 * default constructor: non running stopwatch
	 */
	public Stopwatch()
	{
		mStart = -1;
		mElapsed = -1;
		mRunning = false;
	}
	
	/**
	 * @return elapsed time in milliseconds
	 * Precondition: timer was started and subsequently stopped beforehand
	 */
	public long getElapsedTime() 
	{ 
		if (mElapsed < 0)
			throw new StopwatchException ("no elapsed time");
		return mElapsed; 
	}
	
	/**
	 * @param conversion conversion ratio
	 * @return elapsed time scaled by ratio
	 */
	public long getElapsedTime (Ratio conversion)
	{
		return (long) conversion.apply (mElapsed);
	}
	
	/**
	 * @return true if stopwatch is running, false otherwise
	 */
	public boolean isRunning() { return mRunning; }
	
	/**
	 * @return true if the stopwatch was started and subsequently stopped, 
	 * in order to compute the elapsed time
	 */
	public boolean hasElapsed() { return (mElapsed >= 0); }
	
	/**
	 * starts the timer
	 * sets running flag to true
	 */
	public void start()
	{
		if (mRunning)
			throw new StopwatchException ("stopwatch is already running");
		mStart = System.currentTimeMillis();
		mElapsed = -1;
		mRunning = true;
	}
	
	
	/**
	 * stops the timer, 
	 * sets running flag to false, 
	 * computes elapsed time. 
	 * Precondition: stopwatch is running
	 */
	public void stop()
	{
		if (!mRunning)
			throw new StopwatchException ("stopwatch is not running");
		mElapsed = System.currentTimeMillis() - mStart;
		mStart = -1;
		mRunning = false;
	}
	
	/**
	 * deletes elapsed time (thus reverts object to initial state)
	 * Precondition: stopwatch is not running
	 */
	public void reset()
	{
		if (mRunning)
			throw new StopwatchException ("stopwatch is not running");
		mElapsed = -1;
	}
	
	
	private long mStart, mElapsed;
	private boolean mRunning;
}
