package models;

import models.Matrix.IntegerMatrix;

/**
 * wrapper for glue class
 * allows for linear ordering of glue objects
 * @author martin
 */
public class OrderedGlue extends Glue implements Comparable <Glue>
{
	public OrderedGlue (IntegerMatrix p) { super (p); }
	
	/**
	 * @param comp glue object to compare to
	 * @return 1 if comp is larger, -1 if this is larger, 0 if equal
	 * uses lexicographical ordering on coordinates and number of coordinates
	 */
	public int compareTo (Glue comp)
	{
		for (int cDim = 0; cDim < this.getDimension() && cDim < comp.getDimension(); ++cDim)
		{
			if (this.getPosition (cDim) < comp.getPosition (cDim))
				return -1;
			else if (this.getPosition (cDim) > comp.getPosition (cDim))
				return 1;
		}
		if (this.getDimension() < comp.getDimension())
			return -1;
		if (this.getDimension() > comp.getDimension())
			return 1;
		return 0;
	}
}
