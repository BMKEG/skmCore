package edu.isi.bmkeg.skm.core.uima.ae.ml.features;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.cleartk.classifier.Feature;
import org.cleartk.classifier.feature.extractor.CleartkExtractorException;
import org.cleartk.classifier.feature.extractor.simple.SimpleFeatureExtractor;

import edu.isi.bmkeg.skm.core.utils.fileIterators.FlatFileContentIterator;

public class AutoSlogFeatureExtractor implements SimpleFeatureExtractor
{
	private File casesFolder;

	private static Map<String,AutoSlogFeature> featureMap;

	public AutoSlogFeatureExtractor(String casesFolder, String featureListLocation)
	{
		this.casesFolder = new File(casesFolder);
		if(featureMap==null){
			loadASlogFeatures(featureListLocation);
		}else{
			System.out.println("Feature map is a static object already been loaded!!");
		}
	}



	@Override
	public List<Feature> extract(JCas view, Annotation focusAnnotation) throws CleartkExtractorException
	{
		
		return null;
	}

	private void parse(File f){

	}

	private void loadASlogFeatures(String featureListLocation){
		featureMap = new HashMap<String, AutoSlogFeature>();
		boolean recordActFcns = false;
		StringBuilder actFcnsString = new StringBuilder();
		AutoSlogFeature feature = null;
		FlatFileContentIterator ffcit = new FlatFileContentIterator(featureListLocation);
		String line = null;
		while(ffcit.hasNext()){
			line = ffcit.next().trim();
			if(line.startsWith(AutoSlogFeature.CF)){
				if(feature!=null){
					featureMap.put(feature.getName(), feature);
				}
				feature = new AutoSlogFeature();
				actFcnsString.delete(0, actFcnsString.length());
			}
			if(line.startsWith(AutoSlogFeature.Name)){
				feature.setName(line.replace(AutoSlogFeature.Name, "").trim());
			}
			if(line.startsWith(AutoSlogFeature.Negation)){
				feature.setIsNegation(new Boolean(line.replace(AutoSlogFeature.Negation, "").trim()));
			}
			if(line.startsWith(AutoSlogFeature.Anchor)){
				feature.setAnchor(line.replace(AutoSlogFeature.Anchor, "").trim());
			}
			if(line.startsWith(AutoSlogFeature.Slot)){
				feature.setAct_fcns(actFcnsString.toString());
				recordActFcns = false;
				feature.setSlot(line.replace(AutoSlogFeature.Slot, "").trim());
			}
			if(line.startsWith(AutoSlogFeature.Act_Fncs)){
				recordActFcns = true;
				actFcnsString.append(line.replace(AutoSlogFeature.Act_Fncs, "").trim());
			}else{
				if(recordActFcns){
					actFcnsString.append(line.replace(AutoSlogFeature.Act_Fncs, "").trim());
				}
			}
			if(line.startsWith(AutoSlogFeature.Stats)){
				feature.addStat(AutoSlogFeature.frequency, line.replace(AutoSlogFeature.Stats, "").trim());
			}
			if(line.startsWith(AutoSlogFeature.relativeFreq)){
				feature.addStat(AutoSlogFeature.relativeFreq, line.replace(AutoSlogFeature.relativeFreq, "").replace("=", "").trim());
			}
			if(line.startsWith(AutoSlogFeature.cond_prob)){
				feature.addStat(AutoSlogFeature.cond_prob, line.replace(AutoSlogFeature.cond_prob, "").replace("=", "").trim());
			}

			if(line.startsWith(AutoSlogFeature.rlog_score)){
				feature.addStat(AutoSlogFeature.rlog_score, line.replace(AutoSlogFeature.rlog_score, "").replace("=", "").trim());
			}

		}
		ffcit.close();
		featureMap.put(feature.getName(), feature);
	}



	public static Map<String, AutoSlogFeature> getFeatureMap()
	{
		return featureMap;
	}

}
