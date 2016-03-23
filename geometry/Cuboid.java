package geometry;

import java.util.ArrayList;



import models.Container;
import models.Glue;
import models.Matrix;
import models.Matrix.*;


/**
 * class modelling a cuboid
 * @author martin
 */
public class Cuboid extends GeoShape 
{

	public Cuboid(Glue p1, Glue p2) 
	{
		super(p1, p2);
		
		IntegerMatrix diff = new IntegerMatrix(getDimension(), 1);
		for (int cDim = 0; cDim < getDimension(); ++cDim)
			diff.setCell(cDim, 0, p2.getPosition(cDim) - p1.getPosition (cDim));
		mV1 = new DoubleMatrix (getDimension(), 1);
		mV1.setCell (0, 0, diff.getCell(0, 0));
		mV2 = new DoubleMatrix(getDimension(), 1);
		mV2.setCell (1, 0, diff.getCell (1, 0));
		mV3 = new DoubleMatrix (getDimension(), 1);
		mV3.setCell (2, 0, diff.getCell (2, 0));
	}

	@Override
	public ArrayList<DoubleMatrix> getVectors() 
	{
		ArrayList <DoubleMatrix> vecs = new ArrayList<Matrix.DoubleMatrix>();
		vecs.add (mV1.clone());
		vecs.add (mV2.clone());
		vecs.add (mV3.clone());
		return vecs;
	}
	
	public ArrayList <Integer> getDimensions()
	{
		ArrayList <Integer> dims = new ArrayList<Integer>();
		for (int cDim = 0; cDim < getDimension(); ++cDim)
			dims.add (Math.abs (getSecond().getCell(cDim, 0) - getFirst().getCell(cDim, 0)));
		return dims;
	}
	
	public ArrayList<Glue> getVertices()
	{
		ArrayList<Glue> vertices = new ArrayList<>();
		
		vertices.add (new Glue (getFirst()));
		for (int cSingle = 0; cSingle < getDimension(); ++cSingle)
		{
			IntegerMatrix glueBase = getFirst().clone();
			glueBase.setCell (cSingle, 0, getSecond().getCell (cSingle, 0));
			vertices.add (new Glue (glueBase));
		}
		for (int cDouble1 = 0; cDouble1 < getDimension() - 1; ++cDouble1)
		{
			for (int cDouble2 = cDouble1 + 1; cDouble2 < getDimension(); ++cDouble2)
			{
				IntegerMatrix glueBase = getFirst().clone();
				glueBase.setCell (cDouble1, 0, getSecond().getCell (cDouble1, 0));
				glueBase.setCell (cDouble2, 0, getSecond().getCell (cDouble2, 0));
				vertices.add (new Glue (glueBase));
			}
		}
		vertices.add (new Glue (getSecond()));
		return vertices;
	}
	
	

	@Override
	public DoubleMatrix loadEquationMatrix() 
	{
		DoubleMatrix eq = new DoubleMatrix (3, 4);
		eq.copyValues(mV1, 0, 0, 0, 0, getDimension(), 1);
		eq.copyValues(mV2, 0, 1, 0, 0, getDimension(), 1);
		eq.copyValues(mV3, 0, 2, 0, 0, getDimension(), 1);
		eq.copyValues(getFirst().toDoubleMatrix(), 0, 3, 0, 0, getDimension(), 1);
		return eq;
	}
	
	/**
	 * constructs a container using the dimensions of this cuboid
	 */
	public Container toContainer()
	{
		ArrayList <Integer> dims = getDimensions();
		return new Container (dims.get(0), dims.get(1), dims.get(2));
	}
	
	/**
	 * @param fuse cuboid to fuse this with
	 * @return a new cuboid spanned by combined
	 * min and max vertices of vertices of this cuboid
	 * and vertices of fuse
	 * Precondition: this and fuse should be fuseable
	 */
	public Cuboid fuse (Cuboid fuse)
	{
		ArrayList<Glue> combinedVerts = new ArrayList<>();
		combinedVerts.addAll (this.getVertices());
		combinedVerts.addAll (fuse.getVertices());
		return new Cuboid (getMin (combinedVerts), getMax (combinedVerts));
	}
	
	/**
	 * @param fuse cuboid to test for possible fusion
	 * @return true if this and fuse can be added together to form a new cuboid,
	 * true only if there is one side both cuboids have in common
	 */
	public boolean areFuseable (Cuboid fuse)
	{
		int cntSameVertices = 0;
		for (Glue tVertex : this.getVertices())
		{
			for (Glue fVertex : fuse.getVertices())
			{
				if (tVertex.equals (fVertex))
				{
					++cntSameVertices;
					if (cntSameVertices == 4)
						return true;
				}
			}
		}
		return false;
	}
	
	
	private DoubleMatrix mV1, mV2, mV3;
}
