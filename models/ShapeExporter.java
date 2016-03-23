package models;

import geometry.Line;

import java.io.*;
import java.util.ArrayList;

/**
 * appends shape data to file
 * @author martin
 */
public class ShapeExporter 
{
	
	/**
	 * @param export block to export
	 */
	public ShapeExporter (Block export)
	{
		mExport = export;
	}
	
	/**
	 * @param writer writer to use to write to file
	 * writes block data to file in accordance with shape parser's requirements
	 */
	public void write (PrintWriter writer)
	{
		writer.append (ShapeParser.LOCAL_START_KEY.pattern());
		writer.append('\n');
		writer.append (mExport.getName());
		writer.append('\n');
		
		writerVertices (writer);
		
		writer.append (ShapeParser.CONNECTION_START_KEY.pattern());
		writer.append('\n');
		writeConnections (writer);
		
		writer.append (ShapeParser.VALUE_START_KEY.pattern());
		writer.append('\n');
		writer.append (new Double (mExport.getValue()).toString());
		writer.append ('\n');
		
		writer.append (ShapeParser.LOCAL_END_KEY.pattern());
		writer.append('\n');
	}
	
	/**
	 * appends vertex data without key
	 * @param writer writer to use for writing to the file
	 */
	public void writerVertices (PrintWriter writer)
	{
		for (int cVertex = 0; cVertex < mExport.getNumberOfVertices(); ++cVertex)
		{
			Glue vert = new Glue (mExport.getVertex (cVertex));
			writer.append ("(" + vert.getPosition (0) + ",");
			writer.append (vert.getPosition(1) + "," + vert.getPosition (2) + ")");
			writer.append('\n');
		}
	}
	
	/**
	 * appends connection data without key
	 * @param writer writer to use for writing to the file
	 */
	public void writeConnections (PrintWriter writer)
	{
		ArrayList<Line> lines = mExport.getConnectingLines();
		for (Line connection : lines)
		{
			int v1 = mExport.getVertexIndex (connection.getFirst());
			int v2 = mExport.getVertexIndex (connection.getSecond());
			writer.append (v1 + "-" + v2);
			writer.append('\n');
		}
	}
	
	
	private Block mExport;
}
