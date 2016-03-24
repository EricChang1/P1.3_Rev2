package generic;

import java.util.ArrayList;

/**
 * custom set implementation
 * not iterable yet
 * @author martin
 * @param <T>
 */
public class Set<T extends Comparable<T>> implements Cloneable
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
	 * constructs set and adds elements in initial to set
	 * @param initials set of initial elements
	 */
	public Set (Iterable<T> initials)
	{
		this();
		for (T elem : initials)
		{
			if (!hasElement (elem))
				add (elem);
		}
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
	 * Note: references of this object will be used
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
	 * @return cloned set
	 * performs deep clone on data structure
	 * though elements are shallowly copied
	 */
	public Set<T> clone()
	{
		Set<T> clone = new Set<>();
		clone.mStore = this.mStore.clone();
		return clone;
	}
	
	
	/**
	 * @return elements stored in order
	 * @TODO implement iterator
	 */
	public ArrayList<T> getOrderedElements()
	{
		return mStore.getOrderedElements();
	}
	
	/**
	 * @param val a given value
	 * @return element stored with same value as val
	 */
	public T getElement (T val)
	{
		if (!mStore.hasElement (val))
			throw new SetException ("value " + val + " does not exist");
		return mStore.getElement (val);
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
	 * @return number of elements stored
	 */
	public int getSize() { return mStore.getSize(); }
	
	/**
	 * @param sub another given subset
	 * @return true if sub is a subset of this set
	 */
	public boolean isSubset (Set<T> sub)
	{
		for (T subElem : sub.getOrderedElements())
		{
			if (!this.hasElement (subElem))
				return false;
		}
		return true;
	}
	
	/**
	 * @param comp set to compare this set to
	 * @return true if this and comp are subsets of each other
	 */
	public boolean equals (Set<T> comp)
	{
		return (this.isSubset (comp) && comp.isSubset (this));
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
	 * erases all elements stored
	 */
	public void clear()
	{
		mStore.clear();
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
