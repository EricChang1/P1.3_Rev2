package geometry;

import java.util.ArrayList;

import models.Glue;

import models.Matrix.DoubleMatrix;

/**
 * class modeling a point
 * providing geometric utility as required from base class
 * @author martin
 */
public class Point extends GeoShape 
{
	public Point (Glue p)
	{
		super (p, p);
	}
	
	@Override
	public ArrayList<DoubleMatrix> getVectors() 
	{
		return new ArrayList<>();
	}

	@Override
	public DoubleMatrix loadEquationMatrix() 
	{
		DoubleMatrix eq = new DoubleMatrix (getDimension(), 1);
		eq.copyValues(getFirst().toDoubleMatrix(), 0, 0, 0, 0, getDimension(), 1);
		return eq;
	}

}
