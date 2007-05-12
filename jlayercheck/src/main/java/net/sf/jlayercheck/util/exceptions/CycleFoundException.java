package net.sf.jlayercheck.util.exceptions;

/**
 * Thrown by GraphModuleDependencies when the input graph
 * contains cycles.
 * 
 * @author webmaster@earth3d.org
 */
public class CycleFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5360564935723981975L;

	public CycleFoundException() {
		
	}
}
