package algorithm;

import generic.Set;

import java.util.ArrayList;
import java.util.LinkedList;

import models.Container;
import models.Block;
import algorithm.DynamicAlgo.Resource;
import algorithm.DynamicAlgo.BlockResource;
import algorithm.DynamicAlgo.Subset;

import algorithm.LookupTable.Entry;

@SuppressWarnings("serial")
/**
 * class used for dynamic programming
 * to store solutions of sub problems
 * @author martin
 */
public class LookupTable extends ArrayList <ArrayList <ArrayList <ArrayList <Entry>>>>
{
	/**
	 * class containing information of one entry of table
	 * @author martin
	 */
	public class Entry
	{
		/**
		 * parametric constructor
		 * @param cont container to store
		 */
		public Entry (Container cont)
		{
			mContainer = cont;
			mUsed = new Set<>();
			for (int cBlock = 0; cBlock < mContainer.getAmountOfBlocks(); ++cBlock)
			{
				BlockResource currRes = new BlockResource (mContainer.getBlock (cBlock), 1);
				if (mUsed.hasElement (currRes))
					mUsed.getElement (currRes).refill();
				else
					mUsed.add (currRes);
			}
		}
		
		/**
		 * @param available list of resources available (will remain untouched)
		 * @return list of resources available minus resources used at this entry
		 * Note: no block objects will be cloned
		 * Note: only resource objects whose inventory is altered will be constructed
		 */
		public Subset getUnusedResources (Subset available)
		{
			//unused = difference + intersection deducting quantities
			Subset unused = new Subset();
			for (Resource avail : available.getOrderedElements())
			{
				BlockResource bAvail = new BlockResource (avail);
				if (mUsed.hasElement (bAvail))
				{
					int left = avail.getInventory() - mUsed.getElement (bAvail).getInventory(); 
					if (left > 0)
						unused.add (new Resource (avail.getBlock(), left));
				}
				else
					unused.add (avail);
			}
			
			return unused;
		}
		
		/**
		 * @return value of current entry
		 */
		public double getValue() { return mContainer.getValue(); }
		
		private Container mContainer;
		private Set<BlockResource> mUsed;
	}
	
	/**
	 * @param d depth index
	 * @param w width index
	 * @param h height index
	 * @return list of given indices such that list[0]>=list[1]>=list[2]
	 */
	public static ArrayList<Integer> sortIndices (int d, int w, int h)
	{
		ArrayList<Integer> sorted = new ArrayList<>();
		sorted.add (Math.max (d, Math.max (w, h)));
		sorted.add (Math.max (Math.min (d, Math.max (w, h)), Math.min (Math.max (d, w), h)));
		sorted.add (Math.min (d, Math.min (w, h)));
		return sorted;
	}
	
	/**
	 * parametric constructor
	 * initializes object to 4d matrix of size [d, w, h, sets]
	 * each cell containing a null reference (caution!)
	 * @param d number of depth increments of container
	 * @param w number of width increments of container
	 * @param h number of height increments of container
	 * @param sets number of subsets
	 */
	public LookupTable (int d, int w, int h, int sets)
	{
		for (int cD = 0; cD < d; ++cD)
		{
			ArrayList <ArrayList<ArrayList<Entry>>> newD = new ArrayList<ArrayList<ArrayList<Entry>>>();
			for (int cW = 0; cW < w; ++cW)
			{
				ArrayList <ArrayList<Entry>> newW = new ArrayList<ArrayList<Entry>>();
				for (int cH = 0; cH < h; ++cH)
				{
					ArrayList <Entry> newH = new ArrayList<>();
					for (int cS = 0; cS < sets; ++cS)
						newH.add (null);
					newW.add(newH);
				}
				newD.add (newW);
			}
			add (newD);
		}
	}
	
	public Entry get (int d, int w, int h, int s)
	{
		ArrayList<Integer> is = sortIndices (d, w, h);
		return get (is.get (0)).get (is.get (1)).get (is.get (2)).get(s);
	}
	
	/**
	 * @param d depth index
	 * @param w width index
	 * @param h height index
	 * @param s subset index
	 * @return reference of container stored at given combination of indices
	 */
	public Container getContainer (int d, int w, int h, int s)
	{
		return get (d, w, h, s).mContainer;
	}
	
	/**
	 * @param d depth index
	 * @param w width index
	 * @param h height index
	 * @param s subset index
	 * @return true if cell requested is no longer default initialized to null
	 */
	public boolean isSet (int d, int w, int h , int s)
	{
		return (get (d, w, h, s) != null);
	}
	
	public void set (Entry val, int d, int w, int h, int s)
	{
		ArrayList<Integer> is = sortIndices (d, w, h);
		get (is.get (0)).get (is.get (1)).get (is.get (2)).set (s, val);
	}
}
