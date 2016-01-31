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
		test.setSize(500, 500);
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
		
		ContainerPanel.ZoomListener zoomListen = mContainerPanel.new ZoomListener (0.2);
		ContainerPanel.RotationListener rotateListen = mContainerPanel.new RotationListener (0.4);
		ContainerPanel.ResizeListener resizeListen = mContainerPanel.new ResizeListener();
		
		addMouseListener (rotateListen);
		addMouseMotionListener (rotateListen);
		addMouseWheelListener (zoomListen);
		addComponentListener (resizeListen);
	}
	
	public void setVisible (boolean flag)
	{
		super.setVisible(flag);
		mContainerPanel.init();
	}
	
	
	
	private Container mContainer;
	private ContainerPanel mContainerPanel;
}
