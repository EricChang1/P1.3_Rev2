package testing.algoTest;

import java.util.*;

import models.BasicShape;
import models.Block;
import models.Container;
import models.Resource;
import models.Matrix.*;
import algorithm.*;

public class AlgoTest 
{
	public static void main(String[] args)
	{
		new AlgoTest();
	}
	
	public AlgoTest()
	{
		mResources = new ArrayList<Resource>();
		
		BasicShape b1 = Container.constructInitShape(2, 2, 3);
		BasicShape b2 = Container.constructInitShape (2, 4, 4);
		BasicShape b3 = Container.constructInitShape(4, 3, 2);
		mResources.add(new Resource(new Block (b1, 4, ""), 10, 1, false));
		mResources.add(new Resource(new Block (b2, 6, ""), 10, 1, false));
		mResources.add(new Resource(new Block (b3, 5, ""), 10, 1, false));
		mCont = new Container (15, 10, 10);
		
		/*
		mClimber = new HillClimber(new HighestValueVolume(), new MaximumNumberPieces());
		mClimber.init(mCont, mResources);
		IntegerMatrix startingPoint = new IntegerMatrix (3, 1);
		startingPoint.setCell(0, 0, 0);
		startingPoint.setCell(1, 0, 2);
		startingPoint.setCell(2, 0, 2);
		assert (mCont.checkPositionInside(new Glue (startingPoint)));
		mClimber.setStartingPosition(new Glue (startingPoint));
		mClimber.run();
		mClimber.isAlgoDone();*/
		
		mRandom = new RandomAlgo();
		mRandom.init(mCont, mResources);
		mRandom.run();
		System.out.println ("random score " + mRandom.getFilledContainer().getValue());
	}
	
	ArrayList <Resource> mResources;
	Container mCont;
	HillClimber mClimber; 
	RandomAlgo mRandom;
}
