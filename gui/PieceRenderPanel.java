package gui;

import java.util.ArrayList;

import javax.swing.*;
import java.awt.*;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;
import java.awt.peer.PanelPeer;
import java.awt.Rectangle;

import models.*;
import models.Matrix.*;
import models.Container;


/**
 * class used to draw a container
 * @author martin
 */
public class PieceRenderPanel extends JPanel
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
	
	/**
	 * @param lines lines to draw on 2d plane
	 * @param coordOffset index of the first coordinate to use for drawing
	 * @return smallest rectangle comprising the entire drawing
	 */
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
	
	/**
	 * abstract class to be extended by classes that need to store a sensitivity value
	 * provides getter and setter for sensitivity value
	 * @author martin
	 */
	public abstract class InputListener
	{
		/**
		 * default constructor
		 */
		public InputListener()
		{
			mSensitivity = 1.0;
		}
		
		/**
		 * parametric constructor
		 * @param sensitivity sensitivity to be stored
		 */
		public InputListener (double sensitivity)
		{
			mSensitivity = sensitivity;
		}
		
		/**
		 * @return sensitivity value
		 */
		public double getSensitivity()
		{
			return mSensitivity;
		}
		
		/**
		 * @param sensitivity value to set sensitivity to
		 */
		public void setSensitivity (double sensitivity)
		{
			mSensitivity = sensitivity;
		}
		
		private double mSensitivity;
	}
	
	/**
	 * class used to listen to mouse wheel 
	 * in order to change the zooming level of the drawing
	 * @author martin
	 */
	public class ZoomListener extends InputListener implements MouseWheelListener
	{
		public ZoomListener () {}
		
		public ZoomListener (double sensitivity) {super (sensitivity); }
		
		public void mouseWheelMoved (MouseWheelEvent e)
		{
			int rotation = e.getWheelRotation();
			double rChange = Math.sqrt(mCamera.getRadius() + rotation * getSensitivity()) - Math.sqrt (mCamera.getRadius());
			mCamera.changeRadius(rChange);
			
			DoubleMatrix t = new DoubleMatrix (mProjection.getRows(), mAxisRotation.getColumns());
			mProjection.multiply(mAxisRotation, t);
			ArrayList <LinePair> vertPairs = processLines(t);
			Rectangle2D imgArea = getImageArea(vertPairs, 1);
			adjustPixelMapping(imgArea.getWidth(), imgArea.getHeight());
			adjustCentering(imgArea);
			repaint();
		}
	}
	
	/**
	 * class used to listen to listen to mouse clicks and mouse motion
	 * in order to determine whether to rotate the image or
	 * whether to reset the rotation
	 * @author martin
	 */
	public class RotationListener extends InputListener implements MouseListener, MouseMotionListener
	{
		public RotationListener() {}
		
		public RotationListener (double sensitivity) {super (sensitivity); }
		
		@Override
		public void mouseDragged(MouseEvent e) 
		{
			Point currentLocation = e.getLocationOnScreen();
			if (mPrevMouseLocation != null)
			{
				double distX = currentLocation.getX() - mPrevMouseLocation.getX();
				double distY = currentLocation.getY() - mPrevMouseLocation.getY();
				mCamera.moveX2 (getSensitivity() * distY);
				mCamera.moveX3 (-getSensitivity() * distX);
				//updateDebugOutput();
			}
			mPrevMouseLocation = currentLocation;
			setAxisRotation();
			repaint(getBounds());
		}

		@Override
		public void mouseClicked(MouseEvent e) 
		{
			if (e.getClickCount() == 2)
			{
				mCamera.moveX2 (180 - mCamera.getAngleX2());
				mCamera.moveX3 (-mCamera.getAngleX3());
				setAxisRotation();
				repaint();
				mPrevMouseLocation = null;
				//updateDebugOutput();
			}
			
		}

		@Override
		public void mouseReleased(MouseEvent e) 
		{
			mPrevMouseLocation = null;
		}
		
		@Override
		public void mouseExited(MouseEvent e) 
		{
			mPrevMouseLocation = null;
		}

		@Override
		public void mouseEntered(MouseEvent e) {}
		
		@Override
		public void mousePressed(MouseEvent e) 
		{
			mPrevMouseLocation = e.getLocationOnScreen();
		}
		
		@Override
		public void mouseMoved(MouseEvent e) {}
		
		private Point mPrevMouseLocation;
	}
	
	/**
	 * class used to listen to window resizing events
	 * in order to adjust the size of the image dynamically
	 * @author martin
	 */
	public class ResizeListener extends ComponentAdapter
	{
		
		public ResizeListener()
		{
			mFirst = false;
		}
		
		@Override
		public void componentResized(ComponentEvent e) 
		{
			DoubleMatrix t = new DoubleMatrix (mProjection.getRows(), mAxisRotation.getColumns());
			mProjection.multiply (mAxisRotation, t);
			
			ArrayList <LinePair> projected = processLines (t);
			Rectangle2D imgArea = getImageArea (projected, 1);
			adjustPixelMapping(imgArea.getWidth(), imgArea.getHeight());
			adjustCentering (imgArea);
			repaint();
		}
		
		public void componentShown (ComponentEvent e)
		{
			if (mFirst)
			{
				mFirst = false;
				init();
			}
		}
		
		private boolean mFirst;
	}
	
	/**
	 * parametric constructor
	 * @param c container to draw
	 */
	public PieceRenderPanel (BasicShape c)
	{	
		//construct gui
		setLayout(new GridBagLayout());
		//constructDebugComponents();
		setDebugOutput (true);
		//set viewing point position
		IntegerMatrix centerContPos = new IntegerMatrix (3, 1);
		centerContPos.setCell (0, 0, -c.getDimensions(0) / 2);
		centerContPos.setCell (1, 0, -c.getDimensions(1) / 2);
		centerContPos.setCell (2, 0, -c.getDimensions(2) / 2);
		c.glue(new Glue (centerContPos));
		
		//extract vertices and connections
		mVertices = new ArrayList<DoubleMatrix>();
		mConnections = new ArrayList <ArrayList <Integer>>();
		for (int cVertex = 0; cVertex < c.getNumberOfVertices(); ++cVertex)
		{
			mVertices.add (c.getVertex (cVertex).toDoubleMatrix());
			ArrayList <IntegerMatrix> conns = c.lookUpConnections(cVertex);
			ArrayList <Integer> connIndices = new ArrayList<Integer>();
			for (IntegerMatrix conn : conns)
				connIndices.add (c.getVertexIndex(conn));
			mConnections.add (connIndices);
		}
		
		//construct transformation matrices
		mScreenMapping = new DoubleMatrix(3, 3);
		mProjection = new DoubleMatrix (3, 4);
		mAxisRotation = new DoubleMatrix (4, 4);
		mAxisRotation.getScalarMatrix(1.0, mAxisRotation);
		//initialize camera
		IntegerMatrix initCamPos = new IntegerMatrix(3, 1);
		int maxSide = Math.max(Math.max (c.getDimensions(0), c.getDimensions(1)), c.getDimensions(2));
		initCamPos.setCell(0, 0, maxSide);
		initCamPos.setCell(1, 0, c.getDimensions(1) / 2);
		initCamPos.setCell(2, 0, c.getDimensions(2) / 2);
		mCameraPosition = new Glue (initCamPos);
		mCamera = new Camera(180, 0, 1);
		//initialize projection and rotation matrix
		setProjectionMatrix();
		setAxisRotation();
	}
	
	/**
	 * method to be called before a valid drawing can be made
	 * happens automatically if resize listener is added
	 * Precondition: size of the component is set
	 * Postcondition: drawing is adjusted for this size
	 */
	public void init ()
	{
		DoubleMatrix t = new DoubleMatrix (3, 4);
		mProjection.multiply(mAxisRotation, t);
		ArrayList <LinePair> projected = processLines (t);
		Rectangle2D imgRect = getImageArea(projected, 1);
		adjustPixelMapping(imgRect.getWidth(), imgRect.getHeight());
		adjustCentering(imgRect);
	}
	
	public void setVisible (boolean flag)
	{
		init();
	}
	
	public void paintComponent (Graphics g)
	{
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		
		ArrayList<LinePair> pairsInc = processLines (getTransformation());
		
		for (LinePair p : pairsInc)
		{
			int x1, x2, y1, y2;
			x1 = p.getFirst().getCell(1, 0).intValue();
			y1 = p.getFirst().getCell(2, 0).intValue();
			x2 = p.getSecond().getCell(1, 0).intValue();
			y2 = p.getSecond().getCell(2, 0).intValue();
			//System.out.println ("drew line " + x1 + "|" + y1 + " to " + x2 + "|" + y2);
			g2.drawLine(x1, y1, x2, y2);
		}
		//System.out.println ("done drawing");
	}
	
	private ArrayList <LinePair> processLines (DoubleMatrix transformation)
	{
		int coordOffsetIndex = 0, endCoords = 3;
		ArrayList <DoubleMatrix> transformed = new ArrayList<DoubleMatrix>();
		//transform vertices
		for (int cVertex = 0; cVertex < mVertices.size(); ++cVertex)
		{
			DoubleMatrix proc = processPoint (mVertices.get (cVertex), transformation, coordOffsetIndex, endCoords);
			transformed.add (proc);
		}
		ArrayList <LinePair> transLines = new ArrayList<LinePair>();
		//construct lines from transformed vertices
		for (int cVertex = 0; cVertex < transformed.size(); ++cVertex)
		{
			for (Integer connIndex : mConnections.get (cVertex))
				transLines.add (new LinePair (transformed.get(cVertex), transformed.get (connIndex)));
		}
		
		return transLines;
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
	
	private void constructDebugComponents()
	{
		JLabel angleLabelX2, angleLabelX3;
		angleLabelX2 = new JLabel ("X2 = ");
		angleLabelX3 = new JLabel ("X3 = ");
		mDbgAngleX2 = new JLabel();
		mDbgAngleX3 = new JLabel();
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0; c.anchor = GridBagConstraints.NORTH;
		add (angleLabelX2, c);
		c.gridx = 1;
		add (mDbgAngleX2, c);
		c.gridx = 0; c.gridy = 1;
		add (angleLabelX3, c);
		c.gridx = 1;
		add (mDbgAngleX3, c);
	}
	
	private void setDebugOutput (boolean flag)
	{
		for (Component comp : getComponents())
			comp.setVisible(flag);
	}
	
	private void updateDebugOutput()
	{
		mDbgAngleX2.setText ("" + mCamera.getAngleX2());
		mDbgAngleX3.setText ("" + mCamera.getAngleX3());
	}
	
	private DoubleMatrix getTransformation()
	{
		Matrix <Double> trans = mAxisRotation;
		DoubleMatrix result = new DoubleMatrix (mProjection.getRows(), trans.getColumns());
		mProjection.multiply(trans, result);
		trans = result.clone();
		mScreenMapping.multiply(trans, result);
		return result;
	}
	
	private void setAxisRotation()
	{
		BasicShape.RotationDir d = BasicShape.RotationDir.ONWARD;
		Matrix<Double> rotMat = BasicShape.rotationMatrix(mCamera.getAngleX2(), mCamera.getAngleX3(), d);
		mAxisRotation.copyValues(rotMat, 0, 0, 0, 0, rotMat.getRows(), rotMat.getColumns());
	}
	
	private void setProjectionMatrix()
	{
		double c1 = -(double)mCameraPosition.getPosition (1) / mCameraPosition.getPosition (0);
		double c2 = -(double)mCameraPosition.getPosition (2) / mCameraPosition.getPosition (0);
		mProjection.setCell (0, 3, 1.0);
		mProjection.setCell (1, 0, c1);
		mProjection.setCell (1, 1, 1.0);
		mProjection.setCell (2, 0, c2);
		mProjection.setCell (2, 2, 1.0);
	}
	
	private void adjustPixelMapping (double imageWidth, double imageHeight)
	{
		imageWidth /= mCamera.getRadius();
		imageHeight /= mCamera.getRadius(); 
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
	
	//private ArrayList <Line> mConnectingLines;
	private ArrayList <DoubleMatrix> mVertices;
	private ArrayList <ArrayList <Integer>> mConnections;
	
	private Matrix<Double> mScreenMapping, mProjection, mAxisRotation;
	private Camera mCamera;
	private Glue mCameraPosition;
	
	private JLabel mDbgAngleX2, mDbgAngleX3;
}
