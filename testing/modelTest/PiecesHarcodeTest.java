package testing.modelTest;

import java.util.*;
import java.io.*;

import javax.swing.*;

import algorithm.ShapeRotator;

import java.awt.GridLayout;

import models.*;
import geometry.Cuboid;
import gui.*;

public class PiecesHarcodeTest 
{
	
	public static void main (String[] args) throws Exception
	{
		String fileNamePent = "LPTPentominoes.txt", fileNameParcels = "parcels.txt";
		ShapeParser inputPent = new ShapeParser (new File (fileNamePent));
		ShapeParser inputParcel = new ShapeParser (new File (fileNameParcels));
		inputPent.parse();
		inputParcel.parse();
		ArrayList <Block> blocks = inputPent.getBlocks();
		
		ShapeRotator rotator = new ShapeRotator (blocks.get (2));
		ArrayList<BasicShape> rotations = rotator.getRotations();
		System.out.println ("number of rotations " + rotations.size());
		
		/*
		//blocks.addAll (inputParcel.getBlocks());
		for (int cBlock = 0; cBlock < blocks.size(); ++cBlock)
		{
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
			
			if (cBlock != 5)
			{
				JFrame dissFrame = new JFrame ("dissected piece " + cBlock);
				dissFrame.setLayout (new GridLayout (1, 1));
				dissFrame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
				dissFrame.setSize (400, 400);
				
				blocks.get (cBlock).addMissingRectanglePoints();
				//blocks.get(2).getCuboid (3, 18, 4, 19, 4, 19);
				
				PieceRenderPanel dissRender = new PieceRenderPanel (blocks.get (cBlock).clone());
				PieceRenderPanel.RotationListener dissRListen = dissRender.new RotationListener();
				dissRListen.setSensitivity (0.2);
				dissFrame.addMouseListener (dissRListen);
				dissFrame.addMouseMotionListener (dissRListen);
				dissFrame.add (dissRender);
				dissFrame.setVisible (true);
				dissRender.init();
				
				ArrayList <Cuboid> cs = blocks.get (cBlock).getCuboids();
				System.out.print ("cuboids V = ");
				for (Cuboid c : cs)
				{
					ArrayList <Integer> dims = c.getDimensions();
					System.out.print (dims.get(0) * dims.get(1) * dims.get(2) + ", ");
				}
			}
			
		}
		*/
	}
}
