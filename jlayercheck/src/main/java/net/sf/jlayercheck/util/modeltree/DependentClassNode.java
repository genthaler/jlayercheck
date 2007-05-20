package net.sf.jlayercheck.util.modeltree;

import net.sf.jlayercheck.util.model.ClassDependency;

public class DependentClassNode extends DefaultClassNode implements UnallowedOrAllowedDependency {

	protected ClassDependency classDependency;
	
	public DependentClassNode(ClassDependency cd) {
		super(cd.getDependency());
		
		this.classDependency = cd;
	}

	/**
	 * Returns the ClassDependency that is represented by
	 * this ClassNode.
	 * 
	 * @return
	 */
	public ClassDependency getClassDependency() {
		return classDependency;
	}

	public void setClassDependency(ClassDependency classDependency) {
		this.classDependency = classDependency;
	}

	public boolean isUnallowedDependency() {
		return getClassDependency().isUnallowedDependency();
	}
}
