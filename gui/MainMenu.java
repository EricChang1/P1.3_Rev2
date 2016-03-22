package gui;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import algorithm.*;

import main.*;
import main.AlgorithmSetup.DimName;
import models.Block;
import models.ShapeParser;

/**
 * main menu window
 * @author martin
 */
public class MainMenu extends JFrame
{
	public static final int DEFAULT_WIDTH = 250;
	public static final int DEFAULT_HEIGHT = 500;
	
	public static final String DEFAULT_TITLE = "POLYCUBE SOLVER";
	
	
	public MainMenu()
	{
		setSize (DEFAULT_WIDTH, DEFAULT_WIDTH);
		setLayout (new GridBagLayout());
		setDefaultCloseOperation (EXIT_ON_CLOSE);
		setTitle (DEFAULT_TITLE);
		
		mAlgoSetup = new AlgorithmSetup (new ArrayList<Block>());
	}
	
	public void constructComponents()
	{
		int horPad = 5, verPad = 10;
		
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		gbc.gridy = 0;
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.weightx = 0.1;
		gbc.weighty = 0.01;
		gbc.insets = new Insets (horPad, verPad, horPad, verPad);
		
		JLabel sizeLabel = new JLabel ("container dimensions");
		add (sizeLabel, gbc);
		
		JSpinner spinDep, spinWid, spinHig;
		spinDep = new JSpinner();
		spinDep.setToolTipText ("depth of container");
		++gbc.gridy;
		add (spinDep, gbc);
		spinDep.addChangeListener (new DimensionInputListener (AlgorithmSetup.DimName.DEPTH));
		spinWid = new JSpinner();
		spinWid.setToolTipText ("width of container");
		++gbc.gridx;
		add (spinWid, gbc);
		spinWid.addChangeListener (new DimensionInputListener (AlgorithmSetup.DimName.WIDTH));
		spinHig = new JSpinner();
		
		spinHig.setToolTipText ("height of container");
		++gbc.gridx;
		add (spinHig, gbc);
		spinDep.addChangeListener (new DimensionInputListener (AlgorithmSetup.DimName.HEIGHT));
		
		JLabel pieceLabel = new JLabel ("select pieces");
		++gbc.gridy;
		gbc.gridx = 0;
		add (pieceLabel, gbc);
		JButton loadPieceButton = new JButton ("load pieces");
		gbc.gridx += 2;
		add (loadPieceButton, gbc);
		loadPieceButton.addActionListener (new LoadBlocksListener());
		
		GridBagConstraints gbcPieceEditPanel = new GridBagConstraints();
		gbcPieceEditPanel.gridheight = 4;
		gbcPieceEditPanel.gridwidth = 3;
		gbcPieceEditPanel.gridy = gbc.gridy + 1;
		gbcPieceEditPanel.fill = GridBagConstraints.BOTH;
		gbcPieceEditPanel.weightx = 1.0;
		gbcPieceEditPanel.weighty = 1.0;
		mPieceSelection = new PieceSelectionPanel();
		add (mPieceSelection, gbcPieceEditPanel);
		
		mAlgoChooser = new JComboBox<> (AlgorithmType.values());
		gbc.gridy = gbcPieceEditPanel.gridy + gbcPieceEditPanel.gridheight + 1;
		gbc.gridx = GridBagConstraints.RELATIVE;
		add (mAlgoChooser, gbc);
		mAlgoChooser.addItemListener (new LoadAlgoListener());
		
		JButton startButton = new JButton ("go!");
		add (startButton, gbc);
		startButton.addActionListener (new StartListener());
	}
	
	
	public void chooseFile()
	{
		JFileChooser chooseFile = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter ("text only", "txt");
		chooseFile.setFileFilter (filter);
		
		chooseFile.showOpenDialog (null);
		File input = chooseFile.getSelectedFile();
		
		String errorMessage = null;
		try
		{
			ShapeParser parser = new ShapeParser (input);
			parser.parse();
			ArrayList<Block> parsedBlocks = parser.getBlocks();
			mAlgoSetup = new AlgorithmSetup (parsedBlocks);
			mPieceSelection.constructComponents (mAlgoSetup.getResourceSetups());
		}
		catch (Exception e)
		{
			errorMessage = "I could not load this file, because " + e.getMessage();
			showErrorDialog ("oh oh", errorMessage);
		}
	}
	
	
	public void loadAlgorithm (AlgorithmType loadType)
	{
		switch (loadType)
		{
		case DYNAMIC: mExecute = new DynamicAlgo();
		break;
		default: showErrorDialog ("make another choise", "I cannot load this algorithm");
		}
	}
	
	
	public void startExecution()
	{
		mAlgoSetup.loadAlgorithm (mExecute);
		//do stuff to run and display the algorithm
	}
	
	
	public void showErrorDialog (String title, String message)
	{
		JDialog errorDialog = new JDialog (this, message, false);
		errorDialog.setTitle (title);
		errorDialog.setDefaultCloseOperation (DISPOSE_ON_CLOSE);
		errorDialog.setVisible (true);
	}
	
	private class DimensionInputListener implements ChangeListener
	{
		public DimensionInputListener (AlgorithmSetup.DimName dim)
		{
			mDim = dim;
		}
		
		@Override
		public void stateChanged(ChangeEvent e) 
		{
			JSpinner src = (JSpinner) e.getSource();
			mAlgoSetup.setContainerSize ((int) src.getValue(), mDim);
		}
		
		private AlgorithmSetup.DimName mDim;
	}
	
	private class LoadBlocksListener implements ActionListener
	{
		public void actionPerformed (ActionEvent e)
		{
			chooseFile();
		}
	}
	
	private class LoadAlgoListener implements ItemListener
	{
		@Override
		public void itemStateChanged(ItemEvent e) 
		{
			loadAlgorithm ((AlgorithmType) mAlgoChooser.getSelectedItem());
		}
	}
	
	private class StartListener implements ActionListener
	{
		public void actionPerformed (ActionEvent e)
		{
			if (mExecute == null)
				showErrorDialog ("HOW???", "please choose an algorithm before attemting to run it!");
			else
				startExecution();
		}
	}
	
	private PieceSelectionPanel mPieceSelection;
	private JComboBox<AlgorithmType> mAlgoChooser;
	
	private AlgorithmSetup mAlgoSetup;
	private Algorithm mExecute;
}
