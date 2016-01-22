package gui;

import javax.swing.*;

import algorithm.Container;

public class GUIConstruct{
	
	public static void main(String[] args){
		Container solvedContainer = new Container(250, 200, 200);
		JFrame frame = new JFrame();
		frame.setSize(1200, 1000);
		GUIContainer container = new GUIContainer(100, 100, solvedContainer);
		frame.add(container);
		frame.setTitle("Knapsack Solved");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
