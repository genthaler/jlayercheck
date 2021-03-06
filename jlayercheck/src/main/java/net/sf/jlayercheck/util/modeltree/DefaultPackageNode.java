package net.sf.jlayercheck.util.modeltree;

import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @see PackageNode
 * @author webmaster@earth3d.org
 *
 */
public class DefaultPackageNode extends DefaultMutableTreeNode implements PackageNode {

	protected String packagename;
	protected boolean unassignedPackage;
	
	public DefaultPackageNode(String packagename) {
		super();
		
		this.packagename = packagename;
		setAllowsChildren(true);
	}
		
	/**
	 * 
	 */
	private static final long serialVersionUID = -3407786085047017446L;

	public DefaultPackageNode() {
		setAllowsChildren(true);
	}

	public Vector<ClassNode> getClasses() {
		if (getChildCount() > 0) {
			Vector<ClassNode> result = new Vector<ClassNode>();
			result.addAll(children);
			return result;
		}
		
		return new Vector<ClassNode>();
	}

	public String getPackagename() {
		return packagename;
	}
	
	public String toString() {
		return getPackagename().replace("/", ".");
	}

	public String getName() {
		return getPackagename();
	}

	public void add(ClassNode classnode) {
		super.add(classnode);
	}

	public ClassNode getClass(String name) {
		for(ClassNode node : getClasses()) {
			if (node.getName().equals(name)) {
				return node;
			}
		}
		
		return null;
	}

	/**
	 * Returns true if this package is the "unassigned" package for excluded classes.
	 * 
	 * @return
	 */
	public boolean isUnassignedPackage() {
		return unassignedPackage;
	}

	public void setUnassignedPackage(boolean unassignedPackage) {
		this.unassignedPackage = unassignedPackage;
	}
}
