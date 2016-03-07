package testing.algoTest;

import java.util.*;

import models.BasicShape;
import models.Block;
import models.Container;
import models.MakePieces;
import models.Resource;
import models.Pieces;
import algorithm.*;

public class GreedyAlgoTest 
{
	public static void main(String[] args)
	{
		new GreedyAlgoTest();
	}
	
	public GreedyAlgoTest()
	{
		mResources = new ArrayList<Resource>();
		
		//Parcels
		//BasicShape b1 = Container.constructInitShape(3, 3, 3);
		//mResources.add(new Resource(new Block (b1, 1), 0, 27, false, MakePieces.getC(), Resource.BlockType.PARCEL));
		//BasicShape b2 = Container.constructInitShape(2, 2, 4);
		//mResources.add(new Resource(new Block (b2, 1), 0, 16, false, MakePieces.getA(),Resource.BlockType.PARCEL));
		//BasicShape b3 = Container.constructInitShape(2, 3, 4);
		//mResources.add(new Resource(new Block (b3, 1), 1, 16, true, MakePieces.getB(),Resource.BlockType.PARCEL));
		
		//Pents
		Block T = Pieces.createTBlock();
		mResources.add(new Resource(T, 7, 5, true, MakePieces.getT(), Resource.BlockType.PENT, MakePieces.getTFilled(), MakePieces.getTPos() ));
		Block P = Pieces.createPBlock();
		mResources.add(new Resource(P, 8, 5, true, MakePieces.getP(), Resource.BlockType.PENT, MakePieces.getPFilled(), MakePieces.getPPos() ));
		Block L = Pieces.createLBlock();
		mResources.add(new Resource(L, 20, 5, true, MakePieces.getL(), Resource.BlockType.PENT, MakePieces.getLFilled(), MakePieces.getLPos() ));
		
		
		mCont = new Container (33, 5, 8);
		
		greedy = new GreedyAlgorithm(e,mResources);
		greedy.init(mCont, mResources);
		greedy.run();
		
		System.out.println ("greedy score " + greedy.getFilledContainer().getValue());
		System.out.println ("Blocks " + greedy.getFilledContainer().getAmountOfBlocks());
		System.out.println ("Volume " + greedy.getFilledContainer().getVolume());
		
	}
	
	public Container getCont()
	{
		return mCont;
	}
	
	ArrayList <Resource> mResources;
	Container mCont;
	GreedyAlgorithm greedy;
	private static MaximumDensity e = new MaximumDensity();
}
