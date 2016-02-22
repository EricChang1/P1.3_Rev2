package geometry;


import java.util.ArrayList;


import models.Glue;
import models.Matrix;
import models.Matrix.*;

/**
 * class modeling a rectangle
 * providing utility for intersection searches
 * @author martin
 */
public class Rectangle extends GeoShape {

	public Rectangle (Glue p1, Glue p2) 
	{
		super (p1, p2);
		determineVectors();
	}
	
	/**
	 * @return list of lines defining the rectangle's border
	 */
	public ArrayList <Line> getBorderLines()
	{
		ArrayList <Line> lines = new ArrayList<>(4);

		IntegerMatrix matP2 = new IntegerMatrix (getDimension(), 1);
		IntegerMatrix matP4 = new IntegerMatrix (getDimension(), 1);
		for (int cDim = 0; cDim < getDimension(); ++cDim)
		{
			if (!mVec1.getCell (cDim, 0).equals (0))
				matP2.setCell (cDim, 0, getSecond().getCell (cDim, 0));
			if (!mVec2.getCell (cDim, 0).equals (0))
				matP4.setCell (cDim, 0, getSecond().getCell (cDim, 0));
		}
		
		//stored diagonal
		Glue p1 = new Glue (getFirst()), p3 = new Glue (getSecond());
		//computed diagonal
		Glue p2 = new Glue (matP2), p4 = new Glue (matP4);
		lines.add (new Line (p1, p2));
		lines.add (new Line (p1, p3));
		lines.add (new Line (p2, p4));
		lines.add (new Line (p3, p4));
		return lines;
	}
	
	/**
	 * @param p an arbitrary point within the rectangle
	 * @return one line through p for each vector stored defining
	 * the basis for the vector space this rectangle is in
	 */
	public ArrayList <Line> getBasisLinesThroughPoint (Glue p)
	{
		if (!isInRange (p))
			throw new IllegalArgumentException ("point provided is not within rectangle");
		
		//start & end points of lines
		IntegerMatrix p1, p2, p3, p4;
		p1 = new IntegerMatrix (getDimension(), 1);
		p2 = new IntegerMatrix (getDimension(), 1);
		p3 = new IntegerMatrix (getDimension(), 1);
		p4 = new IntegerMatrix (getDimension(), 1);
		
		for (int cDim = 0; cDim < getDimension(); ++cDim)
		{
			if (!mVec1.getCell (cDim, 0).equals (0))
			{
				p1.setCell (cDim, 0, getFirst().getCell (cDim, 0));
				p2.setCell (cDim, 0, getSecond().getCell (cDim, 0));
			}
			else
			{
				p1.setCell (cDim, 0, p.getPosition(cDim));
				p2.setCell (cDim, 0, p.getPosition(cDim));
			}
			if (!mVec2.getCell (cDim, 0).equals (0))
			{
				p3.setCell (cDim, 0, getFirst().getCell (cDim, 0));
				p4.setCell (cDim, 0, getSecond().getCell (cDim, 0));
			}
			else
			{
				p3.setCell (cDim, 0, p.getPosition(cDim));
				p4.setCell (cDim, 0, p.getPosition(cDim));
			}
		}
		
		ArrayList <Line> lines = new ArrayList<>();
		lines.add (new Line (new Glue (p1), new Glue (p2)));
		lines.add (new Line (new Glue (p3), new Glue (p4)));
		return lines;
	}
	
	/**
	 * @return vertices of this rectangle
	 */
	public ArrayList <Glue> getVertices()
	{
		IntegerMatrix matP2 = getFirst().clone();
		IntegerMatrix matP4 = getFirst().clone();
		for (int cDim = 0; cDim < getDimension(); ++cDim)
		{
			if (!mVec1.getCell (cDim, 0).equals (0))
				matP2.setCell (cDim, 0, getSecond().getCell (cDim, 0));
			if (!mVec2.getCell (cDim, 0).equals (0))
				matP4.setCell (cDim, 0, getSecond().getCell (cDim, 0));
		}
		
		ArrayList <Glue> verts = new ArrayList<>();
		verts.add (new Glue (getFirst()));
		verts.add (new Glue (matP2));
		verts.add (new Glue (getSecond()));
		verts.add (new Glue (matP4));
		return verts;
	}
	
	/**
	 * @param s a shape
	 * @return list of points on the rectangle's border intersecting s
	 */
	public ArrayList <Glue> getBorderLineIntersections (GeoShape s)
	{
		ArrayList <Glue> inters = new ArrayList<>();
		ArrayList <Line> bLines = getBorderLines();
		//set inclusions to exclude duplicate intersections for intersections of border lines
		//for l2 switch 1st off, for l4 switch 2nd off
		for (int cLine = 1; cLine < bLines.size(); cLine += 2)
			bLines.get (cLine).setInclusion (cLine % 4 == 0 ? true : false, cLine % 4 == 0 ? false : true);
		for (Line l : bLines)
		{
			IntersectionSolver interSol = new IntersectionSolver (s, l);
			if (interSol.getSolutionType() == IntersectionSolver.Result.ONE && 
				interSol.isWithinBounds())
				inters.add (interSol.getIntersection());
		}
		return inters;
	}
	
	@Override
	public ArrayList<DoubleMatrix> getVectors() 
	{
		ArrayList <DoubleMatrix> vecs = new ArrayList<Matrix.DoubleMatrix>();
		vecs.add(mVec1.clone());
		vecs.add(mVec2.clone());
		return vecs;
	}
	
	/**
	 * @param p a given point
	 * @return the line p is on or null if p is not located on any line of this rectangle
	 */
	public Line getPointLine (Glue p)
	{
		for (Line border : getBorderLines())
		{
			if (border.isInRange (p))
				return border;
		}
		return null;
	}

	@Override
	public DoubleMatrix loadEquationMatrix() 
	{
		DoubleMatrix eq = new DoubleMatrix(getDimension(), 3);
		eq.copyValues(mVec1, 0, 0, 0, 0, getDimension(), 1);
		eq.copyValues(mVec2, 0, 1, 0, 0, getDimension(), 1);
		eq.copyValues(getFirst().toDoubleMatrix(), 0, 2, 0, 0, getDimension(), 1);
		return eq;
	}
	
	private void determineVectors()
	{
		for (int cDim = 0; cDim < getDimension(); ++cDim)
		{
			int pDiff = getFirst().getCell(cDim, 0) - getSecond().getCell(cDim, 0);
			if (pDiff != 0)
			{
				DoubleMatrix v = new DoubleMatrix(getDimension(), 1);
				v.setCell(cDim, 0, pDiff);
				if (mVec1 == null)
					mVec1 = v;
				else if (mVec2 == null)
					mVec2 = v;
				else
					throw new BadVectorsException ("vector belongs to a higher order subspace than a plane");	
			}
		}
		if (mVec1 == null || mVec2 == null)
			throw new BadVectorsException ("vector belongs to a lower order subspace than a plane");
		mVec1 = normVector(mVec1);
		mVec2 = normVector(mVec2);
	}

	private DoubleMatrix mVec1, mVec2;
}
