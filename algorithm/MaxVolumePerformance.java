package algorithm;

import models.Container;

public class MaxVolumePerformance implements PerformanceMeasure 
{

	@Override
	public int getPerformance(Container c) 
	{
		return c.getVolumeUsed();
	}

}
