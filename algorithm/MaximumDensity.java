package algorithm;

import java.util.ArrayList;
import models.*;
import models.Resource.BlockType;

public class MaximumDensity implements EvaluationHeuristic{
	
	public MaximumDensity(){
	}

	@Override
	public double getScore(Resource res, Position pos, int state){
		
		updateVariables(res, pos, state);
		int CubeVolume = (x*y*z);
		if(CubeVolume<score)
		{
			score = CubeVolume;
		}
		return score;
	}
	
	public void updateVariables(Resource res, Position pos, int state)
	{
		Block block = res.getRot().get(state);
		int Bx = block.getDimensions(0);
		int By = block.getDimensions(1);
		int Bz = block.getDimensions(2);
		int Px = pos.getPosition().get(0);
		int Py = pos.getPosition().get(1);
		int Pz = pos.getPosition().get(2);
		
		x = Bx+Px;
		y = By+Py;
		z = Bz+Pz;
		
		if (prevX>x)
			x=prevX;
		if (prevY>y)
			y=prevY;
		if (prevZ>z)
			z=prevZ;
	}
	
	//Call this method after placing block
	public void update(Resource res, Position pos, BlockType type, int state)
	{
		score=Integer.MAX_VALUE;
		updateVariables(res, pos, state);
		prevX = x;
		prevY = y;
		prevZ = z;
		if (type == Resource.BlockType.PARCEL)
		{
		fillFilled(res, pos, state);
		}
		if (type == Resource.BlockType.PENT)
		{
		pfillFilled(res, pos, state);
		}
	}
	
	public Position freePos(Container truck)
	{
		for (int i=freeX; i<truck.getDimensions(0); i++)
		{
			for(int j=freeY;j<truck.getDimensions(1);j++)
			{
				for(int k=freeZ;k<truck.getDimensions(2);k++)
				{	
					if (containsPos(i, j, k)==false)
					{
						ArrayList<Integer> Pos = new ArrayList<Integer>();
						Pos.add(i);Pos.add(j);Pos.add(k);
						Position pos = new Position(Pos);
						freeX=i; freeY=j;freeZ=k;
						return pos;						 
					}
				}
				freeZ=0;
			}
			freeY=0;
		}
		
		return null;
	}
	
	public boolean checkPos(Position pos, Block block, Container container)
	{
		int Bx = block.getDimensions(0);
		int By = block.getDimensions(1);
		int Bz = block.getDimensions(2);
		int Px = pos.getPosition().get(0);
		int Py = pos.getPosition().get(1);
		int Pz = pos.getPosition().get(2);
		
		int tempX = Bx+Px;
		int tempY = By+Py;
		int tempZ = Bz+Pz;
		//Initial check if the piece goes out of bounds
		if (tempX>container.getDimensions(0)||tempY>container.getDimensions(1)||tempZ>container.getDimensions(2))
		{
			return false;
		}
		//check overlap between placed pieces and new block
		
		for (int i=Px; i<tempX; i++)
		{
			for (int j=Py; j<tempY;j++)
			{
				for (int k=Pz;k<tempZ;k++)
				{
					if (containsPos(i, j, k)==true)
					{
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public boolean pentCheckPos(Resource res, int state, Position pos, Container container)
	{
		Block block = res.getRot().get(state);
		int Bx = block.getDimensions(0);
		int By = block.getDimensions(1);
		int Bz = block.getDimensions(2);
		int Px = pos.getPosition().get(0);
		int Py = pos.getPosition().get(1);
		int Pz = pos.getPosition().get(2);
		
		int tempX = Bx+Px;
		int tempY = By+Py;
		int tempZ = Bz+Pz;
		//Initial check if the piece goes out of bounds
		if (tempX>container.getDimensions(0)||tempY>container.getDimensions(1)||tempZ>container.getDimensions(2))
		{
			return false;
		}
		
		for (int i =0; i<5; i++)
		{
			for (int j=0; j<3;j++)
			{
				int x = res.getCells().get(i).get(j).get(0)+pos.getPosition(0);
				int y = res.getCells().get(i).get(j).get(1)+pos.getPosition(1);
				int z = res.getCells().get(i).get(j).get(2)+pos.getPosition(2);			
				if (containsPos(x, y, z)==true)
				{
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean containsPos(int i, int j, int k)
	{
		boolean bool = false;
		if (filled==null)
		{
			return false;
		}
		for (int l=0; l<filled.size();l++)
		{
			int X = filled.get(l).get(0);
			int Y = filled.get(l).get(1);
			int Z = filled.get(l).get(2);

			if (i==X&&j==Y&&k==Z)
			{
				return true;
			}
		}
		return bool;	
	}
	public void fillFilled(Resource res, Position pos, int state)
	{
		Block block = res.getRot().get(state);
		int Bx = block.getDimensions(0);
		int By = block.getDimensions(1);
		int Bz = block.getDimensions(2);
		int Px = pos.getPosition().get(0);
		int Py = pos.getPosition().get(1);
		int Pz = pos.getPosition().get(2);
		
		int tempX = Bx+Px;
		int tempY = By+Py;
		int tempZ = Bz+Pz;
		
		for (int i=Px; i<tempX; i++)
		{
			for (int j=Py; j<tempY;j++)
			{
				for (int k=Pz;k<tempZ;k++)
				{
					ArrayList<Integer>Pos = new ArrayList<Integer>();
					Pos.add(i);Pos.add(j);Pos.add(k);
					filled.add(Pos);
				}
			}
		}
	}
	public void pfillFilled(Resource res, Position pos, int state)
	{
		for (int i=0; i<5;i++)
		{
			
			ArrayList<Integer> adjFilled = new ArrayList<Integer>();
			int X = pos.getPosition(0);int Y = pos.getPosition(1);int Z = pos.getPosition(2);
			int newX = res.getCells().get(state).get(i).get(0)+X; 
			int newY = res.getCells().get(state).get(i).get(1)+Y;
			int newZ = res.getCells().get(state).get(i).get(2)+Z;
			adjFilled.add(newX);adjFilled.add(newY);adjFilled.add(newZ); 
			filled.add(adjFilled);
		}
	}
	
	public void nextPos()
	{
		freeZ++;
	}

	private int score=Integer.MAX_VALUE;
	private int x, y, z;
	private int freeX, freeY, freeZ;
	private int prevX, prevY, prevZ;
	private ArrayList<ArrayList<Integer>> filled = new ArrayList<ArrayList<Integer>>();
	
}
