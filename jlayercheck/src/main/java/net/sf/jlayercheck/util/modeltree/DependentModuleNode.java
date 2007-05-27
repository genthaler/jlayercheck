package net.sf.jlayercheck.util.modeltree;

public class DependentModuleNode extends DefaultModuleNode implements UnallowedOrAllowedDependency {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5313450651664954471L;

	public DependentModuleNode(String moduleName, boolean unassignedModule) {
		super(moduleName, unassignedModule);
	}

	public DependentModuleNode(String moduleName) {
		super(moduleName);
	}

	protected boolean unallowedDependency;

	public boolean isUnallowedDependency() {
		return unallowedDependency;
	}

	public void setUnallowedDependency(boolean unallowedDependency) {
		this.unallowedDependency = unallowedDependency;
	}
}
