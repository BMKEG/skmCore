package edu.isi.bmkeg.skm.core.uima.ae.ml;

import java.io.File;
import java.io.IOException;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.cleartk.classifier.Classifier;
import org.cleartk.classifier.jar.JarClassifierBuilder;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.ConfigurationParameterFactory;
import org.uimafit.factory.initializable.Initializable;
import org.uimafit.factory.initializable.InitializableFactory;

import edu.isi.bmkeg.skm.core.utils.FileUtils;





public class ISITrainer<OUTCOME_TYPE> extends JCasAnnotator_ImplBase implements Initializable
{

	public final static String PARAM_CLASSIFIER_BUILDER_CLASS_NAME = ConfigurationParameterFactory.createConfigurationParameterName(ISITrainer.class, "classifierBuilderClassName");
	@ConfigurationParameter(mandatory = true, description = "provides the full name of the classifierBuilderClassName to be used.")
	private String classifierBuilderClassName;

	public final static String PARAM_CLASSIFIER_TRAINING_DATA_FOLDER = ConfigurationParameterFactory.createConfigurationParameterName(ISITrainer.class, "classifierTrainingDataFolder");
	@ConfigurationParameter(mandatory = true, description = "provides the full location of the trainingData to be used.")
	private String classifierTrainingDataFolder;


	protected Classifier<?> classifier;
	protected JarClassifierBuilder<?> classifierBuilder;

	
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException
	{
		super.initialize(context);


		if (!new File(classifierTrainingDataFolder).exists()) {
			throw new ResourceInitializationException(new Throwable("Training data location is invalid!!"));
		}

		if (classifierBuilderClassName!=null) {
			// create the factory and instantiate the data writer
			classifierBuilder = InitializableFactory.create(context,classifierBuilderClassName, JarClassifierBuilder.class);
		}
	}


	
	@Override
	public void collectionProcessComplete()
	throws AnalysisEngineProcessException
	{
		try
		{
			System.out.println(classifierTrainingDataFolder);
			classifierBuilder.trainClassifier(new File(classifierTrainingDataFolder)
					, new String[]{});
			classifierBuilder.packageClassifier(new File(
					classifierTrainingDataFolder));
		}
		catch(Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void process(JCas arg0) throws AnalysisEngineProcessException
	{
				
	}
	
	public static AnalysisEngine createAnalysisEngine(TypeSystemDescription typeSystem,
			 String trainingDataFolder, String builderClassName, boolean resolvePaths)
	 throws ResourceInitializationException {
		String trainingDataFolderPath = null;
		if (resolvePaths) {
			try {
				System.out.println("Calling FileUtils.searchFolderInUserDir("
						+ trainingDataFolder + ") from " + ISITrainer.class
						+ "[createAnalysisEngine]");
				//trainingDataFolderPath = FileUtils.searchFolderInLocation(trainingDataFolder, new java.io.File( "." ).getCanonicalPath());
				if (trainingDataFolderPath == null) {
					trainingDataFolderPath = FileUtils
							.searchFolderInUserDir(trainingDataFolder);
				}
				System.out.println(trainingDataFolderPath);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			trainingDataFolderPath = trainingDataFolder;
		}
		AnalysisEngineDescription aed = AnalysisEngineFactory.createPrimitiveDescription(
			ISITrainer.class, typeSystem, 
			// name,      									value
			ISITrainer.PARAM_CLASSIFIER_TRAINING_DATA_FOLDER, 	trainingDataFolderPath,
			ISITrainer.PARAM_CLASSIFIER_BUILDER_CLASS_NAME, 	builderClassName);
	 
	 	return AnalysisEngineFactory.createPrimitive(aed);
	 }
}

