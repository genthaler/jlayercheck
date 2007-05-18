package net.sf.jlayercheck.util.modeltree;

import java.util.Vector;

import javax.swing.tree.MutableTreeNode;


/**
 * Describes a java package that can contain java classes.
 * 
 * @author webmaster@earth3d.org
 */
public interface PackageNode extends MutableTreeNode, NamedTreeNode {
	public Vector<ClassNode> getClasses();
}
