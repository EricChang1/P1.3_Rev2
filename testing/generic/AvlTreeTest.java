package testing.generic;

import java.util.ArrayList;
import java.util.Random;

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
		test.addTest (10);
		//test.removeTest (5);
		test.cloneTest (3);
	}
	
	public AvlTreeTest()
	{
		mTree = new AvlTree<>();
	}
	
	
	public void addTest (int noElems)
	{
		for (int cAdd = 0; cAdd < noElems; ++cAdd)
			mTree.add (cAdd);
		System.out.println ("Tree after addition " + mTree.getOrderedElements());
	}
	
	public void removeTest (int noElems)
	{
		int maxElem = mTree.getSize();
		Random gen = new Random();
		
		for (int cRemove = 0; cRemove < noElems; ++cRemove)
		{
			int remElem = gen.nextInt (maxElem);
			if (mTree.hasElement (remElem))
			{
				System.out.println ("remove " + remElem);
				mTree.remove (remElem);
			}
			else
				System.out.println ("tree does not contain " + remElem);
		}
		System.out.println ("tree after deletion " + mTree.getOrderedElements());
	}
	
	public void cloneTest (int noAddTest)
	{
		Random gen = new Random();
		
		AvlTree<Integer> clone = mTree.clone();
		for (int cAdd = 0; cAdd < noAddTest; ++cAdd)
		{
			clone.add (gen.nextInt());
		}
		
		System.out.println ("Original tree" + mTree.getOrderedElements());
		System.out.println ("cloned & add tree " + clone.getOrderedElements());
	}
	
	private AvlTree<Integer> mTree;
}
