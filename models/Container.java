package models;
import geometry.*;
import geometry.IntersectionSolver.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import models.Matrix.*;




public class Container extends Block
{	
	
	@SuppressWarnings("serial")
	public static class BlockNotFoundException extends IllegalArgumentException
	{
		public BlockNotFoundException () {}
		
		public BlockNotFoundException (Position posFail)
		{
			super ("no corresponding block in container at position " + posFail.toString());
		}

		public BlockNotFoundException (String message) 
		{
			super (message);
		}
		
	}	
	
	@SuppressWarnings("serial")
	public static class WrongPositionException extends IllegalArgumentException
	{
		public WrongPositionException() {}
		
		public WrongPositionException (Position posFail)
		{
			super("position cant be used " + posFail.toString());
		}
		
		public WrongPositionException (String message, Position posFail)
		{
			super(message + " " + posFail.toString());
		}
	}
	
	/**
	 * @param d depth of container
	 * @param w width of container
	 * @param h height of container
	 * @return list of vectors defining a cuboid container of given d, w, h
	 */
	public static ArrayList <IntegerMatrix> computeInitDimVectors (int d, int w, int h)
	{
		ArrayList <IntegerMatrix> vecs = new ArrayList<Matrix.IntegerMatrix>();
		IntegerMatrix vec = null;
		for (int cVertex = 0; cVertex < 8; ++cVertex)
		{
			vec = new IntegerMatrix(3, 1);
			if (cVertex > 3)
				vec.setCell(0, 0, d);
			if (cVertex % 4 == 2 || cVertex % 4 == 3)
				vec.setCell(1, 0, w);
			if (cVertex % 2 == 1)
				vec.setCell(2, 0, h);
			vecs.add (vec);
		}
		return vecs;
	}
	
	/**
	 * constructs an adjacency matrix for verts for a cuboid container
	 * @param verts set of vectors in the same dimension
	 * @return adjacency matrix for verts
	 */
	public static IntegerMatrix computeInitAdjacencyMatrix (ArrayList <IntegerMatrix> verts)
	{
		IntegerMatrix adj = new IntegerMatrix (verts.size(), verts.size());
		for (int cVertex = 0; cVertex < verts.size(); ++cVertex)
		{
			for (int cConnect = 0; cConnect < verts.size(); ++cConnect)
			{
				if (cVertex != cConnect)
				{
					int cSame = 0, cDiff = 0;
					for (int cCoord = 0; cCoord < verts.get(cVertex).getRows(); ++cCoord)
					{
						if (verts.get(cVertex).getCell(cCoord, 0).equals(verts.get(cConnect).getCell (cCoord, 0)))
							++cSame;
						else
							++cDiff;
					}
					if (cSame == 2 && cDiff == 1)
					{
						adj.setCell(cVertex, cConnect, 1);
						adj.setCell(cConnect, cVertex, 1);
					}
				}
			}
		}
		return adj;
	}
	
	/**
	 * @param d given depth
	 * @param w given width
	 * @param h given height
	 * @return cuboid basic shape of d x w x h
	 */
	public static BasicShape constructInitShape (int d, int w, int h)
	{
		ArrayList <IntegerMatrix> vertices = computeInitDimVectors(d, w, h);
		IntegerMatrix adjMat = computeInitAdjacencyMatrix(vertices);
		return new BasicShape(vertices, adjMat);
	}
	
	/**
	 * @param dimensions depth, width, height (x1,x2,x3) of the container
	 * Calls the Block constructor with the default_value of 0
	 */
	public Container (int d, int w, int h)
	{
		super(constructInitShape (d, w, h), 0);
		mPlacedBlocks = new ArrayList <Block>();
	}
	
	
	public Container (Block b)
	{
		super (b, 0);
		mPlacedBlocks = new ArrayList <Block>();
	}
	
	
	/**
	 * @return list of vertices enclosing the free space of a container
	 */
	/*
	public ArrayList <IntegerMatrix> getFreeVertices()
	{
		ArrayList <IntegerMatrix> freeVerts = new ArrayList<>();
		for (int cVertex = 0; cVertex < getNumberOfVertices(); ++cVertex)
		{
			//add to list iff
			//vertex is corner and corner is connected to another corner
			IntegerMatrix vertex = getVertex (cVertex);
			if (isCorner (vertex))
			{
				boolean onlyCornerConns = true;
				ArrayList <IntegerMatrix> conns = lookUpConnections (cVertex);
				int cConn = 0;
				while (onlyCornerConns && cConn < conns.size())
				{
					if (!isCorner (conns.get (cConn)))
						onlyCornerConns = false;
					++cConn;
				}
				if (onlyCornerConns)
					freeVerts.add (vertex);
			}
			//vertex is not corner and has free connections
			else if (!getFreeConnections (cVertex).isEmpty())
				freeVerts.add (vertex);
		}
		return freeVerts;
	}
	*/
	
	/**
	 * @TODO debug
	 * @return list of cuboids which contain together the entire free space in this container
	 */
	public ArrayList <Cuboid> getFreeCuboids()
	{
		ArrayList <Cuboid> free = new ArrayList<>();
		
		Container cloneCont = this.clone();
		cloneCont.addMissingRectanglePoints();
		
		//new approach:
		//iterate through new added vertices in reverse order
		//iterate through all vertices until current new vertex in normal order
		//search cuboid for current new vertex, rest vertex, exclude searched new vertices from connection look ups
		for (int cNewVert = cloneCont.getNumberOfVertices() - 1; cNewVert >= this.getNumberOfVertices(); --cNewVert)
		{
			for (int cOldVert = 0; cOldVert < cNewVert; ++cOldVert)
			{
				Cuboid c = cloneCont.getCuboid (cOldVert, cNewVert, 0, cNewVert, 0, cNewVert);
				if (c != null)
					free.add (c);
			}
		}
		
		
		return free;
	}
	
	/**
	 * @return deep copy of this by placing the cloned blocks
	 * at the same positions in the cloned container
	 */
	public Container clone()
	{
		Container clone = new Container (getDimensions(0), getDimensions(1), getDimensions(2));
		Glue origin = new Glue (new IntegerMatrix (getGlue().getDimension(), 1));
		for (Block b : mPlacedBlocks)
			clone.placeBlock (b, origin);
		
		return clone;
	}
	
	/**
	 * @param index index of block in container
	 * @return the block at index
	 */
	public Block getBlock (int index)
	{
		return mPlacedBlocks.get(index);
	}
	
	/**
	 * @param vertex a given vertex
	 * @return true if vertex is a corner vertex of this container
	 */
	public boolean isCorner (IntegerMatrix vertex)
	{
		Glue minPos = getGlue(), maxPos = getMaxDimension();
		for (int cDim = 0; cDim < minPos.getDimension(); ++cDim)
		{
			if (!vertex.getCell (cDim, 0).equals (minPos.getPosition (cDim)) || 
				!vertex.getCell (cDim, 0).equals (maxPos.getPosition (cDim)))
				return false;
		}
		return true;
	}
	
	/**	Places a block at the specified position
		@param block the block object to place
		@param pos the position to place block 
		Precondition: block is placeable at position
		Postcondition: Container will contain block at pos if pos does not refer to another block already placed
		The position refers to the uppermost top-left corner of the smallest possible cuboid containing the block
	**/
	public void placeBlock (Block block, Glue pos)
	{
		Block cloneBlock = block.clone();
		cloneBlock.glue (pos);
		mPlacedBlocks.add (cloneBlock);
		addShape (cloneBlock);
	}
	
	/**
	 * glue container to specific position
	 * translates all blocks by the difference between the previous
	 * glue and the current glue
	 */
	public void glue (Glue pos)
	{
		if (!(mPlacedBlocks == null || mPlacedBlocks.isEmpty()))
		{
			Glue prev = getGlue();
			//compute difference
			IntegerMatrix diff = new IntegerMatrix (prev.getDimension(), 1);
			for (int cDim = 0; cDim < diff.getRows(); ++cDim)
				diff.setCell (cDim, 0, pos.getPosition (cDim) - prev.getPosition (cDim));
			//glue placed blocks
			for (Block placed : mPlacedBlocks)
			{
				Glue placedPrev = placed.getGlue();
				IntegerMatrix blockGlue = new IntegerMatrix (placedPrev.getDimension(), 1);
				for (int cDim = 0; cDim < blockGlue.getRows(); ++cDim)
					blockGlue.setCell (cDim, 0, placedPrev.getPosition (cDim) + diff.getCell (cDim, 0));
				placed.glue (new Glue (blockGlue));
			}
		}
		super.glue (pos);
	}
	
	/**
	 * rotates the container by given rotation matrix
	 * rotates all blocks placed within
	 */
	public void rotate (Matrix<Double> rotMat)
	{
		for (Block placed : mPlacedBlocks)
			placed.rotate (rotMat);
		super.rotate (rotMat);
	}
	
	/** @param pos Position queried block is at
		@return block at pos as clone
		@throws BlockNotFoundException
	**/
	/*
	public Block getBlockAt (Position pos) throws BlockNotFoundException
	{
		if (mGluedBlocks.containsKey(pos))
			return mPlacedBlocks.get(pos).clone();
		else 
			throw new BlockNotFoundException ("There is no block at" + pos);
	}*/
	
	/** @return a string representation of the container**/
	public String toString()
	{
		//return "Height: " + getDimensions(0) + ",\n width: " + getDimensions(1) + "\nglues at: " + this.mGluedBlocks;
		return "container with " + getAmountOfBlocks() + " blocks";
	}
	
	
	/**
	 * @param pos position to check
	 * @return true if there is a block associated with pos
	 */
	/*
	public boolean hasBlockAt (Position pos)
	{
		return mGluedBlocks.containsKey(pos);
	}*/
	
	/**	@param block the block object to place
		@param pos the position to place block 
		@return true if block placed at pos does not cause any overlapping with previously placed blocks
		The position refers to the top-left corner of the smallest possible rectangle containig the block
	**/
	public boolean checkPositionOverlap (Block block, Glue pos)
	{	
		//check whether lines inside intersect with sides of block
		Glue prevPos = block.getGlue();
		block.glue(pos);
		
		BasicShape completed = new BasicShape (block);
		completed.addMissingRectanglePoints();
		//@TODO optimize
		//ArrayList<Rectangle> blockSides = completed.getRectangles();
		
		for (Block bPlaced : mPlacedBlocks)
		{
			BasicShape placedCompleted = new BasicShape (bPlaced);
			placedCompleted.addMissingRectanglePoints();
			if (placedCompleted.intersect (completed, false) || 
				completed.intersect (placedCompleted, false))
				return false;
			
			if (bPlaced.isWithin (block) || block.isWithin (bPlaced))
				return false;
		}
		block.glue (prevPos);
		return true;
	}
	
	/**
	 * @param mStartingPosition position to check
	 * @return true if position is within the container
	 */
	public boolean checkPositionInside (Glue mStartingPosition)
	{
		for (int cCoord = 0; cCoord < mStartingPosition.getDimension(); ++cCoord)
		{
			int coord = mStartingPosition.getPosition(cCoord);
			if (coord < 0 || coord > getDimensions(cCoord))
				return false;
		}
		return true;
	}
	
	/**
	 * @return number of blocks placed in this container
	 */
	public int getAmountOfBlocks() {
		return mPlacedBlocks.size();
	}
	
	//private HashMap <Glue, Block> mGluedBlocks;
	private ArrayList <Block> mPlacedBlocks;
}
