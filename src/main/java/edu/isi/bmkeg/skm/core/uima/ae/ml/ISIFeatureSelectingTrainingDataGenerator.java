package edu.isi.bmkeg.skm.core.uima.ae.ml;

import java.io.IOException;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.classifier.DataWriter;
import org.cleartk.classifier.DataWriterFactory;
import org.cleartk.util.ReflectionUtil;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.ConfigurationParameterFactory;
import org.uimafit.factory.initializable.Initializable;
import org.uimafit.factory.initializable.InitializableFactory;

public abstract class ISIFeatureSelectingTrainingDataGenerator<OUTCOME_TYPE>
		extends JCasAnnotator_ImplBase implements Initializable {

	public final static String PARAM_DATA_WRITER_FACTORY_CLASS_NAME = ConfigurationParameterFactory
			.createConfigurationParameterName(
					ISIFeatureSelectingTrainingDataGenerator.class,
					"dataWriterFactoryClassName");

	@ConfigurationParameter(mandatory = false, description = "provides the full name of the DataWriterFactory class to be used.")
	private String dataWriterFactoryClassName;

	protected DataWriter<OUTCOME_TYPE> dataWriter;

	@Override
	public void initialize(UimaContext context)
			throws ResourceInitializationException {
		
		super.initialize(context);
		
		// InitializeUtil.initialize(this, context);
		// uimafit magic in uimafit...JCasAnnotatorImplBase is supposed to do
		// this...
		dataWriterFactoryClassName = (String) context
				.getConfigParameterValue(PARAM_DATA_WRITER_FACTORY_CLASS_NAME);

		// create the factory and instantiate the data writer
		DataWriterFactory<?> factory = InitializableFactory.create(
				context, 
				dataWriterFactoryClassName,
				DataWriterFactory.class);

		DataWriter<?> untypedDataWriter = null;
		try {
			untypedDataWriter = factory.createDataWriter();
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}

		InitializableFactory.initialize(untypedDataWriter, context);
		this.dataWriter = ReflectionUtil.uncheckedCast(untypedDataWriter);
	}

	@Override
	public void collectionProcessComplete()
			throws AnalysisEngineProcessException {
		super.collectionProcessComplete();
	}

	protected String getParameterValue(UimaContext context, String paramName)
			throws ResourceInitializationException {
		String returnValue = "";
		Object o = context.getConfigParameterValue(paramName);
		if (o == null) {
			String message = "no value for :" + paramName;
			throw new ResourceInitializationException(new Exception(message));
		}
		if (o.getClass().getName().equals("java.lang.String")) {
			returnValue = (String) o;
		} else {
			if (o == null) {
				Exception squarePeg = new Exception("null value for "
						+ paramName);
				throw new ResourceInitializationException(squarePeg);
			} else {
				Exception squarePeg = new Exception("wrong type: "
						+ o.getClass() + " for " + paramName);
				throw new ResourceInitializationException(squarePeg);
			}
		}

		return returnValue;
	}

	protected boolean isTraining() {
		return dataWriter != null;
	}

}
