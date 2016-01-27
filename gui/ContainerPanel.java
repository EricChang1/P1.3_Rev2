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
		}
		
		public double mZoomSensitivity, mRotationSensitivity;
		public Point mPrevMouseLocation;
	}
	
	public ContainerPanel (Container c)
	{
		mConnectingLines = c.getConnectingLines();
		mScreenMapping = new DoubleMatrix(2, 2);
		mAxisRotation = new DoubleMatrix (4, 4);
		mAxisRotation.getScalarMatrix(1.0, mAxisRotation);
		
		IntegerMatrix initCamPos = new IntegerMatrix(3, 1);
		int maxSide = Math.max(Math.max (c.getDimensions(0), c.getDimensions(1)), c.getDimensions(2));
		initCamPos.setCell(0, 0, maxSide);
		initCamPos.setCell(1, 0, c.getDimensions(1) / 2);
		initCamPos.setCell(2, 0, c.getDimensions(2) / 2);
		mCameraPosition = new Glue (initCamPos);
		mCamera = new Camera(0, 0, 1);
		
		DoubleMatrix totalTransformation = new DoubleMatrix (3, 4);
		getProjectionMatrix().multiply (mAxisRotation, totalTransformation);
		adjustPixelMapping (processLines (totalTransformation));
	}
	
	
	public void paint (Graphics g)
	{
		//compute transformation matrix mapping any point to the respective point on the screen
		//considering the rotation of the axis
		DoubleMatrix totalTransformation = new DoubleMatrix (3, 4);
		getProjectionMatrix().multiply(mAxisRotation, totalTransformation);
		ArrayList <LinePair> pairs = processLines(totalTransformation);
		
		Graphics2D g2 = (Graphics2D) g;
		
		
		for (LinePair p : pairs)
		{
			mScreenMapping.multiply(p.getFirst(), p.getFirst());
			mScreenMapping.multiply(p.getSecond(), p.getSecond());
			
			int x1, x2, y1, y2;
			x1 = p.getFirst().getCell(0, 0).intValue();
			y1 = p.getFirst().getCell(1, 0).intValue();
			x2 = p.getSecond().getCell(0, 0).intValue();
			y2 = p.getSecond().getCell(1, 0).intValue();
			g2.drawLine(x1, y1, x2, y2);
		}
	}
	
	private ArrayList <LinePair> processLines (DoubleMatrix transformation)
	{
		int coordOffsetIndex = 1;
		ArrayList <LinePair> transformed = new ArrayList<LinePair>();
		for (Line l : mConnectingLines)
		{
			DoubleMatrix first = l.getFirst().toDoubleMatrix();
			DoubleMatrix second = l.getSecond().toDoubleMatrix();
			transformation.multiply(first, first);
			transformation.multiply(second, second);
			//reduce to 2D vector
			DoubleMatrix first2D, second2D;
			first2D = new DoubleMatrix (2, 1);
			second2D = new DoubleMatrix (2, 1);
			first2D.copyValues(first, 0, 0, coordOffsetIndex, 0, 2, 1);
			second2D.copyValues(second, 0, 0, coordOffsetIndex, 0, 2, 1);
			transformed.add(new LinePair (first2D, second2D));
		}
		return transformed;
	}
	
	private DoubleMatrix getProjectionMatrix()
	{
		
		DoubleMatrix t = new DoubleMatrix(3, 4);
		t.setCell (1, 0, -mCameraPosition.getPosition(1) / mCameraPosition.getPosition(0));
		t.setCell (1, 1, 1.0);
		t.setCell (2, 0, -mCameraPosition.getPosition (2) / mCameraPosition.getPosition (0));
		t.setCell (2, 2, 1.0);
		return t;
	}
	
	private void setAxisRotation()
	{
		Matrix<Double> rotMat = BasicShape.rotationMatrix(mCamera.getAngleX2(), mCamera.getAngleX3());
		mAxisRotation.copyValues(rotMat, 0, 0, 0, 0, rotMat.getRows(), rotMat.getColumns());
	}
	
	private void adjustPixelMapping (ArrayList <LinePair> lines)
	{
		double max = 0.0;
		for (LinePair line : lines)
		{
			for (int cDim = 0; cDim < line.getFirst().getRows(); ++cDim)
			{
				if (max < line.getFirst().getCell(cDim, 0))
					max = line.getFirst().getCell(cDim, 0);
				if (max < line.getSecond().getCell(cDim, 0))
					max = line.getSecond().getCell(cDim, 0);
			}
		}
		
		double pixelPerUnit = Math.max(getWidth(), getHeight()) / max;
		mScreenMapping.getScalarMatrix(pixelPerUnit, mScreenMapping);
	}
	
	private ArrayList <Line> mConnectingLines;
	private Matrix<Double> mScreenMapping, mAxisRotation;
	private Camera mCamera;
	private Glue mCameraPosition;
	
}
