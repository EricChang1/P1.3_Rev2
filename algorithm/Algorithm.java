package algorithm;

import java.util.ArrayList;

import models.Container;
import models.Resource;
import models.Stopwatch;

/**
 * base class for all algorithms
 * the class can be in 3 states: 1) not started, 2) started but not terminated, 3) terminated
 * on construction and after initialization, the object will be in state 1)
 * after call to run() the object will be in state 2)
 * after call to setAlgoDone() the object will be in state 3)
 * the only way to go back to state 1) from state 2) and 3) is to call init()
 * @author martin
 */
public abstract class Algorithm implements Runnable
{
	@SuppressWarnings("serial")
	public static class AlgorithmNotInitializedException extends IllegalStateException
	{
		public AlgorithmNotInitializedException() {}
		
		public AlgorithmNotInitializedException (String message) {super (message); }
	}
	
	@SuppressWarnings("serial")
	public static class AlgorithmNotStartedException extends IllegalStateException
	{
		public AlgorithmNotStartedException() {}
		
		public AlgorithmNotStartedException (String message) {super (message); }
	}
	
	@SuppressWarnings("serial")
	public static class AlgorithmRunningException extends IllegalStateException
	{
		public AlgorithmRunningException() {}
		
		public AlgorithmRunningException (String message) {super (message); }
	}
	
	@SuppressWarnings("serial")
	public static class AlgorithmTerminatedException extends IllegalStateException
	{
		public AlgorithmTerminatedException() {}
		
		public AlgorithmTerminatedException (String message) {super (message); }
	}
	
	/**
	 * default constructor
	 */
	public Algorithm ()
	{
		mContainer = null;
		mPieces = null;
		mProgress = new Progress();
		mRunningTime = new Stopwatch();
	}
	
	/**
	 * @return a clone of the internal container once the algorithm was started
	 */
	public Container getFilledContainer()
	{
		if (!isAlgoDone())
			throw new AlgorithmNotStartedException ("tried to access presumably filled container before algorithm started");
		return mContainer.clone();
	}
	
	/**
	 * @return remaining increase to progress
	 * Precondition: algorithm is started but did not terminate
	 */
	public Progress getProgress()
	{
		if (isAlgoDone())
			throw new AlgorithmTerminatedException();
		return mProgress;
	}
	
	/**
	 * @return the running time of the algorithm in seconds
	 */
	public long getRunningTime()
	{
		return mRunningTime.getElapsedTime (new Stopwatch.Seconds());
	}
	
	
	/**
	 * @return true if algorithm was set to terminated
	 */
	public boolean isAlgoDone()
	{
		return mRunningTime.hasElapsed();
	}
	
	/**
	 * @return true if algorithm was started
	 */
	public boolean isAlgoStarted()
	{
		return mRunningTime.isRunning();
	}
	
	/**
	 * Initializes resources 
	 * Postcondition: starting/termination flags will be reset
	 * @param container container to perform algorithm for
	 * @param pieces pieces available
	 */
	public void init (Container container, ArrayList <Resource> pieces)
	{
		if (mRunningTime.isRunning())
			throw new AlgorithmRunningException ("cannot initialize while algorithm is running");
		
		mContainer = container;
		mPieces = pieces;
		mRunningTime.reset();
	}
	
	/**
	 * @param endAction action to be performed once the algorithm finishes
	 */
	public void setEndAction (Runnable endAction)
	{
		mEndAction = endAction;
	}
	
	/**
	 * run algorithm
	 */
	public void run()
	{
		System.out.println ("algo runs");
		if (mContainer == null || mPieces == null)
			throw new AlgorithmNotInitializedException ("Missing init parameters to run the algorithm");
		if (isAlgoStarted())
			throw new AlgorithmRunningException ("tried to run algorithm object already running");
		if (isAlgoDone())
			throw new AlgorithmTerminatedException ("tried to run already terminated algorithm");
		
		mRunningTime.start();
	}
	
	/**
	 * @return reference to internal container
	 * Precondition algorithm needs to be terminated
	 */
	protected Container getContainer()
	{
		if (!isAlgoStarted())
			throw new AlgorithmNotStartedException ("tried to accepublic void run()");
		if (isAlgoDone())
			throw new AlgorithmTerminatedException ("tried to access internal container after algorithm terminated");
		return mContainer;
	}
	
	/**
	 * @return list of internal resource objects
	 * Precondition: algorithm needs to be started but not terminated
	 */
	protected ArrayList <Resource> getPieces()
	{
		if (!isAlgoStarted())
			throw new AlgorithmNotStartedException ("tried to access internal pieces before algorithm was started");
		if (isAlgoDone())
			throw new AlgorithmTerminatedException ("tried to access internal container after algorithm terminated");
		return mPieces;
	}
	
	/**
	 * set algorithm to done making getContainer and getPieces inaccessible
	 * Precondition: algorithm needs to be started but not terminated
	 */
	protected void setAlgoDone()
	{
		System.out.println ("algo done");
		if (!isAlgoStarted())
			throw new AlgorithmNotStartedException ("algorithm is not started, cannot set done");
		
		getProgress().increase (getProgress().getRemainingIncrease());
		mRunningTime.stop();
		if (mEndAction != null)
		{
			mEndAction.run();
			System.out.println ("mEndAction" + mEndAction);
		}
	}
	
	private Container mContainer;
	private ArrayList <Resource> mPieces;
	
	private Progress mProgress;
	private Runnable mEndAction;
	
	private Stopwatch mRunningTime;
}
