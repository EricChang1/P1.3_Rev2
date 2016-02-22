package models;

import java.util.ArrayList;

import models.Matrix.*;



public class Block extends BasicShape implements Cloneable {
	
	/**
	*Constructor to pass the matrix handler vectors and the value.
	*@param matrixHVectors Arraylist containing the vectors defining the shape
	*@param adjMat adjacency matrix of block 
	*@param value Value assigned to the block.
	*/
	public Block (ArrayList<IntegerMatrix> matrixHVectors, IntegerMatrix adjMat, double value)
	{
		super (matrixHVectors, adjMat);
		this.value = value;
	}
	/**
	*Constructor to pass the value with the BasicShape object
	*@param value Value assigned to the shape
	*@param bShape Object of BasicShape
	*/
	public Block(BasicShape bShape, double value)
	{
		super (bShape);
		this.value = value;
	}
	
	/**
	 * @return newly constructed Block
	 */
	public Block clone()
	{
		return new Block (this, getValue());
	}

	/**
	*Method to get the value.
	*@return the value.
	*/
	public double getValue(){
		return value;
	}
	
	public boolean equals (Block comp)
	{
		if (this.value != comp.value || !super.equals(comp))
			return false;
		return true;
	}
	
	protected void addShape (Block b)
	{
		this.value += b.getValue();
		super.addShape(b);
	}

	private double value;
}