package gui;

import gui.PieceRenderPanel.RotationListener;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import sun.security.provider.certpath.OCSP.RevocationStatus;

import models.BasicShape;
import models.Container;
import models.ContainerExporter;

/**
 * frame to view solution
 * should open once the algorithm is started
 * @author martin
 */
public class SolutionViewer extends JFrame 
{
	
	public static final int DEFAULT_WIDTH = 600, DEFAULT_HEIGHT = 600;
	public static final int PROGRESS_MIN = 0, PROGRESS_MAX = 100;
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
		getContentPane().removeAll();
		
		StatsPanel stats = new StatsPanel();
		stats.constructComponents();
		stats.update (mSolution);
		
		PieceRenderPanel render = new PieceRenderPanel (new BasicShape (mSolution));
		PieceRenderPanel.ResizeListener resizeListen = render.new ResizeListener();
		PieceRenderPanel.RotationListener rotationListen = render.new RotationListener();
		rotationListen.setSensitivity (DEFAULT_ROTATION_SENSITIVITY);
		PieceRenderPanel.ZoomListener zoomListen = render.new ZoomListener();
		zoomListen.setSensitivity (DEFAULT_ZOOM_SENSITIVITY);
		Dimension renderPref = new Dimension ((int) (getWidth() * .75), (int) (getHeight() * .75));
		render.setPreferredSize (renderPref);
		
		render.addComponentListener (resizeListen);
		render.addMouseListener (rotationListen);
		render.addMouseMotionListener (rotationListen);
		render.addMouseWheelListener (zoomListen);
		
		mProgress = new JProgressBar();
		mProgress.setMinimum (PROGRESS_MIN);
		mProgress.setMaximum (PROGRESS_MAX);
		
		JButton exportButton = new JButton ("export");
		exportButton.addActionListener (new ExportListener());
		
		mComment = new JTextArea();
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		add (mProgress, gbc);
		
		gbc.gridx = 1;
		gbc.gridheight = 2;
		add (mComment, gbc);
		gbc.gridheight = 1;
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		add (render, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		add (stats, gbc);
		
		gbc.gridx = 1;
		add (exportButton, gbc);
		
		//getContentPane().revalidate();
		repaint();
	}
	
	public void exportSolution()
	{
		JFileChooser chooseFile = new JFileChooser();
		FileNameExtensionFilter fileFilter = new FileNameExtensionFilter ("text only", "txt");
		chooseFile.setFileFilter (fileFilter);
		
		chooseFile.showOpenDialog (this);
		File selected = chooseFile.getSelectedFile();
		
		ContainerExporter exporter = new ContainerExporter (mSolution);
		if (!mComment.getText().isEmpty())
			exporter.setDescription (mComment.getText());
		try
		{
			exporter.write (selected.getAbsolutePath());
		}
		catch (IOException ioe)
		{
			showErrorDialog ("error writing", "an error occured while I was trying to export: " + ioe.getMessage());
		}
	}
	
	public void showErrorDialog (String title, String message)
	{
		int textCols = 30;
		JDialog errorDialog = new JDialog (this, false);
		errorDialog.setTitle (title);
		errorDialog.setDefaultCloseOperation (DISPOSE_ON_CLOSE);
		
		JTextField text = new JTextField (message, textCols);
		errorDialog.add (text);
		
		errorDialog.setVisible (true);
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
