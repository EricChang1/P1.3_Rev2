package testing.modelTest;

import algorithm.*;

import geometry.Rectangle;

import java.util.ArrayList;

import models.BasicShape;
import models.Block;
import models.Container;
import models.Glue;
import models.Matrix;
import models.Matrix.*;

public class TestBS 
{
	
	public static void main(String[] args)
	{ 	
		TestBS test = new TestBS();
		test.testRotation();
		//error test.testRotation();
		//test.testAdding();
		//test.testSideDivision();
	}
	
	public static BasicShape constructTriangle (IntegerMatrix v1, IntegerMatrix v2, IntegerMatrix v3)
	{
		ArrayList <IntegerMatrix> vertices = new ArrayList<Matrix.IntegerMatrix>();
		vertices.add(v1);
		vertices.add(v2);
		vertices.add(v3);
		IntegerMatrix adjMat = new IntegerMatrix (3, 3);
		for (int cRow = 0; cRow < adjMat.getRows(); ++cRow)
		{
			for (int cCol = 0; cCol < adjMat.getRows(); ++cCol)
			{
				if (cRow != cCol)
					adjMat.setCell(cRow, cCol, 1);
			}
		}
		return new BasicShape (vertices, adjMat);
	}
	
	public TestBS()
	{
		//0, 0, 0
		Matrix.IntegerMatrix v1 = new Matrix.IntegerMatrix(3, 1);
		//1, 0, 0
		Matrix.IntegerMatrix v2 = new Matrix.IntegerMatrix(3, 1);
		v2.setCell(0, 0, 1);
		//0, 2, 0
		Matrix.IntegerMatrix v3 = new Matrix.IntegerMatrix(3, 1);
		v3.setCell(1, 0, 2);
		
		mBs = constructTriangle (v1, v2, v3);
		
		ArrayList <IntegerMatrix> cubeVecs = Container.computeInitDimVectors(2, 3, 4);
		mCube = new BasicShape (cubeVecs, Container.computeInitAdjacencyMatrix(cubeVecs));
	}
	
	public void testRotation()
	{
		System.out.println ("Before rotation");
		System.out.print ("dimensions ");
		for (int cDim = 0; cDim < 3; ++cDim)
			System.out.print (mBs.getDimensions(cDim) + ", ");
		System.out.println();
		mBs.print (System.out);
		Matrix<Double> rotMat = BasicShape.rotationMatrix(90.0, 90.0, BasicShape.RotationDir.ONWARD);
		mBs.rotate (rotMat);
		
		System.out.println ("After rotation");
		System.out.print ("dimensions ");
		for (int cDim = 0; cDim < 3; ++cDim)
			System.out.print (mBs.getDimensions(cDim) + ", ");
		System.out.println();
		mBs.print(System.out);
		
		Matrix<Double> rotBackMat = BasicShape.rotationMatrix(-90.0, -90.0, BasicShape.RotationDir.BACKWARD);
		mBs.rotate (rotBackMat);
		System.out.println ("Undoing rotation");
		System.out.print ("dimensions ");
		for (int cDim = 0; cDim < 3; ++cDim)
			System.out.print (mBs.getDimensions(cDim) + ", ");
		System.out.println();
		mBs.print(System.out);
		
		System.out.println ("mats multiplied");
		Matrix <Double> rotMult = new DoubleMatrix (3, 3);
		rotMat.multiply (rotBackMat, rotMult);
		rotMult.print (System.out);
	}
	
	public void testSideDivision()
	{
		ArrayList <Rectangle> sides = mCube.getRectangles();
		for (Rectangle side : sides)
			System.out.println ("Rectangle spanned by " + new Glue (side.getFirst()) + " and " + new Glue (side.getSecond()));
	}
	
	public void testAdding()
	{
		IntegerMatrix v1, v2, v3;
		v1 = new IntegerMatrix (3, 1);
		v2 = new IntegerMatrix (3, 1);
		v3 = new IntegerMatrix (3, 1);
		
		v2.setCell (0, 0, -1);
		v3.setCell (1, 0, -1);
		
		Block addShape = new Block (constructTriangle (v1, v2, v3), 0);
		System.out.println ("Trying to add");
		addShape.print(System.out);
		
		System.out.println ("Before adding");
		mBs.print (System.out);
		//change to public for testing
		//mBs.addShape (addShape);
		System.out.println ("After adding");
		mBs.print (System.out);
	}
	
	private BasicShape mBs, mCube;
}