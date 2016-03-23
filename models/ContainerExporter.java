package models;

import java.util.*;
import java.io.*;

/**
 * class writing data about container to file
 * @author martin
 */
public class ContainerExporter 
{
	/**
	 * custom exception class
	 * @author martin
	 */
	public class ContainerExportException extends IOException
	{
		public ContainerExportException() {}
		
		public ContainerExportException (String message) { super (message); }
	}
	
	
	/**
	 * 
	 * @param export container to write to file
	 */
	public ContainerExporter (Container export)
	{
		mExport = export;
	}
	
	/**
	 * constructs file if necessary. 
	 * constructs writer to write to file (preexisting contents will
	 * be overwritten.
	 * @param filePath path to file to write to, usually relative to directory of execution
	 * @throws ContainerExportException 
	 */
	public void write (String filePath) throws ContainerExportException
	{
		try
		{
			PrintWriter writer = null;
			try
			{
				writer = new PrintWriter (filePath);
				writer.write (ContainerParser.GLOBAL_START_KEY.pattern());
				writer.write('\n');
				
				writeContainerInfo (writer);
				
				writer.write (ShapeParser.GLOBAL_START_KEY.pattern());
				writer.write('\n');
				
				for (int cBlock = 0; cBlock < mExport.getAmountOfBlocks(); ++cBlock)
					writeBlock (writer, mExport.getBlock (cBlock));
				
				writer.write (ShapeParser.GLOBAL_END_KEY.pattern());
				writer.write('\n');
				
				writer.write (ContainerParser.GLOBAL_END_KEY.pattern());
			}
			finally
			{
				writer.close();
			}
		}
		catch (IOException ioe)
		{
			throw new ContainerExportException ("while exporting container \n" + ioe.getMessage());
		}
	}
	
	/**
	 * writes description and dimensions to file 
	 * along with necessary keys.
	 * @param writer writer to use to write to file
	 */
	public void writeContainerInfo (PrintWriter writer)
	{
		
		if (mDescription != null)
		{
			writer.write (ContainerParser.DESCRIPTION_KEY.pattern());
			writer.write('\n');
		}
		
		if (mDescription != null)
		{
			Scanner scanDescription = new Scanner (mDescription);
		
			while (scanDescription.hasNextLine())
			{
				writer.write (ContainerParser.COMMENT_CHAR);
				writer.write (scanDescription.nextLine());
				writer.write('\n');
			}
			scanDescription.close();
		}
		
		writer.write (ContainerParser.DIMENSION_KEY.pattern());
		writer.write('\n');
		
		for (int cDim = 0; cDim < mExport.getDimensions().size(); ++cDim)
			writer.write (mExport.getDimensions (cDim) + " ");
		writer.write('\n');
		
	}
	
	/**
	 * @param writer writer to use to write to file
	 * @param b block to write
	 * writes b using writer
	 */
	public void writeBlock (PrintWriter writer, Block b)
	{
		ShapeExporter shapeWrite = new ShapeExporter (b);
		shapeWrite.write (writer);
	}
	
	/**
	 * @param description description to write to file about container
	 */
	public void setDescription (String description)
	{
		mDescription = description;
	}
	
	
	private Container mExport;
	private String mDescription;
}
