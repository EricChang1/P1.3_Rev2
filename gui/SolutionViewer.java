package gui;

import gui.PieceRenderPanel.RotationListener;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import sun.security.provider.certpath.OCSP.RevocationStatus;

import models.BasicShape;
import models.Container;

/**
 * frame to view solution
 * should open once the algorithm is started
 * @author martin
 */
public class SolutionViewer extends JFrame 
{
	
	public static final int DEFAULT_WIDTH = 600, DEFAULT_HEIGHT = 600;
	public static final double DEFAULT_ZOOM_SENSITIVITY = 0.1;
	public static final double DEFAULT_ROTATION_SENSITIVITY = 0.4;
	
	public SolutionViewer (Container solution)
	{
		mSolution = solution;
		setLayout (new GridBagLayout());
		setDefaultCloseOperation (DISPOSE_ON_CLOSE);
		setSize (DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
	public JProgressBar getProgressBar() { return mProgress; }
	
	public void setSolution (Container solution)
	{
		mSolution = solution;

		constructComponents();
	}
	
	public void setComment (String comment)
	{
		mComment.setText (comment);
	}
	
	public void constructComponents()
	{
		removeAll();
		
		StatsPanel stats = new StatsPanel();
		stats.update (mSolution);
		
		PieceRenderPanel render = new PieceRenderPanel (new BasicShape (mSolution));
		PieceRenderPanel.ResizeListener resizeListen = render.new ResizeListener();
		PieceRenderPanel.RotationListener rotationListen = render.new RotationListener();
		rotationListen.setSensitivity (DEFAULT_ROTATION_SENSITIVITY);
		PieceRenderPanel.ZoomListener zoomListen = render.new ZoomListener();
		zoomListen.setSensitivity (DEFAULT_ZOOM_SENSITIVITY);
		
		render.addComponentListener (resizeListen);
		render.addMouseListener (rotationListen);
		render.addMouseMotionListener (rotationListen);
		render.addMouseWheelListener (zoomListen);
		
		mProgress = new JProgressBar();
		
		JButton exportButton = new JButton ("export");
		
		mComment = new JTextArea();
		
		GridBagConstraints gbc = new GridBagConstraints();
		add (mProgress, gbc);
		
		gbc.gridx = 1;
		add (mComment, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		add (render, gbc);
		
		gbc.gridy = 2;
		add (stats);
		
		gbc.gridy = 2;
		gbc.gridx = 1;
		add (exportButton, gbc);
		
		revalidate();
		repaint();
	}
	
	public void exportSolution()
	{
		JFileChooser chooseFile = new JFileChooser();
		FileNameExtensionFilter fileFilter = new FileNameExtensionFilter ("text only", "txt");
		chooseFile.setFileFilter (fileFilter);
		
		File selected = chooseFile.getSelectedFile();
		ContainerExporter exporter = new ContainerExporter();
		exporter.export();
	}
	
	private class ExportListener implements ActionListener
	{
		public void actionPerformed (ActionEvent e)
		{
			exportSolution();
		}
	}
	
	private Container mSolution;
	
	private JProgressBar mProgress;
	private JTextArea mComment;
}
