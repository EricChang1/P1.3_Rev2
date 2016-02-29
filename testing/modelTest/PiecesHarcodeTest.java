package testing.modelTest;

import java.util.*;
import java.io.*;

import javax.swing.*;
import java.awt.GridLayout;

import models.*;
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
		blocks.addAll (inputParcel.getBlocks());
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
			
		}
	}
}
