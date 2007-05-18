package net.sf.jlayercheck.util.modeltree;

import java.util.Set;

import javax.swing.tree.TreeNode;

import net.sf.jlayercheck.util.model.ClassDependency;

/**
 * Describes a single class file in the model tree.
 * 
 * @author webmaster@earth3d.org
 */
public interface ClassNode extends TreeNode, NamedTreeNode {

	/**
	 * Adds a dependency to another class.
	 * 
	 * @param cd
	 */
	public void addClassDependency(ClassDependency cd);

	/**
	 * Returns all dependencies of this class.
	 * 
	 * @return
	 */
	public Set<ClassDependency> getClassDependencies();
}
