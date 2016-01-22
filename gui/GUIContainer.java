package gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.*;

import algorithm.Container;
import algorithm.Matrix.DoubleMatrix;
import algorithm.Matrix.IntegerMatrix;

public class GUIContainer extends JComponent{
	
	public GUIContainer(int xLeft, int yTop, Container container){
		this.xLeft = xLeft;
		this.yTop = yTop;
		vectors = container.clone();
		vectors.print(System.out);
		twoDMatrix = new GUIMatrixCalc(vectors);
	}

	public void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		GUIMatrixCalc newVectors = new GUIMatrixCalc(vectors);
		ArrayList<DoubleMatrix> listOfVectors = new ArrayList<DoubleMatrix>();
		for (int c=0; c<vectors.getNumberOfVertices();c++){
			listOfVectors.add(newVectors.calcNewValues().get(c));
		}
		System.out.println("");
		System.out.println("recalculated vectors");
		for(int i=0; i<listOfVectors.size();i++){
			listOfVectors.get(i).print(System.out);
		}
		System.out.println("the final vectors used");
		for(int i=0;i<listOfVectors.size();i++){
			Point2D.Double vector1 = new Point2D.Double(xLeft + vectors.getVertex(i).getCell(0,0), yTop + vectors.getVertex(i).getCell(1,0));
			System.out.print(listOfVectors.get(i).getCell(0, 0) + " ");
			System.out.print(listOfVectors.get(i).getCell(1, 0) + " ");
			System.out.println(listOfVectors.get(i).getCell(2, 0) + " ");
			ArrayList<IntegerMatrix> connections = vectors.lookUpConnections(i);
			GUIMatrixCalc finalConnections = new GUIMatrixCalc(connections);
			ArrayList<DoubleMatrix> endConnections = new ArrayList<DoubleMatrix>();
			for (int c=0;c<connections.size();c++){
				endConnections.add(finalConnections.calcNewConnValues().get(c));
			}
			for(int j=0;j<vectors.lookUpConnections(i).size(); j++){
				Point2D.Double vector2 = new Point2D.Double(xLeft + endConnections.get(j).getCell(0,0), yTop + endConnections.get(j).getCell(1,0));
				Line2D.Double connection = new Line2D.Double(vector1, vector2);
				g2.draw(connection);
			}	
		}
	}
	
	
	GUIMatrixCalc twoDMatrix;
	Container vectors;
	int xLeft;
	int yTop;
}