package testing.algoTest;

import java.util.*;

import algorithm.QuickSort;

public class QuickSortTest 
{
	
	public static void main (String[] args)
	{
		QuickSortTest test = new QuickSortTest(10);
		test.test();
	}
	
	
	public QuickSortTest (int noElems)
	{
		Random r = new Random();
		ArrayList<Integer> seq = new ArrayList<>();
		for (int cElem = 0; cElem < noElems; ++cElem)
			seq.add (r.nextInt (noElems));
		mSort = new QuickSort<> (seq);
	}
	
	public void test()
	{
		System.out.println ("before");
		System.out.println (mSort);
		mSort.sort (0, mSort.size() - 1);
		System.out.println ("after");
		System.out.println (mSort);
	}
	
	private QuickSort<Integer> mSort;
}
