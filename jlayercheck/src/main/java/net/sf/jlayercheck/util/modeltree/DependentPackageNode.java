package net.sf.jlayercheck.util.modeltree;

public class DependentPackageNode extends DefaultPackageNode implements UnallowedOrAllowedDependency {
	protected boolean unallowedDependency;

	public DependentPackageNode(String packagename) {
		super(packagename);
	}

	public boolean isUnallowedDependency() {
		return unallowedDependency;
	}

	public void setUnallowedDependency(boolean unallowedDependency) {
		this.unallowedDependency = unallowedDependency;
	}
}
