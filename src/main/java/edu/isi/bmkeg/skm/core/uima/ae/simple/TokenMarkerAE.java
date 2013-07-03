/**
 * 
 */
package edu.isi.bmkeg.skm.core.uima.ae.simple;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.cleartk.token.type.Token;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.ConfigurationParameterFactory;

import edu.ucdenver.ccp.common.io.ClassPathUtil;
import edu.uchsc.ccp.util.exception.InitializationException;
import edu.uchsc.ccp.util.nlp.annotation.TextAnnotation;
import edu.uchsc.ccp.util.nlp.tool.ITagger;
import edu.uchsc.ccp.util.nlp.tool.external.lingpipe.LingPipe_Util;

/**
 * @author cartic
 * 
 */

public class TokenMarkerAE extends JCasAnnotator_ImplBase {
	protected static boolean debug = false;

	protected LingPipe_Util lingPipeUtil;
	/**
	 * keyword list that the IN documents should contain
	 */
	public final static String PARAM_STOPWORD_FILE = ConfigurationParameterFactory
			.createConfigurationParameterName(TokenMarkerAE.class, "stopList");
	@ConfigurationParameter(mandatory = false, description = "This is the set of stop words from NLM http://www.ncbi.nlm.nih.gov/entrez/query/static/help/pmhelp.html#Stopwords.")
	protected List stopList;

	public final static String PARAM_MODEL_FILE_NAME = ConfigurationParameterFactory
			.createConfigurationParameterName(TokenMarkerAE.class,
					"modelFileName");
	@ConfigurationParameter(mandatory = true, // name =
												// PARAM_ARTICLE_LIST_FILENAME,
	description = "This is the tokenizer model file.")
	protected String modelFileName;

	public final static String PARAM_TAG_SET_NAME = ConfigurationParameterFactory
			.createConfigurationParameterName(TokenMarkerAE.class, "tagSetName");
	@ConfigurationParameter(mandatory = true, // name =
												// PARAM_ARTICLE_LIST_FILENAME,
	description = "This is the tagset Used.")
	protected String tagSetName;

	public static final String PARAM_VIEW_NAME = ConfigurationParameterFactory
			.createConfigurationParameterName(TokenMarkerAE.class, "viewName");
	@ConfigurationParameter(mandatory = true, defaultValue = CAS.NAME_DEFAULT_SOFA)
	protected String viewName;

	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException
	{
		try {
			viewName  = (String) aContext.getConfigParameterValue(PARAM_VIEW_NAME);
			modelFileName = (String) aContext.getConfigParameterValue(PARAM_MODEL_FILE_NAME);
			tagSetName = (String) aContext.getConfigParameterValue(PARAM_TAG_SET_NAME);
			
			File modelFile = File.createTempFile("lingpipe_POS_model", "bin");
			ClassPathUtil.copyClasspathResourceToFile(TokenMarkerAE.class, modelFileName, modelFile);
			File tagFile = File.createTempFile("lingpipe_POS_tagset", "bin");
			ClassPathUtil.copyClasspathResourceToFile(TokenMarkerAE.class, tagSetName, tagFile);
			String args[] = new String[2];
			args[0] = modelFile.getAbsolutePath();
			args[1] = tagFile.getAbsolutePath();
			
			lingPipeUtil = new LingPipe_Util();
			lingPipeUtil.initialize(ITagger.TOKENIZER, args);
			String dictionaryFile = (String) aContext.getConfigParameterValue(PARAM_STOPWORD_FILE);
			if(stopList==null&&dictionaryFile!=null){
				stopList = new ArrayList(); // "StopWords", dictionaryFile,0);
				System.err.println("Loaded StopList : "+stopList.size() );
			}
		} catch (InitializationException e) {
			throw new ResourceInitializationException(e);
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		} 
		super.initialize(aContext);
	}

	protected boolean isPunctuation(String posTag) {
		char[] c = posTag.toCharArray();
		if (c.length > 1) {
			return false;
		} else {

			if ((c[0] > 32 && c[0] < 48) || (c[0] > 57 && c[0] < 65)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org
	 * .apache.uima.jcas.JCas)
	 */
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		// System.out.println("TRANSLATION");
		try {
			jcas = jcas.getView(viewName);
		} catch (CASException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String documentText = jcas.getDocumentText();

		if (documentText != null) {
			List<TextAnnotation> tokenAnnots = lingPipeUtil
					.getTokensFromText(documentText);

			System.out.println(tokenAnnots.size() + " tokens");
			for (TextAnnotation a : tokenAnnots) {
				// System.out.println(a.getClassMention().getPrimitiveSlotMentionByName("partOfSpeech").getSingleSlotValue().toString());
				// System.out.println();
				String pos = a.getClassMention()
						.getPrimitiveSlotMentionByName("partOfSpeech")
						.getSingleSlotValue().toString().trim();
				if (!isStopWord(a.getCoveredText().trim())
						&& !isPunctuation(pos)) {
					Token t = new Token(jcas, a.getAnnotationSpanStart(),
							a.getAnnotationSpanEnd());
					t.setPos(pos);

					t.addToIndexes(jcas);
				} else {
					if (debug) {
						System.err.println(a.getCoveredText());
					}
				}
			}
		}

	}

	protected boolean isStopWord(String word) {
		if (stopList == null) {
			return false;
		} else {
			if (!stopList.contains(word)) {
				return false;
			}
		}
		return true;
	}

	public static AnalysisEngine createAnalysisEngine(
			TypeSystemDescription typeSystem, String stopListFileName,
			String tokenizerModelFileName, String tokenizerTagSetName,
			String viewName) throws ResourceInitializationException {

		AnalysisEngineDescription aed = AnalysisEngineFactory
				.createPrimitiveDescription(
						TokenMarkerAE.class,
						typeSystem,
						// name, value
						TokenMarkerAE.PARAM_STOPWORD_FILE, stopListFileName,
						TokenMarkerAE.PARAM_MODEL_FILE_NAME,
						tokenizerModelFileName,
						TokenMarkerAE.PARAM_TAG_SET_NAME, tokenizerTagSetName,
						TokenMarkerAE.PARAM_VIEW_NAME, viewName);

		return AnalysisEngineFactory.createPrimitive(aed);
	}

}
