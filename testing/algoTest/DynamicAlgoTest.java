package testing.algoTest;

import geometry.Cuboid;
import gui.PieceRenderPanel;
import gui.PieceRenderPanel.ZoomListener;

import java.util.*;
import java.io.*;
import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import algorithm.*;
import models.*;
import models.Matrix.*;

public class DynamicAlgoTest 
{
	public static Glue getPos (int x1, int x2, int x3)
	{
		ArrayList<Integer> arr = new ArrayList<>();
		arr.add (x1);
		arr.add (x2);
		arr.add (x3);
		return new Glue (arr);
	}
	
	
	public static void main (String[] args) throws IOException, ShapeParser.BadFileStructureException
	{
		int[] quants = {10, 4, 0};
		boolean[] inf = {false, false, false};
		int d = 6, w = 6, h = 6;

		ArrayList <Resource> res = new ArrayList<>();
		String fileName = "parcels.txt";
		ShapeParser input = new ShapeParser (new File (fileName));
		input.parse();
		
		DynamicAlgoTest test = new DynamicAlgoTest();
		test.setResources (input.getBlocks(), quants, inf);
		test.setContainer (d, w, h);
		
		long startTime = System.currentTimeMillis();
		
		test.run();
		
		long runningTime = System.currentTimeMillis() - startTime;
		runningTime /= 1000;
		System.out.println ("running time " + runningTime);
		test.drawResult();
		test.printResult();
		
		//test.testRotateToFit();
	}
	
	public void setResources (ArrayList<Block> blocks, int[] quants, boolean[] infFlags)
	{
		mRes = new ArrayList<>();
		for (int cBlock = 0; cBlock < blocks.size(); ++cBlock)
		{
			Block b = blocks.get (cBlock);
			rotateBlockDecreasingDims(b);
			mRes.add (new Resource (b, quants[cBlock], b.getVolume(), infFlags[cBlock]));
		}
	}
	
	public void rotateBlockDecreasingDims (Block b)
	{
		Matrix<Double> rotX = BasicShape.rotationMatrix(90.0, 0.0, BasicShape.RotationDir.ONWARD);
		Matrix<Double> rotY = BasicShape.rotationMatrix(0.0, 90, BasicShape.RotationDir.ONWARD);
		
		while (b.getDimensions(0) < b.getDimensions(1) || b.getDimensions(1) < b.getDimensions(2))
		{
			b.rotate(rotX);
			int cRot = 0;
			while (cRot < 4 && b.getDimensions(0) < b.getDimensions(1) || b.getDimensions(1) < b.getDimensions(2))
			{
				b.rotate(rotY);
				++cRot;
			}
		}
		b.glue (new Glue (new IntegerMatrix (3, 1)));
	}
	
	public void setContainer (int d, int w, int h)
	{
		mCont = new Container (d, w, h);
	}
	
	public void run()
	{
		mAlgo = new DynamicAlgo();
		mAlgo.init (mCont, mRes);
		
		JFrame showBar = new JFrame ("current progress");
		showBar.setSize (300, 75);
		showBar.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);
		showBar.setLayout (new BorderLayout (25, 10));
		JProgressBar progressIndicator = new JProgressBar (0, 100);
		mAlgo.getProgress().setProgressBar (progressIndicator);
		showBar.add (progressIndicator, BorderLayout.CENTER);
		showBar.setVisible(true);
		
		mAlgo.run();
	}
	
	public void testRotateToFit()
	{
		if (mAlgo == null)
			mAlgo = new DynamicAlgo();
		Container[] c = new Container[3];
		c[0] = new Container (1, 2, 3);
		c[1] = new Container (1, 2, 3);
		c[2] = new Container (2, 3, 2);
		
		Cuboid[] fit = new Cuboid[3];
		fit[0] = new Cuboid (getPos (0, 0, 0), getPos (2, 1, 3));
		fit[1] = new Cuboid (getPos (0, 0, 0), getPos (3, 1, 2));
		fit[2] = new Cuboid (getPos (0, 0, 0), getPos (3, 2, 2));
		
		for (int cnt = 0; cnt < 3; ++cnt)
		{
			mAlgo.rotateToFit (c[cnt], fit[cnt]);
			boolean fits = true;
			ArrayList<Integer> cubeDim = fit[cnt].getDimensions();
			for (int cDim = 0; cDim < fit[cnt].getDimension(); ++cDim)
			{
				if (cubeDim.get(cDim) != c[cnt].getDimensions(cDim))
					fits = false;
			}
			
			if (fits)
				System.out.println ("case " + cnt + " holds");
			else
			{
				System.out.println ("case " + cnt + " does not hold");
				System.out.print (c[cnt].getDimensions(0) + "," + c[cnt].getDimensions(1) + "," + c[cnt].getDimensions(2));
				System.out.println (" does not match " + cubeDim);
			}
		}
		
		
	}
	
	public void printSubsets()
	{
		/*DynamicAlgo algo = new DynamicAlgo();
		algo.init (mCont, mRes);
		algo.generatePowerSet();
		System.out.println ("subsets generated " + algo.mSubsets.size());
		for (DynamicAlgo.Subset subs : algo.mSubsets)
			System.out.println (subs);*/
	}
	
	public void printResult()
	{
		System.out.println ("algo terminated");
		System.out.print (mAlgo.getFilledContainer().getAmountOfBlocks() + "blocks used ");
		System.out.println (mAlgo.getFilledContainer().getValue() + " value");
	}
	
	public void drawResult()
	{
		JFrame frame = new JFrame ("result");
		frame.setSize (400, 400);
		frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		frame.setLayout (new BorderLayout());
		
		PieceRenderPanel render = new PieceRenderPanel(mCont);
		
		PieceRenderPanel.RotationListener rotListen = render.new RotationListener();
		PieceRenderPanel.ResizeListener resizeListen = render.new ResizeListener();
		PieceRenderPanel.ZoomListener zoomListen = render.new ZoomListener();
		zoomListen.setSensitivity (0.1);
		
		frame.addMouseListener (rotListen);
		frame.addMouseMotionListener(rotListen);
		frame.addMouseWheelListener(zoomListen);
		frame.addComponentListener(resizeListen);
		
		frame.add (render, BorderLayout.CENTER);
		frame.setVisible(true);
		render.init();
	}
	
	private ArrayList<Resource> mRes;
	private Container mCont;
	private DynamicAlgo mAlgo;
}
