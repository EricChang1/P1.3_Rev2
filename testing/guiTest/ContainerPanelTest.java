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
		test.setVisible(true);
	}
	
	public ContainerPanelTest()
	{
		mContainer = new Container(10, 10, 10);
		mContainerPanel = new ContainerPanel (mContainer);
		
		setTitle ("container panel test");
		setLayout (new BorderLayout());
		add (mContainerPanel, BorderLayout.CENTER);
		
		ContainerPanel.RotationZoomListener listenRotationZoom = mContainerPanel.new RotationZoomListener();
		addMouseListener (listenRotationZoom);
		addMouseMotionListener (listenRotationZoom);
		addMouseWheelListener (listenRotationZoom);
	}
	
	
	
	private Container mContainer;
	private ContainerPanel mContainerPanel;
}
