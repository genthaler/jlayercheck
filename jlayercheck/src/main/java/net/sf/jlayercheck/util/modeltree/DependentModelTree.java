package net.sf.jlayercheck.util.modeltree;

public class DependentModelTree extends DefaultModelTree implements
		UnallowedOrAllowedDependency {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = -7521909216639713648L;
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
