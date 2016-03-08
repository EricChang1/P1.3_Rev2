package algorithm;

import geometry.Cuboid;
import gui.PieceRenderPanel;
import gui.PieceRenderPanel.ResizeListener;
import gui.PieceRenderPanel.RotationListener;
import gui.PieceRenderPanel.ZoomListener;

import java.awt.BorderLayout;
import java.util.*;

import javax.swing.JFrame;

import models.BasicShape;
import models.Block;
import models.Container;
import models.Glue;
import models.Matrix;
import models.Matrix.DoubleMatrix;
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
		
		public int size() { return mResources.size(); }
		
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
					Container lookedUp = mLookupTable.getContainer (cubeDims.get (0), cubeDims.get (1), cubeDims.get (2), subsetIndex).clone(); 
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
				LinkedList <Cuboid> newOrderList = memo.get (cFree - 1).getOrderedCuboids();
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
		Entry max = mLookupTable.new Entry (current, current.getValue());
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
		generatePowerSet();
		int dep = getContainer().getDimensions(0);
		int wid = getContainer().getDimensions(1);
		int hig = getContainer().getDimensions(2);
		ArrayList <Integer> is = LookupTable.sortIndices (dep, wid, hig);
		dep = is.get(0);
		wid = is.get(1);
		hig = is.get(2);
		mLookupTable = new LookupTable(dep + 1, wid + 1, hig + 1, mSubsets.size());
		
		int dimension = 3;
		Glue zeroPos = new Glue (new IntegerMatrix (dimension, 1));
		Entry best = null;
		
		for (int cDepth = 0; cDepth <= dep; ++cDepth)
		{
			for (int cWidth = 0; cWidth <= cDepth && cWidth <= wid; ++cWidth)
			{
				for (int cHeight = 0; cHeight <= cWidth && cHeight <= hig; ++cHeight)
				{
					for (int cSet = 0; cSet < mSubsets.size(); ++cSet)
					{
						Subset useSub = mSubsets.get (cSet);
						for (int cPiece = 0; cPiece < useSub.size(); ++cPiece)
						{
							Block bRef = useSub.getResources().get(cPiece).getBlock();
							Container c = null;
							c = new Container (cDepth, cWidth, cHeight);
							
							if (bRef.getDimensions(0) <= cDepth && bRef.getDimensions(1) <= cWidth &&
								bRef.getDimensions(2) <= cHeight)
							{
								c.placeBlock(bRef, zeroPos);
								
								ArrayList <Cuboid> emptyCubes = c.getFreeCuboids();
								if (!emptyCubes.isEmpty())
								{
									ArrayList <Container> filledCubes = orderFreeCuboids (emptyCubes, cSet);
									putPiecesTogether (c, filledCubes);
								}
							}
							//set value in table
							/*
							if (best != null && best.getValue() == c.getValue())
							{
								best = mLookupTable.new Entry (c, c.getValue());
								System.out.println ("new highest value found: " + best.getValue());
								
								JFrame frame = new JFrame ("best value: " + best.getValue());
								frame.setSize (400, 400);
								frame.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);
								frame.setLayout (new BorderLayout());
								
								PieceRenderPanel render = new PieceRenderPanel(c.clone());
								
								PieceRenderPanel.RotationListener rotListen = render.new RotationListener();
								PieceRenderPanel.ResizeListener resizeListen = render.new ResizeListener();
								PieceRenderPanel.ZoomListener zoomListen = render.new ZoomListener();
								zoomListen.setSensitivity (0.1);
								
								frame.addMouseListener (rotListen);
								frame.addMouseMotionListener(rotListen);
								frame.addMouseWheelListener(zoomListen);
								frame.addComponentListener(resizeListen);
								
								frame.add (render, BorderLayout.CENTER);
								frame.setVisible(true);
								render.init();
								int x = 2;
								x = x*2;
							}*/
							best = getMax (c, cSet);
							mLookupTable.set (best, cDepth, cWidth, cHeight, cSet);
						}
					}
				}
			}
		}
		setSolution (mLookupTable.getContainer (dep, wid, hig, mSubsets.size() - 1));
		setAlgoDone();
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
