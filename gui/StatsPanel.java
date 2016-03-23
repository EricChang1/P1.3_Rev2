package gui;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map.Entry;

import models.Block;
import models.Container;

/**
 * panel showing some stats about container
 * @author martin
 */
public class StatsPanel extends JPanel
{
	public StatsPanel()
	{
		mUsage = new JScrollPane();
		mVolumeAvailable = new JLabel();
		mVolumeUsed = new JLabel();
		mDensity = new JLabel();
		
		setLayout (new GridBagLayout());
	}
	
	public void constructComponents()
	{
		GridBagConstraints gbcBase = new GridBagConstraints();
		gbcBase.gridx = 1;
		gbcBase.weighty = 0.1;
		gbcBase.fill = GridBagConstraints.BOTH;
		
		GridBagConstraints gbcScroll = new GridBagConstraints();
		gbcScroll.weightx = 1.0;
		gbcScroll.weighty = 1.0;
		
		add (mUsage, gbcScroll);
		add (new JLabel ("volume available"), gbcBase);
		++gbcBase.gridy;
		add (new JLabel ("volume filled "), gbcBase);
		++gbcBase.gridy;
		add (new JLabel ("density: "), gbcBase);
		++gbcBase.gridx;
		gbcBase.gridy = 0;
		add (mVolumeAvailable, gbcBase);
		++gbcBase.gridy;
		add (mVolumeUsed, gbcBase);
		++gbcBase.gridy;
		add (mDensity, gbcBase);
	}
	
	public void update (Container c)
	{
		mVolumeUsed.setText (new Integer (c.getVolumeUsed()).toString() + " units");
		mVolumeAvailable.setText (new Integer (c.getVolume()).toString() + "units ");
		mDensity.setText (new Double (100 * c.getVolumeUsed() / c.getVolume()).toString() + "%");
		setBlockAmounts (c);
	}
	
	public void setBlockAmounts (Container c)
	{
		/**
		 * modifyable integer object
		 * @author martin
		 */
		class ModifyInt
		{
			public ModifyInt (int init) { mInt = init; }
			
			public Integer get() { return mInt; }
			
			public void add (int amount) { mInt += amount; }
			
			private int mInt;
		}
		
		HashMap<String, ModifyInt> amountsPerBlock = new HashMap<>();
		for (int cBlock = 0; cBlock < c.getAmountOfBlocks(); ++cBlock)
		{
			Block b = c.getBlock (cBlock);
			if (amountsPerBlock.containsKey (b.getName()))
				amountsPerBlock.put (b.getName(), new ModifyInt (1));
			else
				amountsPerBlock.get (b.getName()).add (1);
		}
		
		JPanel contents = new JPanel();
		contents.setLayout (new GridLayout (amountsPerBlock.size(), 2));
		for (Entry<String, ModifyInt> e : amountsPerBlock.entrySet())
		{
			contents.add (new JLabel (e.getKey()));
			contents.add (new JLabel (e.getValue().toString()));
		}
		mUsage.setViewportView (contents);
		revalidate();
		repaint();
	}
	
	private JScrollPane mUsage;
	private JLabel mVolumeUsed, mVolumeAvailable, mDensity;
}
