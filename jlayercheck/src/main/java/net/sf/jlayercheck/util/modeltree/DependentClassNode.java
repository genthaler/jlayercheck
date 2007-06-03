package net.sf.jlayercheck.util.modeltree;

import net.sf.jlayercheck.util.model.ClassDependency;

public class DependentClassNode extends DefaultClassNode implements UnallowedOrAllowedDependency {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = -1847980422895139419L;
	protected ClassDependency classDependency;
	protected DependenciesTreeModel dependenciesTreeModel;
	protected DependenciesTreeModel incomingDependenciesTreeModel;
	
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

	/**
	 * Contains the tree that contains all dependencies of this class to other modules, packages and classes.
	 * @return tree model
	 */
	public DependenciesTreeModel getDependenciesTreeModel() {
		return dependenciesTreeModel;
	}

	public void setDependenciesTreeModel(DependenciesTreeModel dependenciesTreeModel) {
		this.dependenciesTreeModel = dependenciesTreeModel;
	}

	public void setIncomingDependenciesTreeModel(DependenciesTreeModel dtm) {
		this.incomingDependenciesTreeModel = dtm;
	}

	public DependenciesTreeModel getIncomingDependenciesTreeModel() {
		return incomingDependenciesTreeModel;
	}
}
