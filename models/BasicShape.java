
package models;

import geometry.Cuboid;
import geometry.GaussElim;
import geometry.IntersectionSolver;
import geometry.Line;
import geometry.Point;
import geometry.Rectangle;
import geometry.IntersectionSolver.Result;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;

import models.Matrix.*;


public class BasicShape
{
	public static enum RelatPos {FRONT, BACK, LEFT, RIGHT, ABOVE, BELOW};
	
	public static enum RotationDir {ONWARD, BACKWARD}
	
	@SuppressWarnings("serial")
	public static class BadNumberOfRowsException extends IllegalArgumentException
	{
		public BadNumberOfRowsException() {super(); }
		
		public BadNumberOfRowsException (String message) { super (message); }
	}
	
	@SuppressWarnings("serial")
	public static class BadNumberOfCollumsException extends IllegalArgumentException
	{
		public BadNumberOfCollumsException() {super(); }
		
		public BadNumberOfCollumsException (String message) { super (message); }
	}
	
	public static class NonExistingVertexException extends IllegalArgumentException
	{
		public NonExistingVertexException() {super(); }
		
		public NonExistingVertexException (String message) { super (message); }
	}
	
	/**
	 * @param points list of points
	 * @param connected 2d list of points connected to points
	 * @return adjacency matrix containing connections in connected to points
	 */
	public static IntegerMatrix buildAdjacencyMatrix (ArrayList <IntegerMatrix> points, ArrayList <ArrayList <IntegerMatrix>> connected)
	{
		IntegerMatrix adj = new IntegerMatrix (points.size(), points.size());
		for (int cPoint = 0; cPoint < points.size(); ++cPoint)
		{
			for (IntegerMatrix connect : connected.get(cPoint))
			{
				int cFindConnect = 0;
				while (cFindConnect < points.size() && !points.get(cFindConnect).equals(connect))
					++cFindConnect;
				adj.setCell(cPoint, cFindConnect, 1);
				adj.setCell(cFindConnect, cPoint, 1);
			}
		}
		return adj;
	}
	
	/** Creates a rotation matrix based on given angles of rotation
	 * @param angle1 Desired amount of rotation in x2 axis (in degrees)
	 * @param angle2 Desired amount of rotation in x3 axis (in degrees)
	 * @return rotation matrix
	 */
	public static Matrix<Double> rotationMatrix (double angle1, double angle2, RotationDir d)
	{
		double radAngle1 = Math.toRadians (angle1);
		double radAngle2 = Math.toRadians (angle2);
		//rotation matrix for y axis
		Matrix.DoubleMatrix rotationMatrix1 = new Matrix.DoubleMatrix (3, 3);
		rotationMatrix1.setCell (0, 0, Math.cos (radAngle1));
		rotationMatrix1.setCell (2, 0, -Math.sin (radAngle1));
		rotationMatrix1.setCell (1, 1, 1.0);
		rotationMatrix1.setCell (0, 2, Math.sin (radAngle1));
		rotationMatrix1.setCell (2, 2, Math.cos (radAngle1));
		//rotation matrix for z axis
		Matrix.DoubleMatrix rotationMatrix2 = new Matrix.DoubleMatrix (3, 3);
		rotationMatrix2.setCell (0, 0, Math.cos (radAngle2));
		rotationMatrix2.setCell (1, 0, Math.sin (radAngle2));
		rotationMatrix2.setCell (0, 1, -Math.sin (radAngle2));
		rotationMatrix2.setCell (1, 1, Math.cos (radAngle2));
		rotationMatrix2.setCell (2, 2, 1.0);
		
		if (angle1 == 0.0)
			return rotationMatrix2;
		else if (angle2 == 0.0)
			return rotationMatrix1;
		else if (d == RotationDir.ONWARD)
			return rotationMatrix1.multiply (rotationMatrix2, new Matrix.DoubleMatrix (3, 3));
		else if (d == RotationDir.BACKWARD)
			return rotationMatrix2.multiply (rotationMatrix1, new DoubleMatrix (3, 3));
		throw new IllegalArgumentException ("uncaught case in rotation matrix creating method");
	}
	
	/**
	 * @param r relative position
	 * @param dimension dimension of the vector
	 * @return return vector to relative position in dimension
	 */
	public static IntegerMatrix getRelativePosVector (RelatPos r, int dimension)
	{
		IntegerMatrix v = new IntegerMatrix (dimension, 1);
		switch (r)
		{
		case ABOVE:	v.setCell(2, 0, 1);
			break;
		case BACK:	v.setCell(0, 0, -1);
			break;
		case BELOW:	v.setCell(2, 0, -1);
			break;
		case FRONT:	v.setCell(0, 0, 1);
			break;
		case LEFT:	v.setCell(1, 0, -1);
			break;
		case RIGHT:	v.setCell(1, 0, 1);
		}
		return v;
	}
	
	/**
	 * @param p1 first point
	 * @param p2 second point
	 * @return relative position of p1 to p2
	 * Precondition: dimension of p1, p2 is the same
	 */
	public static RelatPos getRelativePos (Glue p1, Glue p2)
	{
		if (p1.getPosition (0) < p2.getPosition (0))
			return RelatPos.FRONT;
		if (p1.getPosition (0) > p2.getPosition(0))
			return RelatPos.BACK;
		if (p1.getPosition (1) < p2.getPosition (1))
			return RelatPos.RIGHT;
		if (p1.getPosition (1) > p2.getPosition (1))
			return RelatPos.LEFT;
		if (p1.getPosition (2) < p2.getPosition (2))
			return RelatPos.ABOVE;
		if (p1.getPosition (2) > p2.getPosition (2))
			return RelatPos.BELOW;
		throw new IllegalArgumentException (p1 + " and " + p2 + " are identical");
	}
	
	/**
	 * @param relat a given relative position
	 * @return the opposite relative position
	 */
	public static RelatPos getOpposite (RelatPos relat)
	{
		switch (relat)
		{
		case ABOVE: return RelatPos.BELOW;
		case BACK: return RelatPos.FRONT;
		case BELOW: return RelatPos.ABOVE;
		case FRONT: return RelatPos.BACK;
		case LEFT: return RelatPos.RIGHT;
		case RIGHT: return RelatPos.LEFT;
		default: throw new IllegalArgumentException ("invalid relat pos value, no opposite");
		}
	}
	
	/**
	 * inner class containing intersection point and 
	 * indices of the line the intersection is on
	 * @author martin
	 */
	public class Intersection extends Glue
	{
		/**
		 * @param vecInter vector to intersection
		 * @param indV1 index corresponding to start of line on which intersection is located
		 * @param indV2 index corresponding to end of line on which intersection is located
		 */
		public Intersection (IntegerMatrix vecInter, int indV1, int indV2)
		{
			super (vecInter);
			mVec1 = getVertex (indV1);
			mVec2 = getVertex (indV2);
			mIndV1 = indV1;
			mIndV2 = indV2;
			mAreVertices = true;
		}
		
		/**
		 * @param vecInter vector to intersection
		 * @param vec1 vector to start of line on which intersection is located
		 * @param vec2 vector to end of line on which intersection is located
		 */
		public Intersection (IntegerMatrix vecInter, IntegerMatrix vec1, IntegerMatrix vec2)
		{
			super (vecInter);
			mVec1 = vec1;
			mVec2 = vec2;
			updateVertexIndices();
		}
		
		/**
		 * @return get line start vector
		 */
		public IntegerMatrix getLineStart() { return mVec1; }
		
		/**
		 * @return get line end vector
		 */
		public IntegerMatrix getLineEnd() { return mVec2; }
		
		/**
		 * @return index of first vertex
		 */
		public int getLineStartIndex() { return mIndV1; }
		
		/**
		 * @return index of second vertex
		 */
		public int getLineEndIndex() { return mIndV2; }
		
		/**
		 * @return true if vectors stored refer to vertices of this shape
		 */
		public boolean areVertices() { return mAreVertices; }
		
		/**
		 * updates indices of vectors and boolean flag storing whether vectors
		 * refer to vertices
		 */
		public void updateVertexIndices()
		{
			mIndV1 = getVertexIndex (mVec1);
			mIndV2 = getVertexIndex (mVec2);
			if (mIndV1 < getNumberOfVertices() && mIndV2 < getNumberOfVertices())
				mAreVertices = true;
			else
				mAreVertices = false;
		}
		
		private IntegerMatrix mVec1, mVec2;
		private int mIndV1, mIndV2;
		private boolean mAreVertices;
	}
	
	/**
	 * constructs basic shape from parameters
	 * @param vectors list of vectors
	 * @param adjMatrix adjacency matrix
	 */
	@SuppressWarnings("unchecked")
	public BasicShape(ArrayList <IntegerMatrix> vectors, IntegerMatrix adjMatrix)
	{
		//init vertices
		this.vectors = new ArrayList<IntegerMatrix>();
		for (IntegerMatrix vec : vectors)
			this.vectors.add (vec.clone());
		if (!numberOfCols(vectors)) 
			throw new BadNumberOfCollumsException ("The vectors introduced are not 3x1");
		//compute dimensions
		dimensions = new ArrayList<Integer>();
		calcDim (vectors);
		//initialize offset position
		updateMinPos();
		updateMaxPos();
		//create adjacency matrix
		this.adjMatrix = adjMatrix.clone();
		//set possible connections
		mPossibleConnections = new ArrayList<ArrayList<RelatPos>>();
		for (int cVertex = 0; cVertex < getNumberOfVertices(); ++cVertex)
			mPossibleConnections.add (getHypoPossibleConnections (getVertex (cVertex)));
		//compute actual possible connections
		for (int cVertex = 0; cVertex < getNumberOfVertices(); ++cVertex)
			resetConnections(cVertex);
		mVolume = -1;
	}
	
	/**
	 * construct basic shape by copying clone
	 * @param clone another basic shape
	 */
	public BasicShape (BasicShape clone)
	{
		this (clone.vectors, clone.adjMatrix);
		mVolume = clone.mVolume;
		this.glue (clone.getGlue());
	}
	
	/**
	 * @return list of line objects, containing a line for each connection
	 */
	public ArrayList <Line> getConnectingLines()
	{
		ArrayList <Line> lines = new ArrayList<Line>();
		for (int cVertex = 0; cVertex < getNumberOfVertices(); ++cVertex)
		{
			
			for (int cConnect = cVertex; cConnect < getNumberOfVertices(); ++cConnect)
			{
				if (adjMatrix.getCell(cVertex, cConnect).equals(new Integer (1)))
					lines.add (new Line (new Glue (getVertex (cVertex)), new Glue (getVertex (cConnect))));
			}
				
		}
		return lines;
	}
	
	/**
	 * @return list of the sides of the basic shape
	 * Precondition: for each rectangle (= side) there need to be enough vertices
	 * to form them
	 */
	public ArrayList <Rectangle> getRectangles()
	{
		ArrayList <Rectangle> rects = new ArrayList<Rectangle>();
		for (int cVertex = 0; cVertex < getNumberOfVertices(); ++cVertex)
		{
			for (int cOppoVertex = cVertex + 1; cOppoVertex < getNumberOfVertices(); ++cOppoVertex)
			{
				int cConn = cVertex + 1, sharedConn = 0;
				while (cConn < getNumberOfVertices() && sharedConn < 2)
				{
					if (adjMatrix.getCell(cVertex, cConn).equals(1) && 
						adjMatrix.getCell(cOppoVertex, cConn).equals(1))
						++sharedConn;
					++cConn;
				}
				if (sharedConn == 2)
					rects.add(new Rectangle (new Glue (getVertex (cVertex)), new Glue (getVertex (cOppoVertex))));
			}
		}
		return rects;
	}
	
	/**
	 * cuts the empty space into cuboids
	 * @return list of these cuboids
	 * Precondition: there need to be enough vertices for each cuboid
	 */
	public ArrayList <Cuboid> getCuboids()
	{	
		ArrayList <Cuboid> cuboids = new ArrayList <Cuboid>();
		//condition: two points form a cuboid iff each of these points has 3 points which are connected to the other 3
		//these two points must not be connected
		
		int nOfV = getNumberOfVertices();
		//iterate through potential p1
		for (int cVert1 = 0; cVert1 < nOfV - 1; ++cVert1)
		{
			//iterate through potential p2
			for (int cVert2 = cVert1 + 1; cVert2 < nOfV; ++cVert2)
			{
				//is +1 correct???
				Cuboid cbd = getCuboid (cVert1, cVert2, cVert1 + 1, nOfV - 1, cVert1 + 1, nOfV - 1);
				if (cbd != null)
					cuboids.add (cbd);	
			}
		}
		return cuboids;
	}
	
	/**
	 * @param indDirect point to be directly connected
	 * @param indIndirect point to be indirectly connected
	 * @return set of all vertices directly connected to indDirect and indirectly connected to indIndirect
	 * !!! legacy !!!
	 */
	public ArrayList <Integer> findTriangleIndices (int indDirect, int indIndirect)
	{
		ArrayList <Integer> tPoints = new ArrayList <Integer>();
		IntegerMatrix indirectAdjacency = getIndirectAdjacencyMatrix(indIndirect);
		for (int cCol = 0; cCol < getNumberOfVertices(); ++cCol)
		{
			if (adjMatrix.getCell(indDirect, cCol).equals(1) && 
				indirectAdjacency.getCell (indIndirect, cCol).equals(1))
				tPoints.add (cCol);
		}
		return tPoints;
	}
	
	/**
	 * @param l2 line to search for intersection
	 * @return list of intersection points, each intersection involving the line
	 * intersecting with l2 in this object
	 */
	public ArrayList <Intersection> getLineIntersections (Line l2)
	{
		ArrayList <Intersection> intersections = new ArrayList<>();
		
		for (int cVert1 = 0; cVert1 < getNumberOfVertices() - 1; ++cVert1)
		{
			for (int cVert2 = cVert1 + 1; cVert2 < getNumberOfVertices(); ++cVert2)
			{
				if (adjMatrix.getCell (cVert1, cVert2).equals (1))
				{
					Line connection = new Line (new Glue (getVertex (cVert1)), new Glue (getVertex (cVert2)));
					IntersectionSolver solver = new IntersectionSolver (connection, l2);
					if (solver.getSolutionType() == IntersectionSolver.Result.ONE && solver.isWithinBounds())
						intersections.add (new Intersection(solver.getIntersection().toVector(), cVert1, cVert2));
				}
			}
		}
		return intersections;
	}
	
	/**
	 * @param place basic shape to place
	 * @param iVertex location to place place at
	 * @return list of positions where place may be placed adjacently to vertex at iVertex
	 */
	public ArrayList <Position> getRelativePlacements (BasicShape place, int iVertex)
	{
		ArrayList <Position> places = new ArrayList<Position>();
		places.add (new Position (getVertex (iVertex)));
		for (RelatPos r : mPossibleConnections.get(iVertex))
		{
			IntegerMatrix pos = getVertex(iVertex);
			switch (r)
			{
			case BACK:	pos.setCell (0, 0, pos.getCell (0, 0) - place.getDimensions(0));
			break;
			case FRONT:	pos.setCell (0, 0, pos.getCell (0, 0) + place.getDimensions(0));
			break;
			case LEFT:	pos.setCell (1, 0, pos.getCell (1, 0) - place.getDimensions(1));
			break;
			case RIGHT:	pos.setCell (1, 0, pos.getCell (1, 0) + place.getDimensions(1));
			break;
			case BELOW:	pos.setCell (2, 0, pos.getCell (2, 0) - place.getDimensions(2));
			break;
			case ABOVE:	pos.setCell (2, 0, pos.getCell (2, 0) + place.getDimensions(2));
			}
			places.add (new Position(pos));
		}
		return places;
	}
	
	/**
	 * @param index index to a vertex in this shape
	 * @return free connections for vertex associated with index
	 * @throws NonExistingVertexException if no vertex is associated with index
	 */
	public ArrayList <RelatPos> getFreeConnections (int index)
	{
		if (index < 0 || index >= getNumberOfVertices())
			throw new NonExistingVertexException ("invalid index");
		return (ArrayList <RelatPos>)mPossibleConnections.get (index).clone();
	}
	
	/**
	 * @param index index of point to look up connections for
	 * @return array list containing vectors to points connected to point at index each as a clone of original
	 */
	public ArrayList <IntegerMatrix> lookUpConnections (int index)
	{
		ArrayList<IntegerMatrix> connections = new ArrayList<IntegerMatrix>();
		for(int counter=0; counter<adjMatrix.getRows(); counter++){
			if(adjMatrix.getCell(index,counter)!=0){
					connections.add (vectors.get(counter).clone());
			}
		}
		return connections;
	}
	
	/**
	 * @param vertex a given vertex within the container
	 * @return hypothetically possible connections for vertex thus
	 * excluding connections which could only exist if the connected point
	 * was outside of the container
	 */
	public ArrayList <RelatPos> getHypoPossibleConnections (IntegerMatrix vertex)
	{
		int zInd = 0, xInd = 1, yInd = 2;
		ArrayList <RelatPos> relats = new ArrayList<>();
		IntegerMatrix maxPos = getMaxDimension().toVector();
		if (!vertex.getCell (zInd, 0).equals (mGlue.getPosition(zInd)))
			relats.add (RelatPos.BACK);
		if (!vertex.getCell (zInd, 0).equals (maxPos.getCell (zInd, 0)))
			relats.add (RelatPos.FRONT);
		if (!vertex.getCell (xInd, 0).equals (mGlue.getPosition (xInd)))
			relats.add (RelatPos.LEFT);
		if (!vertex.getCell (xInd, 0).equals (maxPos.getCell (xInd, 0)))
			relats.add (RelatPos.RIGHT);
		if (!vertex.getCell (yInd, 0).equals (mGlue.getPosition (yInd)))
			relats.add (RelatPos.BELOW);
		if (!vertex.getCell (yInd, 0).equals (maxPos.getCell (yInd, 0)))
			relats.add (RelatPos.ABOVE);
		return relats;
	}
	
	/**
	 * @param index index of vertex to search for common connections
	 * @return square matrix containing a 1 for every other vertex that is a shared connection
	 */
	public IntegerMatrix getIndirectAdjacencyMatrix (int index)
	{
		IntegerMatrix indirectAdj = new IntegerMatrix (vectors.size(), vectors.size());
		for (int cRow = 0; cRow < indirectAdj.getRows(); ++cRow)
		{
			for (int cCol = 0; cCol < indirectAdj.getColumns(); ++cCol)
			{
				if (cRow != index && adjMatrix.getCell(cRow, cCol).equals(1) && 
					adjMatrix.getCell(index, cCol).equals(1))
					indirectAdj.setCell(cRow, cCol, 1);
			}
		}
		return indirectAdj;
	}
	
	/**
	 * @param indP1 index of first vertex
	 * @param indP2 index of second vertex
	 * @param minP1Conn offset index of indP1's connections (inclusive)
	 * @param maxP1Conn max. index of indP1's connections (inclusive)
	 * @param minP2Conn offset index of indP2's connections (inclusive)
	 * @param maxP2Conn max. index of indP2's connections (inclusive)
	 * @return a cuboid contained in this shape where the first and the second vertex are
	 * diagonally opposite to each other, if such a cuboid exists. Otherwise this method
	 * returns null
	 */
	public Cuboid getCuboid (int indP1, int indP2, int minP1Conn, int maxP1Conn, int minP2Conn, int maxP2Conn)
	{
		int connConnReq = 6;
		//if p1, p2 are disconnected
		if (adjMatrix.getCell (indP1, indP2).equals(0))
		{
			int cConnConnections = 0;
			int cConn1 = minP1Conn;
			//iterate through all connections of p1 starting at given offset
			while (cConn1 <= maxP1Conn && cConnConnections < connConnReq)
			{
				if (adjMatrix.getCell (indP1, cConn1).equals (1))
				{
					int cConn2 = minP2Conn;
					int connConnFound = 0, connConnPerConn = 2;
					//iterate through all connections of p2 starting at given offset
					//until connections of connections is found
					while (cConn2 <= maxP2Conn && connConnFound < connConnPerConn)
					{
						
						if (adjMatrix.getCell (indP2, cConn2).equals (1) &&
							adjMatrix.getCell (cConn1, cConn2).equals (1))
						{
							//System.out.println ("mutual connection of connections " + new Glue (getVertex (cConn1)) + " to " + new Glue (getVertex (cConn2)));
							++cConnConnections;
							++connConnFound;
						}
						++cConn2;
					}
				}
				++cConn1;
			}
			if (cConnConnections >= connConnReq)
			{
				Glue p1 = new Glue (getVertex (indP1));
				Glue p2 = new Glue (getVertex (indP2));
				return new Cuboid (p1, p2);
			}
		}
		return null;
	}
	
	/**
	 * @param lineStart start point of line
	 * @param dir direction of line
	 * @return line from lineStart to end point touching side of range of this shape in dir
	 * or null if such an end point not equal to lineStart would be outside of the range
	 */
	public Line getLineToBorder (Glue lineStart, RelatPos dir)
	{
		Glue maxPos = getMaxDimension();
		IntegerMatrix lineEnd = getRelativePosVector(dir, lineStart.getDimension());
		for (int cDim = 0; cDim < lineStart.getDimension(); ++cDim)
		{
			int dirCoord = lineEnd.getCell (cDim, 0);
			if (dirCoord != 0)
			{
				int border = (dirCoord >= 0 ? maxPos.getPosition (cDim) : mGlue.getPosition(cDim));
				//check whether distance to border is not 0 if direction is not zero
				if (border == lineStart.getPosition (cDim))
					return null;
				lineEnd.setCell (cDim, 0, border);
			}
			else
				lineEnd.setCell (cDim, 0, lineStart.getPosition (cDim));
		}
		return new Line (lineStart, new Glue (lineEnd));
	}
	
	/**
	 * @param index index of vertex
	 * @return vertex at index translated by glued offset
	 */
	public IntegerMatrix getVertex (int index)
	{
		return vectors.get(index).clone();
	}
	
	/**
	 * @return the point within the container having the
	 * largest coordinate values
     */
	public Glue getMaxDimension () 
	{
		return mMax;
	}
	
	/**
	 * @return position where shape is glued at
	 */
	public Glue getGlue()
	{
		return mGlue;
	}
	
	/**
	 * @return the volume of the basic shape
	 */
	public int getVolume()
	{
		if (mVolume < 0)
		{
			mVolume = 0;
			BasicShape cut = new BasicShape (this);
			cut.addMissingRectanglePoints();
			ArrayList <Cuboid> cubes = cut.getCuboids();
			for (Cuboid cube : cubes)
			{
				ArrayList <Integer> dims = cube.getDimensions();
				int vol = 1;
				for (int dim : dims)
					vol *= dim;
				mVolume += vol;
			}
		}
		return mVolume;
	}
	
	/** calculates the maximum vector value
	* @param vector ArrayList containing all the vectors
	* @param index The index of the vector in the Matrix Handler
	* @return the maximum value.
	*/
	public int maximum(ArrayList <IntegerMatrix> vectors, int index){

		int max = Integer.MIN_VALUE;
    	for(Matrix<Integer> temp : vectors){
       		if(temp.getCell (index, 0) > max){
          		  max = temp.getCell (index, 0);
       		}
   		}
    	return max;

	}
	/** calculates the minimum vector value
	* @param vector ArrayList containing all the vectors
	* @param index The index of the vector in the Matrix Handler
	* @return the minimum value.
	*/
	public int minimum(ArrayList<IntegerMatrix> vectors, int index){

		int min = Integer.MAX_VALUE;
    	for(Matrix<Integer> temp: vectors){
       		if(temp.getCell (index, 0) < min){
          		  min= temp.getCell (index, 0);
       		}
   		}
    	return min;
	}
	
	/** compares that all the Matrix Handlers have the same number of rows
	* @param vectors ArrayList containing all the vectors
	* @return false if one Matrix Handler doesn't have the same number of rows
	*/
	public boolean numberOfMH(ArrayList<IntegerMatrix> vectors){

		int numberOfRows=vectors.get(0).getRows();
		for(Matrix<Integer> temp: vectors){
			if(temp.getRows() != numberOfRows)
				return false;
		}
		return true;
	}
	
	/**
	 * @param vectors set of vectors
	 * @return true if all vectors have the same number of rows
	 */
	public boolean numberOfCols(ArrayList<IntegerMatrix> vectors){

		int numberOfCols = 0;
		for(Matrix<Integer> temp: vectors){
			if (numberOfCols == 0)
				numberOfCols = temp.getColumns();
			else if(temp.getColumns() != numberOfCols)
				return false;
		}
		return true;
	}
	
	/** @return the dimensions of a shape given an index.
	 */
	public int getDimensions(int index){

		return dimensions.get(index);
	}
	
	/**
	 * @return number of vertices defining the shape
	 */
	public int getNumberOfVertices()
	{
		return vectors.size();
	}
	
	/**
	 * @param vertex vertex to search index for
	 * @return index of vertex or vectors.size() if vertex was not found
	 */
	public int getVertexIndex (IntegerMatrix vertex)
	{
		for (int cVertex = 0; cVertex < vectors.size(); ++cVertex)
		{
			if (vectors.get(cVertex).equals(vertex))
				return cVertex;
		}
		return vectors.size();
	}
	
	/**
	 * @param cmp basic shape to compare this to
	 * @return true if volume, glue, adjacency matrix and vertices are equal
	 */
	public boolean equals (Object cmp)
	{
		BasicShape comp = (BasicShape) cmp;
		if (this.mVolume != comp.mVolume)
			return false;
		if (this.getNumberOfVertices() != comp.getNumberOfVertices())
			return false;
		if (!this.mGlue.equals (comp.mGlue))
			return false;
		if (!this.adjMatrix.equals (comp.adjMatrix))
			return false;
		
		for (int cDim = 0; cDim < getGlue().getDimension(); ++cDim)
		{
			if (this.getDimensions (cDim) != comp.getDimensions (cDim))
				return false;
		}
		
		for (int cVec = 0; cVec < getNumberOfVertices(); ++cVec)
		{
			if (!this.getVertex (cVec).equals (comp.getVertex (cVec)))
				return false;
		}
			
		return true;
	}
	
	/**
	 * @param vec1 a vertex
	 * @param vec2 another vertex
	 * @return true if vec1 and vec2 are connected, false otherwise
	 * @throws NonExistingVertexException if vec1 or vec2 are not vertices of this object
	 */
	public boolean isConnected (IntegerMatrix vec1, IntegerMatrix vec2)
	{
		int ind1 = getVertexIndex (vec1);
		int ind2 = getVertexIndex (vec2);
		if (ind1 == getNumberOfVertices() || ind2 == getNumberOfVertices())
			throw new NonExistingVertexException ("given vertices do not exist");
		return (adjMatrix.getCell(ind1, ind2).equals (1));
	}
	
	/**
	 * @param ind1 a 0-based index referring to a vertex
	 * @param ind2 another 0-based index referring to a vertex
	 * @return true if referenced vertices are connected
	 */
	public boolean isConnected (int ind1, int ind2)
	{
		if (ind1 < 0 || ind1 >= adjMatrix.getRows() ||
			ind2 < 0 || ind2 >= adjMatrix.getRows())
			throw new NonExistingVertexException ("there are no vertices with corresponding indices");
		return (adjMatrix.getCell (ind1, ind2).equals (1));
	}
	
	/**
	 * @param b a given basic shape
	 * @return true if there is a line between two vertices of b on which
	 * there is an intersection with a side of this
	 * Precondition: there are enough vertices such that this and b
	 * can be dissected into cuboids smaller or equal to the entire shape
	 */
	public boolean intersect (BasicShape b)
	{
		ArrayList<Rectangle> sides = this.getRectangles();
		ArrayList<Line> lines = b.getConnectingLines();
		
		for (Rectangle side : sides)
		{
			for (Line line : lines)
			{
				line.setInclusion (false, false);
				IntersectionSolver checkInter = new IntersectionSolver(side, line);
				if (checkInter.getSolutionType() == IntersectionSolver.Result.ONE && 
					checkInter.isWithinBounds())
					return true;
			}
		}
		return false;
	}
	
	/**
	 * @param b a given basic shape in the same space as this shape
	 * @return true if b is within this
	 */
	public boolean isWithin (BasicShape b)
	{
		//check whether range of this encompasses b's range
		Glue bOffs = b.getGlue(), bMax = b.getMaxDimension();
		Glue tOffs = this.getGlue(), tMax = this.getMaxDimension();
		for (int cDim = 0; cDim < tOffs.getDimension(); ++cDim)
		{
			if (bOffs.getPosition (cDim) < tOffs.getPosition (cDim) ||
				bMax.getPosition (cDim) > tMax.getPosition (cDim))
				return false;
		}
		//do line check for every vertex of b
		for (int cVert = 0; cVert < b.getNumberOfVertices(); ++cVert)
		{
			if (!isWithin (new Glue (b.getVertex (cVert))))
				return false;
		}
		return true;
	}
	
	/**
	 * @param p a given point in the same space as shape
	 * @return true if p is within this shape
	 */
	public boolean isWithin (Glue p)
	{
		Glue origin = new Glue (new IntegerMatrix (p.getDimension(), 1));
		int inters = 0;
		for (Line l : getConnectingLines())
		{
			IntersectionSolver solver = new IntersectionSolver(new Line (origin, p), l);
			if (solver.getSolutionType() == IntersectionSolver.Result.ONE && solver.isWithinBounds())
				++inters;
		}
		
		return (inters % 2 == 1);
	}
	
	/** Calculates the dimensions of a shape
	** @param vectors ArrayList containing all the vectors
	*/
	public void calcDim(ArrayList<IntegerMatrix> vectors) throws BadNumberOfRowsException
	{
		
		if (!numberOfMH(vectors)) 
			throw new BadNumberOfRowsException ("vectors don't have the same dimension");
		
		dimensions.clear();
		for(int i=0; i<vectors.get(0).getRows(); i++)
		{

			int max = maximum (vectors,i);
			int min = minimum (vectors,i);
			dimensions.add(max-min);

		}
	}
	
	
	/**
	 * inserts new vertices into the shape such that it consists of
	 * multiple adjacent cuboids
	 * whenever a vertex has a free connection a new vertex will be inserted if 
	 * a line from the first vertex intersects in one point with an existing line.
	 * the intersection will be the new vertex
	 * new vertices will be added at the end of the list of vertices thus having
	 * higher number indices compared to the 'old' vertices
	 */
	public void addMissingRectanglePoints()
	{
		//compute min and max point of cuboid encompassing entire shape
		IntegerMatrix outerMax = new IntegerMatrix (mGlue.getDimension(), 1);	
		for (int cDim = 0; cDim < mGlue.getDimension(); ++cDim)
			outerMax.setCell(cDim, 0, mGlue.getPosition(cDim) + getDimensions(cDim));
		
		//get well defined sides S
		LinkedList <Rectangle> sides = new LinkedList <> (getRectangles());
		int cSides = 0;
		//for each side s in S
		while (cSides < sides.size())
		{
			//check: does it return first not second element?
			Rectangle r = sides.get (cSides);
			//for all vertices v
			int cVertex = 0;
			boolean foundFreeConnInter = false;
			while (cVertex < getNumberOfVertices() && !foundFreeConnInter)
			{
				Glue vertex = new Glue (getVertex (cVertex));
				//for all free connections c of v
				int cFree = 0;
				ArrayList <RelatPos> freeConns = getFreeConnections (cVertex);
				while (cFree < freeConns.size() && !foundFreeConnInter)
				{
					RelatPos free = freeConns.get (cFree);
					//if line from v in direction c intersects s:
					Line dirLine = getLineToBorder (vertex, free);
					if (dirLine != null)
					{
						dirLine.setInclusion (false, true);
						IntersectionSolver solInter = new IntersectionSolver (dirLine, r);
						if (solInter.getSolutionType() == IntersectionSolver.Result.ONE &&
							solInter.isWithinBounds())
						{
							
							Glue inter = solInter.getIntersection();
							//if there is a split
							if (!r.isVertex (inter))
							{
								//add intersection objects with lines touching the border of s through intersection
								ArrayList <Intersection> inters = new ArrayList<>();
								//split s into smaller s[] and add s[] to S
								ArrayList <Rectangle> splitSides = r.splitRectangle (inter);
								
								if (splitSides.size() == 2)
								{
									Line interLine = r.getPointLine (inter);
									//add intersection on intersection line
									inters.add (new Intersection (inter.toVector(), interLine.getFirst(), interLine.getSecond()));
									//add intersection on line parallel to intersection line
									ArrayList <Glue> splitVertices = splitSides.get (0).getVertices();
									IntersectionSolver isFirstOnInterLine = new IntersectionSolver (interLine, new Point (splitVertices.get(1)));
									Glue onParallel = null;
									if (isFirstOnInterLine.getSolutionType() == IntersectionSolver.Result.ONE)
										onParallel = splitVertices.get (3);
									else
										onParallel = splitVertices.get (1);
									Line parallelLine = r.getPointLine (onParallel);
									inters.add (new Intersection (onParallel.toVector(), parallelLine.getFirst(), parallelLine.getSecond()));
									addVertices(inters);
									inters.clear();
									//add intersection connecting above points
									inters.add (new Intersection (inter.toVector(), inter.toVector(), onParallel.toVector()));
								}
								else if (splitSides.size() == 4)
								{
									ArrayList <Glue> addedRectVertices = new ArrayList<>(4);
									//add unique non-defining vertices as intersections on original rectangle
									//for each second split rectangle
									int splitStep = splitSides.size() == 2 ? 1 : 2;
									for (int cSplit = 0; cSplit < splitSides.size(); cSplit += splitStep)
									{
										//add intersection objects for both non-defining point in shape's data structure
										ArrayList <Glue> splitVertices = splitSides.get (cSplit).getVertices();
										for (int cSplitVert = 1; cSplitVert < splitVertices.size(); cSplitVert += 2)
										{
											Glue splitVertex = splitVertices.get (cSplitVert);
											addedRectVertices.add (splitVertex);
											Line origLine = r.getPointLine (splitVertex);
											inters.add (new Intersection (splitVertex.toVector(), origLine.getFirst(), origLine.getSecond()));
										}
									}
									addVertices (inters);
									inters.clear();
									//add intersections for intersection and new vertices added above
									for (int cAdded1 = 0; cAdded1 < addedRectVertices.size(); ++cAdded1)
									{
										int cAdded2 = cAdded1 + 1;
										boolean foundOppo = false;
										while (!foundOppo && cAdded2 < addedRectVertices.size())
										{
											Line l = new Line (addedRectVertices.get (cAdded1), addedRectVertices.get (cAdded2));
											IntersectionSolver testInterOnLine = new IntersectionSolver (l, new Point (inter));
											if (testInterOnLine.getSolutionType() == IntersectionSolver.Result.ONE)
											{
												inters.add (new Intersection (inter.toVector(), l.getFirst(), l.getSecond()));
												foundOppo = true;
											}
											++cAdded2;
										}
									}
								}
								//add all intersection objects
								addVertices (inters);
								//remove current rect & add split rects
								sides.remove (r);
								sides.addAll (splitSides);
								--cSides;
								foundFreeConnInter = true;
								modifyConnection (cVertex, getVertexIndex (inter.toVector()), true);
							}
							else
							{
								int indInters = getVertexIndex (inter.toVector());
								//if intersection has opposite free connection available
								//applies if intersection already existed
								if (getFreeConnections (indInters).contains (getOpposite (free)))
									modifyConnection (cVertex, indInters, true);
							}
							
							
							/*
							if (splitSides.size() > 1)
							{
								
								
								
								ArrayList <Glue> addedRectVertices = new ArrayList<>(4);
								//for each second split rectangle
								int splitStep = splitSides.size() == 2 ? 1 : 2;
								for (int cSplit = 0; cSplit < splitSides.size(); cSplit += splitStep)
								{
									//add intersection objects for both non-defining point in shape's data structure
									ArrayList <Glue> splitVertices = splitSides.get (cSplit).getVertices();
									for (int cSplitVert = 1; cSplitVert < splitVertices.size(); cSplitVert += 2)
									{
										Glue splitVertex = splitVertices.get (cSplitVert);
										addedRectVertices.add (splitVertex);
										Line origLine = r.getPointLine (splitVertex);
										inters.add (new Intersection (splitVertex.toVector(), origLine.getFirst(), origLine.getSecond()));
									}
								}
								addVertices (inters);
								inters.clear();
								//add intersections objects for intersection point
								//iterate 2d through added vertices to find matching ones
								for (int cAdded1 = 0; cAdded1 < addedRectVertices.size(); ++cAdded1)
								{
									if (addedRectVertices.get (cAdded1) != null)
									{
										int cAdded2 = cAdded1 + 1;
										boolean lineEndFound = false;
										while (cAdded2 < addedRectVertices.size())
										{
											if (addedRectVertices.get (cAdded2) != null)
											{
												Line addLine = new Line (addedRectVertices.get (cAdded1), addedRectVertices.get (cAdded2));
												Point interPoint = new Point (inter);
												IntersectionSolver solve = new IntersectionSolver (interPoint, addLine);
												
												//if vertices are on one line => add intersection
												if (solve.getSolutionType() == IntersectionSolver.Result.ONE && solve.isWithinBounds() ||
													splitSides.size() == 2)
												{
													inters.add (new Intersection (inter.toVector(), addLine.getFirst(), addLine.getSecond()));
													lineEndFound = true;
												}
											}
											++cAdded2;
										}
									}
								}
								addVertices (inters);
								//add split rectangles, remove split rectangle
								sides.addAll (splitSides);
								sides.remove (cSides);
								--cSides;
								//skip remaining connections and vertices
								foundFreeConnInter = true;
							}
							
							int indInters = getVertexIndex (inter.toVector());
							//if intersection has opposite free connection available
							//applies if intersection already existed
							if (splitSides.size() > 1 || getFreeConnections (indInters).contains (getOpposite (free)))
								modifyConnection (cVertex, indInters, true);
							*/
						}
					}
					++cFree;
				}
				++cVertex;
			}
			++cSides;
		}
	}
	
	/** Performs actual rotation
	 * @param rotMatrix created from rotationMatrix()
	 * @return matrix after rotation
	 */
	public void rotate (Matrix<Double> rotMatrix){

		for(int cCounter=0; cCounter<vectors.size();cCounter++){
			Matrix.DoubleMatrix result = new Matrix.DoubleMatrix (3,1);
			Matrix.DoubleMatrix vec = vectors.get(cCounter).toDoubleMatrix();
			rotMatrix.multiply (vec, result);
			vectors.set (cCounter, result.toIntegerMatrix());
		}
		calcDim (vectors);
		updateMinPos();
		updateMaxPos();
	}
	
	/**
	 * Glues shape to g and translates all vertices
	 * @param g position
	 */
	public void glue (Glue g)
	{
		//translate vectors (needs old glue)
		for (int cVertex = 0; cVertex < getNumberOfVertices(); ++cVertex)
			vectors.set(cVertex, g.translateMat(vectors.get(cVertex), mGlue));
		//adapt glue
		mGlue = g.clone();
		//adapt max pos
		IntegerMatrix maxVec = new IntegerMatrix (mGlue.getDimension(), 1);
		for (int cDim = 0; cDim < mGlue.getDimension(); ++cDim)
			maxVec.setCell (cDim, 0, g.getPosition(cDim) + getDimensions (cDim));
		mMax = new Glue (maxVec);
	}
	
	/**
	 * updates glue position based on vertices
	 * call this method after changes were applied on vertices
	 */
	public void updateMinPos()
	{
		Glue min = null;
		int lowestSum = Integer.MAX_VALUE;
		for (int cVert = 0; cVert < getNumberOfVertices(); ++cVert)
		{
			IntegerMatrix vert = getVertex (cVert);
			int sum = 0;
			for (int cDim = 0; cDim < vert.getRows(); ++cDim)
				sum += vert.getCell (cDim, 0);
			if (sum < lowestSum)
			{
				lowestSum = sum;
				min = new Glue (vert);
			}
		}
		mGlue = min;
	}
	
	/**
	 * updates max position based on vertices
	 * call this method after changes were applied on vertices
	 */
	public void updateMaxPos()
	{
		Glue max = null;
		int largestSum = Integer.MIN_VALUE;
		for (int cVert = 0; cVert < getNumberOfVertices(); ++cVert)
		{
			IntegerMatrix vert = getVertex (cVert);
			int sum = 0;
			for (int cDim = 0; cDim < vert.getRows(); ++cDim)
				sum += vert.getCell (cDim, 0);
			if (sum > largestSum)
			{
				largestSum = sum;
				max = new Glue (vert);
			}
		}
		mMax = max;
	}
	
	public void print(PrintStream p)
	{
		p.println ("Printing vertices of basic shape");
		for (int cVec = 0; cVec < getNumberOfVertices(); ++cVec)
		{
			p.println ("vector " + cVec + " ");
			vectors.get(cVec).print(System.out);
			p.print("connections: ");
			for (int cConnect = 0; cConnect < getNumberOfVertices(); ++cConnect)
			{
				if (adjMatrix.getCell (cVec, cConnect).equals(1))
					p.print (cConnect + ", ");
			}
		}
	}
	
	/**
	 * Expands this shape by adding vectors of bs and connecting bs' vertices with existing ones
	 * @param bs shape to add to this shape
	 */
	protected void addShape (Block b)
	{
		BasicShape bs = (BasicShape)b;
		addVertices (bs.vectors, bs.adjMatrix);
		mVolume += b.getVolume();
	}
	
	/**
	 * adds intersection points to the list of vectors and
	 * manipulates connections such that the points the intersection is on
	 * are now indirectly connected through the intersection
	 * Precondition: the indices provided in the intersection objects 
	 * refer to valid vertices of this object
	 * @param inters list of intersections
	 */
	private void addVertices (ArrayList <Intersection> inters)
	{
		//structure: intersection, line start, line end => next intersection...
		ArrayList <IntegerMatrix> newVertices = new ArrayList<>();
		IntegerMatrix adj = new IntegerMatrix (3 * inters.size(), 3 * inters.size());
		
		int offset = 0;
		for (int cInter = 0; cInter < inters.size(); ++cInter)
		{
			Intersection inter = inters.get (cInter);
			//disconnect
			if (inter.areVertices())
				modifyConnection (inter.getLineStartIndex(), inter.getLineEndIndex(), false);
			
			newVertices.add (inter.toVector());
			int startingOffset = offset;
			++offset;
			int interVertex = getVertexIndex (inter.toVector());
			if (interVertex != inter.getLineStartIndex())
			{
				newVertices.add (getVertex (inter.getLineStartIndex()));
				adj.setCell (startingOffset, offset, 1);
				adj.setCell (offset, startingOffset, 1);
				++offset;
			}
			if (interVertex != inter.getLineEndIndex())
			{
				newVertices.add (getVertex (inter.getLineEndIndex()));
				adj.setCell (startingOffset, offset, 1);
				adj.setCell (offset, startingOffset, 1);
				++offset;
			}
		}
		addVertices (newVertices, adj);
	}
	
	/**
	 * adds missing points in newVertices to list of vectors in their exact order
	 * adds elements to mPossibleConnections
	 * fills in connections in adjacent
	 * @param newVertices vertices to add
	 * @param adjacent adjacency matrix containing connections of vertices to add
	 */
	private void addVertices (ArrayList <IntegerMatrix> newVertices, IntegerMatrix adjacent)
	{
		int lastOldIndex = getNumberOfVertices() - 1;
		//stores indices in list in this object of every element in newVertices
		ArrayList <Integer> addedIndices = new ArrayList<Integer>();
		//add vertices not yet contained to the end
		for (int cNewVertex = 0; cNewVertex < newVertices.size(); ++cNewVertex)
		{
			int cVertex = getVertexIndex (newVertices.get(cNewVertex));
			if (cVertex == vectors.size())
			{
				vectors.add (newVertices.get(cNewVertex));
				mPossibleConnections.add (getHypoPossibleConnections (newVertices.get (cNewVertex)));
			}
			addedIndices.add (cVertex);
		}
		
		//copy adjacency matrix into larger one if necessary
		if (vectors.size() > adjMatrix.getRows())
		{
			IntegerMatrix oldAdjMat = adjMatrix;
			adjMatrix = new IntegerMatrix (vectors.size(), vectors.size());
			adjMatrix.copyValues(oldAdjMat, 0, 0, 0, 0, oldAdjMat.getRows(), oldAdjMat.getColumns());
		}
		
		//fill in connections in adjacent
		/* legacy
		for (int cNewVertex = 0; cNewVertex < newVertices.size(); ++cNewVertex)
		{
			int iVertex = addedIndices.get(cNewVertex);
			for (int cAdj = 0; cAdj < adjacent.getColumns(); ++cAdj)
			{
				if (adjacent.getCell (cNewVertex, cAdj).equals(1))
				{
					int iAdj = getVertexIndex (newVertices.get(cAdj));
					modifyConnection (iVertex, iAdj, true);
				}
			}
		}
		*/
		for (int cNewVertex = newVertices.size() - 1; cNewVertex >= 0; --cNewVertex)
		{
			int iVertex = addedIndices.get (cNewVertex);
			for (int cAdj = 0; cAdj < adjacent.getColumns(); ++cAdj)
			{
				if (adjacent.getCell (cNewVertex, cAdj).equals (1))
				{
					int iAdj = getVertexIndex (newVertices.get (cAdj));
					modifyConnection (iVertex, iAdj, true);
					//if new connected to old
					if (iAdj <= lastOldIndex || iVertex <= lastOldIndex)
					{
						//iterate through iAdj's connections
						int cAdj2 = 0;
						while (cAdj2 <= lastOldIndex)
						{
							if (isConnected (iAdj, cAdj2) && cAdj2 != iVertex)
							{
								Glue newP = new Glue (getVertex (iVertex));
								Glue identical = new Glue (getVertex (iAdj));
								Glue oldP = new Glue (getVertex (cAdj2));
								IntersectionSolver checkOneLine = new IntersectionSolver (new Line (oldP, identical), new Point (newP));
								//if oldP is connected to identical s.t. new, old, identical are on one line
								if (checkOneLine.getSolutionType() == IntersectionSolver.Result.ONE)
								{
									//if newP is between oldP and identical
									if (checkOneLine.isWithinBounds())
									{
										modifyConnection (iAdj, cAdj2, false);
										modifyConnection (iVertex, cAdj2, true);
									}
									else
									{
										modifyConnection (iAdj, iVertex, false);
										modifyConnection (cAdj2, iVertex, true);
									}
								}
							}
							++cAdj2;
						}
					}
					else
					{
						//search for connection line both connected points are on
						for (int cLine1 = 0; cLine1 < lastOldIndex; ++cLine1)
						{
							for (int cLine2 = cLine1 + 1; cLine2 <= lastOldIndex; ++cLine2)
							{
								Glue exist1, exist2, newP, newAdj;
								exist1 = new Glue (getVertex (cLine1));
								exist2 = new Glue (getVertex (cLine2));
								newP = new Glue (getVertex (iVertex));
								newAdj = new Glue (getVertex (iAdj));
								
								Line connExist = new Line (exist1, exist2);
								Line connAdd = new Line (newP, newAdj);
								
								//if existing contains added
								if (connExist.isInRange (newP) && connExist.isInRange (newAdj) || 
									connAdd.isInRange (exist1) && connAdd.isInRange (exist2))
								{
									Line outer;
									Glue in1, in2;
									int iOut1, iOut2, iIn1, iIn2;
									if (connExist.isInRange (newP) && connExist.isInRange (newAdj))
									{
										outer = connExist;
										in1 = newP;
										in2 = newAdj;
										iOut1 = cLine1;
										iOut2 = cLine2;
										iIn1 = iVertex;
										iIn2 = iAdj;
									}
									else
									{
										outer = connAdd;
										in1 = exist1;
										in2 = exist2;
										iOut1 = iVertex;
										iOut2 = iAdj;
										iIn1 = cLine1;
										iIn2 = cLine2;
									}
									
									IntersectionSolver solve1 = new IntersectionSolver (outer, new Point (in1));
									IntersectionSolver solve2 = new IntersectionSolver (outer, new Point (in2));
									if (solve1.getSolutionType() == IntersectionSolver.Result.ONE && 
										solve2.getSolutionType() == IntersectionSolver.Result.ONE &&
										solve1.isWithinBounds() && solve2.isWithinBounds())
									{
										modifyConnection (iOut1, iOut2, false);
										if (in1.getDistance (new Glue (outer.getFirst())) < in1.getDistance (new Glue (outer.getSecond())))
										{
											modifyConnection (iIn1, iOut1, true);
											modifyConnection (iIn2, iOut2, true);
										}
										else
										{
											modifyConnection (iIn1, iOut2, true);
											modifyConnection (iIn2, iOut1, true);
										}
									}
								}
								//if one each is contained within the other
								else if ((connExist.isInRange (newP) || connExist.isInRange (newAdj)) && 
										(connAdd.isInRange (exist1) || connAdd.isInRange (exist2)))
								{
									Glue in1, in2;
									int iIn1, iIn2;
									if (connExist.isInRange(newP))
									{
										in1 = newP;
										iIn1 = iVertex;
									}
									else
									{
										in1 = newAdj;
										iIn1 = iAdj;
									}
									if (connAdd.isInRange (exist1))
									{
										in2 = exist1;
										iIn2 = cLine1;
									}
									else
									{
										in2 = exist2;
										iIn2 = cLine2;
									}
									IntersectionSolver solve1 = new IntersectionSolver (connExist, new Point (in1));
									IntersectionSolver solve2 = new IntersectionSolver (connAdd, new Point (in2));
									if (solve1.getSolutionType() == IntersectionSolver.Result.ONE && 
										solve2.getSolutionType() == IntersectionSolver.Result.ONE &&
										solve1.isWithinBounds() && solve2.isWithinBounds())
									{
										modifyConnection (cLine1, cLine2, false);
										modifyConnection (iVertex, iAdj, false);
										modifyConnection (cLine1, iIn1, true);
										modifyConnection (cLine2, iIn1, true);
										modifyConnection (iVertex, iIn2, true);
										modifyConnection (iAdj, iIn2, true);
									}
								}
								
							}
						}
					}
				}
			}
			
		}
		
		//compute remaining connections
		for (int addedIndex : addedIndices)
			resetConnections (addedIndex);
	}
	
	/**
	 * modifies a connection between vertices provided
	 * @param iVert1 index of first vertex
	 * @param iVert2 index of second vertex
	 * @param connected true: establish connection, false delete connection
	 * Precondition: iVert1, iVert2 are valid 0-based indices to vertices
	 */
	private void modifyConnection (int iVert1, int iVert2, boolean connected)
	{
		Glue p1 = new Glue (getVertex (iVert1)), p2 = new Glue (getVertex (iVert2));
		if (connected)
		{
			adjMatrix.setCell (iVert1, iVert2, 1);
			adjMatrix.setCell (iVert2, iVert1, 1);
			mPossibleConnections.get (iVert1).remove (getRelativePos (p1, p2));
			mPossibleConnections.get (iVert2).remove (getRelativePos (p2, p1));
		}
		else
		{
			adjMatrix.setCell (iVert1, iVert2, 0);
			adjMatrix.setCell (iVert2, iVert1, 0);
			mPossibleConnections.get (iVert1).add (getRelativePos (p1, p2));
			mPossibleConnections.get (iVert2).add (getRelativePos (p2, p1));
		}
		
	}
	
	/**
	 * Recalculates list of available connections for vertex at index iVertex
	 * @param iVertex
	 */
	private void resetConnections (int iVertex)
	{
		ArrayList <IntegerMatrix> connections = lookUpConnections(iVertex);
		Glue vertex = new Glue (getVertex(iVertex));
		ArrayList <RelatPos> remain = mPossibleConnections.get(iVertex);
		for (IntegerMatrix conn : connections)
			remain.remove (getRelativePos (vertex, new Glue (conn)));
	}
	
	private ArrayList<IntegerMatrix> vectors;
	private ArrayList<Integer> dimensions;
	private ArrayList <ArrayList <RelatPos>> mPossibleConnections;
	private IntegerMatrix adjMatrix;
	private Glue mGlue, mMax;
	private int mVolume;
}