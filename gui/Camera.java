package gui;

import algorithm.Matrix.DoubleMatrix;

public class Camera{
	/**
	 * Class creating a camera
	 * @author maxim
	 */
	public Camera(double radius, double alfa, double beta){
		this.radius = radius;
		this.alfa = alfa;
		this.beta = beta;
	}
	
	/*
	 * Method for setting the radius separate
	 * @author maxim
	 */
	public void setRadius(double radius){
		this.radius = radius;
		resetVariable();
	}
	
	/*
	 * Method for setting the alfa value separate
	 * @author maxim
	 */
	public void setAlfa(double alfa){
		this.alfa = alfa;
		resetVariable();
	}
	
	/*
	 * Method for setting the beta value separate
	 * @author maxim
	 */
	public void setBeta(double beta){
		this.beta = beta;
		resetVariable();
	}
	
	/*
	 * Method for getting the coordinates of the camera
	 * @author maxim
	 */
	public DoubleMatrix getCoor(){
		DoubleMatrix Coor =  new DoubleMatrix(3, 1);
		Coor.setCell(0, 0, x1);
		Coor.setCell(1, 0, x2);
		Coor.setCell(2, 0, x3);
		return Coor;
	}
	
	/*
	 * Method for resetting the x1, x2 and x3 values
	 * @author maxim
	 */
	private void resetVariable(){
		x1 = Math.cos(alfa) * radius;
		x2 = Math.sin(alfa) * radius;
		x3 = Math.cos(beta) * radius;
	}
	
	double x1;
	double x2;
	double x3;
	double radius;
	double alfa;
	double beta;
}