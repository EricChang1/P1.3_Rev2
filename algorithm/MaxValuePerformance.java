package algorithm;

import models.Container;

public class MaxValuePerformance implements PerformanceMeasure 
{
	@Override
	public int getPerformance(Container c) 
	{
		return (int)Math.round (c.getValue());
	}

}
