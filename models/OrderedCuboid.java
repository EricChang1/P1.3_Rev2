package models;

import geometry.Cuboid;

public class OrderedCuboid extends Cuboid implements Comparable <OrderedCuboid>
{
	/**
	 * @param p1 first spanning point
	 * @param p2 second spanning point
	 */
	public OrderedCuboid (Glue p1, Glue p2) { super (p1, p2); }
	
	/**
	 * @param comp cuboid to compare to
	 * @return -1 if this is less than comp, 1 if this is larger than comp, 0 if both are equal.
	 * Compares first spanning point and second spanning point afterwards
	 */
	public int compareTo (OrderedCuboid comp)
	{
		int firstComp = new OrderedGlue (this.getFirst()).compareTo (new Glue (comp.getFirst()));
		if (firstComp != 0)
			return firstComp;
		return new OrderedGlue (this.getSecond()).compareTo (new Glue (comp.getSecond()));
	}
}
