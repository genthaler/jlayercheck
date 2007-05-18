package net.sf.jlayercheck.util.model;

import java.util.Set;
import java.util.TreeSet;

public class ClassDependency {
	protected String dependency;
	protected Set<Integer> lineNumbers = new TreeSet<Integer>();
	
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

	public Set<Integer> getLineNumbers() {
		return lineNumbers;
	}

	public String getDependency() {
		return dependency;
	}
}
