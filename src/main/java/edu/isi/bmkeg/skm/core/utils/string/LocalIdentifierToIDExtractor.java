package edu.isi.bmkeg.skm.core.utils.string;

import java.util.Scanner;
import java.util.regex.Pattern;

import edu.isi.bmkeg.skm.core.exceptions.StringCleanerException;

public class LocalIdentifierToIDExtractor implements IStringCleaner
{
	protected String pattern; 
	protected String idType;
	
	public static final String PMID="PMID:";
	public static final String BMKEGID="BMKEGID:";
	public static final String defaultPattern = "[0-9]+";
	
	public LocalIdentifierToIDExtractor(boolean useBmkegID)
	{
		if(!useBmkegID)
			this.idType = PMID;
		else
			this.idType = BMKEGID;
	}
		
	public String cleanItUp(String dirtyString) throws StringCleanerException
	{
		Scanner s= new Scanner(dirtyString);
		String match = s.findInLine(Pattern.compile(defaultPattern));
		if(match!=null){
			return match;
		}
		return null;
	}

	public String getPattern()
	{
		return pattern;
	}

	public String getIdType()
	{
		return idType;
	}

	public void setIdType(String idType)
	{
		this.idType = idType;
	}

}
