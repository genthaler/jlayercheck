package net.sf.jlayercheck.util.modeltree;

import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @see ModuleNode
 * @author webmaster@earth3d.org
 *
 */
public class DefaultModuleNode extends DefaultMutableTreeNode implements ModuleNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1419905413709544489L;

	protected String moduleName;
	
	public DefaultModuleNode(String moduleName) {
		assert(moduleName != null);
		
		this.moduleName = moduleName; 
		
		setAllowsChildren(true);
	}

	public Vector<PackageNode> getPackages() {
		return (Vector<PackageNode>) children();
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
}
