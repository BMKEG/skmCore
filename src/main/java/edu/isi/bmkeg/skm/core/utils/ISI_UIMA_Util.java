package edu.isi.bmkeg.skm.core.utils;

import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;

import edu.isi.bmkeg.skm.core.exceptions.ISITriageMissingIDException;
import edu.uchsc.ccp.uima.annotation.CCPDocumentInformation;
import edu.uchsc.ccp.uima.annotation.CCPTextAnnotation;
import edu.uchsc.ccp.uima.util.UIMA_Util;

public class ISI_UIMA_Util extends UIMA_Util
{

	public ISI_UIMA_Util()
	{
		super();
	}
	/**
	 * 
	 * Sets the CCPDocumentInformation documentCollectionID field
	 * @author cartic
	 * @param jcas
	 * @param documentCollectionID
	 */
	public static void setDocumentCollectionID(JCas jcas, int documentCollectionID) {
		CCPDocumentInformation docInfo;
		FSIterator it = jcas.getJFSIndexRepository().getAnnotationIndex(CCPDocumentInformation.type).iterator();
		if (it.hasNext()) { /* there should be at most one CCPDocumentInformation annotation */
			docInfo = (CCPDocumentInformation) it.next();
		} else {
			docInfo = new CCPDocumentInformation(jcas);
			docInfo.addToIndexes();
		}
		docInfo.setDocumentCollectionID(documentCollectionID);
	}

	/**
	 * 
	 * Sets the CCPDocumentInformation documentCollectionID field
	 * @author cartic
	 * @param jcas
	 * @return documentCollectionID
	 */
	public static int getDocumentCollectionID(JCas jcas) {
		CCPDocumentInformation docInfo;
		FSIterator it = jcas.getJFSIndexRepository().getAnnotationIndex(CCPDocumentInformation.type).iterator();
		if (it.hasNext()) { /* there should be at most one CCPDocumentInformation annotation */
			docInfo = (CCPDocumentInformation) it.next();
			return docInfo.getDocumentCollectionID();
		} else {
			System.err.println("No documentcollectionID found, returning -1.");
			return -1;
		}
		

	}
	
	
	
	
	/**
	 * Set alternate document IDS
	 * @param jcas
	 * @param s StringArray containing the IDs
	 */
	public static void setDocumentSecondaryIDs(JCas jcas,StringArray s){
		CCPDocumentInformation docInfo;
		FSIterator it = jcas.getAnnotationIndex(CCPDocumentInformation.type).iterator();//jcas.getJFSIndexRepository().getAnnotationIndex(ISIDocumentInformation.type).iterator();
		if (it.hasNext()) { /* there should be at most one CCPDocumentInformation annotation */
			docInfo = (CCPDocumentInformation) it.next();
		}else{
			docInfo = new CCPDocumentInformation(jcas);
			docInfo.addToIndexes();
		}
		docInfo.setSecondaryDocumentIDs(s);
	}

	private static String getSecondaryIdByKey(String key, StringArray s){
		for (int i = 0; i < s.size(); i++)
		{
			String val = s.get(i);
			if(val.toLowerCase().startsWith(key.toLowerCase())){
				return val.replace(key, "").replace(":", "");
			}
		}
		return null;
	}
	/**
	 * get alternate document IDs
	 * @param jcas
	 * @return StringArray containing the IDs
	 */
	public static String getDocumentSecondaryID(JCas jcas, String idKey) throws ISITriageMissingIDException{
		CCPDocumentInformation docInfo;
		FSIterator it = jcas.getAnnotationIndex(CCPDocumentInformation.type).iterator();//jcas.getJFSIndexRepository().getAnnotationIndex(ISIDocumentInformation.type).iterator();
		if (it.hasNext()) { /* there should be at most one CCPDocumentInformation annotation */
			docInfo = (CCPDocumentInformation) it.next();
			return getSecondaryIdByKey(idKey,docInfo.getSecondaryDocumentIDs());
		} else {
			System.err.println("No secondary id with key="+idKey+" found, returning -1.");
			return "-1";
		}
	}

	public static StringArray getDocumentSecondaryIDs(JCas jcas) throws ISITriageMissingIDException{
		CCPDocumentInformation docInfo;
		FSIterator it = jcas.getAnnotationIndex(CCPDocumentInformation.type).iterator();//jcas.getJFSIndexRepository().getAnnotationIndex(ISIDocumentInformation.type).iterator();
		if (it.hasNext()) { /* there should be at most one CCPDocumentInformation annotation */
			docInfo = (CCPDocumentInformation) it.next();
			return docInfo.getSecondaryDocumentIDs();
		} else {
			System.err.println("No secondary ids found, returning null.");
			return null;
		}

	}

	
	
	/**
	 * Set the classification type for a document
	 * @param jcas
	 * @param type
	 */
	public static void setClassificationType(JCas jcas, String type){
		CCPDocumentInformation docInfo;
		FSIterator it = jcas.getJFSIndexRepository().getAnnotationIndex(CCPDocumentInformation.type).iterator();
		if (it.hasNext()) { /* there should be at most one CCPDocumentInformation annotation */
			docInfo = (CCPDocumentInformation) it.next();
		} else {
			docInfo = new CCPDocumentInformation(jcas);
			docInfo.addToIndexes();
		}
		docInfo.setClassificationType(type);
	}
	/**
	 * get the classification Type
	 * @param jcas
	 * @return
	 */
	public static String getClassificationType(JCas jcas){
		CCPDocumentInformation docInfo;
		FSIterator it = jcas.getJFSIndexRepository().getAnnotationIndex(CCPDocumentInformation.type).iterator();
		if (it.hasNext()) { /* there should be at most one CCPDocumentInformation annotation */
			docInfo = (CCPDocumentInformation) it.next();
			return docInfo.getClassificationType();
		} else {
			System.err.println("No classification information found, returning -1.");
			return "-1";
		}
	}

	public static void displayAllAnnotations(JCas jcas){
		FSIterator it = jcas.getJFSIndexRepository().getAnnotationIndex(Annotation.type).iterator();
		it.moveToFirst();
		while(it.hasNext()){
			Annotation fs = (Annotation) it.get();
			it.moveToNext();
			System.out.print(fs.toString(5));
			if(fs instanceof CCPTextAnnotation){
				System.out.println(((CCPTextAnnotation)fs).getCoveredText()+"-[found_in]-"+((CCPTextAnnotation)fs).getClassMention().getMentionName());
			}
		}
	}

	
	
	
	
}
