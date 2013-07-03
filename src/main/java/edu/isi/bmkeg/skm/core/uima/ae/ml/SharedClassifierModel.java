package edu.isi.bmkeg.skm.core.uima.ae.ml;

import java.io.File;
import java.io.IOException;

import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.SharedResourceObject;
import org.cleartk.classifier.svmlight.SVMlightClassifier;
import org.cleartk.classifier.svmlight.SVMlightClassifierBuilder;

public class SharedClassifierModel implements SharedResourceObject
{

	private SVMlightClassifier classifier;
	
	

	public void load(DataResource arg0) throws ResourceInitializationException
	{
		try
		{
			File f = new File(arg0.getUri());
			classifier = new SVMlightClassifierBuilder().loadClassifierFromTrainingDirectory(f);
		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	public SVMlightClassifier getClassifier()
	{
		return classifier;
	}

	public void setClassifier(SVMlightClassifier classifier)
	{
		this.classifier = classifier;
	}

}
