package gui;

import generic.Set;

import java.awt.*;
import java.awt.event.*;
import java.util.Collection;

import javax.swing.*;

import main.ResourceSetup;

/**
 * panel for piece selection
 * stores resource setup objects as set
 * @author martin
 */
public class PieceSelectionPanel extends JScrollPane
{
	public static JPanel getDefaultContentPanel()
	{
		JPanel contentPanel = new JPanel();
		//contentPanel.setLayout (new GridBagLayout());
		return contentPanel;
	}
	
	
	public PieceSelectionPanel()
	{
		super (new JPanel());
		
		mSetups = new Set<>();
	}
	
	
	public Set<ResourceSetup> getResources() { return mSetups; }
	
	/**
	 * @param setups setup components to edit
	 * removes all previous components and adds components for each resource setup
	 */
	public void constructComponents (Collection<ResourceSetup> setups)
	{
		JPanel contents = new JPanel();
		contents.setLayout (new GridBagLayout());
		
		GridBagConstraints gbcBase = new GridBagConstraints();
		gbcBase.anchor = GridBagConstraints.CENTER;
		gbcBase.weightx = 1.0;
		
		GridBagConstraints gbcLabel = (GridBagConstraints) gbcBase.clone();
		gbcLabel.weighty = 0.01;
		gbcLabel.gridx = 0;
		gbcLabel.gridy = 0;
		
		/*
		contents.add (new JLabel ("quantity"), gbcLabel);
		gbcLabel.gridx = 1;
		contents.add (new JLabel ("infinity"), gbcLabel);*/
		
		GridBagConstraints gbcEdit = (GridBagConstraints) gbcBase.clone();
		gbcEdit.gridwidth = GridBagConstraints.REMAINDER;
		gbcEdit.gridx = 0;
		gbcEdit.gridy = 1;
		gbcEdit.fill = GridBagConstraints.BOTH;
		gbcEdit.weightx = 1.0;
		gbcEdit.weighty = 1.0;
		
		mSetups = new Set<>();
		for (ResourceSetup setup : setups)
		{
			mSetups.add (setup);
			PieceEditPanel edit = new PieceEditPanel (setup);
			edit.constructComponents();
			contents.add (edit, gbcEdit);
			++gbcEdit.gridy;
		}
		
		setViewportView (contents);
	}
	
	private Set<ResourceSetup> mSetups;
	private JPanel mContentPanel;
}
