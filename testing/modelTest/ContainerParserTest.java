package testing.modelTest;

import models.*;
import models.Container;
import models.ContainerParser.ContainerParserException;
import models.ShapeParser.BadFileStructureException;

import java.awt.*;

import javax.swing.*;

import java.io.*;

import gui.*;

public class ContainerParserTest 
{
	
	public static void main (String[] args) throws FileNotFoundException, ContainerParserException, BadFileStructureException
	{
		ContainerParserTest test = new ContainerParserTest();
		test.load();
		
		test.show();
	}
	
	public void load() throws FileNotFoundException, ContainerParserException, BadFileStructureException
	{
		ContainerParser getContainer = new ContainerParser (new File ("guiexport.txt"));
		getContainer.parser();
		
		mContainer = getContainer.constructContainer();
		
		System.out.println ("description " + getContainer.getDescription());
	}
	
	public void show()
	{
		JFrame frame = new JFrame();
		frame.setTitle("imported container");
		frame.setSize(400, 400);
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		PieceRenderPanel render = new PieceRenderPanel (mContainer);
		frame.add(render, BorderLayout.CENTER);
		
		PieceRenderPanel.RotationListener rotListen = render.new RotationListener();
		PieceRenderPanel.ResizeListener resizeListen = render.new ResizeListener();
		PieceRenderPanel.ZoomListener zoomListen = render.new ZoomListener();
		
		frame.addMouseListener(rotListen);
		frame.addMouseMotionListener(rotListen);
		frame.addMouseWheelListener(zoomListen);
		frame.addComponentListener(resizeListen);
		
		frame.setVisible(true);
	}
	
	
	private Container mContainer;
}
