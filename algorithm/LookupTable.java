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
			mUsed = new Set<>();
			mUnused = new Subset();
			mOptimal = true;
			
			for (Resource av : available.getOrderedElements())
				mAvailable.add (av);
			
			computeUnusedResources();
			determineOptimality();
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
			mUsed = new Set<>();
			mAvailable = new Subset();
			mOptimal = true;
			
			for (Resource av : resources.getOrderedElements())
				mAvailable.add (av);
			computeUnusedResources();
			determineOptimality();
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
		 * assumes container sizes are the same
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
		
		public boolean isOptimal() { return mOptimal; }
		
		/**
		 * @param comp entry to compare this with
		 * @return true if each available resource in comp is available in this
		 */
		public boolean hasSubsetAvailable (Entry comp)
		{
			if (this.mAvailable.getSize() < comp.mAvailable.getSize())
				return false;
			
			Set<BlockResource> tAvail = new Set<>();
			for (Resource avail : comp.mAvailable.getOrderedElements())
				tAvail.add (new BlockResource (avail));
			
			
			for (Resource avail : comp.mAvailable.getOrderedElements())
			{
				BlockResource blockAvail = new BlockResource (avail);
				if (!tAvail.hasElement (blockAvail))
					return false;
				BlockResource matching = tAvail.getElement (blockAvail);
				if (matching.getInventory() < blockAvail.getInventory())
					return false;
			}
			return true;
				
		}
		
		/**
		 * @param comp entry to compare with
		 * @return true if comp has all resources available which are used in this
		 */
		public boolean containsAllUsed (Entry comp)
		{
			//match if no resources used in either entry
			if (this.mUsed.getSize() == 0 && comp.mUsed.getSize() == 0)
				return true;
			
			//if optimal only check if comp has at least used amount of resources
			if (this.mUsed.getSize() <= comp.mAvailable.getSize() && comp.mUsed.getSize() <= this.mAvailable.getSize())
			{
				//idea return equal (== indicate match during look up) if
				//number of available resources in comp if the used resources are contained in the available resources
				//of the other
				
				ArrayList<BlockResource> tUsed = this.mUsed.getOrderedElements();
				Set<BlockResource> compAvail = new Set<>();
				for (Resource compRes : comp.mAvailable.getOrderedElements())
					compAvail.add (new BlockResource (compRes));
				
				int cUsed = 0;
				while (cUsed < tUsed.size())
				{
					if (!compAvail.hasElement (tUsed.get (cUsed)))
						return false;
					else if (compAvail.getElement (tUsed.get (cUsed)).getInventory() < tUsed.get (cUsed).getInventory())
						return false;
					else
						++cUsed;
				}
				return true;
			}
			return false;
		}
		
		
		private void determineOptimality()
		{
			int maxVolume = 1;
			for (int cDim = 0; cDim < mContainer.getDimensions().size(); ++cDim)
				maxVolume *= mContainer.getDimensions (cDim);
			
			
			ArrayList<Resource> listAvailable = mAvailable.getOrderedElements();
			int cRes = 0;
			while (mOptimal && cRes < listAvailable.size())
			{
				Resource res = listAvailable.get (cRes);
				if (res.getInventory() * res.getVolume() < maxVolume)
					mOptimal = false;
				++cRes;
			}
		}
		
		private void computeUnusedResources ()
		{
			mUsed.clear();
			for (int cBlock = 0; cBlock < mContainer.getAmountOfBlocks(); ++cBlock)
			{
				BlockResource currRes = new BlockResource (mContainer.getBlock (cBlock), 1);
				if (mUsed.hasElement (currRes))
					mUsed.getElement (currRes).refill();
				else
					mUsed.add (currRes);
			}
			//unused = difference + intersection deducting quantities
			mUnused.clear();
			
			for (Resource avail : mAvailable.getOrderedElements())
			{
				BlockResource blockAvail = new BlockResource (avail);
				int left = avail.getInventory();
				if (mUsed.hasElement (blockAvail))
					left -= mUsed.getElement (blockAvail).getInventory(); 
				if (left > 0)
					mUnused.add (new Resource (avail.getBlock(), left));
			}
		}
		
		private Container mContainer;
		private Subset mUnused, mAvailable;
		private Set<BlockResource> mUsed;
		private boolean mOptimal;
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
	
	
	public Entry getEquivalent (int d, int w, int h, Entry e)
	{
		ArrayList<Integer> sDim = sortIndices(d, w, h);
		ArrayList<Entry> entries = get (sDim.get (0)).get (sDim.get (1)).get (sDim.get (2)).getOrderedElements();
		for (Entry comp : entries)
		{			
			if ((comp.isOptimal() || comp.hasSubsetAvailable (e)) && comp.containsAllUsed (e))
		//	if (comp.hasSubsetAvailable (e) && comp.containsAllUsed (e))
				return comp;
		}
		return null;
	}
	
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
	
	public void addEntry (int d, int w, int h, Entry e)
	{
		ArrayList<Integer> is = sortIndices (d, w, h);
		get (is.get (0)).get (is.get (1)).get (is.get (2)).add (e);
		
	//	System.out.print ("added " + is.get(0) + " " + is.get(1) + " " + is.get(2));
	//	System.out.println (" having " + e.mAvailable);
	}
}
