package net.sf.jlayercheck.util.modeltree;

import java.util.Vector;

import javax.swing.tree.MutableTreeNode;

/**
 * Describes a module that can contain java packages.
 * 
 * @author webmaster@earth3d.org
 */
public interface ModuleNode extends MutableTreeNode, NamedTreeNode, Comparable<ModuleNode> {
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
	
	/**
	 * Searches for the PackageNode with the given name.
	 * 
	 * @param packagename
	 * @return
	 */
	public PackageNode getPackage(String packagename);

	/**
	 * Returns true if this module is the "unassigned" module for excluded classes.
	 */
	public boolean isUnassignedModule();
}
