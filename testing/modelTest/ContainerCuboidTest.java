package testing.modelTest;

import java.io.File;
import java.util.ArrayList;

import models.BasicShape;
import models.Block;
import models.Container;
import models.Glue;
import models.ShapeParser;
import models.Matrix.IntegerMatrix;

import geometry.*;

public class ContainerCuboidTest 
{
	public static void main (String[] args) throws Exception
	{
		String[] files = {"parcels.txt", "LPTPentominoes.txt"};
		Container cont = new Container (4, 3, 5);
		
		//BasicShapeConnectionTest test = new BasicShapeConnectionTest(files[1], 2);
		
		for (String file : files)
		{
			System.out.println (file);
			for (int cBlock = 0; cBlock < 3; ++cBlock)
			{
				System.out.println ("block " + cBlock);
				ContainerCuboidTest test = new ContainerCuboidTest (cont, file, cBlock);
				
				System.out.println ("success: " + test.testOverlappingCuboids());
			}
			System.out.println();
		}
	}
	
	
	public ContainerCuboidTest (Container c, String file, int index) throws Exception
	{
		ShapeParser parseShape = new ShapeParser (new File (file));
		parseShape.parse();
		BasicShape place = parseShape.getBlocks().get (index);
		
		c.placeBlock (new Block (place, 3, "test"), new Glue (new IntegerMatrix(3, 1)));
		mCont = c;
	}
	
	
	public boolean testOverlappingCuboids()
	{
		boolean retVal = true;
		BasicShape clone = new BasicShape (mCont);
		clone.addMissingRectanglePoints();
		
		ArrayList<Cuboid> processed = new ArrayList<>();
		for (Cuboid cube : clone.getCuboids())
		{
			ArrayList<Glue> cubeVerts = cube.getVertices();
			cube = new Cuboid (cube.getMin (cubeVerts), cube.getMax (cubeVerts));
			
			for (Cuboid proc : processed)
			{
				if (testOverlap (cube, proc))
				{
					System.out.println ("overlap " + cube + " " + proc);
					retVal = false;
				}
			}
			processed.add(cube);
		}
		
		return retVal;
	}
	
	public boolean testOverlap (Cuboid c1, Cuboid c2)
	{
		Glue min = c1.getMin (c1.getVertices()), max = c1.getMax (c1.getVertices());
		
		for (Glue vert : c2.getVertices())
		{
			for (int cDim = 0; cDim < vert.getDimension(); ++cDim)
			{
				if (vert.getPosition (cDim) > min.getPosition (cDim) && vert.getPosition (cDim) < max.getPosition(cDim))
					return true;
			}
		}
		return false;
	}
	
	private Container mCont;
}
