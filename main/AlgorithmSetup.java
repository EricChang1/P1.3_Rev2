package main;

import java.util.*;

import algorithm.*;

import generic.Set;

import models.Block;
import models.Container;
import models.Resource;

/**
 * utility class for setting up data to run algorithms
 * @author martin
 */
public class AlgorithmSetup 
{
	public enum DimName {DEPTH, WIDTH, HEIGHT}
	
	/**
	 * @param possible list of possible blocks
	 * initializes resources to 0 inventory, infinite false
	 * initializes container dimensions to 0, 0, 0
	 */
	public AlgorithmSetup ()
	{
		mResources = new Set<>();
	}
	
	/**
	 * @return all resource setup objects
	 */
	public ArrayList<ResourceSetup> getResourceSetups() { return mResources.getOrderedElements(); }
	
	/**
	 * @param b a given block
	 * @return resource setup object corresponding to b or null if such an object is not stored
	 */
	public ResourceSetup getResourceSetup (Block b)
	{
		if (!mResources.hasElement (new ResourceSetup (b)))
			return mResources.getElement (new ResourceSetup (b));
		return null;
	}
	
	/**
	 * @return container of specified dimensions
	 */
	public Container getContainer()
	{
		return new Container (mContD, mContW, mContH);
	}
	
	/**
	 * @param a algorithm to load
	 * @return reference to a, loaded with container, resources
	 */
	public Algorithm loadAlgorithm (Algorithm a)
	{
		Container c = new Container (mContD, mContW, mContH);
		ArrayList<Resource> rs = new ArrayList<>();
		for (ResourceSetup set : mResources.getOrderedElements())
			rs.add (set.constructResource());
		a.init (c, rs);
		return a;
	}
	
	/**
	 * @return container dimensions and resources as string
	 */
	public String getSetupString()
	{
		String s = new String();
		s += "setup\n" + mContD + " x " + mContW + " x " + mContH + " container\n";
		for (ResourceSetup set : mResources.getOrderedElements())
		{
			if (set.getInfinite())
				s += set.getBlock().getName() + " infinite\n";
			else
				s += set.getBlock().getName() + " x " + set.getCapacity() + "\n";
		}
		return s;
	}
	
	public void setBlocks (Collection<Block> possible)
	{
		mResources.clear();
		for (Block b : possible)
		{
			ResourceSetup r = new ResourceSetup (b);
			if (!mResources.hasElement (r))
				mResources.add (r);
		}
	}
	
	/**
	 * @param size a number >= 0 indicating size of dimension
	 * @param dim name of dimension to set
	 */
	public void setContainerSize (int size, DimName dim)
	{
		if (size < 0)
			throw new IllegalArgumentException ("negative dimension not permissible");
		switch (dim)
		{
		case DEPTH: mContD = size;
			break;
		case WIDTH: mContW = size;
			break;
		case HEIGHT: mContH = size;
			break;
		default: ;	
		}
	}
	
	/**
	 * @param b block of resource stored to alter
	 * @param count capacity to set resource to
	 * Note constructs a new resource if resource of block b does not exist
	 */
	public void modifyResourceCount (Block b, int count)
	{
		ResourceSetup set = new ResourceSetup (b);
		if (mResources.hasElement (set))
			mResources.getElement (set).setCapacity (count);
		else
		{
			set.setCapacity (count);
			mResources.add (set);
		}
	}
	
	/**
	 * @param b block of resource stored to alter
	 * @param flag true to make resource of b infinite, false to make finite
	 * Note constructs a new resource if resource of block b does not exist
	 */
	public void modifyResourceFinite (Block b, boolean flag)
	{
		ResourceSetup set = new ResourceSetup (b);
		if (mResources.hasElement (set))
			mResources.getElement (set).setInifite (flag);
		else
		{
			set.setInifite (flag);
			mResources.add (set);
		}
	}
	
	
	private Set<ResourceSetup> mResources;
	private int mContD, mContW, mContH;
}
