package gui;

import algorithm.Algorithm;
import algorithm.DynamicAlgo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class DynamicAlgorithmConfigurator extends AlgorithmConfigurator
{
	public DynamicAlgorithmConfigurator()
	{
		mAlgo = new DynamicAlgo();
		
		setSize (150, 150);
		setLayout (new GridLayout (2, 1));
		constructComponents();
	}
	
	
	public Algorithm getAlgorithm()
	{
		return mAlgo;
	}
	
	public void constructComponents()
	{
		mCheckFuse = new JCheckBox ("enable cuboid fusion");
		JButton okayButton = new JButton ("okay");
		
		add (mCheckFuse);
		add (okayButton);
		
		mCheckFuse.addItemListener (new FuseCheckListen());
		okayButton.addActionListener (new CloseButtonListen());
	}
	
	
	private class FuseCheckListen implements ItemListener
	{
		@Override
		public void itemStateChanged(ItemEvent e) 
		{
			mAlgo.setFuse (mCheckFuse.isSelected());	
		}	
	}
	
	private class CloseButtonListen implements ActionListener
	{
		public void actionPerformed (ActionEvent e)
		{
			setVisible (false);
			dispose();
		}
	}
	
	private DynamicAlgo mAlgo;
	private JCheckBox mCheckFuse;
}
