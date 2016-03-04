package testing.algoTest;

import gui.PieceRenderPanel;

import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.GridLayout;

import algorithm.*;
import models.*;
import models.Matrix.*;

public class DynamicAlgoTest 
{
	public static void main (String[] args) throws IOException, ShapeParser.BadFileStructureException
	{
		int[] quants = {2, 3, 1};

		ArrayList <Resource> res = new ArrayList<>();
		String fileName = "LPTPentominoes.txt";
		ShapeParser input = new ShapeParser (new File (fileName));
		input.parse();
		ArrayList <Block> blocks = input.getBlocks();
		for (int cBlock = 0; cBlock < blocks.size(); ++cBlock)
		{
			res.add (new Resource(blocks.get (cBlock), quants[cBlock], blocks.get(cBlock).getVolume(), false));
			/*
			JFrame frame = new JFrame ("piece " + cBlock);
			frame.setLayout (new GridLayout (1, 1));
			frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
			frame.setSize (400, 400);
			PieceRenderPanel render = new PieceRenderPanel (blocks.get (cBlock).clone());
			PieceRenderPanel.RotationListener rListen = render.new RotationListener();
			rListen.setSensitivity (0.2);
			frame.addMouseListener (rListen);
			frame.addMouseMotionListener (rListen);
			frame.add (render);
			frame.setVisible (true);
			render.init();
			*/
		}
		

		Container cont = new Container (10, 10, 10);
		
		DynamicAlgo algo = new DynamicAlgo();
		algo.init (cont, res);
		algo.generatePowerSet();
		System.out.println ("subsets generated " + algo.mSubsets.size());
		for (DynamicAlgo.Subset subs : algo.mSubsets)
			System.out.println (subs);
	}
}
