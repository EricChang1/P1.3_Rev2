package gui;

import models.Glue;
import models.Matrix.*;

/**
 * class modelling a point in a 3d space on a sphere
 * whose coordinates are determined by the angles relative to the x2, x3 axis and the radius of the sphere
 * @author martin
 */
public class Camera
{
	/**
	 * @param previous previous angle
	 * @param add angle to add
	 * @return the angle corresponding to the sum of previous and add, ranging from 0 to 360
	 */
	public static double angleAdd (double previous, double add)
	{
		double result = previous + add;
		if (result > 360)
			result -= 360;
		else if (result < 0)
			result += 360;
		return result;
	}
	
	/**
	 * parametric constructor, center of sphere is initialized to be at the origin
	 * @param angleX2 angle relative to the x2 axis
	 * @param angleX3 angle relative to the x3 axis
	 * @param radius radius of the sphere
	 */
	public Camera (double angleX2, double angleX3, double radius)
	{
		mAngleX2 = angleX2;
		mAngleX3 = angleX3;
		mRadius = radius;
		mCenter = new Glue (new IntegerMatrix (3, 1));
	}
	
	/**
	 * @return coordinates of the point on the sphere
	 */
	public DoubleMatrix getCameraPosition()
	{
		DoubleMatrix posVec = new DoubleMatrix (3, 1);
		posVec.setCell(0, 0, mRadius * Math.sin(mAngleX2));
		posVec.setCell(1, 0, mRadius * Math.cos(mAngleX2));
		posVec.setCell(2, 0, mRadius * Math.cos(mAngleX3));
		for (int cDim = 0; cDim < 3; ++cDim)
			posVec.setCell(cDim, 0, mCenter.getPosition(cDim));
		return posVec;
	}
	
	/**
	 * @return angle relative to the x2 axis
	 */
	public double getAngleX2()
	{
		return mAngleX2;
	}
	
	/**
	 * @return angle relative to the x3 axis
	 */
	public double getAngleX3()
	{
		return mAngleX3;
	}
	
	/**
	 * @return radius of the sphere
	 */
	public double getRadius()
	{
		return mRadius;
	}
	
	/**
	 * @param degrees number of degrees to add to the x2 angle
	 * postcondition: the x2 angle will be the value corresponding to the sum
	 * of the current angle and degrees normed to be within the interval [0, 360]
	 */
	public void moveX2 (double degrees)
	{
		mAngleX2 = angleAdd (mAngleX2, degrees);
	}
	
	/**
	 @param degrees number of degrees to add to the x2 angle
	 * postcondition: the x2 angle will be the value corresponding to the sum
	 * of the current angle and degrees normed to be within the interval [0, 360]
	 */
	public void moveX3 (double degrees)
	{
		mAngleX3 = angleAdd (mAngleX3, degrees);
	}
	
	/**
	 * @param units units to add to the current radius
	 */
	public void changeRadius (double units)
	{
		mRadius += units;
	}
	
	/**
	 * @param center new center of the sphere
	 */
	public void setCenter (Glue center)
	{
		mCenter = center;
	}
	
	private Glue mCenter;
	private double mAngleX2, mAngleX3, mRadius;
}
