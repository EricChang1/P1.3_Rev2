package testing.generic;

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
		for (int cAdd = 0; cAdd < 10; ++cAdd)
			mTree.add (cAdd);
		/*
		for (int cRem = 9; cRem >= 0; --cRem)
			mTree.remove (cRem);
		*/
		//mTree.remove(3);
		//mTree.remove (1);
		mTree.remove (2);
	}
	
	private AvlTree<Integer> mTree;
}
