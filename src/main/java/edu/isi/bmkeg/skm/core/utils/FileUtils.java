package edu.isi.bmkeg.skm.core.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

public class FileUtils {
	
	static Logger logger = Logger.getLogger(FileUtils.class);
	
	public static void copyClasspathResourceToFile(Class<?> clazz, String resourceName, File file) 
	throws IOException {
		BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(file));
		InputStream is =  clazz.getResourceAsStream(resourceName);
		if (is == null) {
			throw new RuntimeException("can't get resource:" + resourceName);
		}

		try {
			IOUtils.copyLarge(is, outStream);
			is.close();
		} 
		catch (IOException x) {
			logger.error("couldn't open: " + resourceName + ", " + file);
			throw new RuntimeException(x);
		}
		finally {
			IOUtils.closeQuietly(outStream);
		}
	}
	
	public static String searchFolderInClasspath(String folder)
	throws IOException {
		// folder is a misnomer since this could either be a file or a folder
		// when called from FeatureAnnotatorBooleanAE from th prediction pipeline
		// it expects a model file. whose relative path is the parameter folders
		
		String []pathElements = System.getProperty("java.class.path").split(System.getProperty("path.separator"));
		for(String classPathEntry : pathElements){
			if(classPathEntry.endsWith(folder)){
				return classPathEntry;
			}
		}

		for(String classPathEntry : pathElements){
			return searchFolderInLocation(folder,classPathEntry);
		}
		
		return null;
		/*String path = System.getProperty("java.class.path").split(System.getProperty("path.separator"))[0]+folder;
		System.out.println("searchFolderInClasspath:"+path);
		return path;*/
	}

	public static String searchFolderInUserDir(String folder)
	throws IOException {
		// folder is a misnomer since this could either be a file or a folder
		// when called from FeatureAnnotatorBooleanAE from th prediction pipeline
		// it expects a model file. whose relative path is the parameter folders
		
		String []pathElements = System.getProperty("user.dir").split(System.getProperty("path.separator"));
		for(String classPathEntry : pathElements){
			if(classPathEntry.endsWith(folder)){
				return classPathEntry;
			}
		}

		for(String classPathEntry : pathElements){
			return searchFolderInLocation(folder,classPathEntry);
		}
		
		return null;
		/*String path = System.getProperty("java.class.path").split(System.getProperty("path.separator"))[0]+folder;
		System.out.println("searchFolderInClasspath:"+path);
		return path;*/
	}

	public static String searchFolderInLocation(String resource, String parentLocation){
		String path = null;
		File location = new File(parentLocation);
		FilenameFilter filter = new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return !name.startsWith(".");
		    }
		};
		if(location.isDirectory()){
			for(File f:location.listFiles(filter)){
				if(f.isDirectory()&&path==null){
					if(f.getAbsolutePath().endsWith(resource)){
						System.out.println("Found resource: ["+resource+"] at location on classpath: ["+f.getAbsolutePath()+"]");
						path = f.getAbsolutePath();
						break;
					}else{
						path = searchFolderInLocation(resource,f.getAbsolutePath());
					}
				}else{
					continue;
				}
			}
		}
		return path;
	}
	
	public static String searchFileInClasspath(String file)
	throws IOException {
		// folder is a misnomer since this could either be a file or a folder
		// when called from FeatureAnnotatorBooleanAE from th prediction pipeline
		// it expects a model file. whose relative path is the parameter folders
		
		String []pathElements = System.getProperty("java.class.path").split(System.getProperty("path.separator"));
		for(String classPathEntry : pathElements){
			if(classPathEntry.endsWith(file)){
				return classPathEntry;
			}
		}

		for(String classPathEntry : pathElements){
			return searchFileInLocation(file,classPathEntry);
		}
		
		return null;
		/*String path = System.getProperty("java.class.path").split(System.getProperty("path.separator"))[0]+folder;
		System.out.println("searchFolderInClasspath:"+path);
		return path;*/
	}

	
	private static String searchFileInLocation(String resource, String parentLocation){
		String path = null;
		File location = new File(parentLocation);
		if(location.isDirectory()){
			for(File f:location.listFiles()){
				if(f.isDirectory()){
					path = searchFileInLocation(resource,f.getAbsolutePath());
				}else{
					if(f.getAbsolutePath().endsWith(resource)){
						System.out.println("Found resource: ["+resource+"] at location on classpath: ["+f.getAbsolutePath()+"]");
						path = f.getAbsolutePath();
						break;
					}else{
						continue;
					}
				}
			}
		}
		return path;
	}
	
}
