package algorithm;


import java.util.ArrayList;

import models.*;

public class HighestValueInvDensity implements SelectionHeuristic {
	
	public HighestValueInvDensity()
	{
		
	}
	

	@Override
	public int getBestBlock(ArrayList<Resource> list) {
		
		double maxValue=0;
		for(int box=0; box<list.size();box++){
			if (list.get(box).getInventory()>0 || list.get(box).isInfinite()==true)
			{
				int x = list.get(box).getBlock().getDimensions(0);
				int y = list.get(box).getBlock().getDimensions(1);
				int z = list.get(box).getBlock().getDimensions(2);
				
				double cubeVolume = x*y*z;
				double newValue = cubeVolume/list.get(box).getBlock().getValue(); 
				if(newValue > maxValue)
				{
					maxValue=newValue;
					index=box;
				}
			}
		}
		return index;
	}
	private int index;
}
