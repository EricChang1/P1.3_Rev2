package testing.geomTest;

import java.util.*;

import geometry.*;
import models.*;
import models.Matrix.*;

public class CuboidTest 
{
	public static void main (String[] args)
	{
		CuboidTest tester = new CuboidTest();
		tester.getVerticesTest();
	}
	
	public void getVerticesTest()
	{
		IntegerMatrix glueBase = new IntegerMatrix (3, 1);
		ArrayList<Glue> expectedVertices = new ArrayList<>();
		//0, 0, 0
		expectedVertices.add (new Glue (glueBase));
		//3, 3, 2
		glueBase.setCell (0, 0, 3);
		glueBase.setCell (1, 0, 3);
		glueBase.setCell (2, 0, 2);
		expectedVertices.add (new Glue (glueBase));
		//0, 3, 2
		glueBase.setCell (0, 0, 0);
		expectedVertices.add (new Glue (glueBase));
		//3, 0, 2
		glueBase.setCell (0, 0, 3);
		glueBase.setCell (1, 0, 0);
		expectedVertices.add (new Glue (glueBase));
		//3, 3, 0
		glueBase.setCell (1, 0, 3);
		glueBase.setCell (2, 0, 0);
		expectedVertices.add (new Glue (glueBase));
		//0, 0, 2
		glueBase.setCell (0, 0, 0);
		glueBase.setCell (1, 0, 0);
		glueBase.setCell (2, 0, 2);
		expectedVertices.add (new Glue (glueBase));
		//0, 3, 0
		glueBase.setCell (1, 0, 3);
		glueBase.setCell (2, 0, 0);
		expectedVertices.add (new Glue (glueBase));
		//3, 0, 0
		glueBase.setCell (0, 0, 3);
		glueBase.setCell (1, 0, 0);
		expectedVertices.add (new Glue (glueBase));
		
		Cuboid c = new Cuboid (expectedVertices.get (0), expectedVertices.get (1));
		ArrayList<Glue> actualVertices = c.getVertices();
		
		System.out.println ("expected vs actual");
		for (int cVert = 0; cVert < 8; ++cVert)
			System.out.println (expectedVertices.get (cVert) + " vs " + actualVertices.get(cVert));
		/*
		for (Glue actual : actualVertices)
		{
			if (!expectedVertices.contains (actual))
				System.out.println ("Does not contain " + actual);
		}*/
	}
}
