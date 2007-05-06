package net.sf.jlayercheck.util;

/**
 * This exception is used in the search for orphaned classes when an error occurs.
 * 
 * @author webmaster@earth3d.org
 */
public class OrphanedSearchException extends Exception {
    /** SUID  */
	private static final long serialVersionUID = -8167511109891572811L;
	
	protected String message;
    
    public OrphanedSearchException(String msg) {
        this.message = msg;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }
}
