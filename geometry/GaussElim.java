package geometry;

import java.util.ArrayList;

import generic.QuickSort;
import models.Matrix.*;


/**
 * solves systems of linear equations
 * for augmented matrices
 * @author martin
 *
 */
public class GaussElim implements Runnable
{
	//double matrix uses 5 digit precision
	public static final double EPSILON = 0.001;
	public static boolean mDebug = false;
	
	/**
	 * @param d1 double type
	 * @param d2 double type
	 * @return true if d2 is greater than d1 - EPSILON and less than d1 + EPSILON
	 */
	public static boolean epsilonEquals (double d1, double d2)
	{
		return (d2 >= d1 - EPSILON && d2 <= d1 + EPSILON);
	}
	
	/**
	 * @param less double type to be less
	 * @param larger double type to be larger
	 * @return true if less < larger + EPSILON
	 */
	public static boolean epsilonLess (double less, double larger)
	{
		return (less < larger + EPSILON);
	}
	
	/**
	 * testing main method
	 * @param args
	 */
	public static void main (String[] args)
	{
		DoubleMatrix m = new DoubleMatrix(2, 3);
		m.setCell(0, 0, 2.0);
		m.setCell(0, 1, 1.0);
		m.setCell(0, 2, 32.0);
		m.setCell(1, 0, 1.0);
		m.setCell(1, 2, 9.0);
		GaussElim g = new GaussElim(m);
		g.run();
		g.mMat.print(System.out);
		System.out.println ("all basic " + g.allBasicVariables());
		System.out.println ("consistent " + g.isConsistent());
	}
	
	/**
	 * swaps elements in r, p given by i1, i2
	 * @param r array representation of matrix
	 * @param p array of mPivots
	 * @param i1 row 1
	 * @param i2 row 2
	 */
	public static void swap (Double[][] r, int[] p, int i1, int i2)
	{
		Double[] rtemp = r[i1];
		int ptemp = p[i1];
		r[i1] = r[i2];
		p[i1] = p[i2];
		r[i2] = rtemp;
		p[i2] = ptemp;
	}
	
	/**
	 * Quick sort algorithm, sorting rows such that rows with a low pivot index are first
	 * @param rows array representation of matrix
	 * @param pivotIndex array of pivot indices
	 * @param start row to start sorting at
	 * @param end row to end sorting at
	 */
	public static void quickSort (Double[][] rows, int[] pivotIndex, int start, int end)
	{
		assert (start >= 0 && end < rows.length && start < end);
		int cLess = start, cLarger = end - 1;//pivot is always to the right
		
		while (cLess < cLarger)
		{
			while (cLess <= cLarger && pivotIndex[cLess] < pivotIndex[end])
				++cLess;
			while (cLess < cLarger && pivotIndex[cLarger] >= pivotIndex[end])
				--cLarger;
			if (cLess < cLarger)
			{
				swap (rows, pivotIndex, cLess, cLarger);
			}
		}
		if (pivotIndex[cLess] > pivotIndex[end])
			swap (rows, pivotIndex, cLess, end);
		
		if (cLess - 1 > start)
			quickSort (rows, pivotIndex, start, cLess - 1);
		if (cLess + 1 < end)
			quickSort (rows, pivotIndex, cLess + 1, end);
	}
	
	/**
	 * constructor
	 * @param m augmented matrix to perform algo for
	 */
	public GaussElim (DoubleMatrix m)
	{
		mMat = m.clone();
		mOrig = m.clone();
		mPivots = findmPivots();
	}
	
	/**
	 * runs the complete algo
	 */
	public void run()
	{
		if (mDebug)
		{
			System.out.println ("Initial");
			mMat.print(System.out);
		}
		order();
		if (mDebug)
		{
			System.out.println ("ordered");
			mMat.print(System.out);
		}
		forward();
		if (mDebug)
		{
			System.out.println ("forward");
			mMat.print(System.out);
		}
		backward();
		if (mDebug)
		{
			System.out.println ("backward");
			mMat.print(System.out);
		}
		scaleToOne();
		if (mDebug)
		{
			System.out.println ("scaled");
			mMat.print(System.out);
		}
	}
	
	/**
	 * @return list of vectors spanning the solution set
	 */
	public ArrayList <DoubleMatrix> getSolutionVectors()
	{
		ArrayList <DoubleMatrix> vecs = new ArrayList <DoubleMatrix>();
		
		for (int cCol = mPivots[0]; cCol < mMat.getColumns() - 1; ++cCol)
		{
			DoubleMatrix vec = new DoubleMatrix (mMat.getRows(), 1);
			for (int cRow = 0; cRow < mMat.getRows(); ++cRow)
			{
				if (mPivots[cRow] < cCol && !mMat.getCell(cRow, cCol).equals(0))
				{
					double coeff = mMat.getCell(cRow, cCol);
					for (int cDim = 0; cDim < mMat.getRows(); ++cDim)
						vec.setCell(cDim, 0, vec.getCell (cDim, 0) + coeff * mOrig.getCell(cRow, cCol));
				}
			}
			if (!vec.equals (new DoubleMatrix (mMat.getRows(), 1)))
				vecs.add (vec);
		}
		
		
		return vecs;
	}
	
	/**
	 * @return the vector which translates the subspace of the solution set
	 */
	public DoubleMatrix getTranslationVector()
	{
		DoubleMatrix trans = new DoubleMatrix (Math.min (mMat.getColumns() - 1, mMat.getRows()), 1);
		int cVar = 0;
		for (int cRow = 0; cRow < mMat.getRows(); ++cRow)
		{
			//if there is a pivot
			if (mPivots[cRow] < mMat.getColumns())
			{
				trans.setCell (cVar, 0, mMat.getCell (cRow, mMat.getColumns() - 1));
				++cVar;
				if (cVar == trans.getRows())
					return trans;
			}
		}
		return trans;
	}
	
	/**
	 * @return a clone of the internal matrix
	 */
	public DoubleMatrix getMatrix()
	{
		return mMat.clone();
	}
	
	/**
	 * @return the number of free variables
	 */
	public int getNumberOfFreeVars()
	{
		return (mMat.getColumns() - 1 - getNumberOfBasicVars());
	}
	
	/**
	 * @return the number of basic variables
	 */
	public int getNumberOfBasicVars()
	{
		int cnt = 0;
		//row counter less than number of variables = number of columns - 1
		for (int cRow = 0; cRow < mMat.getRows(); ++cRow)
		{
			if (mPivots[cRow] < mMat.getColumns())
				++cnt;
		}
		return cnt;
	}
	
	/**
	 * @param startCol
	 * @param endCol
	 * @return true if all variables are basic variables
	 */
	public boolean allBasicVariables ()
	{
		return (getNumberOfFreeVars() == 0);
	}
	
	/**
	 * @return true if system is consistent
	 */
	public boolean isConsistent()
	{
		int cRow = mMat.getRows() - 1;
		while (mPivots[cRow] == mMat.getColumns() && cRow > 0)
			--cRow;
		return (mPivots[cRow] != mMat.getColumns() - 1);
	}
	
	/**
	 * @return array of pivot indices
	 */
	public int[] findmPivots()
	{
		int[] pivotIndex = new int[mMat.getRows()];
		for (int cRow = 0; cRow < mMat.getRows(); ++cRow)
		{
			int cCol = 0;
			while (cCol < mMat.getColumns() && mMat.getCell(cRow, cCol) == 0)
				++cCol;
			pivotIndex[cRow] = cCol;
		}
		return pivotIndex;
	}
	
	/**
	 * orders the rows of the matrix such that rows with low pivot indices are placed first
	 * @param mPivots array of pivot indices
	 */
	public void order()
	{
		/**
		 * wrapper class for row and pivot
		 * @author martin
		 */
		class Row implements Comparable<Row>
		{
			/**
			 * parametric constructor
			 * @param data row to store
			 * @param pivotIndex index of pivot in row
			 */
			public Row (Double[] data, int pivotIndex)
			{
				mData = data;
				mPivotIndex = pivotIndex;
			}
			
			/**
			 * @return row stored
			 */
			public Double[] getRow() { return mData; }
			
			/**
			 * @return pivot index stored
			 */
			public int getPivot() { return mPivotIndex; }
			
			/**
			 * @param comp row object to compare this row object to
			 * @return 	-1 if pivot index of this row < pivot index of comp
			 * 			0 if pivot index of this row == pivot index of comp
			 * 			1 if pivot index of this row > pivot index of comp
			 */
			public int compareTo (Row comp)
			{
				if (this.mPivotIndex < comp.mPivotIndex)
					return -1;
				else if (this.mPivotIndex == comp.mPivotIndex)
					return 0;
				else
					return 1;
			}
			
			private Double[] mData;
			private int mPivotIndex;
		}
		
		QuickSort<Row> sorter = new QuickSort<>();
		for (int cRow = 0; cRow < mMat.getRows(); ++cRow)
			sorter.add (new Row (mMat.getRow (cRow).clone(), mPivots[cRow]));
		
		sorter.sort (0, sorter.size() - 1);

		for (int cRow = 0; cRow < mMat.getRows(); ++cRow)
		{
			Double[] row = sorter.get (cRow).getRow();
			mPivots[cRow] = sorter.get (cRow).getPivot();
			for (int cCol = 0; cCol < mMat.getColumns(); ++cCol)
				mMat.setCell(cRow, cCol, row[cCol]);
		}
		
		int x = 2;
		x = x + 1;
		/* legacy
		Double[][] rows = new Double[mMat.getRows()][mMat.getColumns()];
		for (int cRow = 0; cRow < rows.length; ++cRow)
			rows[cRow] = mMat.getRow(cRow).clone();
		
		quickSort (rows, mPivots, 0, rows.length - 1);
		for (int cRow = 0; cRow < rows.length; ++cRow)
		{
			for (int cCol = 0; cCol < rows[cRow].length; ++cCol)
				mMat.setCell(cRow, cCol, rows[cRow][cCol]);
		}
		*/
	}
	
	/**
	 * performs forward step
	 * @param mPivots array of pivot indices
	 */
	public void forward()
	{
		int cRow = 0;
		while (cRow < mMat.getRows() - 1 && mPivots[cRow] < mMat.getColumns())
		{
			double pivot = mMat.getCell(cRow, mPivots[cRow]);
			for (int cRestRow = cRow + 1; cRestRow < mMat.getRows(); ++cRestRow)
			{
				double c = -mMat.getCell(cRestRow, mPivots[cRow]) / pivot;
				boolean beyondPivot = (mPivots[cRow] > mPivots[cRestRow]);
				for (int cRestCol = mPivots[cRow]; cRestCol < mMat.getColumns(); ++cRestCol)
				{
					boolean nonZero = (!mMat.getCell (cRestRow, cRestCol).equals (0.0));
					mMat.setCell (cRestRow, cRestCol, mMat.getCell(cRestRow, cRestCol) + c * mMat.getCell(cRow, cRestCol));
					if (epsilonEquals (mMat.getCell (cRestRow, cRestCol), 0.0))
						mMat.setCell (cRestRow, cRestCol, 0.0);
					//if element is zero and not past pivot then set pivot to current column + 1
					//set beyond pivot to true if current element is not zero
					if (!beyondPivot && mMat.getCell (cRestRow, cRestCol).equals (0.0))
						mPivots[cRestRow] = cRestCol + 1;
					else
						beyondPivot = true;
				}
			}
			++cRow;
		}
	}
	
	/**
	 * performs backward step
	 * @param mPivots array of pivot indices
	 */
	public void backward()
	{
		for (int cRow = mMat.getRows() - 1; cRow > 0; --cRow)
		{
			if (mPivots[cRow] < mMat.getColumns())
			{
				double pivot = mMat.getCell (cRow, mPivots[cRow]);
				for (int cRestRow = cRow - 1; cRestRow >= 0; --cRestRow)
				{
					double c = -mMat.getCell(cRestRow, mPivots[cRow]) / pivot;
					for (int cRestCol = mPivots[cRow]; cRestCol < mMat.getColumns(); ++cRestCol)
					{
						mMat.setCell(cRestRow, cRestCol, mMat.getCell(cRestRow, cRestCol) + c * mMat.getCell(cRow, cRestCol));
						if (epsilonEquals (mMat.getCell (cRestRow, cRestCol), 0.0))
							mMat.setCell (cRestRow, cRestCol, 0.0);
					}
				}
			}
		}
	}
	
	/**
	 * scales mPivots to 1
	 * @param mPivots array of pivot indices
	 */
	public void scaleToOne()
	{
		int cRow = 0;
		while (cRow < mMat.getRows() && mPivots[cRow] < mMat.getColumns())
		{
			double c = 1 / mMat.getCell(cRow, mPivots[cRow]);
			for (int cCol = mPivots[cRow]; cCol < mMat.getColumns(); ++cCol)
				mMat.setCell(cRow, cCol, mMat.getCell(cRow, cCol) * c);
			++cRow;
		}
	}
	
	private DoubleMatrix mMat, mOrig;
	private int[] mPivots;
}
