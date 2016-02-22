package testing.modelTest;

import java.util.*;
import javax.swing.*;

import algorithm.*;

import models.Block;
import models.Container;
import models.Glue;
import models.Position;
import models.Matrix.*;

import geometry.Cuboid;
import gui.PieceRenderPanel;

/**
 * test class for container class
 * @author martin
 */
public class ContainerTest 
{
	public static void main (String[] args)
	{
		ContainerTest test = new ContainerTest();
		test.addTest();
		test.freeCuboidTest();
	}
	
	public ContainerTest()
	{
		mContainer = new Container(10, 10, 10);
	}
	
	public Position setVector (int c1, int c2, int c3)
	{
		IntegerMatrix v3d = new IntegerMatrix (3, 1);
		v3d.setCell(0, 0, c1);
		v3d.setCell(1, 0, c2);
		v3d.setCell(2, 0, c3);
		return new Position (v3d);
	}
	
	public void addTest()
	{	
		ArrayList <IntegerMatrix> vecs = Container.computeInitDimVectors(2, 3, 3);
		IntegerMatrix adj = Container.computeInitAdjacencyMatrix(vecs);
		Block cuboid1 = new Block (vecs, adj, 2);
		System.out.println("cuboid 1");
		cuboid1.print(System.out);
		
		vecs = Container.computeInitDimVectors(2, 2, 4);
		adj = Container.computeInitAdjacencyMatrix(vecs);
		Block cuboid2 = new Block (vecs, adj, 5);
		System.out.println("cuboid 2");
		cuboid2.print(System.out);
		
		
		Position posZero = setVector (0, 0, 0);
		Position posOutsideConflict = setVector (11, 11, 11);
		
		System.out.println ("demo 0|0|0");
		printPlaceTest (mContainer, cuboid1, posZero);
		System.out.println ("demo 11|11|11");
		printPlaceTest (mContainer, cuboid2, posOutsideConflict);
		
		Position posInsideConflict_Bounds = setVector (9, 9, 9);
		System.out.println ("demo 9|9|9");
		printPlaceTest(mContainer, cuboid1, posInsideConflict_Bounds);
		
		System.out.println ("actually placing cuboid 1 at 0|0|0");
		mContainer.placeBlock(cuboid1, posZero);
		mContainer.print(System.out);
		
		Position posInsideConflict_Placed = setVector (1, 1, 2);
		System.out.println ("demo 2|1|3");
		printPlaceTest (mContainer, cuboid2, posInsideConflict_Placed);
		
		System.out.println ("demo 2|3|3: possible");
		Position vertex = setVector (2, 3, 3);
		ArrayList<Position> relatives = mContainer.getRelativePlacements(cuboid2, mContainer.getVertexIndex(vertex.toVector()));
		/*
		for (Position relat : relatives)
		{
			System.out.println (relat.toString());
			printPlaceTest (mContainer, cuboid2, relat);
			if (mContainer.checkPositionInside(relat) && mContainer.checkPositionOverlap(cuboid2, relat))
			{
				mContainer.placeBlock(cuboid2, relat);
				System.out.println ("Can block be found?");
				//Block b = mContainer.getBlockAt(relat);
				mContainer.print (System.out);
				System.out.println ("placed at " + relat);
				System.out.println ("success!");
				break;
			}
		}
		*/
		
	}
	
	public void freeCuboidTest()
	{
		PieceRenderPanel show = new PieceRenderPanel (mContainer.clone());
		JFrame frame = new JFrame ("container");
		frame.setSize(400, 400);
		frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		frame.add (show);
		frame.setVisible(true);
		PieceRenderPanel.RotationListener rListen = show.new RotationListener();
		rListen.setSensitivity(0.25);
		frame.addMouseListener(rListen);
		frame.addMouseMotionListener(rListen);
		show.init();
		
		Container diss = mContainer.clone();
		diss.addMissingRectanglePoints();
		
		PieceRenderPanel showDiss = new PieceRenderPanel (diss);
		JFrame dissFrame = new JFrame ("dissected container");
		dissFrame.setSize (400, 400);
		dissFrame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		dissFrame.add (showDiss);
		PieceRenderPanel.RotationListener dissRListen = showDiss.new RotationListener();
		dissRListen.setSensitivity (0.25);
		dissFrame.addMouseListener (dissRListen);
		dissFrame.addMouseMotionListener (dissRListen);
		dissFrame.setVisible (true);
		showDiss.init();
		
		ArrayList <Cuboid> freeCubes = mContainer.getFreeCuboids();
		System.out.println ("printing free cuboids");
		for (Cuboid free : freeCubes)
		{
			System.out.println (free);
		}
		
	}
	
	public void printPlaceTest (Container c, Block b, Glue g)
	{
		System.out.print ("Placing " + c.checkPositionInside(g));
		System.out.println (" " + c.checkPositionOverlap(b, g));
	}
	
	
	private Container mContainer;
}
