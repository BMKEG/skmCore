/**
 * 
 */
package edu.isi.bmkeg.skm.core.exceptions;

/**
 * @author cartic
 *
 */
public class StringCleanerException extends Exception
{
	
	public StringCleanerException(String message)
	{
		super(message);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage()
	{
		return "Check the input citation stems. They must either end in -ns or -ns.xml";
	}
}
