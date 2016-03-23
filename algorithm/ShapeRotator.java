package algorithm;

import java.util.*;


import models.*;
import models.BasicShape.RotationDir;

/**
 * class computing rotations of a basic shape
 * @author martin
 */
public class ShapeRotator 
{
	/**
	 * @param b1 a given basic shape
	 * @param b2 another given basic shape
	 * @return true if each vertex of b1 is contained in b2 and each vertex in
	 * b2 is contained in b1
	 */
	public static boolean compareVertices (BasicShape b1, BasicShape b2)
	{
		if (b1.getNumberOfVertices() != b2.getNumberOfVertices())
			return false;
		for (int cVert1 = 0; cVert1 < b1.getNumberOfVertices(); ++cVert1)
		{
			if (b2.getVertexIndex (b1.getVertex (cVert1)) == b2.getNumberOfVertices())
				return false;
		}
		return true;
	}
	
	
	public ShapeRotator (BasicShape shape)
	{
		mShape = shape;
	}
	
	/**
	 * @return every distince 90 degrees rotation of the shape stored
	 */
	public ArrayList<BasicShape> getRotations()
	{
		Matrix<Double> xRotation = BasicShape.rotationMatrix (90.0, 0, RotationDir.ONWARD);
		Matrix<Double> yRotation = BasicShape.rotationMatrix (0, 90.0, RotationDir.ONWARD);
		
		ArrayList<BasicShape> rotations = new ArrayList<>();
		
		boolean xDifferent = true;
		BasicShape xRotated = new BasicShape (mShape);
		do
		{
			
			boolean yDifferent = true;
			BasicShape yRotated = new BasicShape (xRotated);
			do
			{
				rotations.add (new BasicShape (yRotated));
				
				yRotated.rotate (yRotation);
				yRotated.glue (mShape.getGlue());
				yDifferent = !compareVertices (xRotated, yRotated);
			}while (yDifferent);
			
			xRotated.rotate (xRotation);
			xRotated.glue (mShape.getGlue());
			xDifferent = !compareVertices (mShape, xRotated);
		}while (xDifferent);
		
		return rotations;
	}
	
	
	
	
	private BasicShape mShape;
}
