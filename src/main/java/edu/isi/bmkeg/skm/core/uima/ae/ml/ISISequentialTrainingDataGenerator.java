                                                                     
                                                                     
                                                                     
                                             
package edu.isi.bmkeg.skm.core.uima.ae.ml;

import java.io.IOException;

import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.classifier.SequenceDataWriter;
import org.cleartk.classifier.SequenceDataWriterFactory;
import org.cleartk.util.ReflectionUtil;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.ConfigurationParameterFactory;
import org.uimafit.factory.initializable.Initializable;
import org.uimafit.factory.initializable.InitializableFactory;


public abstract class ISISequentialTrainingDataGenerator<OUTCOME_TYPE> extends JCasAnnotator_ImplBase implements Initializable {
	
		public final String PARAM_DATA_WRITER_FACTORY_CLASS_NAME = ConfigurationParameterFactory
		.createConfigurationParameterName(ISISequentialTrainingDataGenerator.class, "dataWriterFactoryClassName");

		@ConfigurationParameter(mandatory = false, description = "provides the full name of the DataWriterFactory class to be used.")
		private String dataWriterFactoryClassName;

		protected SequenceDataWriterFactory<OUTCOME_TYPE> dataWriter;

		@Override
		public void initialize(UimaContext context) throws ResourceInitializationException {
			super.initialize(context);

			dataWriterFactoryClassName = (String) getParameterValue(context,PARAM_DATA_WRITER_FACTORY_CLASS_NAME);
			if (dataWriterFactoryClassName != null) {
				// create the factory and instantiate the data writer
				SequenceDataWriterFactory<?> factory = InitializableFactory
				.create(context, dataWriterFactoryClassName, SequenceDataWriterFactory.class);
				SequenceDataWriter<?> untypedDataWriter;
				try {
					untypedDataWriter = factory.createDataWriter();
				}
				catch (IOException e) {
					throw new ResourceInitializationException(e);
				}

				InitializableFactory.initialize(untypedDataWriter, context);
				this.dataWriter = ReflectionUtil.uncheckedCast(untypedDataWriter);
			}
		}

	
		
		private String getParameterValue(UimaContext context, String paramName) throws ResourceInitializationException {
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
