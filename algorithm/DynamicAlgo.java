package algorithm;

import geometry.Cuboid; 
import gui.PieceRenderPanel;
import gui.PieceRenderPanel.ResizeListener;
import gui.PieceRenderPanel.RotationListener;
import gui.PieceRenderPanel.ZoomListener;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JFrame;

import generic.Set;

import models.BasicShape;
import models.Block;
import models.Container;
import models.Glue;
import models.Matrix;
import models.Matrix.DoubleMatrix;
import models.Matrix.*;

import algorithm.LookupTable.Entry;

/**
 * class performing a dynamic algorithm to solve the polycube puzzle
 * @author martin
 */
public class DynamicAlgo extends Algorithm 
{
	/**
	 * comparable resource class for finite amounts
	 * @author martin
	 */
	public static class Resource extends models.Resource implements Comparable<Resource>
	{
		/**
		 * @param b block to store
		 * @param capacity capacity of b to store
		 * constructs finite resource
		 */
		public Resource (Block b, int capacity)
		{
			super (b, capacity, b.getVolume(), false);
		}
		
		
		public Resource clone()
		{
			return new Resource (this.getBlock(), this.getInventory());
		}
		
		/**
		 * @param comp resource to compare with
		 * @return 	-1 if volume of block stored is less than volume of block in comp,
		 * 0 if volume is equal, 1 if volume is greater
		 */
		public int compareTo (Resource comp)
		{
			if (this.getVolume() < comp.getVolume())
				return -1;
			else if (this.getVolume() == comp.getVolume())
				return 0;
			else
				return 1;
		}
		
	}
	
	
	/**
	 * class storing a set of blocks
	 * modelling a subset
	 * @author martin
	 */
	public static class Subset extends Set<Resource> implements Comparable<Subset>
	{	
		public Subset ()
		{
			mVolume = 0;
		}
		
		public Subset clone()
		{
			Subset clone = new Subset();
			for (Resource r : getOrderedElements())
				clone.add (r);
			return clone;
		}
		
		public LinkedList <Resource> getResources()
		{
			return new LinkedList<Resource> (getOrderedElements());
		}
		
		public String toString()
		{
			String s = new String();
			for (Resource r : getOrderedElements())
				s += r.getVolume() + " vol x " + r.getInventory() + ", ";
			return s;
		}
		
		/**
		 * @param comp subset to compare this subset to
		 * @return -1 if volume of this subset is less than
		 * volume of comp, 0 if volumes are equal, 1 if volume is greater
		 */
		public int compareTo (Subset comp)
		{
			if (this.getVolume() < comp.getVolume())
				return -1;
			else if (this.getVolume() == comp.getVolume())
				return 0;
			else
				return 1;
		}
		
		public int getVolume()
		{
			return mVolume;
		}
		
		public boolean equals (Subset comp)
		{
			if (this.getVolume() == comp.getVolume())
				return super.equals (comp);
			return false;
		}
		
		public void add (Resource bAdd)
		{
			if (bAdd.isInfinite())
				throw new IllegalArgumentException ("only finite resources can be added to form finite subsets");
			super.add (bAdd);
			mVolume += bAdd.getVolume() * bAdd.getInventory();
		}
		
		private int mVolume;
	}
	
	/**
	 * mapping of index to specific subset
	 * @author martin
	 */
	public static class SubsetAtIndex
	{
		public SubsetAtIndex (Subset s, int index)
		{
			mSet = s;
			mIndex = index;
		}
		
		public Subset getSet() { return mSet; }
		
		public int getIndex() { return mIndex; }
		
		private Subset mSet;
		private int mIndex;
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
	public ArrayList <Container> fillFreeCuboids (ArrayList <Cuboid> freeCuboids, int subsetIndex)
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
					//if entry needed is not set
					if (!mLookupTable.isSet (cubeDims.get (0), cubeDims.get (1), cubeDims.get (2), subsetIndex))
					{
						Container x = new Container (cubeDims.get (0), cubeDims.get (1), cubeDims.get (2));
						explore (x, mSubsets.get (subsetIndex));
					}
					//get optimal for parameters
					Entry e = mLookupTable.get (cubeDims.get (0), cubeDims.get (1), cubeDims.get (2), subsetIndex);
					ArrayList <Resource> remainder = e.getUnusedResources (mSubsets.get (subsetIndex));
					subsetIndex = getSubsetIndex (remainder);
					//get container, set to fit current cuboid
					Container lookedUp = mLookupTable.getContainer (cubeDims.get (0), cubeDims.get (1), cubeDims.get (2), subsetIndex).clone(); 
					rotateToFit (lookedUp, free);
					Glue gluePos = new Glue (new IntegerMatrix (3, 1)).getClosest (free.getVertices());
					lookedUp.glue (gluePos);
					mConts.add (lookedUp);
					mVal += e.getValue();
				}
			}
			
			public ArrayList<Container> getOrderedContainers()
			{
				return mConts;
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
			LinkedList<Cuboid> prev;
			if (cFree > 0)
				prev = (LinkedList<Cuboid>) memo.get (cFree - 1).getOrderedCuboids().clone();
			else
				prev = new LinkedList<Cuboid>();
			prev.add (freeCuboids.get (cFree));
			memo.add (new Order (prev, subsetIndex));
			
			//current cuboid needs to be added to a sequence of cuboids. Position? => determine
			//iterate through [0, n] such that current value is index to place current cuboid
			for (int cInsert = 0; cInsert < cFree; ++cInsert)
			{
				//decision: keep current order with element inserted
				//or: new order left and right of current index
				LinkedList <Cuboid> newOrderList = (LinkedList<Cuboid>) memo.get (cFree - 1).getOrderedCuboids().clone();
				//add element to be inserted
				newOrderList.add (cInsert, freeCuboids.get (cFree));
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
			for (int cInPart = 0; cInPart < part.getAmountOfBlocks(); ++cInPart)
			{
				Block place = part.getBlock (cInPart);
				if (!stump.checkPositionOverlap (place, place.getGlue()));
					assert (stump.checkPositionOverlap (place, place.getGlue()));
				stump.placeBlock (place, place.getGlue());
			}
		}
		return stump;
	}
	
	/**
	 * @param current container of current iteration to be compared with contents of table
	 * @param subIndex current index of subset
	 * @return the largest entry of look up table whose container and subset are smaller or equal
	 * or current as Entry if current is larger than max in table
	 */
	public Entry getMax (Container current, int subIndex)
	{
		Entry max = mLookupTable.new Entry (current);
		int d, w, h;
		d = current.getDimensions(0);
		w = current.getDimensions(1);
		h = current.getDimensions(2);
		if (d > w)
		{
			Entry less = mLookupTable.get (d - 1, w, h, subIndex);
			if (less != null && less.getValue() > max.getValue())
				max = less;
		}
		if (w > h)
		{
			Entry less = mLookupTable.get (d, w - 1, h, subIndex);
			if (less != null && less.getValue() > max.getValue())
				max = less;
		}
		if (h > 0)
		{
			Entry less = mLookupTable.get (d, w, h - 1, subIndex);
			if (less != null && less.getValue() > max.getValue())
				max = less;
		}
		if (subIndex > 0)
		{
			Entry less = mLookupTable.get (d, w, h, subIndex - 1);
			if (less != null && less.getValue() > max.getValue())
				max = less;
		}
		return max;
	}
	
	/**
	 * @param obtained a given container
	 * @param fit the cuboid obtained should fit
	 * @return obtained rotated such that it fits fit
	 */
	public Container rotateToFit (Container obtained, Cuboid fit)
	{
		Glue offset = obtained.getGlue();
		ArrayList<Integer> fitDim = fit.getDimensions();
		
		Matrix<Double> manipulation = new DoubleMatrix (3, 3);
		manipulation.getScalarMatrix (1.0, manipulation);
		//manipulate x1
		if (obtained.getDimensions (0) != fitDim.get (0))
		{
			Matrix<Double> rot = null;
			if (obtained.getDimensions (0) == fitDim.get (1))
				rot = BasicShape.rotationMatrix (0.0, 90.0, BasicShape.RotationDir.ONWARD);
			else if (obtained.getDimensions (0) == fitDim.get (2))
				rot = BasicShape.rotationMatrix (90.0, 0.0, BasicShape.RotationDir.ONWARD);
			manipulation = manipulation.multiply (rot, new DoubleMatrix (3, 3));
		}
		//manipulate x2
		if (obtained.getDimensions (1) != fitDim.get (1))
		{
			if (obtained.getDimensions (1) == fitDim.get (2))
			{
				Matrix<Double> rot = null;
				rot = BasicShape.rotationMatrix (0.0, 90.0, BasicShape.RotationDir.ONWARD);
				manipulation = manipulation.multiply (rot, new DoubleMatrix (3, 3));
				rot = BasicShape.rotationMatrix (90.0, 0.0, BasicShape.RotationDir.ONWARD);
				manipulation = manipulation.multiply (rot, new DoubleMatrix (3, 3));
				rot = BasicShape.rotationMatrix (0.0, -90.0, BasicShape.RotationDir.ONWARD);
				manipulation = manipulation.multiply (rot, new DoubleMatrix (3, 3));
			}
		}
		obtained.rotate (manipulation);
		obtained.glue (offset);
		return obtained;
	}
	
	/**
	 * @param subset given subset of resources
	 * @return index corresponding to subset equal to given subset
	 */
	public int getSubsetIndex (ArrayList <Resource> subset)
	{
		//FIND INDEX USING SET IMPLEMENTATION SORTING RESOURCES BASED ON VOLUME
		
		for (int cSub = 0; cSub < mSubsets.size(); ++cSub)
		{
			Subset sub = mSubsets.get (cSub);
			if (sub.getResources().size() == subset.size())
			{
				int cElem = 0;
				boolean equal = true;
				while (equal && cElem < sub.getResources().size())
				{
					if (!sub.getResources().get (cElem).getBlock().equals (subset.get (cElem).getBlock()))
						equal = false;
					++cElem;
				}
				if (equal)
					return cSub;
			}
		}
		return mSubsets.size();
	}
	
	public void run()
	{
		super.run();
		generatePowerSet();
		int dep = getContainer().getDimensions(0);
		int wid = getContainer().getDimensions(1);
		int hig = getContainer().getDimensions(2);
		
		explore (getContainer(), mSubsets.get (mSubsets.size() - 1));
		
		setSolution (mLookupTable.getContainer (dep, wid, hig, mSubsets.size() - 1));
		setAlgoDone();
	}
	
	/**
	 * @param c a given empty container
	 * @param s a given subset
	 * computes optimal filling for c using s
	 */
	public void explore (Container c, Subset s)
	{
		final int MAXDIM = 3;
		
		Entry best = null;
		
		//ONLY EXPLORE RESOURCES WHICH ARE NOT A SUBSET OF OTHER RESOURCES
		for (Resource piece : s.getResources())
		{
			Block bRef = piece.getBlock();
			ArrayList<Integer> sortContDims, sortPieceDims;
			sortContDims = LookupTable.sortIndices (c.getDimensions (0), c.getDimensions (1), c.getDimensions (2));
			sortPieceDims = LookupTable.sortIndices (bRef.getDimensions (0), bRef.getDimensions (1), bRef.getDimensions (2));
			//if piece fits
			if (sortContDims.get (0) >= sortPieceDims.get (0) && 
				sortContDims.get (1) >= sortPieceDims.get (1) &&
				sortContDims.get (2) >= sortPieceDims.get (2))
			{
				//place
				int iSub = getSubsetIndex (new ArrayList<Resource> (s.getResources()));
				c.placeBlock (piece.getBlock().clone(), new Glue (new IntegerMatrix (MAXDIM, 1)));
				//cut remainder, fill remainder, assemble remainder
				ArrayList <Container> filled = fillFreeCuboids (c.getFreeCuboids(), iSub);
				if (!filled.isEmpty())
					putPiecesTogether (c, filled);
				//check for new max
				Entry current = mLookupTable.new Entry (c);
				if (best == null || current.getValue() > best.getValue())
					best = current;
				//set max value to current cell
				mLookupTable.set (best, c.getDimensions (0), c.getDimensions (1), c.getDimensions (2), iSub);
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
			Block block = getPieces().get(cRes).getBlock();
			int inventory = getPieces().get (cRes).getInventory();
			Resource res = new Resource (block, inventory);
			int maxIndex = mSubsets.size();
			//iterate through partially existing subsets
			for (int cExistSub = 0; cExistSub < maxIndex; ++cExistSub)
			{
				Subset last = mSubsets.get(cExistSub);
				//clone subset and add resource
				if (res.isInfinite())
				{
					Subset add = last.clone();
					add.add (res.clone());
					mSubsets.add (add);
				}
				else
				{
					int cAdd = 1;
					while (last.getVolume() + cAdd * res.getVolume() <= getContainer().getVolume() && cAdd <= res.getInventory())
					{
						Subset add = last.clone();
						Resource addRes = new Resource (res.getBlock().clone(), cAdd);
						add.add (addRes);
						mSubsets.add (add);
						++cAdd;
					}
				}
			}
		}
	}
	
	
	private void setSolution (Container sol)
	{
		for (int cBlock = 0; cBlock < sol.getAmountOfBlocks(); ++cBlock)
		{
			Block placed = sol.getBlock (cBlock);
			getContainer().placeBlock (placed, placed.getGlue());
		}
	}
	
	public ArrayList <Subset> mSubsets;
	private LookupTable mLookupTable;
}
