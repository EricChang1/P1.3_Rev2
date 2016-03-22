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
	public PieceSelectionPanel()
	{
		mContentPanel = new JPanel();
		mContentPanel.setLayout (new GridBagLayout());
		
		mSetups = new Set<>();
	}
	
	
	public Set<ResourceSetup> getResources() { return mSetups; }
	
	/**
	 * @param setups setup components to edit
	 * removes all previous components and adds components for each resource setup
	 */
	public void constructComponents (Collection<ResourceSetup> setups)
	{
		mContentPanel.removeAll();
		
		GridBagConstraints gbcBase = new GridBagConstraints();
		gbcBase.anchor = GridBagConstraints.CENTER;
		gbcBase.weightx = 1.0;
		
		GridBagConstraints gbcLabel = (GridBagConstraints) gbcBase.clone();
		gbcLabel.weighty = 0.0;
		gbcLabel.gridx = 0;
		gbcLabel.gridy = 0;
		
		mContentPanel.add (new JLabel ("quantity"), gbcLabel);
		gbcLabel.gridx = 1;
		mContentPanel.add (new JLabel ("infinity"), gbcLabel);
		
		GridBagConstraints gbcEdit = (GridBagConstraints) gbcBase.clone();
		gbcEdit.gridwidth = GridBagConstraints.REMAINDER;
		gbcEdit.gridx = 0;
		gbcEdit.gridy = 1;
		
		for (ResourceSetup setup : setups)
		{
			mSetups.add (setup);
			mContentPanel.add (new PieceEditPanel (setup), gbcEdit);
			++gbcEdit.gridy;
		}
		
		mContentPanel.revalidate();
	}
	
	private Set<ResourceSetup> mSetups;
	private JPanel mContentPanel;
}
