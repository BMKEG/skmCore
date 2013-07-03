package edu.isi.bmkeg.skm.core.utils.fileIterators;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

public class GZipFileContentIterator implements Iterator<String>
{

	private InputStream in;
	private BufferedReader input;
	private InputStream in1;
	private BufferedReader input1;
	private String line;
	private String line1;

	public GZipFileContentIterator(String inputFile)
	{
		
		try
		{
			in = new FileInputStream(new File(inputFile));
			input = new BufferedReader( new InputStreamReader(in) );
			in1 = new FileInputStream(new File(inputFile));
			input1 = new BufferedReader( new InputStreamReader(in1) );
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 
	 */
	public GZipFileContentIterator(File f)
	{
		try
		{
			in = new GZIPInputStream(new FileInputStream(f));
			in1 = new GZIPInputStream(new FileInputStream(f));
			input = new BufferedReader( new InputStreamReader(in) );
			input1 = new BufferedReader( new InputStreamReader(in1) );
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
	public boolean hasNext()
	{

		try
		{
			line1 = input1.readLine();
			if(line1!=null){
				return true;
			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	public String next()
	{
		try
		{
			line = input.readLine();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return line;
	}
	public void remove()
	{
		// TODO Auto-generated method stub

	}
	public void close(){
		try
		{
			input.close();
			in.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
