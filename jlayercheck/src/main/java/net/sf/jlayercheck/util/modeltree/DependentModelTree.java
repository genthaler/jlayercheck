package net.sf.jlayercheck.util.modeltree;

public class DependentModelTree extends DefaultModelTree implements
		UnallowedOrAllowedDependency {

	protected boolean unallowedDependency;

	public DependentModelTree() {
	}

	public boolean isUnallowedDependency() {
		return unallowedDependency;
	}

	public void setUnallowedDependency(boolean unallowedDependency) {
		this.unallowedDependency = unallowedDependency;
	}
}
