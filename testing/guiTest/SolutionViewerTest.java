package testing.guiTest;

import gui.*;
import models.*;
import models.Container;

import javax.swing.*;
import java.awt.*;

public class SolutionViewerTest 
{
	public static void main (String[] args)
	{
		SolutionViewerTest test = new SolutionViewerTest();
		test.view();
	}
	
	public void view()
	{
		Container c = new Container (3, 2, 6);
		
		SolutionViewer view = new SolutionViewer (c);
		view.constructComponents();
		view.setVisible(true);
	}
	
}
