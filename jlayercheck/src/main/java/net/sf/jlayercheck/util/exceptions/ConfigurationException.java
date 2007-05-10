package net.sf.jlayercheck.util.exceptions;

/**
 * Thrown if configuration contains errors.
 * 
 * @author timo 
 * @author $Author: gunia $
 * @version $Id: ConfigurationException.java,v 1.2 2007/05/04 06:08:56 gunia Exp $
 */
public class ConfigurationException extends Exception {
	/** SUID  */
	private static final long serialVersionUID = 5867760385728545393L;
	
	public ConfigurationException() {
		super();
	}
	
	public ConfigurationException(String msg) {
		super(msg);
	}
	
	public ConfigurationException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
}
