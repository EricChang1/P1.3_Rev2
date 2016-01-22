package gui;

import java.util.ArrayList;

import algorithm.Container;
import algorithm.Matrix.DoubleMatrix;
import algorithm.Matrix.IntegerMatrix;

public class GUIMatrixCalc{
	
	public GUIMatrixCalc(Container container){
		
		this.container = (Container) container.clone();
		c1 = cam.getCoor().getCell(0, 0);
		c2 = cam.getCoor().getCell(1, 0);
		c3 = cam.getCoor().getCell(2, 0);
	}
	
	public GUIMatrixCalc(ArrayList<IntegerMatrix> connectionVectors){
		connectionMatrices = (ArrayList<IntegerMatrix>) connectionVectors.clone();
		c1 = cam.getCoor().getCell(0, 0);
		c2 = cam.getCoor().getCell(1, 0);
		c3 = cam.getCoor().getCell(2, 0);
	}
	
	private double calcDistance(){
		double alfa = (c1 - x1) * (c1 - x1);
		double beta = (c2 - x2) * (c2 - x2);
		double gamma = (c3 - x3) * (c3 - x3);
		d = Math.sqrt(alfa + beta + gamma);
		return d;
	}
	
	public ArrayList<DoubleMatrix> calcNewConnValues(){
		ArrayList<DoubleMatrix> twoDContainer = new ArrayList<DoubleMatrix>();
		for(int i=0;i<connectionMatrices.size();i++){
			vector = connectionMatrices.get(i).toDoubleMatrix();
			twoDContainer.add(calcNewVector(vector));
			}
		return twoDContainer;
	}
	
	public ArrayList<DoubleMatrix> calcNewValues(){
		//gets every vector from the original container
		//and adapts the vectors using calcNewVector
		ArrayList<DoubleMatrix> twoDContainer = new ArrayList<DoubleMatrix>();
		for(int i=0;i<container.getNumberOfVertices();i++){
			vector = container.getVertex(i).toDoubleMatrix().clone();
			twoDContainer.add(calcNewVector(vector));
			}
		return twoDContainer;
		}
	
	private double calcXi1Value(){
		double Xi1Value = (((x2 - c2)*c1) / calcDistance()) + c2;
		return Xi1Value;
	}
	
	private double calcXi2Value(){
		double Xi2Value = (((x3 - c3)*c1) / calcDistance()) + c3;
		return Xi2Value;
	}
	
	//calculating the new values of the matrix
	//Martin math
	private DoubleMatrix calcNewVector(DoubleMatrix vector){
		x1 = vector.getCell(0, 0);
		x2 = vector.getCell(1, 0);
		x3 = vector.getCell(2, 0);
		DoubleMatrix xNew = new DoubleMatrix(3, 1);
		xNew.setCell(0, 0, 0.0);
		xNew.setCell(1, 0, (calcXi1Value() + c2));
		xNew.setCell(2, 0, (calcXi2Value() + c3));
		return xNew;
		}
	
	//book math
	private DoubleMatrix calcNewVector2(DoubleMatrix vector){
		x1 = vector.getCell(0, 0);
		x2 = vector.getCell(1, 0);
		x3 = vector.getCell(2, 0);
		
		DoubleMatrix matrix1 = new DoubleMatrix(4,4);
		for (int i=0;i<matrix1.getRows();i++){
			for (int j=0;j<matrix1.getColumns();j++){
				if(i==j){
					matrix1.setCell(i, j, 1.0);
				}else{
					matrix1.setCell(i, j, 0.0);
				}
			}
		}
		matrix1.setCell(2, 2, 0.0);
		matrix1.setCell(2, 3, (-1/calcDistance()));
		DoubleMatrix projectablePoint = new DoubleMatrix(4,1);
		projectablePoint.setCell(0, 0, x1);
		projectablePoint.setCell(1, 0, x2);
		projectablePoint.setCell(2, 0, x3);
		projectablePoint.setCell(3, 0, 1.0);
		DoubleMatrix xNew = new DoubleMatrix(4, 1);
		matrix1.multiply(projectablePoint, xNew);
		DoubleMatrix xFinal = new DoubleMatrix(3,1);
		xFinal.setCell(0, 0, (xNew.getCell(0, 0)/xNew.getCell(3, 0)));
		xFinal.setCell(1, 0, (xNew.getCell(1, 0)/xNew.getCell(3, 0)));
		xFinal.setCell(2, 0, (xNew.getCell(2, 0)/xNew.getCell(3, 0)));
		return xFinal;
	}
	
	
	//book kind of hardcoding
	private DoubleMatrix calcNewVector3(DoubleMatrix vector){
		x1 = vector.getCell(0, 0);
		x2 = vector.getCell(1, 0);
		x3 = vector.getCell(2, 0);
		
		DoubleMatrix matrix1 = new DoubleMatrix(4,4);
		for (int i=0;i<matrix1.getRows();i++){
			for (int j=0;j<matrix1.getColumns();j++){
				if(i==j){
					matrix1.setCell(i, j, 1.0);
				}else{
					matrix1.setCell(i, j, 0.0);
				}
			}
		}
		matrix1.setCell(2, 2, 0.0);
		matrix1.setCell(2, 3, (-1/calcDistance()));
		DoubleMatrix projectablePoint = new DoubleMatrix(4,1);
		projectablePoint.setCell(0, 0, x1);
		projectablePoint.setCell(1, 0, x2);
		projectablePoint.setCell(2, 0, x3);
		projectablePoint.setCell(3, 0, 1.0);
		DoubleMatrix xNew = new DoubleMatrix(4, 1);
		matrix1.multiply(projectablePoint, xNew);
		DoubleMatrix xFinal = new DoubleMatrix(3,1);
		xFinal.setCell(0, 0, x1/calcDistance());
		xFinal.setCell(1, 0, x2/calcDistance());
		xFinal.setCell(2, 0, 0.0);
		return xFinal;
	}
	
	//book straight calculations
	private DoubleMatrix calcNewVector4(DoubleMatrix vector){
		x1 = vector.getCell(0, 0);
		x2 = vector.getCell(1, 0);
		x3 = vector.getCell(2, 0);
		double xDash = x1 / (1 - (x3 / calcDistance()));
		double yDash = x2 / (1 - (x3 / calcDistance()));
		DoubleMatrix xFinal = new DoubleMatrix(3,1);
		xFinal.setCell(0, 0, xDash);
		xFinal.setCell(1, 0, yDash);
		xFinal.setCell(2, 0, 0.0);
		System.out.println("printing new vector");
		xFinal.print(System.out);
		return xFinal;
	}
	
	
	//just for the time being
	DoubleMatrix vector = new DoubleMatrix(3,1);
	//camera point c
	double c1;
	double c2;
	double c3;
	
	//projected point x
	double x1;
	double x2;
	double x3;
	
	double d;
	
	ArrayList<IntegerMatrix> connectionMatrices;
	
	Container container;
	
	Camera cam = new Camera(1200, 1200, 1200);
}