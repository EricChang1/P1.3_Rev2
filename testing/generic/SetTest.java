package testing.generic;

import java.util.Random;

import generic.Set;

public class SetTest 
{
	public static void main (String[] args)
	{
		SetTest test = new SetTest(10, 5);
		test.printContent();
		test.testUnion();
		test.testIntersection();
		test.testDifference();
	}
	
	public SetTest (int size1, int size2)
	{
		mSet1 = new Set<>();
		mSet2 = new Set<>();
		Random gen = new Random (System.currentTimeMillis());
		
		for (int cSize1 = 0; cSize1 < size1; ++cSize1)
		{
			int add = gen.nextInt (2 * (size1 + 1));
			while (mSet1.hasElement (add))
				add = gen.nextInt (2 * (size1 + 1));
			mSet1.add (add);
		}
		
		for (int cSize2 = 0; cSize2 < size2; ++cSize2)
		{
			int add = gen.nextInt (2 * (size2 + 1));
			while (mSet2.hasElement (add))
				add = gen.nextInt (2 * (size2 + 1));
			mSet2.add (add);
		}
	}
	
	
	public void printContent()
	{
		System.out.println ("set 1 " + mSet1);
		System.out.println ("set 2 " + mSet2);
	}
	
	public void testUnion()
	{
		System.out.println ("set 1 U set 2 " + mSet1.getUnion (mSet2));
		System.out.println ("set 2 U set 1 " + mSet2.getUnion (mSet1));
	}
	
	public void testIntersection()
	{
		System.out.println ("set 1 I set 2 " + mSet1.getIntersection (mSet2));
		System.out.println ("set 2 I set 1 " + mSet2.getIntersection (mSet1));
	}
	
	public void testDifference()
	{
		System.out.println ("set 1 \\ set 2 " + mSet1.getDifference (mSet2));
		System.out.println ("set 2 \\ set 1 " + mSet2.getDifference (mSet1));
	}
	
	
	
	
	private Set<Integer> mSet1, mSet2;
}
