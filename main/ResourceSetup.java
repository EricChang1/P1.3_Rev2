package main;

import models.Block;
import models.Resource;

/**
 * flexible, modifyable & comparable resource extension
 * use only for resources of unique blocks
 * @author martin
 */
public class ResourceSetup implements Comparable<ResourceSetup>
{
	public ResourceSetup (Block b) 
	{ 
		mBlock = b;
		mCapacity = 0;
		mInfinite = false;
	}
	
	/**
	 * @return a new resource object using values specified in this object
	 */
	public Resource constructResource()
	{
		return new Resource (mBlock, mCapacity, mBlock.getVolume(), mInfinite);
	}
	
	/**
	 * @return -1 if this < comp, 1 if this > comp, 0 if this == comp
	 * based on value, volume, number of vertices of block
	 */
	public int compareTo (ResourceSetup comp)
	{
		if (this.mBlock.getValue() < comp.mBlock.getValue())
			return -1;
		if (this.mBlock.getValue() > comp.mBlock.getValue())
			return 1;
		if (this.mBlock.getVolume() < comp.mBlock.getVolume())
			return -1;
		if (this.mBlock.getVolume() > comp.mBlock.getVolume())
			return 1;
		if (this.mBlock.getNumberOfVertices() < comp.mBlock.getNumberOfVertices())
			return -1;
		if (this.mBlock.getNumberOfVertices() > comp.mBlock.getNumberOfVertices())
			return 1;
		if (this.mCapacity < comp.mCapacity)
			return -1;
		if (this.mCapacity > comp.mCapacity)
			return 1;
		return 0;
	}
	
	/**
	 * @return capacity stored
	 */
	public int getCapacity() { return mCapacity; }
	
	/**
	 * @return true if infinite, false otherwise
	 * @return
	 */
	public boolean getInfinite() { return mInfinite; }
	
	/**
	 * @param flag true to set to infinite, false to set to finite
	 */
	public void setInifite (boolean flag)
	{
		mInfinite = flag;
	}
	
	/**
	 * @param cap capacity to set
	 */
	public void setCapacity (int cap)
	{
		if (cap < 0)
			throw new IllegalArgumentException ("negative capacity is not allowed");
		mCapacity = cap;
	}
	
	private Block mBlock;
	private int mCapacity;
	private boolean mInfinite;
}