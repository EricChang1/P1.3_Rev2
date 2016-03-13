package testing.generic;

import java.util.ArrayList;

import generic.*;

/**
 * test class for avl tree
 * @author martin
 */
public class AvlTreeTest 
{
	public static void main (String[] args)
	{
		AvlTreeTest test = new AvlTreeTest();
	}
	
	public AvlTreeTest()
	{
		mTree = new AvlTree<>();
		for (int cAdd = 0; cAdd < 122; ++cAdd)
			mTree.add (cAdd);
		/*
		for (int cRem = 9; cRem >= 0; --cRem)
			mTree.remove (cRem);
		*/
		//mTree.remove(3);
		//mTree.remove (1);
		//mTree.remove (2);
		ArrayList<Integer> order = mTree.getOrderedElements();
		System.out.println (order);
	}
	
	private AvlTree<Integer> mTree;
}
