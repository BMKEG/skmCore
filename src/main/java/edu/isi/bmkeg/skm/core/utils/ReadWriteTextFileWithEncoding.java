package edu.isi.bmkeg.skm.core.utils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/** 
 Read and write a file using an explicit encoding.
 Removing the encoding from this code will simply cause the 
 system's default encoding to be used instead.  
 */
public class ReadWriteTextFileWithEncoding {

	
	/** Write fixed content to the given file. */
	public static void write(String fFileName, String fEncoding, String text) throws IOException  {
		log("Writing to file named " + fFileName + ". Encoding: " + fEncoding);
		Writer out = new OutputStreamWriter(new FileOutputStream(fFileName), fEncoding);
		try {
			out.write(text);
		}
		finally {
			out.close();
		}
	}

	/** Read the contents of the given file. */
	public static String read(String fFileName, String fEncoding) throws IOException {
		//log("Reading from file.");
		StringBuilder text = new StringBuilder();
		String NL = System.getProperty("line.separator");
		Scanner scanner = new Scanner(new FileInputStream(fFileName), fEncoding);
		try {
			while (scanner.hasNextLine()){
				text.append(scanner.nextLine() + NL);
			}
		}
		finally{
			scanner.close();
		}
		return text.toString();
	}

	public static Map<Integer,String> readIntegerStringMapFromFile(String fileName, String encoding, String fieldSeparator) throws IOException {
		String NL = System.getProperty("line.separator");
		Map<Integer,String> map = new HashMap<Integer, String>();
		Scanner scanner = new Scanner(new FileInputStream(fileName), encoding);
		try {
			while (scanner.hasNextLine()){
				String line = scanner.nextLine();
				String lineParts [] = line.split(fieldSeparator);
				map.put(Integer.parseInt(lineParts[0]), lineParts[1]);
			}
		}
		finally{
			scanner.close();
		}
		return map;
	}
	
	
	private static void log(String aMessage){
		System.out.println(aMessage);
	}
	
	public static void main(String[] args)
	{
		try
		{
			System.out.println(ReadWriteTextFileWithEncoding.read("/Users/cartic/Documents/work/Corpora/MGI/Tumor/output/22025563.pdf_spatialFiltered.txt", "UTF-8"));
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}