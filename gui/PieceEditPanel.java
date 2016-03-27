package gui;

import gui.PieceRenderPanel.RotationListener;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import main.ResourceSetup;
import models.BasicShape;

/**
 * gui class allowing to edit a single resource setup
 * @author martin
 */
public class PieceEditPanel extends JPanel
{
	public static int DEFAULT_WIDTH = 250, DEFAULT_HEIGHT = 60;
	public static int DEFAULT_SPINNER_WIDTH = 40, DEFAULT_SPINNER_HEIGHT = 30;
	
	
	public PieceEditPanel (ResourceSetup setup)
	{
		mResSetup = setup;
		setLayout (new GridBagLayout());
		setPreferredSize (new Dimension (DEFAULT_WIDTH, DEFAULT_HEIGHT));
	}
	
	
	public ResourceSetup getSetup() {  return mResSetup; }
	
	public void constructComponents()
	{
		int horInset = 3, vertInset = 5;
		GridBagConstraints gbcBase, gbcNumber, gbcBox, gbcPiece;
		
		gbcBase = new GridBagConstraints();
		gbcBase.insets = new Insets (horInset, vertInset, horInset, vertInset);
		
		gbcNumber = (GridBagConstraints) gbcBase.clone();
		gbcNumber.gridx = 0;
		
		gbcBox = (GridBagConstraints) gbcBase.clone();
		gbcBox.gridx = 1;
		
		gbcPiece = (GridBagConstraints) gbcBase.clone();
		gbcPiece.gridx = 2;
		gbcPiece.gridwidth = 2;
		gbcPiece.fill = GridBagConstraints.BOTH;
		gbcPiece.weightx = 0.75;
		gbcPiece.weighty = 1.0;
		
		mCapacitySpinner = new JSpinner();
		mCapacitySpinner.addChangeListener (new CapacityListen());
		mCapacitySpinner.setPreferredSize (new Dimension (DEFAULT_SPINNER_WIDTH, DEFAULT_SPINNER_HEIGHT));
		
		mCheckInfinity = new JCheckBox ();
		mCheckInfinity.addItemListener (new InfinityListen());
		
		mRenderPiece = new PieceRenderPanel (new BasicShape (mResSetup.getBlock()));
		PieceRenderPanel.ResizeListener renderResize = mRenderPiece.new ResizeListener();
		PieceRenderPanel.RotationListener renderRotate = mRenderPiece.new RotationListener();
		PieceRenderPanel.ZoomListener renderZoom = mRenderPiece.new ZoomListener();
		mRenderPiece.addComponentListener (renderResize);
		mRenderPiece.addMouseListener (renderRotate);
		mRenderPiece.addMouseMotionListener (renderRotate);
		mRenderPiece.addMouseWheelListener (renderZoom);
		
		add (mCapacitySpinner, gbcNumber);
		add (mCheckInfinity, gbcBox);
		add (mRenderPiece, gbcPiece);
		
		mRenderPiece.setVisible(true);
	}
	
	
	private class InfinityListen implements ItemListener
	{
		@Override
		public void itemStateChanged(ItemEvent e) 
		{
			mResSetup.setInifite (mCheckInfinity.isSelected());
		}
	}
	
	private class CapacityListen implements ChangeListener
	{
		@Override
		public void stateChanged(ChangeEvent e) 
		{
			mResSetup.setCapacity ((int) mCapacitySpinner.getValue());
		}
	}
	
	private ResourceSetup mResSetup;
	private JSpinner mCapacitySpinner;
	private JCheckBox mCheckInfinity;
	private PieceRenderPanel mRenderPiece;
}
