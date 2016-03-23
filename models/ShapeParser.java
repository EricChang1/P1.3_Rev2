package models;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

import models.Matrix.*;


/**
 * class parsing special files to construct blocks
 * format needs to be the following
 * PIECES
 * <possibility for comments here>
 * new
 * (<z coord>,<x coord>,<y coord>)
 * <repeat for every vertex>
 * connect
 * i0-i2-i5
 * i3-i2
 * <repeat for chain of connections>
 * value
 * <value>
 * end
 * <more comments>
 * new
 * ...
 * end
 * PIECES_END
 * 
 * Note that round brackets for vertices are necessary 
 * and that no white spaces between coordinates are allowed
 * Note that the commands new, connect, value, end need to be in separate lines
 * To indicate connections, indices are used
 * A vertex is accessed through a 0-based index corresponding to the order
 * in which the vertices are listed in the file
 * As many vertices can be connected using chains
 * the hyphen between two indices establishes a connection between them (mutually)
 * @author martin
 */
public class ShapeParser 
{
	public static final Pattern GLOBAL_START_KEY = Pattern.compile ("PIECES");
	public static final Pattern GLOBAL_END_KEY = Pattern.compile ("PIECES_END");
	public static final Pattern LOCAL_START_KEY = Pattern.compile ("new");
	public static final Pattern LOCAL_END_KEY = Pattern.compile ("end");
	public static final Pattern CONNECTION_START_KEY = Pattern.compile ("connect");
	public static final Pattern VALUE_START_KEY = Pattern.compile ("value");
	
	
	@SuppressWarnings("serial")
	public class BadFileStructureException extends Exception
	{
		public BadFileStructureException() {}
		
		public BadFileStructureException (String message) { super (message); }
	}
	
	
	public ShapeParser (File input) throws FileNotFoundException
	{
		mRead = new BufferedReader (new FileReader (input));
		mBlocks = new ArrayList<Block>();
	}
	
	public ArrayList <Block> getBlocks()
	{
		ArrayList <Block> blocks = new ArrayList <Block>();
		for (Block b : mBlocks)
			blocks.add (b.clone());
		return blocks;
	}
	
	public ArrayList <BasicShape> getShapes()
	{
		ArrayList <BasicShape> shapes = new ArrayList <BasicShape>();
		for (Block b : mBlocks)
			shapes.add (new BasicShape (b));
		return shapes;
	}
	
	public void parse() throws BadFileStructureException, IOException
	{
		boolean endReached = false;
		//read until global start key
		String startLine = readToKey (GLOBAL_START_KEY);
		if (startLine.isEmpty())
			endReached = true;
		
		while (mRead.ready() && !endReached)
		{
			//skip unusable part
			String endOrStart = GLOBAL_END_KEY.pattern() + "|" + LOCAL_START_KEY.pattern();
			
			String match = readToKey (Pattern.compile (endOrStart));
			if (!match.isEmpty() && LOCAL_START_KEY.matcher (match).find())
			{
				ArrayList <IntegerMatrix> vecs = parseVectors();
				IntegerMatrix adjacent = parseConnections (vecs);
				double value = Double.parseDouble(mRead.readLine());
				mBlocks.add (new Block (vecs, adjacent, value));
				if (!LOCAL_END_KEY.matcher (mRead.readLine()).matches())
					throw new BadFileStructureException ("missing terminating 'end' token");
			}
			else
				endReached = true;
		}
	}
	
	private ArrayList <IntegerMatrix> parseVectors() throws IOException
	{
		ArrayList <IntegerMatrix> vecs = new ArrayList<IntegerMatrix>();
		String line = mRead.readLine();
		while (mRead.ready() && !line.startsWith("connect"))
		{
			Scanner scan = new Scanner (line);
			scan.skip("[(]");
			scan.useDelimiter("[,)]");
			ArrayList <Double> entries = new ArrayList <Double>();
			while (scan.hasNextDouble())
				entries.add (scan.nextDouble());
			
			DoubleMatrix vec = new DoubleMatrix (entries.size(), 1);
			for (int cEntry = 0; cEntry < entries.size(); ++cEntry)
				vec.setCell (cEntry, 0, entries.get (cEntry));
			vecs.add (vec.toIntegerMatrix());
			line = mRead.readLine();
			scan.close();
		}
		return vecs;
	}
	
	private IntegerMatrix parseConnections (ArrayList <IntegerMatrix> vecs) throws IOException, BadFileStructureException
	{
		IntegerMatrix adj = new IntegerMatrix (vecs.size(), vecs.size());
		String line = null;
		if (mRead.ready())
			 line = mRead.readLine();
		//read in lines
		while (mRead.ready() && !line.startsWith("value"))
		{
			Scanner scanFragment = new Scanner (line);
			scanFragment.useDelimiter(",");
			//read in comma-separated fragments
			while (scanFragment.hasNext())
			{
				Scanner scanSingle = new Scanner (scanFragment.next());
				scanSingle.useDelimiter("[-,]");
				int prevIndex = -1, currIndex = -1;
				//read in indices
				while (scanSingle.hasNextInt())
				{
					prevIndex = currIndex;
					currIndex = scanSingle.nextInt();
					if (currIndex < 0 || currIndex > vecs.size() - 1)
					{
						scanSingle.close();
						scanFragment.close();
						throw new BadFileStructureException("Index read in does not correspond to a vector!");
					}
					if (prevIndex != -1)
					{
						adj.setCell(prevIndex, currIndex, 1);
						adj.setCell(currIndex, prevIndex, 1);
					}
				}
				scanSingle.close();
			}
			scanFragment.close();
			line = mRead.readLine();
		}
		
		
		return adj;
	}
	
	/**
	 * @param key key to match
	 * @return line matching key or empty string if no match was found
	 * advances reader until line containing key is found or file ends
	 */
	private String readToKey (Pattern key) throws IOException
	{
		boolean found = false;
		while (mRead.ready() && !found)
		{
			String line = mRead.readLine();
			Matcher matcher = key.matcher (line);
			if (matcher.matches())
			{
				found = true;
				return line;
			}
		}
		return new String();
	}
	
	private BufferedReader mRead;
	private ArrayList<Block> mBlocks;
}
