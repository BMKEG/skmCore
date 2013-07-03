package edu.isi.bmkeg.skm.core.uima.ae.simple;

import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.token.type.Sentence;

import edu.uchsc.ccp.util.exception.InitializationException;
import edu.uchsc.ccp.util.nlp.annotation.TextAnnotation;
import edu.uchsc.ccp.util.nlp.tool.ITagger;
import edu.uchsc.ccp.util.nlp.tool.external.lingpipe.LingPipe_Util;

/**
 * @author cartic
 *
 */
public class SentenceMarkerAE extends JCasAnnotator_ImplBase 
{
	private static boolean debug = false;
	
	protected LingPipe_Util lingPipeUtil;
	
	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException
	{
		// TODO Auto-generated method stub
		super.initialize(aContext);
		try {
			lingPipeUtil = new LingPipe_Util();
			lingPipeUtil.initialize(ITagger.SENTENCE_DETECTOR, null);
		}
		catch (InitializationException x) {
			throw new ResourceInitializationException(x);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
	 */
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException
	{
		//System.out.println("TRANSLATION");
		String documentText = jcas.getDocumentText();
		
		if (documentText!=null)
		{
			List<TextAnnotation> setnences = lingPipeUtil.getSentencesFromText(documentText);
			for (TextAnnotation a : setnences)
			{
				Sentence s = new Sentence(jcas, a.getAnnotationSpanStart(), a.getAnnotationSpanEnd());
				s.addToIndexes(jcas);
			}
		}

	}

}
