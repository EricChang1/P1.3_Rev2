package gui;

import algorithm.Algorithm;
import algorithm.DynamicAlgo;
import algorithm.MaxValuePerformance;
import algorithm.MaxVolumePerformance;
import algorithm.PerformanceMeasure;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class DynamicAlgorithmConfigurator extends AlgorithmConfigurator
{
	public static final String VALUE_BUTTON_TEXT = "value";
	public static final String VOLUME_BUTTON_TEXT = "volume";
	
	public DynamicAlgorithmConfigurator()
	{
		mAlgo = new DynamicAlgo();
		
		setSize (150, 150);
		setLayout (new GridBagLayout ());
		constructComponents();
	}
	
	
	public Algorithm getAlgorithm()
	{
		return mAlgo;
	}
	
	public void constructComponents()
	{
		JCheckBox checkFuse = new JCheckBox ("enable cuboid fusion");
		JLabel desOptimize = new JLabel ("optimize");
		JRadioButton value = new JRadioButton (VALUE_BUTTON_TEXT);
		JRadioButton volume = new JRadioButton (VOLUME_BUTTON_TEXT);
		JButton okayButton = new JButton ("okay");
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		
		add (checkFuse, gbc);
		gbc.gridx = 0;
		++gbc.gridy;
		add (desOptimize, gbc);
		++gbc.gridx;
		add (value, gbc);
		++gbc.gridx;
		add (volume, gbc);
		gbc.gridx = 1;
		++gbc.gridy;
		add (okayButton, gbc);
		
		checkFuse.addItemListener (new FuseCheckListen (checkFuse));
		okayButton.addActionListener (new CloseButtonListen());
		
		value.addItemListener (new PMeasureListen (value, PerformanceMeasureType.VALUE));
		volume.addItemListener (new PMeasureListen (volume, PerformanceMeasureType.VOLUME));
		
		ButtonGroup choosePMeasure = new ButtonGroup();
		choosePMeasure.add (value);
		choosePMeasure.add (volume);
		value.setSelected (true);
		pack();
	}
	
	
	
	
	private class FuseCheckListen implements ItemListener
	{
		public FuseCheckListen (JCheckBox box) 
		{
			mBox = box;
		}
		
		@Override
		public void itemStateChanged(ItemEvent e) 
		{
			mAlgo.setFuse (mBox.isSelected());	
		}
		
		private JCheckBox mBox;
	}
	
	private class PMeasureListen implements ItemListener
	{
		public PMeasureListen (JRadioButton button, PerformanceMeasureType type)
		{
			mType = type;
			mButton = button;
		}
		
		public PerformanceMeasure construct()
		{
			switch (mType)
			{
			case VALUE: return new MaxValuePerformance();
			case VOLUME: return new MaxVolumePerformance();
			}
			return null;
		}
		
		public void itemStateChanged (ItemEvent e)
		{
			if (mButton.isSelected())
			{
				mAlgo.setPerformanceMeasure (construct());
			}
		}
		
		private PerformanceMeasureType mType;
		private JRadioButton mButton;
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
}
