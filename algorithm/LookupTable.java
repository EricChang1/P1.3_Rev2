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
public class LookupTable extends ArrayList <ArrayList <ArrayList <Set <Entry>>>>
{
	/**
	 * class containing information of one entry of table
	 * @author martin
	 */
	public class Entry implements Comparable <Entry>
	{
		/**
		 * parametric constructor
		 * @param cont container to store
		 * @param set of resources available before container was filled
		 */
		public Entry (Container cont, Set<Resource> available)
		{
			mContainer = cont;
			mAvailable = new Subset();
			for (Resource av : available.getOrderedElements())
				mAvailable.add (av);
			mUnused = new Subset();
			computeUnusedResources();
		}
		
		/**
		 * constructor for mock objects
		 * to be used for look ups
		 * @param resources list of available resources
		 */
		public Entry (Set<Resource> resources)
		{
			mContainer = new Container (0, 0, 0);
			mUnused = new Subset();
			mAvailable = new Subset();
			for (Resource av : resources.getOrderedElements())
				mAvailable.add (av);
			computeUnusedResources();
		}
		
		/**
		 * @param available list of resources available (will remain untouched)
		 * @return list of resources available minus resources used at this entry
		 * Note: no block objects will be cloned
		 * Note: only resource objects whose inventory is altered will be constructed
		 */
		public Subset getUnusedResources() { return mUnused; }
		
		public Container getContainer() { return mContainer; }
		
		/**
		 * @param comp entry to compare with
		 * @return -1 if less, 0 if equal, 1 if larger
		 * uses subset of used resources to determine comparison outcome
		 */
		public int compareTo (Entry comp)
		{
			ArrayList<Resource> tRes = this.mAvailable.getOrderedElements();
			ArrayList<Resource> cRes = comp.mAvailable.getOrderedElements();
			
			for (int cComp = 0; cComp < tRes.size() && cComp < cRes.size(); ++cComp)
			{
				int comparison = tRes.get (cComp).compareTo (cRes.get (cComp));
				if (comparison != 0)
					return comparison;
			}
			
			if (tRes.size() < cRes.size())
				return -1;
			if (tRes.size() > cRes.size())
				return 1;
			return 0;
		}
		
		/**
		 * @return value of current entry
		 */
		public double getValue() { return mContainer.getValue(); }
		
		
		private void computeUnusedResources ()
		{
			Set<BlockResource> used = new Set<>();
			for (int cBlock = 0; cBlock < mContainer.getAmountOfBlocks(); ++cBlock)
			{
				BlockResource currRes = new BlockResource (mContainer.getBlock (cBlock), 1);
				if (used.hasElement (currRes))
					used.getElement (currRes).refill();
				else
					used.add (currRes);
			}
			//unused = difference + intersection deducting quantities
			mUnused.clear();
			
			for (Resource avail : mAvailable.getOrderedElements())
			{
				BlockResource blockAvail = new BlockResource (avail);
				int left = avail.getInventory();
				if (used.hasElement (blockAvail))
					left -= used.getElement (blockAvail).getInventory(); 
				if (left > 0)
					mUnused.add (new Resource (avail.getBlock(), left));
			}
		}
		
		private Container mContainer;
		private Subset mUnused, mAvailable;
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
	public LookupTable (int d, int w, int h)
	{
		for (int cD = 0; cD < d; ++cD)
		{
			ArrayList <ArrayList<Set<Entry>>> newD = new ArrayList<>();
			for (int cW = 0; cW < w; ++cW)
			{
				ArrayList <Set<Entry>> newW = new ArrayList<>();
				for (int cH = 0; cH < h; ++cH)
				{
					Set <Entry> newH = new Set<>();
					newW.add(newH);
				}
				newD.add (newW);
			}
			add (newD);
		}
	}
	
	public Entry get (int d, int w, int h, Entry e)
	{
		ArrayList<Integer> is = sortIndices (d, w, h);
		return get (is.get (0)).get (is.get (1)).get (is.get (2)).getElement(e);
	}
	
	
	/**
	 * @param d depth index
	 * @param w width index
	 * @param h height index
	 * @param s subset index
	 * @return reference of container stored at given combination of indices
	 */
	/*
	public Container getContainer (int d, int w, int h, int s)
	{
		return get (d, w, h, s).mContainer;
	}*/
	
	/**
	 * @param d depth index
	 * @param w width index
	 * @param h height index
	 * @param s subset index
	 * @return true if cell requested is no longer default initialized to null
	 */
	public boolean isSet (int d, int w, int h , Entry e)
	{
		ArrayList<Integer> is = sortIndices (d, w, h);
		return (get (is.get (0)).get (is.get (1)).get (is.get (2)).hasElement (e));
	}
	
	public void addEntry (Entry val, int d, int w, int h, Entry e)
	{
		ArrayList<Integer> is = sortIndices (d, w, h);
		get (is.get (0)).get (is.get (1)).get (is.get (2)).add (e);
	}
}
