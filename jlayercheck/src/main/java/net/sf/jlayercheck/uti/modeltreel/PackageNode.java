package net.sf.jlayercheck.uti.modeltreel;

import java.util.Vector;

import javax.swing.tree.TreeNode;


/**
 * Describes a java package that can contain java classes.
 * 
 * @author webmaster@earth3d.org
 */
public interface PackageNode extends TreeNode {
	public Vector<ClassNode> getClasses();
}
