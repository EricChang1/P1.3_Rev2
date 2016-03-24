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
	
	public PieceEditPanel (ResourceSetup setup)
	{
		mResSetup = setup;
		setLayout (new GridBagLayout());
	}
	
	
	public ResourceSetup getSetup() {  return mResSetup; }
	
	public void constructComponents()
	{
		int horInset = 1, vertInset = 3;
		GridBagConstraints gbcBase, gbcNumber, gbcBox, gbcPiece;
		
		gbcBase = new GridBagConstraints();
		gbcBase.gridy = 0;
		gbcBase.fill = GridBagConstraints.BOTH;
		gbcBase.anchor = GridBagConstraints.CENTER;
		gbcBase.insets = new Insets (horInset, vertInset, horInset, vertInset);
		
		gbcNumber = (GridBagConstraints) gbcBase.clone();
		gbcNumber.gridx = 0;
		gbcNumber.weightx = 0.1;
		gbcNumber.weighty = 0.25;
		
		gbcBox = (GridBagConstraints) gbcBase.clone();
		gbcBox.gridx = 1;
		gbcBox.weightx = 0.1;
		gbcBox.weighty = 0.25;
		
		gbcPiece = (GridBagConstraints) gbcBase.clone();
		gbcPiece.gridx = 2;
		gbcPiece.gridwidth = 2;
		gbcPiece.weightx = 0.75;
		gbcPiece.weighty = 1.0;
		gbcPiece.insets = new Insets (10, 10, 10, 10);
		
		mCapacitySpinner = new JSpinner();
		mCapacitySpinner.addChangeListener (new CapacityListen());
		
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
