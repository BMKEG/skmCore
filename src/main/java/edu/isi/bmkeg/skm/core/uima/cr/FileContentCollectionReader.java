package edu.isi.bmkeg.skm.core.uima.cr;

import java.io.IOException;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.ConfigurationParameterFactory;

import edu.isi.bmkeg.skm.core.utils.fileIterators.FlatFileContentIterator;

public class FileContentCollectionReader extends CollectionReader_ImplBase
{

	private FlatFileContentIterator ffcit;
	
	public static final String PARAM_INPUT_FILE = ConfigurationParameterFactory.createConfigurationParameterName(FileContentCollectionReader.class,"inputFile");
	@ConfigurationParameter(mandatory=true,description="")
	private String inputFile;

	private String nextLine;

	private int currentDocument;

	
	@Override
	public void initialize() throws ResourceInitializationException
	{
		inputFile = (String) getConfigParameterValue(PARAM_INPUT_FILE);
		ffcit = new FlatFileContentIterator(inputFile);
		ffcit.next();
		super.initialize();
	}
	
	@Override
	public void getNext(CAS aCAS) throws IOException, CollectionException
	{
		JCas jcas;
		try {
			jcas = aCAS.getJCas();
		} catch (CASException e) {
			throw new CollectionException(e);
		}
		nextLine = ffcit.next();
		jcas.setDocumentText(nextLine);
		
		currentDocument++;

	}

	@Override
	public void close() throws IOException
	{
		ffcit.close();
	}

	@Override
	public Progress[] getProgress()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasNext() throws IOException, CollectionException
	{
		if(ffcit.hasNext()){
			return true;
		}
		return false;
	}

}
