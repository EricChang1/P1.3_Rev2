package algorithm;

import geometry.Cuboid;

import java.util.*;

import models.Block;
import models.Container;
import models.Glue;
import models.Resource;
import models.Matrix.*;

import algorithm.LookupTable.Entry;

/**
 * class performing a dynamic algorithm to solve the polycube puzzle
 * @author martin
 */
public class DynamicAlgo extends Algorithm 
{
	/**
	 * class storing a set of blocks
	 * modelling a subset
	 * @author martin
	 */
	public static class Subset
	{	
		public Subset ()
		{
			mResources = new LinkedList<>();
			mVolume = 0;
		}
		
		public Subset clone()
		{
			Subset s = new Subset();
			for (Resource r : this.mResources)
				s.mResources.add (r.clone());
			s.mVolume = this.mVolume;
			return s;
		}
		
		public LinkedList <Resource> getResources()
		{
			return mResources;
		}
		
		public String toString()
		{
			String s = new String();
			for (Resource r : mResources)
				s += r.getVolume() + " vol x " + r.getInventory() + ", ";
			return s;
		}
		
		public int getVolume()
		{
			return mVolume;
		}
		
		public boolean equals (Subset comp)
		{
			return this.mResources.equals (comp.mResources);
		}
		
		public boolean equals (LinkedList <Resource> comp)
		{
			return comp.equals (mResources);
		}
		
		public void addResource (Resource bAdd)
		{
			if (bAdd.isInfinite())
				throw new IllegalArgumentException ("only finite resources can be added to form finite subsets");
			mResources.add(bAdd.clone());
			mVolume += bAdd.getVolume() * bAdd.getInventory();
		}
		
		private LinkedList <Resource> mResources;
		private int mVolume;
	}
	
	
	public DynamicAlgo() 
	{
		mSubsets = new ArrayList<>();
	}
	
	
	/**
	 * @param freeCuboids free cuboids obtained from container
	 * @param subsetIndex index of subset available
	 * @return optimal order in which the free cuboids should be filled to the full extent
	 */
	public ArrayList <Container> orderFreeCuboids (ArrayList <Cuboid> freeCuboids, int subsetIndex)
	{
		/**
		 * class used to compute and store list of containers
		 * corresponding to a given list of cuboids and a given
		 * subset
		 * @author martin
		 */
		class Order
		{
			public Order (LinkedList <Cuboid> cubes, int subsetIndex)
			{
				mCubes = cubes;
				mConts = new ArrayList <>();
				for (Cuboid free : mCubes)
				{
					ArrayList <Integer> cubeDims = free.getDimensions();
					Entry e = mLookupTable.get (cubeDims.get (0), cubeDims.get (1), cubeDims.get (2), subsetIndex);
					ArrayList <Resource> remainder = e.getUnusedResources (mSubsets.get (subsetIndex).getResources());
					subsetIndex = getSubsetIndex (remainder);
					Container lookedUp = mLookupTable.getContainer (cubeDims.get (0), cubeDims.get (1), cubeDims.get (2), subsetIndex); 
					Glue gluePos = new Glue (new IntegerMatrix (3, 1)).getClosest (free.getVertices());
					lookedUp.glue (gluePos);
					mConts.add (lookedUp);
					mVal += e.getValue();
				}
			}
			
			public ArrayList<Container> getOrderedContainers()
			{
				ArrayList <Container> ordered = new ArrayList<>();
				for (Container c : mConts)
					ordered.add (c.clone());
				return ordered;
			}
			
			public LinkedList <Cuboid> getOrderedCuboids() { return mCubes; }
			
			public double getValue() { return mVal; }
			
			private LinkedList <Cuboid> mCubes;
			private ArrayList <Container> mConts;
			private double mVal;
		}
		
		ArrayList <Order> memo = new ArrayList<>();
		//get free cuboids
		memo.ensureCapacity (freeCuboids.size());
		//do dynamic algo for cuboids and elements of subset:
		//iterate through cuboids
		for (int cFree = 0; cFree < freeCuboids.size(); ++cFree)
		{
			//set best to last
			if (cFree == 0)
				memo.add (new Order (new LinkedList <Cuboid>(), 0));
			else
				memo.add (memo.get (cFree - 1));
			//current cuboid needs to be added to a sequence of cuboids. Position? => determine
			//iterate through [0, n] such that current value is index to place current cuboid
			for (int cInsert = 0; cInsert <= cFree; ++cInsert)
			{
				//decision: keep current order with element inserted
				//or: new order left and right of current index
				LinkedList <Cuboid> newOrderList = new LinkedList<>();
				//add elements left of insert position
				if (cInsert > 0)
					newOrderList.addAll (memo.get (cInsert - 1).getOrderedCuboids());
				//add element to be inserted
				newOrderList.add (freeCuboids.get (cFree));
				//add element right of the insert position
				if (cFree - cInsert > 0)
					newOrderList.addAll (memo.get (cFree - cInsert - 1).getOrderedCuboids());
				
				//does subsetIndex refer to the correct subset
				//=> did subset change? (i think so)
				Order newOrder = new Order (newOrderList, subsetIndex);
				if (newOrder.getValue() > memo.get (cFree).getValue())
					memo.set (cFree, newOrder);
			}
		}
		
		return memo.get (memo.size() - 1).getOrderedContainers();
	}
	
	/**
	 * @param stump partially filled container
	 * @param filledParts container objects of the exact size 
	 * and in the exact location of stump's free cuboids
	 * @return stump with filledParts placed inside
	 */
	public Container putPiecesTogether (Container stump, ArrayList<Container> filledParts)
	{
		for (Container part : filledParts)
		{
			assert (stump.checkPositionOverlap(part, part.getGlue()));
			stump.placeBlock (part, part.getGlue());
		}
		return stump;
	}
	
	/**
	 * @param subset given subset of resources
	 * @return index corresponding to subset equal to given subset
	 */
	public int getSubsetIndex (ArrayList <Resource> subset)
	{
		for (int cSub = 0; cSub < mSubsets.size(); ++cSub)
		{
			Subset sub = mSubsets.get (cSub);
			if (sub.getResources().size() == subset.size())
			{
				int cElem = 0;
				boolean equal = true;
				while (equal && cElem < sub.getResources().size())
				{
					if (!sub.getResources().get (cElem).getBlock().equals (subset.get (cElem)))
						equal = false;
					++cElem;
				}
				if (equal)
					return cSub;
			}
		}
		return mSubsets.size();
	}
	
	public void init (Container container, ArrayList <Resource> pieces)
	{
		super.init(container, pieces);
		mLookupTable = new LookupTable(container.getDimensions(0), container.getDimensions(1), container.getDimensions(2), mSubsets.size());
	}
	
	public void run()
	{
		super.run();
		generatePowerSet();
		int dimension = 3;
		Glue zeroPos = new Glue (new IntegerMatrix (dimension, 1));
		
		for (int cDepth = 0; cDepth <= getContainer().getDimensions(0); ++cDepth)
		{
			for (int cWidth = 0; cWidth <= getContainer().getDimensions(1); ++cWidth)
			{
				for (int cHeight = 0; cHeight <= getContainer().getDimensions(2); ++cHeight)
				{
					for (int cSet = 0; cSet < mSubsets.size(); ++cSet)
					{
						double bestVal = 0;
						for (int cPiece = 0; cPiece < getPieces().size(); ++cPiece)
						{
							Block bRef = getPieces().get(cPiece).getBlock();
							double current = 0;
							if (bRef.getDimensions(0) <= cDepth && bRef.getDimensions(1) <= cWidth &&
								bRef.getDimensions(2) <= cHeight)
							{
								Container c = new Container (cDepth, cHeight, cWidth);
								c.placeBlock(bRef, zeroPos);
								current += c.getValue();
								
								ArrayList <Cuboid> emptyCubes = c.getCuboids();
								ArrayList <Container> filledCubes = orderFreeCuboids (emptyCubes, cSet);
								putPiecesTogether (c, filledCubes);
								Entry setEntry = mLookupTable.new Entry (c, c.getValue());
								if (bestVal < current)
								{
									mLookupTable.set (setEntry, cDepth, cWidth, cHeight, cSet);
									bestVal = setEntry.getValue();
								}
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * populates mSubsets with elements of the power set of the given resources
	 */
	public void generatePowerSet()
	{
		mSubsets.add (new Subset());
		//iterate through resources
		for (int cRes = 0; cRes < getPieces().size(); ++cRes)
		{
			Resource res = getPieces().get(cRes);
			int maxIndex = mSubsets.size();
			//iterate through partially existing subsets
			for (int cExistSub = 0; cExistSub < maxIndex; ++cExistSub)
			{
				Subset last = mSubsets.get(cExistSub);
				//clone subset and add resource
				Subset added = last.clone();
				added.addResource (res);
				mSubsets.add (added);
			}
		}
	}
	
	public ArrayList <Subset> mSubsets;
	private LookupTable mLookupTable;
}
