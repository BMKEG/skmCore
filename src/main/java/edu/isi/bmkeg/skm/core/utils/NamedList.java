package edu.isi.bmkeg.skm.core.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import edu.isi.bmkeg.skm.core.exceptions.InvalidInputDataException;

public class NamedList implements Iterable<String>{
	
	public static boolean debug = false;
	private String name;
	private String sourceOrganization;
	protected Collection<String> entities;

	private int column=0;
	
	private boolean splitRow =false;
	private String columnSeparator="\\s+";
	
	/**
	 *  Just creates an empty entity list
	 */
	public NamedList(String _name) {
		this.entities = new HashSet<String>();
		this.name = _name;
	}

	/**
	 * Creates an entity list from a file on the disk or 
	 * a file in the classpath if it doesn't exist
	 */
	public NamedList(String _name, String inputFileName, int col) 
	throws FileNotFoundException, IOException {
		this.entities = new HashSet<String>();
		this.name = _name;
		this.column = col;
		this.splitRow = false;
		File inputFile = new File(inputFileName);
		if (inputFile.canRead()) {
			this.load(inputFileName);
		}
		else {
			File file = new File("tempFile"); // TODO
			FileUtils.copyClasspathResourceToFile(this.getClass(), inputFileName, file);
			this.load(file.getAbsolutePath());
		}
	}
	
	
	public NamedList(String _name, String inputFileName, int col, boolean splitRow, String columnSeparator) 
	throws FileNotFoundException, IOException {
		this.columnSeparator = columnSeparator;
		this.entities = new HashSet<String>();
		this.name = _name;
		this.column = col;
		this.splitRow = splitRow;
		File inputFile = new File(inputFileName);
		if (inputFile.canRead()) {
			this.load(inputFileName);
		}
		else {
			File file = new File("tempFile"); // TODO
			FileUtils.copyClasspathResourceToFile(this.getClass(), inputFileName, file);
			this.load(file.getAbsolutePath());
		}
	}

	
	/**
	 * Creates an entity list from a file on the disk or 
	 * a file in the classpath if it doesn't exist
	 */
	public NamedList(String _name, String inputFileName, int col, boolean splitRow) 
	throws FileNotFoundException, IOException {
		this.entities = new HashSet<String>();
		this.name = _name;
		this.column = col;
		this.splitRow = splitRow;
		File inputFile = new File(inputFileName);
		if (inputFile.canRead()) {
			this.load(inputFileName);
		}
		else {
			File file = new File("tempFile"); // TODO
			FileUtils.copyClasspathResourceToFile(this.getClass(), inputFileName, file);
			this.load(file.getAbsolutePath());
		}
	}




	
	/**
	 * Creates an entity list from a list of strings
	 */ 
	public NamedList(String _name, Collection<String> _entities) {
		this.entities = new HashSet<String>();
		this.addAll(_entities);
		this.name = _name;
	}
	
	
	/**
	 * Creates an entity list from a list of strings
	 */ 
	public NamedList(String _name, String[] _entities) {
		this.entities = new HashSet<String>();
		for(String entity:_entities){
			this.entities.add(entity);
		}
		this.name = _name;
	}
	
	
	/**
	 * Loads entity list from a file 
	 * 
	 * @param inputFileName
	 * @throws FileNotFoundException
	 */
	public void load(String inputFileName) throws FileNotFoundException, IOException {

        InputStream in = new FileInputStream(new File(inputFileName));
        
		//declared here only to make visible to finally clause
		BufferedReader input = null;
		try {
			//use buffering, reading one line at a time
			//FileReader always assumes default encoding is OK!
			//input = new BufferedReader( new FileReader(in) );
			input = new BufferedReader( new InputStreamReader(in) );
			String line = null; //not declared within while loop
			
			/*
			 * readLine is a bit quirky :
			 * it returns the content of a line MINUS the newline.
			 * it returns null only for the END of the stream.
			 * it returns an empty String if two newlines appear in a row.
			 */
			while (( line = input.readLine()) != null){
				try
				{
					processEntry(line);
				} catch (InvalidInputDataException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		finally {
			try {
				if (input!= null) {
					//flush and close both "input" and its underlying FileReader
					input.close();
				}
			}
			catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}
	
	public void addAll(Collection<String> entities) {
		for (String e : entities) {
			//System.out.println(e);
			try
			{
				this.processEntry(e);
			} catch (InvalidInputDataException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	/**
	 * For each line read from a file, or for each entity in a string list, this method will be called
	 * @param line
	 * @throws InvalidInputDataException 
	 */
	public void processEntry(String line) throws InvalidInputDataException {
		if (!this.splitRow)
		{
			// Add entity to set
			if (line != null && !line.equals(""))
			{
				this.add(line);
			}
		}else{
			// the list is being loaded from a single column that is contained in a multicolumn file and the target column is not the 
			String [] parts = line.split(columnSeparator);
			if(parts.length>0){
				if(column>=parts.length){
					throw new InvalidInputDataException("The input file provided contains "+parts.length+" columns. Requested column index ="+column+". Requested column index must be strictly < "+parts.length);
				}else{
					this.add(parts[column]);
				}
			}else{
				throw new InvalidInputDataException("The input file provided does not contain multi-column data as expected");
			}
		}
	}

	public int size() {
		return entities.size();
	}
	
	public void add(String entity) {
		this.entities.add(entity.trim());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void clear() {
		entities.clear();
	}

	public StringBuffer getAll() {
		StringBuffer str = new StringBuffer();
		Iterator<String> it = entities.iterator();
		while (it.hasNext()) {
			str.append(it.next());
		}
		return str;
	}

	public boolean contains(String input){
		return entities.contains(input);
	}
	
	public Collection<String> getEntities() {
		return this.entities;
	}

	public Iterator<String> iterator()
	{
		return this.entities.iterator();
	}

	public void setSourceOrganization(String sourceOrganization)
	{
		this.sourceOrganization = sourceOrganization;
	}

	public String getSourceOrganization()
	{
		return sourceOrganization;
	}
	/**
	 * 
	 * @param outputLocation
	 * @throws IOException
	 */
	public void save(String outputLocation) throws IOException {
		if(!new File(outputLocation).getParentFile().exists()){
			System.out.println("Target location for journalIds does not exist..");
			new File(outputLocation).getParentFile().mkdir();
			System.out.println("Target location for journalIds created..");
		}
		System.out.println("Writing list of mgi Journal Ids to "+outputLocation+" ...");
		PrintWriter pw = new PrintWriter(new File(outputLocation));
		for(String item : this.entities){
			pw.println(item);
			pw.flush();
		}
		pw.close();
	}
	
}
