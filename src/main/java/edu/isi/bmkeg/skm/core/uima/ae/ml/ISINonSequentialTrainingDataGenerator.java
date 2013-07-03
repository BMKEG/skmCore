package edu.isi.bmkeg.skm.core.uima.ae.ml;

import java.io.IOException;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.classifier.Classifier;
import org.cleartk.classifier.ClassifierFactory;
import org.cleartk.classifier.CleartkProcessingException;
import org.cleartk.classifier.DataWriter;
import org.cleartk.classifier.DataWriterFactory;
import org.cleartk.util.ReflectionUtil;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.descriptor.ExternalResource;
import org.uimafit.factory.ConfigurationParameterFactory;
import org.uimafit.factory.initializable.Initializable;
import org.uimafit.factory.initializable.InitializableFactory;


public abstract class ISINonSequentialTrainingDataGenerator<OUTCOME_TYPE> 
extends JCasAnnotator_ImplBase implements Initializable {
	
	public static final String RESOURCE_INJECTED_CLASSIFIER = "sharedClassifier";
	@ExternalResource(mandatory=false, key = RESOURCE_INJECTED_CLASSIFIER)
	private SharedClassifierModel sharedClassifier;
	
	public static final String PARAM_CLASSIFIER_FACTORY_CLASS_NAME = ConfigurationParameterFactory
		.createConfigurationParameterName(ISINonSequentialTrainingDataGenerator.class, "classifierFactoryClassName");

	@ConfigurationParameter(mandatory = false, 
			description = "provides the full name of the ClassifierFactory class to be used.", 
			defaultValue = "org.cleartk.classifier.jar.JarClassifierFactory")
	private String classifierFactoryClassName;

	public final static String PARAM_DATA_WRITER_FACTORY_CLASS_NAME = ConfigurationParameterFactory
		.createConfigurationParameterName(ISINonSequentialTrainingDataGenerator.class, "dataWriterFactoryClassName");

	@ConfigurationParameter(mandatory = false, 
			description = "provides the full name of the DataWriterFactory class to be used.")
	private String dataWriterFactoryClassName;

	public final static String PARAM_IS_CLASSIFIER_EXTERNAL_RESOURCE = ConfigurationParameterFactory
	.createConfigurationParameterName(ISINonSequentialTrainingDataGenerator.class, "isClassifierInstantiated");
	@ConfigurationParameter(mandatory = true, 
			description = "boolean value used to account for the case when the classifier is provided as an externally injected dependency")
	private boolean isClassifierInstantiated;


	
	
	protected Classifier<OUTCOME_TYPE> classifier;
	protected DataWriter<OUTCOME_TYPE> dataWriter;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		isClassifierInstantiated = (Boolean) context.getConfigParameterValue(PARAM_IS_CLASSIFIER_EXTERNAL_RESOURCE);
		if (!isClassifierInstantiated)
		{
			//InitializeUtil.initialize(this, context);
			// uimafit magic in uimafit...JCasAnnotatorImplBase is supposed to do this...
			dataWriterFactoryClassName = (String) context.getConfigParameterValue(PARAM_DATA_WRITER_FACTORY_CLASS_NAME);
			classifierFactoryClassName = (String) context.getConfigParameterValue(PARAM_CLASSIFIER_FACTORY_CLASS_NAME);
			if (dataWriterFactoryClassName != null)
			{
				// create the factory and instantiate the data writer
				DataWriterFactory<?> factory
				//= UIMAUtil.create(dataWriterFactoryClassName, DataWriterFactory.class, context);
				= InitializableFactory.create(context, dataWriterFactoryClassName, DataWriterFactory.class);

				DataWriter<?> untypedDataWriter = null;
				try
				{
					untypedDataWriter = factory.createDataWriter();
				} catch (IOException e)
				{
					throw new ResourceInitializationException(e);
				}

				//UIMAUtil.initialize(untypedDataWriter, context);
				InitializableFactory.initialize(untypedDataWriter, context);
				this.dataWriter = ReflectionUtil.uncheckedCast(untypedDataWriter);
			} else
			{
				// create the factory and instantiate the classifier
				ClassifierFactory<?> factory = InitializableFactory.create(context, classifierFactoryClassName, ClassifierFactory.class);
				Classifier<?> untypedClassifier;
				try
				{
					untypedClassifier = factory.createClassifier();
				} catch (IOException e)
				{
					throw new ResourceInitializationException(e);
				}

				this.classifier = ReflectionUtil.uncheckedCast(untypedClassifier);

				ReflectionUtil.checkTypeParameterIsAssignable(ISINonSequentialTrainingDataGenerator.class, "OUTCOME_TYPE", this, Classifier.class, "OUTCOME_TYPE", this.classifier);
				//UIMAUtil.initialize(untypedClassifier, context);
				InitializableFactory.initialize(untypedClassifier, context);

			}
		}else{//isClassifierInstantiated==true
			this.classifier = ReflectionUtil.uncheckedCast(sharedClassifier.getClassifier());
			ReflectionUtil.checkTypeParameterIsAssignable(ISINonSequentialTrainingDataGenerator.class, "OUTCOME_TYPE", this, Classifier.class, "OUTCOME_TYPE", this.classifier);
		}
	}

	@Override
	public void collectionProcessComplete() throws AnalysisEngineProcessException {
		super.collectionProcessComplete();
		if (isTraining()) {
			try {
				dataWriter.finish();
			}
			catch (CleartkProcessingException ctke) {
				throw new AnalysisEngineProcessException(ctke);
			}
		}
	}
	protected String getParameterValue(UimaContext context, String paramName) throws ResourceInitializationException {
		String returnValue = "";
		Object o = context.getConfigParameterValue(paramName);
		if (o == null) {
			String message = "no value for :" + paramName;
			throw new ResourceInitializationException(new Exception(message));
		}
		if (o.getClass().getName().equals("java.lang.String")) {
			returnValue = (String) o;
		}  
		else {
			if (o == null) {
				Exception squarePeg = new Exception("null value for " + paramName );
				throw new ResourceInitializationException(squarePeg);
			} else {
				Exception squarePeg = new Exception("wrong type: " + o.getClass() + " for " + paramName );
				throw new ResourceInitializationException(squarePeg);
			}
		}

		return returnValue;
	}
	protected boolean isTraining() {
		return dataWriter != null;
	}


}
