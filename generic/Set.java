package generic;

import java.util.ArrayList;

/**
 * 
 * @author martin
 *
 * @param <T>
 */
public class Set<T extends Comparable<T>>
{
	/**
	 * default exception for set class
	 * @author martin
	 */
	public static class SetException extends RuntimeException
	{
		public SetException() {}
		
		public SetException (String mess) { super (mess); }
	}
	
	/**
	 * default constructor
	 */
	public Set()
	{
		mStore = new AvlTree<>();
	}
	
	/**
	 * @param op2 another given set of same type
	 * @return union of this with op2
	 * NOTE: elements in this have priority over elements in op2.
	 * Thus if there exists a duplicate, the reference in this set
	 * will be used
	 */
	public Set<T> getUnion (Set<T> op2)
	{
		Set<T> union = new Set<>();
		//add elements in this
		for (T elem : this.getOrderedElements())
			union.add (elem);
		//add missing elements in op2
		for (T elem : op2.getOrderedElements())
		{
			if (!union.hasElement (elem))
				union.add (elem);
		}
		return union;
	}
	
	/**
	 * @param op2 another given set of same type
	 * @return intersection of both sets
	 */
	public Set<T> getIntersection (Set<T> op2)
	{
		Set<T> intersection = new Set<>();
		for (T elem : this.getOrderedElements())
		{
			if (op2.hasElement (elem))
				intersection.add (elem);
		}
		return intersection;
	}
	
	/**
	 * @param op2 another given set
	 * @return difference set between this and op2
	 */
	public Set<T> getDifference (Set<T> op2)
	{
		Set<T> difference = new Set<>();
		for (T elem : this.getOrderedElements())
		{
			if (!op2.hasElement (elem))
				difference.add (elem);
		}
		return difference;
	}
	
	
	/**
	 * @return elements stored in order
	 * @TODO implement iterator
	 */
	public ArrayList<T> getOrderedElements()
	{
		return mStore.getOrderedElements();
	}
	
	
	public String toString()
	{
		ArrayList<T> elems = getOrderedElements();
		String ret = "[ ";
		for (T elem : elems)
			ret += elem + ", ";
		ret += " ]";
		return ret;
	}
	
	/**
	 * @param elem element to search for
	 * @return true if elem is an element of this set
	 */
	public boolean hasElement (T elem)
	{
		return mStore.hasElement (elem);
	}
	
	/**
	 * @param add element to add to this set
	 * PRECONDITION: add may not already exist in this set
	 */
	public void add (T add)
	{
		if (mStore.hasElement (add))
			throw new SetException ("value " + add + " to be added exists already");
		mStore.add (add);
	}
	
	/**
	 * @param rem element to remove from this set
	 */
	public void remove (T rem)
	{
		mStore.remove (rem);
	}
	
	private AvlTree<T> mStore;
}
