package edu.isi.bmkeg.skm.core.uima.ae.ml.featureGenerators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.classifier.CleartkProcessingException;
import org.cleartk.classifier.Instance;
import org.cleartk.classifier.feature.extractor.WindowExtractor;
import org.cleartk.classifier.feature.extractor.simple.SimpleFeatureExtractor;
import org.cleartk.classifier.feature.extractor.simple.SpannedTextExtractor;
import org.cleartk.classifier.feature.extractor.simple.TypePathExtractor;
import org.cleartk.classifier.feature.proliferate.CharacterNGramProliferator;
import org.cleartk.token.type.Sentence;
import org.cleartk.token.type.Token;
import org.uimafit.descriptor.ConfigurationParameter;

import edu.isi.bmkeg.skm.core.uima.ae.ml.ISINonSequentialTrainingDataGenerator;
import edu.isi.bmkeg.skm.core.uima.ae.ml.features.DictionaryFeatureExtractor;
import edu.isi.bmkeg.skm.core.utils.AnnotationRetrieval;
import edu.isi.bmkeg.skm.core.utils.ISI_UIMA_Util;
import edu.isi.bmkeg.skm.core.utils.NamedList;




public class FeatureAnnotatorAE extends ISINonSequentialTrainingDataGenerator<String>
{
	
	private List<SimpleFeatureExtractor> tokenFeatureExtractors;

	private List<WindowExtractor> tokenSentenceFeatureExtractors;

	
	/**
	 * keyword list that the IN documents should contain
	 */
	public final String PARAM_KEYWORD_FILE = "dictionaryFile";
	@ConfigurationParameter(mandatory = true, description="This is the set of keywords relevant to MGI.")
	private NamedList dictionary;
	/**
	 * Name of the dictionary
	 */
	public final String PARAM_DICTIONARY_NAME = "dictionaryName";
	@ConfigurationParameter(mandatory = true, description="This is the name of the dictionary that will be used in downstream code. This SHOULD reflect the contents of the dictionary.")
	private String dictionaryName;
	
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException
	{
		String dictionaryFile = (String) getParameterValue(context,PARAM_KEYWORD_FILE);
		dictionaryName = (String) getParameterValue(context,PARAM_DICTIONARY_NAME);
		try
		{
			dictionary = new NamedList(dictionaryName, dictionaryFile,0);
		} 
		catch (IOException e)
		{
			throw new ResourceInitializationException(e);
		}
		super.initialize(context);
		
		// a list of feature extractors that require only the token
		this.tokenFeatureExtractors = new ArrayList<SimpleFeatureExtractor>();

		// a list of feature extractors that require the token and the sentence
		this.tokenSentenceFeatureExtractors = new ArrayList<WindowExtractor>();

		// basic feature extractors for word, stem and part-of-speech
		SimpleFeatureExtractor wordExtractor, dictionaryFeatureExtractor, stemExtractor;
		wordExtractor = new SpannedTextExtractor();
		stemExtractor = new TypePathExtractor(Token.class, "stem");
		dictionaryFeatureExtractor = new DictionaryFeatureExtractor(dictionary);
		this.tokenFeatureExtractors.add(wordExtractor);
		this.tokenFeatureExtractors.add(stemExtractor);		
		this.tokenFeatureExtractors.add(dictionaryFeatureExtractor);
		// aliases for NGram feature parameters
		int fromLeftToRight = CharacterNGramProliferator.LEFT_TO_RIGHT;
		// add the feature extractors for the stem and part of speech
		//this.tokenFeatureExtractors.add(dictionaryFeatureExtractor);
		// add the feature extractor for the word itself
		// also add proliferators which create new features from the word text
		//this.tokenFeatureExtractors.add(new ProliferatingExtractor(wordExtractor, new CharacterNGramProliferator(fromLeftToRight,0, 2), new CharacterNGramProliferator(fromLeftToRight, 0, 3)));
		//this.tokenSentenceFeatureExtractors.add(new WindowExtractor(Token.class, new SpannedTextExtractor(), WindowFeature.ORIENTATION_LEFT, 0, 3));

/*
		// add 2 stems to the left and right
		this.tokenSentenceFeatureExtractors.add(new WindowExtractor(Token.class, dictionaryFeatureExtractor,
				WindowFeature.ORIENTATION_LEFT, 0, 2));
		this.tokenSentenceFeatureExtractors.add(new WindowExtractor(Token.class, dictionaryFeatureExtractor,
				WindowFeature.ORIENTATION_RIGHT, 0, 2));*/
	}
	
	@Override
	/*public void process(JCas jCas) throws AnalysisEngineProcessException
	{
		if (jCas.getDocumentText()!=null)
		{
			//System.out.println("FEATURES");
			try
			{
				if (this.isTraining())
				{
					this.dataWriter.writeOutCome(ISI_UIMA_Util.getClassificationType(jCas));
					//this.dataWriter.write(instance);

					//System.out.println("wrote ");
				}
				for (Sentence sentence : AnnotationRetrieval.getAnnotations(jCas, Sentence.class))
				{

					List<Token> tokens = AnnotationRetrieval.getAnnotations(jCas, Token.class);
					// for each token, extract all feature values and the label
					for (Token token : tokens)
					{
						//System.out.println(token.getCoveredText());
						// extract all features that require only the token
						// annotation
						for (SimpleFeatureExtractor extractor : this.tokenFeatureExtractors)
						{
							for(Feature f : extractor.extract(jCas, token)){
								this.dataWriter.writeFeature(f);
							}
						}
						
						// extract all features that require the token and sentence
						// annotations
						for (WindowExtractor extractor : this.tokenSentenceFeatureExtractors)
						{
							for(Feature f: extractor.extract(jCas, token, sentence)){
								this.dataWriter.writeFeature(f);
							}
						}
						this.dataWriter.writeFeature(new Feature("POS", token.getPos()));
					}
				}
				this.dataWriter.writeNewLine();
			} catch (CleartkException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.println("THE END");
			//ISI_UIMA_Util.displayAllAnnotations(jCas);
		}
	}*/
	public void process(JCas jCas) throws AnalysisEngineProcessException
	{
		
			if (jCas.getDocumentText()!=null)
			{
				//System.out.println("FEATURES");
				try
				{
					
					Instance<String> instance = new Instance<String>();
					
					for (Sentence sentence : AnnotationRetrieval.getAnnotations(jCas, Sentence.class))
					{

						List<Token> tokens = AnnotationRetrieval.getAnnotations(jCas, sentence, Token.class);
						// for each token, extract all feature values and the label
						for (Token token : tokens)
						{
							//System.out.println(token.getCoveredText());
							// extract all features that require only the token
							// annotation
							for (SimpleFeatureExtractor extractor : this.tokenFeatureExtractors)
							{
								
								instance.addAll(extractor.extract(jCas, token));
							}
						
							// extract all features that require the token and sentence
							// annotations
							for (WindowExtractor extractor : this.tokenSentenceFeatureExtractors)
							{
								instance.addAll(extractor.extract(jCas, token, sentence));
							}
							//instance.add(new Feature("POS", token.getPos()));
							//instance.add(new Feature("STEM", token.getStem()));
						}
					}
					if (this.isTraining())
					{
						instance.setOutcome(ISI_UIMA_Util.getClassificationType(jCas));
						//System.out.println(instance.getOutcome());
						
						this.dataWriter.write(instance);

						//System.out.println("wrote ");
					}
				} 
				catch (CleartkProcessingException e) 
				{
					throw new AnalysisEngineProcessException(e);
				}
				//System.out.println("THE END");
				//ISI_UIMA_Util.displayAllAnnotations(jCas);
			}
		} 
	
	
	
}
