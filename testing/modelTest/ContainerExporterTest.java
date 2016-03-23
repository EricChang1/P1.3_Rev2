package testing.modelTest;

import java.util.*;
import java.io.*;

import javax.swing.*;

import models.*;
import models.ContainerExporter.ContainerExportException;
import models.Matrix.*;


public class ContainerExporterTest 
{
	public static void main (String[] args) throws ContainerExportException
	{
		ContainerExporterTest test = new ContainerExporterTest();
		test.generateContainer();
		test.export();
	}

	
	public void generateContainer()
	{
		ShapeParser getBlocks = null;
		try
		{
			getBlocks = new ShapeParser (new File ("parcels.txt"));
			getBlocks.parse();
		}
		catch (Exception e) {}
		
		
		IntegerMatrix rawPos = new IntegerMatrix (3, 1);
		mCont = new Container (10, 10, 10);
		
		Glue p1 = new Glue (rawPos);
		rawPos.setCell (0, 0, 5);
		
		Glue p2 = new Glue (rawPos);
		
		mCont.placeBlock (getBlocks.getBlocks().get(0), p1);
		mCont.placeBlock (getBlocks.getBlocks().get(1), p2);
	}
	
	
	public void export () throws ContainerExportException
	{
		String dummyFilePath = "exportTest.txt";
		ContainerExporter export = new ContainerExporter (mCont);
		export.setDescription ("bla bla bla");
		export.write (dummyFilePath);
	}
	
	
	private Container mCont;
}
