package edu.isi.bmkeg.skm.core.utils.fileIterators;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Iterator;

public class FileIterator implements Iterator<File>, Iterable<File> {

	private File [] list; 
	private int ctr;

	public FileIterator(String folder) {
		init(folder, null);
	}
	
	public FileIterator(String folder, String fileExtension) {
		CustomFileFilter fnf = new CustomFileFilter(fileExtension);
		init(folder,fnf);
	}

	private void init(String folder, FilenameFilter fnf) {
		ctr = 0;
		File dir = new File(folder);
		if (dir != null && dir.exists() && dir.isDirectory()) {
			list = dir.listFiles(fnf);
		} else {
			//FIXME Please pardon me for this RuntimeException. Will come back and fix.
			throw new RuntimeException("Path provided in constructor does not exist or is not a dir.");
		}
	}
	
	public File next()	{
		return list[ctr++];
	}
	
	public boolean hasNext()
	{
		if(ctr<=list.length-1){
			return true;
		}
		return false;
	}
	
	public void remove() {
		// TODO Will we need to implement remove() for Iterator? Auto-generated method stub
	}
	
	public class CustomFileFilter implements FilenameFilter 	{
		// The default file extension. For test.txt, the extension is txt
		// Might need to use a list here performing an OR within the accept method
		private String extension = "txt"; 
		
		public CustomFileFilter() {
			super();
		}
		
		public CustomFileFilter(String extension) {
			this.extension = extension;
		}
		
		public boolean accept(File dir, String name)
		{
			return name.endsWith(extension);
		}
	}


	public Iterator<File> iterator() {
		return Arrays.asList(list).iterator();
	}

	//For testing only
	public static void main(String[] args) {
		System.out.println("\nAll of data:");
		for (File f : new FileIterator("data")) {
			System.out.println(f);
		}
		System.out.println("\nWith filter:");
		for (File f : new FileIterator("data", "properties")) {
			System.out.println(f);
		}
	}
}
