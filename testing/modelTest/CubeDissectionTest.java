package testing.modelTest;

import javax.swing.*;

import java.awt.BorderLayout;
import java.util.*;
import java.io.*;

import geometry.Cuboid;
import geometry.Rectangle;
import gui.*;
import models.*;
import models.Matrix.*;

import models.ShapeParser.BadFileStructureException;

import algorithm.Scaler;

public class CubeDissectionTest 
{
	public static void main (String[] args) throws BadFileStructureException, IOException
	{
		CubeDissectionTest test = new CubeDissectionTest();
		/*test.showBefore();
		test.showAfter();*/
		
		//test.testSidesDecompos();
		//test.testCubeDecompos();
		test.testContainerDecompos();
	}
	
	public static JFrame getFrame (PieceRenderPanel render, String title)
	{
		PieceRenderPanel.RotationListener rotListen = render.new RotationListener();
		PieceRenderPanel.ZoomListener zoomListen = render.new ZoomListener();
		zoomListen.setSensitivity(0.15);
		PieceRenderPanel.ResizeListener resizeListen = render.new ResizeListener();
		
		JFrame frame = new JFrame();
		frame.setTitle(title);
		frame.setSize(400, 400);
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(render, BorderLayout.CENTER);
		frame.addMouseListener(rotListen);
		frame.addMouseMotionListener(rotListen);
		frame.addMouseWheelListener(zoomListen);
		frame.addComponentListener(resizeListen);
		return frame;
	}

	public CubeDissectionTest() throws BadFileStructureException, IOException
	{
		
		mShape = null;

		ShapeParser readInShapes = new ShapeParser(new File ("parcels.txt"));
		readInShapes.parse();
		mShape = readInShapes.getShapes().get(0);
		mCont = new Container (4, 3, 3);
	}
	
	public void showBefore()
	{
		PieceRenderPanel showPiece = new PieceRenderPanel (new BasicShape (mShape));
		mBeforeWin = getFrame (showPiece, "before");
		mBeforeWin.setVisible(true);
		showPiece.init();
	}
	
	public void showAfter()
	{
		BasicShape cut = new BasicShape(mShape);
		cut.addMissingRectanglePoints();
		
		PieceRenderPanel showPiece = new PieceRenderPanel (cut);
		mAfterWin = getFrame (showPiece, "after");
		mAfterWin.setVisible(true);
		showPiece.init();
	}
	
	public void testSidesDecompos()
	{
		BasicShape decompose = new BasicShape (mShape);
		decompose.addMissingRectanglePoints();
		
		PieceRenderPanel p = new PieceRenderPanel(new BasicShape (decompose));
		JFrame win = getFrame (p, "");
		win.setVisible(true);
		p.init();
		
		ArrayList <Rectangle> rects = decompose.getRectangles();
		System.out.println ("printing rects");
		for (Rectangle rect : rects)
			System.out.println (rect.toString());
		if (rects.isEmpty())
			System.out.println ("no rects");
	}
	
	public void testCubeDecompos()
	{
		BasicShape decompose = new BasicShape (mShape);
		decompose.addMissingRectanglePoints();
		
		ArrayList <Cuboid> cuboids = decompose.getCuboids();
		System.out.println ("printing cuboids");
		for (Cuboid cube : cuboids)
			System.out.println (cube.toString());
		if (cuboids.isEmpty())
			System.out.println ("no cuboids");
		System.out.println ("volume V = " + decompose.getVolume());
	}
	
	public void testContainerDecompos()
	{
		
		mShape.rotate (BasicShape.rotationMatrix (90.0, 0, BasicShape.RotationDir.ONWARD));
		mCont.placeBlock (new Block (mShape, 3, ""), new Glue (new IntegerMatrix (3, 1)));
		
		System.out.println ("free cuboids");
		for (Cuboid c : mCont.getFreeCuboids())
			System.out.println (c);
		
		mCont.addMissingRectanglePoints();
		PieceRenderPanel render = new PieceRenderPanel(mCont);
		JFrame win = getFrame (render, "dissected container");
		win.setVisible(true);
		render.init();
		
		
	}
	
	
	private JFrame mBeforeWin, mAfterWin;
	private BasicShape mShape;
	private Container mCont;
}
