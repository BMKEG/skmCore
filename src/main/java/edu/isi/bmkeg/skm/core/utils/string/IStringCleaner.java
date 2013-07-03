package edu.isi.bmkeg.skm.core.utils.string;

import edu.isi.bmkeg.skm.core.exceptions.StringCleanerException;


public interface IStringCleaner {

	/**
	 * Clean string passed as argument according to rules implemented the filter
	 * @param dirtyString
	 * @return clean string
	 * @throws StringCleanerException 
	 * @throws edu.isi.bmkeg.bin.StringCleanerException 
	 */
	public String cleanItUp(String dirtyString) throws StringCleanerException;
	
}
