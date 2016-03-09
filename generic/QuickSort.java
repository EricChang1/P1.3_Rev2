package generic;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Random;

/**
 * class performing a randomized quick sort on contents of a collection
 * extends array list and uses this data structure internally
 * @author martin
 * @param <T> a comparable type
 */
public class QuickSort<T extends Comparable<T>> extends ArrayList<T> 
{
	private static long cnt = 100;
	
	/**
	 * @param data list of data to be stored
	 * data will not be deep copied
	 */
	public QuickSort (Collection<T> data)
	{
		super (data);
		mRand = new Random (System.currentTimeMillis() % cnt);
		if (cnt < Long.MAX_VALUE)
			++cnt;
		else
			cnt = 100;
	}
	
	/**
	 * @param iStart first index of sequence
	 * @param iEnd last index of sequence
	 * sorts sequence from iStart to iEnd (inclusive)
	 */
	public void sort (int iStart, int iEnd)
	{
		int cLess = iStart, cLarger = iEnd;
		int pivot = mRand.nextInt (iEnd - iStart + 1) + iStart;
		T pElem = get (pivot);
		
		while (cLess <= cLarger)
		{
			while (cLess <= cLarger && get (cLess).compareTo (pElem) <= 0)
				++cLess;
			while (cLess <= cLarger && get (cLarger).compareTo (pElem) >= 0)
				--cLarger;
			if (cLess < cLarger)
				swap (cLess, cLarger);
		}
		if (cLess < iEnd && get (cLess).compareTo (pElem) > 0)
			swap (cLess, pivot);
		if (cLess > iEnd)
			--cLess;
		if (iStart < cLess)
			sort (iStart, cLess);
		if (cLess + 1 < iEnd)
			sort (cLess + 1, iEnd);
	}
	
	private void swap (int i1, int i2)
	{
		T temp = get (i1);
		set (i1, get (i2));
		set (i2, temp);
	}
	
	private Random mRand;
}
