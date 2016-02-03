package testing.modelTest;

import gui.PieceRenderPanel;

import javax.swing.*;
import java.util.*;
import java.awt.BorderLayout;
import java.io.*;
import models.*;

public class ShapeParserTest 
{
	public static void main (String[] args)
	{
		File testFile = new File ("pieces.txt");
		try
		{
			ShapeParserTest test = new ShapeParserTest (testFile);
			test.run();
		}
		catch (Exception e)
		{
			System.out.println ("oh no");
			e.printStackTrace();
		}
	}
	
	
	public ShapeParserTest (File in) throws FileNotFoundException
	{
		mParser = new ShapeParser (in);
	}
	
	public void run() throws ShapeParser.BadFileStructureException, IOException
	{
		mParser.parse();
		ArrayList <Block> blocks = mParser.getBlocks();
		System.out.println ("parsed blocks: " + blocks.size());
		showBlocks (blocks);
	}
	
	
	public void showBlocks (ArrayList <Block> blocks)
	{
		for (int cBlock = 0; cBlock < blocks.size(); ++cBlock)
		{
			JFrame win = new JFrame ("showing block " + cBlock);
			PieceRenderPanel render = new PieceRenderPanel(blocks.get(cBlock));
			win.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
			win.setSize(400, 400);
			win.setLayout (new BorderLayout ());
			win.add(render, BorderLayout.CENTER);
			win.setVisible(true);
			render.init();
			
		}
	}
	
	private ShapeParser mParser;
}
