package geometry;


import java.util.ArrayList;

import models.Position;
import models.Matrix.*;



/**
 * performs intersection checking and stores results
 * @author martin
 */
public class IntersectionSolver 
{
	public static enum Result {INCONSISTENT, ONE, INFINITE}
	
	/**
	 * custom exception class for intersection solver
	 * @author martin
	 */
	@SuppressWarnings("serial")
	public static class IntersectionSolverException extends IllegalStateException
	{
		public IntersectionSolverException() {}
		
		public IntersectionSolverException (String message) { super (message); }
	}
	
	/**
	 * @param s a given geometric shape
	 * @param p a point represented as a double matrix
	 * @return true if p is within s
	 */
	public static boolean solveWithin (GeoShape s, DoubleMatrix p)
	{
		int numVectors = s.getVectors().size();
		double[] maxScalar = new double[numVectors];
		//compute max scalar using first dimension
		for (int cNumVec = 0; cNumVec < numVectors; ++cNumVec)
		{
			int cCoord = 0;
			boolean found = false;
			do
			{
				double dist = s.getSecond().getCell (cCoord, 0) - s.getFirst().getCell (cCoord, 0);
				double vecCoord = s.getVectors().get (cNumVec).getCell (cCoord, 0);
				if (!GaussElim.epsilonEquals (vecCoord, 0.0))
				{
					maxScalar[cNumVec] = dist / vecCoord;
					found = true;
				}
				else
					++cCoord;
			}
			while (!found);
		}
		
		//standard form for rect
		//q + u + v = p => u + v = p - q
		DoubleMatrix wMat = s.loadEquationMatrix();
		for (int cRow = 0; cRow < wMat.getRows(); ++cRow)
			wMat.setCell (cRow, wMat.getColumns() - 1, p.getCell (cRow, 0) - wMat.getCell (cRow, wMat.getColumns() - 1));
		//solve augmented matrix
		GaussElim solve = new GaussElim (wMat);
		solve.run();
		//true if system is consistent and 0 <= scalar <= maxScalar
		if (solve.isConsistent())
		{ 
			DoubleMatrix solution = solve.getTranslationVector();
			for (int cNumVec = 0; cNumVec < numVectors; ++cNumVec)
			{
				double scalar = solution.getCell (cNumVec, 0);
				//if scalar is less/(equal) to 0 or scalar is larger/(equals) to max				
				if (s.isFirstIncluded() && !GaussElim.epsilonEquals (scalar, 0.0) && scalar < 0.0)
					return false;
				else if (!s.isFirstIncluded() && (scalar < 0.0 || GaussElim.epsilonEquals (scalar, 0.0)))
					return false;
				if (s.isSecondIncluded() && !GaussElim.epsilonEquals (scalar, maxScalar[cNumVec]) && scalar > maxScalar[cNumVec])
					return false;
				else if (!s.isSecondIncluded() && (scalar > maxScalar[cNumVec] || GaussElim.epsilonEquals (maxScalar[cNumVec], scalar)))
					return false;
			}
		}
		return true;
	}
	
	/**
	 * constructor
	 * @param l1 line 1
	 * @param l2 line 2
	 */
	public IntersectionSolver (GeoShape s1, GeoShape s2)
	{
		mS1 = s1;
		mS2 = s2;
		mScalars = new ArrayList<Double>();
		solve();
	}
	
	/**
	 * @return position of intersection
	 * @throws IntersectionSolverException if there is no intersection
	 */
	public DoubleMatrix getIntersection () throws IntersectionSolverException
	{
		if (mSolutionType == Result.INCONSISTENT)
			throw new IntersectionSolverException ("non solvable linear equations");
		else if (mSolutionType == Result.INFINITE)
			throw new IntersectionSolverException ("system has infinite many solutions");
		
		DoubleMatrix p = mS1.getFirst().toDoubleMatrix();
		ArrayList <DoubleMatrix> vecs = mS1.getVectors();
		for (int cDim = 0; cDim < mS1.getDimension(); ++cDim)
		{
			for (int cVec = 0; cVec < vecs.size(); ++cVec)
				p.setCell(cDim, 0, p.getCell(cDim, 0) + mScalars.get(cVec) * vecs.get(cVec).getCell(cDim,  0));
		}
		
		
		return p;
	}
	
	
	public Result getSolutionType()
	{
		return mSolutionType;
	}
	
	
	public ArrayList<Double> getScalars()
	{
		return (ArrayList<Double>) mScalars.clone();
	}
	
	
	public ArrayList <GeoShape> getShapes()
	{
		ArrayList <GeoShape> shapes = new ArrayList<GeoShape>();
		shapes.add (mS1);
		shapes.add (mS2);
		return shapes;
	}
	
	public boolean isWithinBounds()
	{
		if (mSolutionType == Result.INCONSISTENT)
			throw new IntersectionSolverException ("non solvable linear equations");
		return mOnline;
	}
	
	private void solve()
	{
		DoubleMatrix eq = mS1.loadEquationMatrix(mS2);
		GaussElim solver = new GaussElim(eq);
		solver.run();
		if (solver.isConsistent() && solver.allBasicVariables())
		{
			mSolutionType = Result.ONE;
			DoubleMatrix solution = solver.getTranslationVector();
			for (int cVar = 0; cVar < solver.getNumberOfBasicVars(); ++cVar)
				mScalars.add (solution.getCell(cVar, 0));
			DoubleMatrix inter = getIntersection();
			
			
			mOnline = solveWithin (mS1, inter) && solveWithin (mS2, inter);
			
		}
		else if (!solver.isConsistent())
			mSolutionType = Result.INCONSISTENT;
		else
		{
			mSolutionType = Result.INFINITE;
			mOnline = mS1.doesRangeOverlap(mS2) || mS2.doesRangeOverlap(mS1);
		}
		mGelim = solver;
	}
	
	private GaussElim mGelim;
	private GeoShape mS1, mS2;
	private ArrayList <Double> mScalars;
	private Result mSolutionType;
	private boolean mOnline;
}
