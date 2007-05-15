package net.sf.jlayercheck.util.modeltree;

import java.util.Vector;

import javax.swing.tree.MutableTreeNode;

/**
 * Describes a module that can contain java packages.
 * 
 * @author webmaster@earth3d.org
 */
public interface ModuleNode extends MutableTreeNode {
	public Vector<PackageNode> getPackages();

	/**
	 * Returns the name of this module.
	 * 
	 * @return modulename
	 */
	public String getModuleName();

	/**
	 * Adds the given packageNode as child.
	 * 
	 * @param packagenode
	 */
	public void add(PackageNode packagenode);
}
