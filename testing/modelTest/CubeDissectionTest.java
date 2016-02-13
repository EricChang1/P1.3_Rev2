package testing.modelTest;

import javax.swing.*;

import java.awt.BorderLayout;
import java.util.*;
import java.io.*;

import geometry.Rectangle;
import gui.*;
import models.*;
import models.Matrix.DoubleMatrix;
import algorithm.Scaler;

public class CubeDissectionTest 
{
	
	public static void main (String[] args)
	{
		CubeDissectionTest test = new CubeDissectionTest();
		/*test.showBefore();
		test.showAfter();
		*/
		test.testSidesDecompos();
	}
	
	public static JFrame getFrame (PieceRenderPanel render, String title)
	{
		PieceRenderPanel.RotationListener rotListen = render.new RotationListener();
		PieceRenderPanel.ZoomListener zoomListen = render.new ZoomListener();
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
	
	public CubeDissectionTest()
	{
		
		mShape = null;
		try
		{
			ShapeParser readInShapes = new ShapeParser(new File ("pieces.txt"));
			readInShapes.parse();
			mShape = readInShapes.getShapes().get(0);
		}
		catch (Exception e)
		{
			System.out.println ("oh no");
			e.printStackTrace();
		}
		
	}
	
	public void showBefore()
	{
		PieceRenderPanel showPiece = new PieceRenderPanel(mShape);
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
	
	
	private JFrame mBeforeWin, mAfterWin;
	private BasicShape mShape;
}