package models;

import java.util.*;
import java.io.*;
import java.util.regex.*;

/**
 * class parsing file for contents of container
 * 
 * file structure
 * CONTAINER
 * describe
 * <description>
 * dimensions
 * <depth> <width> <height>
 * PIECES
 * <code listing all pieces to be placed>
 * END_PIECES
 * END_CONTAINER
 */
public class ContainerParser 
{
	
	public static final Pattern GLOBAL_START_KEY = Pattern.compile ("CONTAINER");
	public static final Pattern GLOBAL_END_KEY = Pattern.compile ("END_CONTAINER");
	public static final Pattern DESCRIPTION_KEY = Pattern.compile ("describe");
	public static final Pattern DIMENSION_KEY = Pattern.compile ("dimensions");
	public static final char COMMENT_CHAR = '#';
	
	/**
	 * custom exception class
	 * @author martin
	 */
	public class ContainerParserException extends Exception
	{
		public ContainerParserException() {}
		
		public ContainerParserException (String message) { super (message); }
	}
	
	
	public ContainerParser (File f) throws FileNotFoundException
	{
		mRead = new BufferedReader (new FileReader (f));
		mParseShapes = new ShapeParser (f);
		mDescription = new String();
	}
	
	/**
	 * @return new container with all parsed blocks inside
	 */
	public Container constructContainer()
	{
		if (mContainer == null || mDescription == "")
			throw new IllegalStateException ("file was not parsed yet");
		
		Container construct = mContainer.clone();
		for (Block place : mParseShapes.getBlocks())
			construct.placeBlock (place, place.getGlue());
		return construct;
	}
	
	/**
	 * @return description read or null if none was read
	 */
	public String getDescription() { return mDescription; }
	
	/**
	 * reads the file for container dimensions and pieces
	 * @throws ContainerParserException
	 * @throws ShapeParser.BadFileStructureException
	 */
	public void parser() throws ContainerParserException, ShapeParser.BadFileStructureException
	{
		try
		{
			try
			{
				mParseShapes.parse();
				
				readToKey (GLOBAL_START_KEY);
				
				String desOrDim = DESCRIPTION_KEY.pattern() + "|" + DIMENSION_KEY.pattern();
				String hasDescription = readToKey (Pattern.compile (desOrDim));
				
				if (DESCRIPTION_KEY.matcher (hasDescription).find())
					readDescription();
				else if (!DIMENSION_KEY.matcher (hasDescription).matches())
				{
					String dim = readToKey (DIMENSION_KEY);
					if (dim.isEmpty())
						throw new ContainerParserException ("file does not specify dimensions");
				}
				readDimensions();
				
				String end = readToKey (GLOBAL_END_KEY);
				if (end.isEmpty())
					throw new ContainerParserException ("file does not contain end key");
			}
			finally
			{
				mRead.close();
			}
		}
		catch (IOException eio)
		{
			throw new ContainerParserException (eio.getMessage());
		}
		
	}
	
	public void readDescription() throws IOException, ContainerParserException
	{
		String line = mRead.readLine();
		
		do
		{
			Scanner read = new Scanner (line);
			read.skip (new String() + COMMENT_CHAR);
			mDescription += read.nextLine() + "\n";
			
			if (!mRead.ready())
				throw new ContainerParserException ("no description provided or no dimension key to delimit");
			
			line = mRead.readLine();
		} while (!DIMENSION_KEY.matcher (line).matches());
	}
	
	public void readDimensions () throws IOException
	{
		Scanner s = new Scanner (mRead.readLine());
		int dep, wid, hig;
		dep = s.nextInt();
		wid = s.nextInt();
		hig = s.nextInt();
		mContainer = new Container(dep, wid, hig);
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
	private ShapeParser mParseShapes;
	private String mDescription;
	private Container mContainer;
}
