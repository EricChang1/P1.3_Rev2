package testing.modelTest;

import java.util.*;

import java.io.*;

import models.*;
import models.Matrix.IntegerMatrix;

public class BasicShapeConnectionTest 
{
	public static void main (String[] args) throws Exception
	{
		String[] files = {"parcels.txt", "LPTPentominoes.txt"};
		Container cont = new Container (5, 5, 5);
		
		//BasicShapeConnectionTest test = new BasicShapeConnectionTest(files[1], 2);
		
		for (String file : files)
		{
			
			for (int cBlock = 0; cBlock < 3; ++cBlock)
			{
				BasicShapeConnectionTest test = new BasicShapeConnectionTest (cont, file, cBlock);
				
				System.out.println ("success: " + test.testMultipleSameConnection());
			}
		}
	}
	
	public BasicShapeConnectionTest (String file, int index) throws Exception
	{
		ShapeParser parseShape = new ShapeParser (new File (file));
		parseShape.parse();
		mShape = parseShape.getBlocks().get (index);
	}
	
	public BasicShapeConnectionTest (Container c, String file, int index) throws Exception
	{
		ShapeParser parseShape = new ShapeParser (new File (file));
		parseShape.parse();
		BasicShape place = parseShape.getBlocks().get (index);
		
		c.placeBlock (new Block (place, 3, "test"), new Glue (new IntegerMatrix(3, 1)));
		mShape = c;
	}
	
	
	public boolean testMultipleSameConnection()
	{
		boolean retVal = true;
		BasicShape clone = new BasicShape (mShape);
		clone.addMissingRectanglePoints();
		
		for (int cVertex = 0; cVertex < clone.getNumberOfVertices(); ++cVertex)
		{
			ArrayList<BasicShape.RelatPos> usedConnections = new ArrayList<>();
			for (IntegerMatrix connected : clone.lookUpConnections (cVertex))
			{
				Glue vertex = new Glue (clone.getVertex (cVertex));
				Glue connec = new Glue (connected);
				BasicShape.RelatPos relat = BasicShape.getRelativePos (vertex, connec);
				
				if (usedConnections.contains (relat))
				{
					System.out.println ("duplicate: " + vertex + " to " + connec);
					retVal = false;
				}
				else
					usedConnections.add (relat);
			}
		}
		return retVal;
	}
	
	private BasicShape mShape;
}
