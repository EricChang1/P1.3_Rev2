package algorithm;

import geometry.Cuboid; 
import gui.PieceRenderPanel;
import gui.PieceRenderPanel.ResizeListener;
import gui.PieceRenderPanel.RotationListener;
import gui.PieceRenderPanel.ZoomListener;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

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
		 * @return -1 if this is considered lesser, 1 if this is considered larger, 0 if this is considered equal
		 * criteria: volume of block stored, number of blocks stored (2nd), number of vertices of block stored (3rd), value of block stored (4th)
		 */
		public int compareTo (Resource comp)
		{
			if (this.getVolume() < comp.getVolume())
				return -1;
			else if (this.getVolume() > comp.getVolume())
				return 1;
			else if (this.getInventory() < comp.getInventory())
				return -1;
			else if (this.getInventory() > comp.getInventory())
				return 1;
			else if (this.getBlock().getNumberOfVertices() < comp.getBlock().getNumberOfVertices())
				return -1;
			else if (this.getBlock().getNumberOfVertices() > comp.getBlock().getNumberOfVertices())
				return 1;
			else if (this.getBlock().getValue() < comp.getBlock().getValue())
				return -1;
			else if (this.getBlock().getValue() > comp.getBlock().getValue())
				return 1;
			else 
				return 0;
		}
	}
	
	/**
	 * wrapper for models.Resource
	 * orders resources according to volume of a single block stored
	 * and (secondary) according to its number of vertices
	 * and (tertiary) according to its value
	 * number of blocks stored is not considered
	 * @author martin
	 */
	public static class BlockResource extends models.Resource implements Comparable <BlockResource>
	{
		public BlockResource (Resource r) { super (r.getBlock(), r.getInventory(), r.getVolume(), false); }
		
		public BlockResource (Block b, int capacity) { super (b, capacity, b.getVolume(), false); }
		
		public int compareTo (BlockResource comp)
		{
			if (this.getVolume() < comp.getVolume())
				return -1;
			else if (this.getVolume() > comp.getVolume())
				return 1;
			else if (this.getBlock().getNumberOfVertices() < comp.getBlock().getNumberOfVertices())
				return -1;
			else if (this.getBlock().getNumberOfVertices() > comp.getBlock().getNumberOfVertices())
				return 1;
			else if (this.getBlock().getValue() < comp.getBlock().getValue())
				return -1;
			else if (this.getBlock().getValue() > comp.getBlock().getValue())
				return 1;
			else
				return 0;
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
		
		public Subset (ArrayList<Resource> resources)
		{
			this();
			for (Resource r : resources)
				add (r);
		}
		
		public Subset clone()
		{
			Subset clone = new Subset();
			for (Resource r : getOrderedElements())
				clone.add (r);
			return clone;
		}
		
		public Subset deepClone()
		{
			Subset clone = new Subset();
			for (Resource r : getOrderedElements())
				clone.add (r.clone());
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
		 * @return performs lexicographical comparison between ordered resources stored
		 */
		public int compareTo (Subset comp)
		{
			ArrayList<Resource> orderedT = this.getOrderedElements();
			ArrayList<Resource> orderedC = comp.getOrderedElements();
			for (int cComp = 0; cComp < orderedT.size() && cComp < orderedC.size(); ++cComp)
			{
				int comparison = orderedT.get (cComp).compareTo (orderedC.get (cComp));
				if (comparison != 0)
					return comparison;
			}
			if (orderedT.size() < orderedC.size())
				return -1;
			else if (orderedT.size() == orderedC.size())
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
	public static class SubsetAtIndex implements Comparable<SubsetAtIndex>
	{
		public SubsetAtIndex (Subset s, int index)
		{
			mSet = s;
			mIndex = index;
		}
		
		public Subset getSet() { return mSet; }
		
		public String toString()
		{
			return mIndex + ". " + mSet.toString();
		}
		
		public int getIndex() { return mIndex; }
		
		public int compareTo (SubsetAtIndex comp)
		{
			return this.getSet().compareTo (comp.getSet());
		}
		
		private Subset mSet;
		private int mIndex;
	}
	
	/**
	 * @param list list to perform swap on
	 * @param i1 index of first element
	 * @param i2 index of second element
	 * swaps elements referred to by i1, i2
	 */
	public static <T> void swapInList (ArrayList<T> list, int i1, int i2)
	{
		T temp = list.get (i1);
		list.set (i1, list.get (i2));
		list.set (i2, temp);
	}
	
	
	public DynamicAlgo() 
	{
		mSubsets = new Set<>();
		mFuseUse = false;
	}
	
	
	/**
	 * @param freeCuboids free cuboids obtained from container
	 * @param subsetIndex index of subset available
	 * @return optimal order in which the free cuboids should be filled to the full extent
	 */
	public ArrayList <Container> fillFreeCuboids (ArrayList <Cuboid> freeCuboids, Subset available)
	{
		/**
		 * class used to compute and store list of containers
		 * corresponding to a given list of cuboids and a given
		 * subset
		 * @author martin
		 */
		
		class Order
		{
			public Order (LinkedList <Cuboid> cubes, Subset s)
			{
				mCubes = cubes;
				mConts = new ArrayList <>();
				
				if (mCubes.size() > 0)
					mCurrentIncrease.split (mCubes.size());
				for (Cuboid free : mCubes)
				{
					ArrayList <Integer> cubeDims = free.getDimensions();
					int subsetIndex = getSubsetIndex (s);
					//if entry needed is not set
					if (!mLookupTable.isSet (cubeDims.get (0), cubeDims.get (1), cubeDims.get (2), subsetIndex))
					{
						Container x = new Container (cubeDims.get (0), cubeDims.get (1), cubeDims.get (2));
						explore (x, s.clone());
						assert (mLookupTable.get (cubeDims.get (0), cubeDims.get (1), cubeDims.get (2), subsetIndex) != null);
					}
					//get optimal for parameters
					Entry e = mLookupTable.get (cubeDims.get (0), cubeDims.get (1), cubeDims.get (2), subsetIndex);
					//get container, set to fit current cuboid
					Container lookedUp = mLookupTable.getContainer (cubeDims.get (0), cubeDims.get (1), cubeDims.get (2), subsetIndex).clone(); 
					rotateToFit (lookedUp, free);
					Glue gluePos = new Glue (new IntegerMatrix (3, 1)).getClosest (free.getVertices());
					lookedUp.glue (gluePos);
					mConts.add (lookedUp);
					mVal += e.getValue();
					
					//get remaining subset
					s = e.getUnusedResources (s);
				}
				if (mCubes.size() > 0)
					mCurrentIncrease.unite();
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
		
		mCurrentIncrease.split (2 * freeCuboids.size());
		for (int cFree = 0; cFree < freeCuboids.size(); ++cFree)
		{
			//set best to last
			LinkedList<Cuboid> prev;
			if (cFree > 0)
				prev = (LinkedList<Cuboid>) memo.get (cFree - 1).getOrderedCuboids().clone();
			else
				prev = new LinkedList<Cuboid>();
			prev.add (freeCuboids.get (cFree));
			memo.add (new Order (prev, available));
			
			//current cuboid needs to be added to a sequence of cuboids. Position? => determine
			//iterate through [0, n] such that current value is index to place current cuboid
			if (cFree > 0)
				mCurrentIncrease.split (cFree);
			for (int cInsert = 0; cInsert < cFree; ++cInsert)
			{
				//decision: keep current order with element inserted
				//or: new order left and right of current index
				LinkedList <Cuboid> newOrderList = (LinkedList<Cuboid>) memo.get (cFree - 1).getOrderedCuboids().clone();
				//add element to be inserted
				newOrderList.add (cInsert, freeCuboids.get (cFree));
				//does subsetIndex refer to the correct subset
				//=> did subset change? (i think so)
				Order newOrder = new Order (newOrderList, available);
				if (newOrder.getValue() > memo.get (cFree).getValue())
					memo.set (cFree, newOrder);
			}
			if (cFree > 0)
				mCurrentIncrease.unite();
		}
		mCurrentIncrease.unite();
		
		return memo.get (memo.size() - 1).getOrderedContainers();
	}
	
	/**
	 * @param raw list of raw cuboids as obtained from basic shape
	 * @return new list of cuboids fusing as many cuboids as possible (greedy, first)
	 */
	public ArrayList<Cuboid> fuseAdjacentCuboids (ArrayList<Cuboid> raw)
	{
		LinkedList<Cuboid> working = new LinkedList<> (raw);
		boolean change = true;
		//iterate over cuboids while there is a change
		while (change)
		{
			change = false;

			ListIterator<Cuboid> iFuse1 = working.listIterator();
			while (!change && iFuse1.nextIndex() < working.size())
			{
				Cuboid c1 = iFuse1.next();
				int cFuse2 = iFuse1.nextIndex();
				while (!change && cFuse2 < working.size())
				{
					Cuboid c2 = working.get (cFuse2);
					if (c1.areFuseable (c2))
					{
						change = true;
						iFuse1.remove();
						iFuse1 = null;
						working.remove (cFuse2 - 1);
						working.addFirst (c1.fuse (c2));
					}
					++cFuse2;
				}
				
			}
		}
		
		return new ArrayList<> (working);
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
				/*
				if (!stump.checkPositionOverlap (place, place.getGlue()));
				{
					stump.checkPositionOverlap (place, place.getGlue());
					assert (stump.checkPositionOverlap (place, place.getGlue().clone()));
				}*/
				stump.placeBlock (place.clone(), place.getGlue());
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
		//manipulation = manipulation.getScalarMatrix (1.0, manipulation);
		
		ArrayList<Integer> obtainedDims = obtained.getDimensions();
		
		
		/*Container clone = obtained.clone();
		clone = rotateToFitClone (clone, fit);
		
		if (!clone.getDimensions().equals (fitDim))
		{
			int x = 2;
			x += 3;
		}*/
		
		//iterate through dimensions
		for (int cFit = 0; cFit < obtainedDims.size(); ++cFit)
		{
			//find matching dimension in fit
			int cObt = 0;
			boolean found = false;;
			while (cObt < obtainedDims.size() && !found)
			{
				if (fitDim.get (cFit).equals (obtainedDims.get (cObt)))
				{
					//search whether fit dimension is already used
					int cPrev = 0;
					while (cPrev < cFit && manipulation.getCell (cPrev, cObt).equals (0.0))
						++cPrev;
					if (cPrev >= cFit)
					{
						manipulation.setCell (cFit, cObt, 1.0);
						found = true;
						found = true;
					}
				}
				++cObt;
			}	
		}
		
		/*
		//manipulate x1
		if (obtainedDims.get (0) != fitDim.get (0))
		{
			Matrix<Double> rot = null;
			if (obtainedDims.get (0) == fitDim.get (1))
			{
				rot = BasicShape.rotationMatrix (0.0, 90.0, BasicShape.RotationDir.ONWARD);
				swapInList (obtainedDims, 0, 1);
			}
			else if (obtainedDims.get (0) == fitDim.get (2))
			{
				rot = BasicShape.rotationMatrix (90.0, 0.0, BasicShape.RotationDir.ONWARD);
				swapInList (obtainedDims, 0, 2);
			}
			manipulation = manipulation.multiply (rot, new DoubleMatrix (3, 3));
		}
		//manipulate x2
		if (obtainedDims.get (1) != fitDim.get (1))
		{
			Matrix<Double> rot = null;
			if (obtainedDims.get (1) == fitDim.get (0))
			{
				rot = BasicShape.rotationMatrix (0.0, 90.0, BasicShape.RotationDir.ONWARD);
			}
			if (obtainedDims.get (1) == fitDim.get (2))
			{
				Matrix<Double> temp = null;
				rot = BasicShape.rotationMatrix (0.0, 90.0, BasicShape.RotationDir.ONWARD);
				temp = BasicShape.rotationMatrix (90.0, 0.0, BasicShape.RotationDir.ONWARD);
				rot = rot.multiply (temp, new DoubleMatrix (3, 3));
				temp = BasicShape.rotationMatrix (0.0, -90.0, BasicShape.RotationDir.ONWARD);
				rot = rot.multiply (temp, new DoubleMatrix (3, 3));
			}
			manipulation = rot.multiply (manipulation, new DoubleMatrix (3, 3));
			//no swapping in dimensions list required
		}*/
		
		obtained.rotate (manipulation);
		obtained.glue (offset);
		
		assert (obtained.getDimensions().equals (fitDim));
		return obtained;
	}
	
	
	public Container rotateToFitClone (Container obtained, Cuboid fit)
	{
		Glue offset = obtained.getGlue();
		ArrayList<Integer> fitDim = fit.getDimensions();
		
		Matrix<Double> manipulation = new DoubleMatrix (3, 3);
		//manipulation = manipulation.getScalarMatrix (1.0, manipulation);
		
		ArrayList<Integer> obtainedDims = obtained.getDimensions();
		
		//iterate through dimensions
		for (int cFit = 0; cFit < obtainedDims.size(); ++cFit)
		{
			//find matching dimension in fit
			int cObt = 0;
			boolean found = false;;
			while (cObt < obtainedDims.size() && !found)
			{
				if (fitDim.get (cFit).equals (obtainedDims.get (cObt)))
				{
					//search whether fit dimension is already used
					int cPrev = 0;
					while (cPrev < cFit && manipulation.getCell (cPrev, cObt).equals (0.0))
						++cPrev;
					if (cPrev >= cFit)
					{
						manipulation.setCell (cFit, cObt, 1.0);
						found = true;
						found = true;
					}
				}
				++cObt;
			}	
		}
		
		obtained.rotate (manipulation);
		obtained.glue (offset);
		
		//assert (obtained.getDimensions().equals (fitDim));
		return obtained;
	}
	
	/**
	 * @param subset given subset of resources
	 * @return index corresponding to subset equal to given subset
	 */
	public int getSubsetIndex (Subset subset)
	{
		//FIND INDEX USING SET IMPLEMENTATION SORTING RESOURCES BASED ON VOLUME
		if (mSubsets.hasElement (new SubsetAtIndex (subset, 0)))
			return mSubsets.getElement (new SubsetAtIndex (subset, 0)).getIndex();
		else
			return mSubsets.getSize();
	}
	
	/**
	 * @param fuseUse true to enable fusing, false to disable
	 * default is false
	 */
	public void setFuse (boolean fuseUse)
	{
		mFuseUse = fuseUse;
	}
	
	public void run()
	{
		super.run();
		generatePowerSet();
		int dep = getContainer().getDimensions(0);
		int wid = getContainer().getDimensions(1);
		int hig = getContainer().getDimensions(2);
		ArrayList<Integer> tDims = LookupTable.sortIndices (dep, wid, hig);
		mLookupTable = new LookupTable (tDims.get(0) + 1, tDims.get(1) + 1, tDims.get(2) + 1, mSubsets.getSize());
		
		mCurrentIncrease = getProgress().getRemainingIncrease();
		
		explore (getContainer(), mLargestSubset.getSet());
		
		setSolution (mLookupTable.getContainer (dep, wid, hig, mLargestSubset.getIndex()));
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
		
		Entry best = mLookupTable.new Entry (c);
		int iSub = getSubsetIndex (s);
		
		ArrayList<Integer> sortContDims;
		sortContDims = LookupTable.sortIndices (c.getDimensions (0), c.getDimensions (1), c.getDimensions (2));
		
		if (s.getSize() > 0)
			mCurrentIncrease.split (s.getResources().size());
		
		for (Resource piece : s.getResources())
		{
			Block bRef = piece.getBlock();
			ArrayList<Block> rotatedBlocks = new ArrayList<>();
			for (BasicShape rotation : new ShapeRotator (bRef).getRotations())
				rotatedBlocks.add (new Block (rotation, bRef.getValue(), bRef.getName()));
			
			mCurrentIncrease.split (rotatedBlocks.size());
			for (Block rotatedPiece : rotatedBlocks)
			{
				//if piece fits
				if (sortContDims.get (0) >= rotatedPiece.getDimensions(0) && 
					sortContDims.get (1) >= rotatedPiece.getDimensions(1) &&
					sortContDims.get (2) >= rotatedPiece.getDimensions(2))
				{
					//clone and deduct subset
					//maybe only clone?
					Subset sClone = s.deepClone();
					Resource use = sClone.getElement (piece);
					use.deduct();
					if (use.getInventory() <= 0)
						sClone.remove (use);
					
					//construct new empty container of sorted dimension's size and place
					Container cloneC = new Container (sortContDims.get (0), sortContDims.get (1), sortContDims.get (2));
					cloneC.placeBlock (rotatedPiece, new Glue (new IntegerMatrix (MAXDIM, 1)));
					
					//cut remainder, fill remainder, assemble remainder
					ArrayList<Cuboid> freeRemain = cloneC.getFreeCuboids();
					if (!freeRemain.isEmpty())	
					{
						if (mFuseUse)
							freeRemain = fuseAdjacentCuboids (freeRemain);
						mCurrentIncrease.split (freeRemain.size() + 1);
						
						ArrayList <Container> filled = fillFreeCuboids (freeRemain, sClone);
						
						if (!filled.isEmpty())
							putPiecesTogether (cloneC, filled);
						
						getProgress().increase (mCurrentIncrease);
						mCurrentIncrease.unite();
					}
					
					//check for new max
					Entry current = mLookupTable.new Entry (cloneC);
					if (best == null || current.getValue() > best.getValue())
						best = current;
				}
			}
			mCurrentIncrease.unite();
		}
		
		if (s.getSize() > 0)
			mCurrentIncrease.unite();
		
		//set max value to current cell
		mLookupTable.set (best, c.getDimensions (0), c.getDimensions (1), c.getDimensions (2), iSub);
	}
	
	/**
	 * populates mSubsets with elements of the power set of the given resources
	 */
	public void generatePowerSet()
	{
		mLargestSubset = new SubsetAtIndex (new Subset(), 0);
		mSubsets.add (mLargestSubset);
		//iterate through resources
		for (int cRes = 0; cRes < getPieces().size(); ++cRes)
		{
			Block block = getPieces().get(cRes).getBlock();
			int inventory = getPieces().get (cRes).getInventory();
			Resource res = new Resource (block, inventory);
			
			//iterate through partially existing subsets
			for (SubsetAtIndex exist : mSubsets.getOrderedElements())
			{
				SubsetAtIndex last = exist;
				//clone subset and add resource
				
				assert (!res.isInfinite());
				
				int cAdd = 1;
				while (/*last.getSet().getVolume() + cAdd * res.getVolume() <= getContainer().getVolume() && */cAdd <= res.getInventory())
				{
					Subset add = last.getSet().clone();
					Resource addRes = new Resource (res.getBlock().clone(), cAdd);
					add.add (addRes);
					
					SubsetAtIndex addAtIndex = new SubsetAtIndex (add, mSubsets.getSize());
					
					if (add.getVolume() > mLargestSubset.getSet().getVolume())
						mLargestSubset = addAtIndex;
					mSubsets.add (addAtIndex);
					++cAdd;
				}
			}
		}
	}
	
	
	private void setSolution (Container sol)
	{
		Cuboid fit = new Cuboid (getContainer().getGlue(), getContainer().getMaxDimension());
		rotateToFit(sol, fit);
		
		for (int cBlock = 0; cBlock < sol.getAmountOfBlocks(); ++cBlock)
		{
			Block placed = sol.getBlock (cBlock);
			getContainer().placeBlock (placed, placed.getGlue());
		}
	}
	
	private Set<SubsetAtIndex> mSubsets;
	private SubsetAtIndex mLargestSubset;
	private LookupTable mLookupTable;
	
	private Progress.Increase mCurrentIncrease;
	
	private boolean mFuseUse;
}
