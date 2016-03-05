package models;

import java.util.ArrayList;
import java.util.Comparator;

import models.Matrix.*;


public class Glue implements Cloneable 
{
	/**
	 * class comparing glue objects with respect to their distance
	 * to a reference point
	 * This reference point is the Glue object used to construct the comparator
	 * @author martin
	 */
	public class DistanceComp implements Comparator
	{
		@Override
		/**
		 * @param o1 a Glue object referring to the first point
		 * @param o2 a Glue object referring to the second point
		 * @return 1 if o1 is closer to the reference point than o2, -1 if o2 is closer or 0 if they are equally close
		 */
		public int compare (Object o1, Object o2) 
		{
			Glue p1 = (Glue) o1, p2 = (Glue) o2;
			double dist1 = getDistance (p1);
			double dist2 = getDistance (p2);
			if (dist1 < dist2)
				return 1;
			if (dist1 > dist2)
				return -1;
			return 0;
		}
		
		/**
		 * @return the reference position held by the outer class
		 */
		public Glue getReferencePosition()
		{
			return new Glue (pos);
		}
		
		public boolean equals (DistanceComp compare)
		{
			return pos.equals(compare.getReferencePosition());
		}
	}
	
	
	/** Nested class to create a custom exception
	 */
	@SuppressWarnings("serial")
	static class GlueException extends IllegalArgumentException{
		
		/**Exception constructor
		 */
		public GlueException(){
			super();
		}
		public GlueException(String message) {
			super(message);
			}
	}
	
	@SuppressWarnings("serial")
	public static class NotAVectorException extends IllegalArgumentException
	{
		public NotAVectorException() {}
		
		public NotAVectorException (String message) {super (message); }
	}
	
	public Glue(ArrayList<Integer> position)
	{
		if (position == null)
			throw new NullPointerException("null argument");
		pos = (ArrayList<Integer>) position.clone(); 
		if (pos.size() != 3) throw new GlueException("This ArrayList does not contain the appropriate number of positions");
	}
	
	/**
	 * constructor from matrix
	 * @param vec n x 1 matrix containing position vector
	 */
	public Glue (Matrix<Integer> vec)
	{
		if (vec.getColumns() != 1)
			throw new NotAVectorException ("Matrix of size " + vec.getRows() + " x " + vec.getColumns() + " is not a vector!");
		pos = new ArrayList<Integer>();
		for (int cRow = 0; cRow < vec.getRows(); ++cRow)
			pos.add(vec.getCell (cRow, 0));
	}
	
	public Glue clone()
	{
		return new Glue (pos);
	}
	
	/**
	* @return Position of glue
	*/
	public ArrayList<Integer> getPosition(){
			return (ArrayList<Integer>) pos.clone();
	}
	
	/**
	 * @param positions list of glue objects
	 * @return glue object the closest to this
	 */
	public Glue getClosest (ArrayList <Glue> positions)
	{
		double min = Double.MAX_VALUE;
		Glue minPos = null;
		for (Glue pos : positions)
		{
			double dist = getDistance (pos); 
			if (min > dist)
			{
				min = dist;
				minPos = pos;
			}
		}
		return minPos;
	}
	
	/**
	 * @param v matrix to translate
	 * @return matrix v translated by position held by the object
	 */
	public IntegerMatrix translateMat (IntegerMatrix v, Glue before)
	{
		IntegerMatrix trans = v.clone();
		for (int cRow = 0; cRow < trans.getRows(); ++cRow)
		{
			int diff = this.getPosition(cRow) - before.getPosition(cRow);
			for (int cCol = 0; cCol < trans.getColumns(); ++cCol)
				trans.setCell(cRow, cCol, trans.getCell(cRow, cCol) + diff);
		}
		return trans;
	}
	
	public String toString()
	{
		String s = "position ";
		for (int cCoord = 0; cCoord < getDimension(); ++cCoord)
			s += getPosition(cCoord) + " ";
		return s;
	}
	
	/**
	 * @param p2 another point
	 * @return distance between this and p2
	 * @throws GlueException if this and p2 do not belong to the same subspace
	 */
	public double getDistance (Glue p2)
	{
		if (this.getDimension() != p2.getDimension())
			throw new GlueException ("dimension mismatch: distance cannot be computed");
		IntegerMatrix diff = new IntegerMatrix (getDimension(), 1);
		for (int cDim = 0; cDim < getDimension(); ++cDim)
			diff.setCell(cDim, 0, this.getPosition (cDim) - p2.getPosition (cDim));
		Integer[] diffAsArray = diff.getColumn(0);
		return Math.sqrt(diff.vectorProduct(diffAsArray, diffAsArray));
	}
	
	/**
	 * @param index index of coordinate
	 * @return coordinate at index
	 */
	public int getPosition (int index)
	{
		return pos.get(index);
	}
	
	/**
	 * @return the coordinates in vector form as a integer matrix of size dim x 1
	 */
	public IntegerMatrix toVector()
	{
		IntegerMatrix vec = new IntegerMatrix (getDimension(), 1);
		for (int cCoord = 0; cCoord < getDimension(); ++cCoord)
			vec.setCell (cCoord, 0, getPosition().get(cCoord));
		return vec;
	}
	
	/**
	 * @return dimension of subspace the point is in
	 */
	public int getDimension()
	{
		return pos.size();
	}
	
	public boolean equals (Glue comp)
	{
		for (int cDim = 0; cDim < getDimension(); ++cDim)
		{
			if (this.getPosition(cDim) != comp.getPosition(cDim))
				return false;
		}
		return true;
	}
	
	
		
	private ArrayList<Integer> pos;
}
