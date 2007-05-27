package net.sf.jlayercheck.util.modeltree;

import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @see ModuleNode
 * @author webmaster@earth3d.org
 */
public class DefaultModuleNode extends DefaultMutableTreeNode implements ModuleNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1419905413709544489L;

	protected String moduleName;
	
	protected boolean unassignedModule;
	
	public DefaultModuleNode(String moduleName) {
		assert(moduleName != null);
		
		this.moduleName = moduleName; 
		
		setAllowsChildren(true);
	}

	public DefaultModuleNode(String moduleName2, boolean unassignedModule2) {
		this(moduleName2);
		
		setUnassignedModule(unassignedModule2);
	}

	public Vector<PackageNode> getPackages() {
		if (getChildCount() > 0) {
			Vector<PackageNode> result = new Vector<PackageNode>();
			result.addAll(children);
			return result;
		}
		
		return new Vector<PackageNode>();
	}

	/**
	 * Returns the name of this module.
	 */
	public String getModuleName() {
		return moduleName;
	}

	/**
	 * Adds the given package node as child.
	 */
	public void add(PackageNode packagenode) {
		super.add(packagenode);
	}
	
	public String toString() {
		return getModuleName();
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

	public String getName() {
		return getModuleName();
	}
	
	/**
	 * Searches for the PackageNode with the given name.
	 * 
	 * @param packagename
	 * @return
	 */
	public PackageNode getPackage(String packagename) {
		if (getPackages() == null) return null;
		
		for(PackageNode node : getPackages()) {
			if (node.getPackagename().equals(packagename)) {
				return node;
			}
		}
		
		return null;
	}

	public int compareTo(ModuleNode o) {
		return getModuleName().compareTo(o.getModuleName());
	}

	/**
	 * Returns true if this module is the "unassigned" module for excluded classes.
	 */
	public boolean isUnassignedModule() {
		return unassignedModule;
	}

	public void setUnassignedModule(boolean unassignedModule) {
		this.unassignedModule = unassignedModule;
	}
}
