package gui;

import java.util.ArrayList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;
import java.awt.Rectangle;

import models.*;
import models.Matrix.*;
import models.Container;
import geometry.*;

public class ContainerPanel extends JPanel
{
	public static class LinePair
	{
		public LinePair (DoubleMatrix p1, DoubleMatrix p2)
		{
			mP1 = p1;
			mP2 = p2;
		}
		
		public DoubleMatrix getFirst()
		{
			return mP1;
		}
		
		public DoubleMatrix getSecond()
		{
			return mP2;
		}
		
		DoubleMatrix mP1, mP2;
	}
	
	public class RotationZoomListener 	extends MouseAdapter
										implements MouseWheelListener, MouseMotionListener
	{
		public RotationZoomListener() 
		{
			mZoomSensitivity = 1.0;
			mRotationSensitivity = 1.0;
		}
		
		public RotationZoomListener (double zoomSensitivity, double rotationSensitivity)
		{
			mZoomSensitivity = zoomSensitivity;
			mRotationSensitivity = rotationSensitivity;
		}
		
		public void setZoomSensitivity (double zoomSens)
		{
			mZoomSensitivity = zoomSens;
		}
		
		public void setRotationSensitivity (double rotationSens)
		{
			mRotationSensitivity = rotationSens;
		}
		
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) 
		{
			int rotation = e.getWheelRotation();
			if (rotation < 0)
				mCamera.changeRadius(rotation * mZoomSensitivity);
		}
		
		@Override
		public void mousePressed (MouseEvent e)
		{
			mPrevMouseLocation = e.getLocationOnScreen();
		}
		
		@Override
		public void mouseReleased (MouseEvent e)
		{
			mPrevMouseLocation = null;
		}

		@Override
		public void mouseDragged(MouseEvent e) 
		{
			Point currentLocation = e.getLocationOnScreen();
			if (mPrevMouseLocation != null)
			{
				double distX = currentLocation.getX() - mPrevMouseLocation.getX();
				double distY = currentLocation.getY() - mPrevMouseLocation.getY();
				mCamera.moveX2 (mRotationSensitivity * distY);
				mCamera.moveX3 (mRotationSensitivity * distX);
			}
			mPrevMouseLocation = currentLocation;
			setAxisRotation();
			repaint(getBounds());
		}
		
		public double mZoomSensitivity, mRotationSensitivity;
		public Point mPrevMouseLocation;
	}
	
	public ContainerPanel (Container c)
	{
		mConnectingLines = c.getConnectingLines();
		mScreenMapping = new DoubleMatrix(3, 3);
		mAxisRotation = new DoubleMatrix (3, 3);
		mAxisRotation.getScalarMatrix(1.0, mAxisRotation);
		
		IntegerMatrix initCamPos = new IntegerMatrix(3, 1);
		int maxSide = Math.max(Math.max (c.getDimensions(0), c.getDimensions(1)), c.getDimensions(2));
		initCamPos.setCell(0, 0, maxSide);
		initCamPos.setCell(1, 0, c.getDimensions(1) / 2);
		initCamPos.setCell(2, 0, c.getDimensions(2) / 2);
		mCameraPosition = new Glue (initCamPos);
		mCamera = new Camera(0, 0, 1);
	}
	
	public static Rectangle2D getImageArea (ArrayList <LinePair> lines, int coordOffset)
	{
		double[] min = new double[2], max = new double[2];
		for (int cDim = 0; cDim < 2; ++cDim)
		{
			for (LinePair line : lines)
			{
				if (line.getFirst().getCell(cDim + coordOffset, 0) < min[cDim])
					min[cDim] = line.getFirst().getCell (cDim + coordOffset, 0);
				if (line.getFirst().getCell(cDim, 0) > max[cDim])
					max[cDim] = line.getFirst().getCell (cDim + coordOffset, 0);
				if (line.getSecond().getCell (cDim + coordOffset, 0) < min[cDim])
					min[cDim] = line.getSecond().getCell (cDim + coordOffset, 0);
				else if (line.getSecond().getCell (cDim + coordOffset, 0) > max[cDim])
					max[cDim] = line.getSecond().getCell (cDim + coordOffset, 0);
			}
		}
		return new Rectangle2D.Double(min[0], min[1], max[0] - min[0], max[1] - min[1]);
	}
	
	public void paintComponent (Graphics g)
	{
		super.paintComponent(g);
		//compute transformation matrix mapping any point to the respective point on the screen
		//considering the rotation of the axis
		/*DoubleMatrix totalTransformation = new DoubleMatrix (3, 4);
		getProjectionMatrix().multiply(mAxisRotation, totalTransformation);*/
		DoubleMatrix totalTransformation = getProjectionMatrix();
		ArrayList <LinePair> pairs = processLines(totalTransformation);
		
		Rectangle2D imgFrame = getImageArea(pairs, 1);
		adjustPixelMapping (imgFrame.getWidth(), imgFrame.getHeight());
		adjustCentering (imgFrame);
		setAxisRotation();
		
		Graphics2D g2 = (Graphics2D) g;
		
		DoubleMatrix totTrans = getProjectionMatrix();
		DoubleMatrix result = new DoubleMatrix (totTrans.getRows(), totTrans.getColumns());
		mScreenMapping.multiply(totTrans, result);
		totTrans = result.clone();
		mAxisRotation.multiply (totTrans, result);
		totTrans = result.clone();
		ArrayList<LinePair> pairsInc = processLines (totTrans);
		
		for (LinePair p : pairsInc)
		//for (LinePair p : pairs)
		{
			int rows = p.getFirst().getRows(), cols = p.getFirst().getColumns();
			
			
			
			/*DoubleMatrix first = new DoubleMatrix (rows, cols), second = new DoubleMatrix (rows, cols);
			mScreenMapping.multiply(p.getFirst(), first);
			mScreenMapping.multiply(p.getSecond(), second);*/
			
			DoubleMatrix first = p.getFirst(), second = p.getSecond();
			
			int x1, x2, y1, y2;
			x1 = first.getCell(1, 0).intValue();
			y1 = first.getCell(2, 0).intValue();
			x2 = second.getCell(1, 0).intValue();
			y2 = second.getCell(2, 0).intValue();
			System.out.println ("drew line " + x1 + "|" + y1 + " to " + x2 + "|" + y2);
			g2.drawLine(x1, y1, x2, y2);
		}
		System.out.println ("done drawing");
	}
	
	private ArrayList <LinePair> processLines (DoubleMatrix transformation)
	{
		int coordOffsetIndex = 0, endCoords = 3;
		ArrayList <LinePair> transformed = new ArrayList<LinePair>();
		for (Line l : mConnectingLines)
		{
			DoubleMatrix first = processPoint (l.getFirst().toDoubleMatrix(), transformation, coordOffsetIndex, endCoords);
			DoubleMatrix second = processPoint (l.getSecond().toDoubleMatrix(), transformation, coordOffsetIndex, endCoords);
			//@Todo let transformation map to x1 = 1
			/*
			first.setCell(0, 0, 1.0);
			second.setCell(0, 0, 1.0);
			*/
			transformed.add (new LinePair (first, second));
		}
		return transformed;
	}
	
	private DoubleMatrix processPoint (DoubleMatrix p, DoubleMatrix transformation, int obtainIndex, int newRows)
	{
		//3x1
		DoubleMatrix transformed = new DoubleMatrix (p.getRows(), p.getColumns());
		//4x1
		DoubleMatrix homP = new DoubleMatrix (p.getRows() + 1, p.getColumns());
		homP.copyValues(p, 0, 0, 0, 0, p.getRows(), p.getColumns());
		homP.setCell(homP.getRows() - 1, 0, 1.0);
		transformation.multiply(homP, transformed);
		
		DoubleMatrix returnVal = new DoubleMatrix(newRows, transformed.getColumns());
		returnVal.copyValues(transformed, 0, 0, obtainIndex, 0, newRows, transformed.getColumns());
		return returnVal;
	}
	
	private DoubleMatrix getProjectionMatrix()
	{
		
		DoubleMatrix t = new DoubleMatrix(3, 4);
		t.setCell (0, 3, 1.0);
		t.setCell (1, 0, -(double)mCameraPosition.getPosition(1) / mCameraPosition.getPosition(0));
		t.setCell (1, 1, 1.0);
		t.setCell (2, 0, -(double)mCameraPosition.getPosition (2) / mCameraPosition.getPosition (0));
		t.setCell (2, 2, 1.0);
		return t;
	}
	
	private void setAxisRotation()
	{
		Matrix<Double> rotMat = BasicShape.rotationMatrix(mCamera.getAngleX2(), mCamera.getAngleX3());
		mAxisRotation.copyValues(rotMat, 0, 0, 0, 0, rotMat.getRows(), rotMat.getColumns());
	}
	
	private void adjustPixelMapping (double imageWidth, double imageHeight)
	{
		double pixelPerUnit = Math.min (getWidth() / imageWidth, getHeight() / imageHeight);
		DoubleMatrix mapScalar = new DoubleMatrix (2, 2);
		mapScalar.getScalarMatrix(pixelPerUnit, mapScalar);
		mScreenMapping.copyValues(mapScalar, 1, 1, 0, 0, mapScalar.getRows(), mapScalar.getColumns());
	}
	
	private void adjustCentering (Rectangle2D imgArea)
	{
		double scalar = mScreenMapping.getCell(2, 2);
		double transX = (getWidth() - scalar * imgArea.getWidth()) / 2 - scalar * imgArea.getMinX();
		double transY = (getHeight() - scalar * imgArea.getHeight()) / 2 - scalar * imgArea.getMinY();
		mScreenMapping.setCell(1, 0, transX);
		mScreenMapping.setCell(2, 0, transY);
	}
	
	private ArrayList <Line> mConnectingLines;
	private Matrix<Double> mScreenMapping, mAxisRotation;
	private Camera mCamera;
	private Glue mCameraPosition;
	
}
