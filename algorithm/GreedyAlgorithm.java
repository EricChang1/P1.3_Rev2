package algorithm;
import java.util.ArrayList;

import models.BasicShape;
import models.Block;
import models.Container;
import models.Matrix;
import models.Position;
import models.Resource;
import models.Resource.BlockType;
import algorithm.*;

public class GreedyAlgorithm extends Algorithm {
	
	public GreedyAlgorithm(EvaluationHeuristic e, ArrayList<Resource> list){
		this.currentE = e;
		Resources = list;
	}
	
	@Override
		public void run() 
	{
		super.run();
		while (!isAlgoDone())
		{
			placeBlock();
		}
	}
	public void placeBlock()
	{
		//get left top back position
		Position currentPos = currentE.freePos(this.getContainer());
		pentPos = currentPos;
		if (currentPos!=null)
		{
			//Select Resource
			for (int k=0; k<Resources.size(); k++)
			{
				//Select Block
				if (Resources.get(k).isInfinite()==true||Resources.get(k).getInventory()>0)
				{
					//Select Rotation
					for (int l=0;l<Resources.get(k).getRot().size();l++)
					{
						Container cloneTruck = getContainer().clone();
						Block currentBlock = Resources.get(k).getRot().get(l);	
						//Adjust Position for PENT only
						if (Resources.get(k).getType()==Resource.BlockType.PENT)
						{
							ArrayList <Integer> Pos = new ArrayList <Integer> ();
							int X = Resources.get(k).rotatedPos().get(l).getPosition(0)+pentPos.getPosition(0);
							int Y = Resources.get(k).rotatedPos().get(l).getPosition(1)+pentPos.getPosition(1);
							int Z = Resources.get(k).rotatedPos().get(l).getPosition(2)+pentPos.getPosition(2);
							Pos.add(X); Pos.add(Y); Pos.add(Z); 
							Position pos = new Position(Pos);
							currentPos = pos;
						}
						if (Resources.get(k).getType()==Resource.BlockType.PARCEL&&currentE.checkPos(currentPos, currentBlock, cloneTruck))
						{
							cloneTruck.placeBlock(currentBlock, currentPos); //place
							if (currentE.getScore(Resources.get(k), currentPos, l)>=score)//higher than score
							{
								score = currentE.getScore(Resources.get(k), currentPos, l);//update score
								bestBlock = currentBlock;
								state=l;
								bool=false;
								bestResource = Resources.get(k);
								index=k;
							}	
						}	
						if (Resources.get(k).getType()==Resource.BlockType.PENT&&currentE.pentCheckPos(Resources.get(k), l, pentPos, cloneTruck))
						{
							cloneTruck.placeBlock(currentBlock, currentPos); //place
							if (currentE.getScore(Resources.get(k), pentPos, l)>=score)//higher than score
							{
								score = currentE.getScore(Resources.get(k), pentPos, l);//update score
								bestBlock = currentBlock;
								state=l;
								bool=false;
								pentAdjPos = currentPos;
								bestResource = Resources.get(k);
								index=k;
							}	
						}	
					}
				}
			}
			if (bool)
			{
				currentE.nextPos();
			}
			if (bestBlock!=null)
			{
				if (bestResource.getType()==Resource.BlockType.PENT)
				{
					getContainer().placeBlock(bestBlock, pentAdjPos);
					currentE.update(bestResource, pentPos, bestResource.getType(),state);
				}
				if (bestResource.getType()==Resource.BlockType.PARCEL)
				{
					getContainer().placeBlock(bestBlock, currentPos);
					currentE.update(bestResource, currentPos, bestResource.getType(),state);
				}
				Resources.get(index).deduct(); 
				bestBlock = null;
				bool=true;
			}	
		}
		if(currentPos==null||resourceEmpty())
		{
			setAlgoDone();
		}
		
		else if(currentPos!=null)
		{
			this.placeBlock();
		}
	}
	
	public boolean resourceEmpty()
	{
		boolean bool = true;
		for (int k=0; k<Resources.size(); k++)
		{
			if (Resources.get(k).getInventory()>0)
			{
				return false;
			}
		}
		return bool;
	}

	private EvaluationHeuristic currentE;
	private ArrayList<Resource> Resources;
	private double score;
	private Block bestBlock;
	private int state;
	private boolean bool;
	private Position pentPos;
	private Position pentAdjPos;
	private Resource bestResource;
	private int index;

}
