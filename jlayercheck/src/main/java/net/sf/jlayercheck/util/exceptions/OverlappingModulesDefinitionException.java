package net.sf.jlayercheck.util.exceptions;

public class OverlappingModulesDefinitionException extends Exception {
    /** SUID  */
	private static final long serialVersionUID = -2437761350644402423L;
	
	protected String message;
    
    public OverlappingModulesDefinitionException(String msg) {
        this.message = msg;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }
}
