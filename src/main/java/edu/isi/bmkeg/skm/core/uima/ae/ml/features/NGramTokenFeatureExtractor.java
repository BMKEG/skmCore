package edu.isi.bmkeg.skm.core.uima.ae.ml.features;

import java.util.List;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.cleartk.classifier.Feature;
import org.cleartk.classifier.feature.extractor.CleartkExtractorException;
import org.cleartk.classifier.feature.extractor.simple.NGramExtractor;
import org.cleartk.classifier.feature.extractor.simple.SimpleFeatureExtractor;
import org.cleartk.classifier.feature.extractor.simple.SpannedTextExtractor;
import org.cleartk.token.type.Token;


public class NGramTokenFeatureExtractor implements SimpleFeatureExtractor
{

	private int mode;
	private NGramExtractor ngramExtractor;

	public static String UNIGRAM = "UNIGRAM";
	public static String BIGRAM = "BIGRAM";
	public static String TRIGRAM = "TRIGRAM";
	
	public NGramTokenFeatureExtractor()
	{
		this.mode = 2;
		init();
	}
	
	public NGramTokenFeatureExtractor(String modeString)
	{
		if(modeString.equals(UNIGRAM))
			this.mode = 1;
		else if(modeString.equals(BIGRAM))
			this.mode = 2;
		else if(modeString.equals(TRIGRAM))
			this.mode = 3;
		else
			this.mode = 2;
		init();
	}
	
	private void init(){
		ngramExtractor = new NGramExtractor(this.mode, Token.class, new SpannedTextExtractor());
	}
	
	@Override
	public List<Feature> extract(JCas view, Annotation focusAnnotation) throws CleartkExtractorException
	{
		System.out.println(view.getViewName());
		List<Feature> features = ngramExtractor.extract(view, focusAnnotation);
		return features;
	}

	
	
}
