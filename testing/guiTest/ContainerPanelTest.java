package testing.guiTest;

import javax.swing.*;
import java.awt.BorderLayout;

import models.*;
import models.Matrix.*;
import gui.*;

public class ContainerPanelTest extends JFrame
{
	
	public static void main (String[] args)
	{
		ContainerPanelTest test = new ContainerPanelTest();
		test.setSize(250, 250);
		test.setVisible(true);
	}
	
	public ContainerPanelTest()
	{
		mContainer = new Container(10, 10, 10);
		mContainerPanel = new ContainerPanel (mContainer);
		
		setTitle ("container panel test");
		BorderLayout layout = new BorderLayout();
		setLayout (layout);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		add (mContainerPanel, BorderLayout.CENTER);
		
		ContainerPanel.RotationZoomListener listenRotationZoom = mContainerPanel.new RotationZoomListener();
		addMouseListener (listenRotationZoom);
		addMouseMotionListener (listenRotationZoom);
		addMouseWheelListener (listenRotationZoom);
	}
	
	
	
	private Container mContainer;
	private ContainerPanel mContainerPanel;
}
