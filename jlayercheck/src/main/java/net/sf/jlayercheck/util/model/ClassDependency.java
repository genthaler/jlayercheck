package net.sf.jlayercheck.util.model;

import java.util.Set;
import java.util.TreeSet;

/**
 * Represents the dependency to a specified class.
 * 
 * @author webmaster@earth3d.org
 */
public class ClassDependency {
	protected String dependency;
	protected Set<Integer> lineNumbers = new TreeSet<Integer>();
	protected boolean unallowedDependency;
	
	public ClassDependency(String dependency) {
		this.dependency = dependency;
	}
	
	@Override
	public boolean equals(Object obj) {
		return dependency.equals(obj);
	}

	@Override
	public int hashCode() {
		return dependency.hashCode();
	}

	public void addLineNumber(int lineNumber) {
		lineNumbers.add(lineNumber);
	}

	/**
	 * Returns a Set of line numbers, where dependencies to this
	 * class occured in the source file.
	 * 
	 * @return Set of line numbers
	 */
	public Set<Integer> getLineNumbers() {
		return lineNumbers;
	}

	/**
	 * The name of the class to which the dependency exists.
	 * 
	 * @return classname
	 */
	public String getDependency() {
		return dependency;
	}

	/**
	 * If this dependency is allowed or not by the module/architecture configuration.
	 * @return
	 */
	public boolean isUnallowedDependency() {
		return unallowedDependency;
	}

	public void setUnallowedDependency(boolean unallowedDependency) {
		this.unallowedDependency = unallowedDependency;
	}
}
