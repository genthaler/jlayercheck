package net.sf.jlayercheck.util.modeltree;

import java.util.Vector;

import javax.swing.tree.MutableTreeNode;


/**
 * Describes a java package that can contain java classes.
 * 
 * @author webmaster@earth3d.org
 */
public interface PackageNode extends MutableTreeNode, NamedTreeNode {
	/**
	 * Returns the classes in this package.
	 * @return vector of classes
	 */
	public Vector<ClassNode> getClasses();

	/**
	 * Returns the name of this package.
	 * 
	 * @return packagename
	 */
	public String getPackagename();

	/**
	 * Adds the given ClassNode as child.
	 * 
	 * @param classnode
	 */
	public void add(ClassNode classnode);

	/**
	 * Retrieves the ClassNode with the given name. 
	 * @param name
	 * @return ClassNode
	 */
	public ClassNode getClass(String name);
	
	/**
	 * Returns true if this is the "unassigned" package that
	 * contains excluded classes.
	 * 
	 * @return
	 */
	public boolean isUnassignedPackage();
}
