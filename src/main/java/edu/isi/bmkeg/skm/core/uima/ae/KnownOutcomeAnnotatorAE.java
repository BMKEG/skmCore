package edu.isi.bmkeg.skm.core.uima.ae;


import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.ConfigurationParameterFactory;

import edu.isi.bmkeg.skm.core.exceptions.ISITriageMissingIDException;
import edu.isi.bmkeg.skm.core.utils.ISI_UIMA_Util;
import edu.isi.bmkeg.skm.core.utils.NamedList;



/**
 * 
 * @author roederc
 *
 * 2011-09-26 changed the name of the namedList filename and of the named list in order to make the 
 * annotation for the filename work right, and remove a line from the intialize function. Pipelines
 * that use this code should pass the filename into the parameter using the final static string
 * PARAM_MGI_ARTICLE_LIST_FILENAME.
 */
public class KnownOutcomeAnnotatorAE extends JCasAnnotator_ImplBase
{

	Logger logger = Logger.getLogger(KnownOutcomeAnnotatorAE.class);
	
	
	public static String PARAM_COLUMN_SEPARATOR = ConfigurationParameterFactory.createConfigurationParameterName(KnownOutcomeAnnotatorAE.class, "columnSeparator");
	@ConfigurationParameter(mandatory=false, description="columnSeparator",defaultValue={","})
	private String columnSeparator;
	
	public final static String PARAM_IN_LIST_COLUMN_IDENTIFIER = ConfigurationParameterFactory.createConfigurationParameterName(
			KnownOutcomeAnnotatorAE.class, "inListColumnId");
	@ConfigurationParameter(mandatory=true, description="this is the column in the articleIdFileName that contains the ids of the documents")
	private Integer inListColumnId;
	
	/**
	 * set of journals that MGI is concerned with 
	 * This is the specific journal that we need to iterate over
	 */
	public final static String PARAM_ARTICLE_LIST_FILENAME 
		= ConfigurationParameterFactory.createConfigurationParameterName(
				KnownOutcomeAnnotatorAE.class, "articleIdFileName");
		// plain names and the name attribute don't seem to work!
		//= "articleIdFileName";
	@ConfigurationParameter(mandatory = true, //name = PARAM_ARTICLE_LIST_FILENAME,
			description="This file lists the set of article pmids that are within MGI.")
	protected String articleIdFileName;
	
	protected NamedList mgiBibEntries;
	
	protected String IN="IN"; 
	protected String OUT="OUT";
	
	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException
	{
		super.initialize(aContext);
		logger.info("reading article ids from: " + articleIdFileName);
		try	{
			articleIdFileName = (String) aContext.getConfigParameterValue(PARAM_ARTICLE_LIST_FILENAME);
			columnSeparator = (String) aContext.getConfigParameterValue(PARAM_COLUMN_SEPARATOR);
			inListColumnId = (Integer) aContext.getConfigParameterValue(PARAM_IN_LIST_COLUMN_IDENTIFIER);
			mgiBibEntries = new NamedList("articleIds", articleIdFileName,inListColumnId,true,columnSeparator);
		} 
		catch (IOException e) {
			throw new ResourceInitializationException(e);
		}


	}
	
	
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException
	{
		String pmid=null;
		try
		{
			pmid = ISI_UIMA_Util.getDocumentSecondaryID(jcas, "PMID");
			System.out.println("PMID: "+ pmid);
		} catch (ISITriageMissingIDException e)
		{
			System.err.println("PMID is "+pmid);
			System.err.println(" Treating this document as an out ");
			System.err.println(" TODO implement KnownOutcomeAnnotatorAE:useAlternateIDToGetPMID" );
			//e.printStackTrace();
		}

		if (mgiBibEntries.contains(pmid))
		{
			ISI_UIMA_Util.setClassificationType(jcas, IN);
		} else
		{
			ISI_UIMA_Util.setClassificationType(jcas, OUT);
		}
	}

	public static AnalysisEngine createAnalysisEngine(TypeSystemDescription typeSystem, String articleListFileName, Integer columnId)
	 throws ResourceInitializationException {
		
			AnalysisEngineDescription aed = AnalysisEngineFactory.createPrimitiveDescription(
					KnownOutcomeAnnotatorAE.class, typeSystem, 
				// name,      									value
					KnownOutcomeAnnotatorAE.PARAM_ARTICLE_LIST_FILENAME, articleListFileName,
					KnownOutcomeAnnotatorAE.PARAM_IN_LIST_COLUMN_IDENTIFIER,columnId
					);
	 
	 	return AnalysisEngineFactory.createPrimitive(aed);
	 }

	private String useAlternateIDToGetPMID(JCas jcas){
		return null;
	}
}
