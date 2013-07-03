package edu.isi.bmkeg.skm.core.uima.ae.ml.features;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.cleartk.classifier.Feature;
import org.cleartk.classifier.feature.extractor.CleartkExtractorException;
import org.cleartk.classifier.feature.extractor.simple.SimpleFeatureExtractor;
import org.cleartk.token.type.Token;

import edu.isi.bmkeg.skm.core.utils.AnnotationRetrieval;
import edu.isi.bmkeg.skm.core.utils.NamedList;


public class DictionaryFeatureExtractor implements SimpleFeatureExtractor
{
	private NamedList dictionary;
	public DictionaryFeatureExtractor(NamedList dictionary)
	{
		this.dictionary = dictionary;
	}
	@Override
	public List<Feature> extract(JCas view, Annotation focusAnnotation) throws CleartkExtractorException
	{
		List<Feature> features = new ArrayList<Feature>();
		List<Token> tokenAnnots = AnnotationRetrieval.getAnnotations(view, focusAnnotation.getBegin(), focusAnnotation.getEnd(), Token.class);
		for(Token t : tokenAnnots){
			Feature f = new Feature();
			String featureName = dictionary.getName();
			if(featureName==null){
				featureName = "DICTIONARY_TERM";
			}
			f.setName(featureName);
			f.setValue(inDictionary(t.getCoveredText()));
			features.add(f);
		}
		return features;
	}

	private boolean inDictionary(String token){
		if(dictionary.contains(token.toLowerCase())){
			return true;
		}
		if(dictionary.contains(token.toUpperCase())){
			return true;
		}
		return false;
	}
	
}

