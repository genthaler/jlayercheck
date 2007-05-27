package net.sf.jlayercheck.util.modeltree;

import java.util.Vector;

import javax.swing.tree.TreeNode;


/**
 * The root of the dependency tree. It contains modules as defined
 * by the configuration file.
 *  
 * @author webmaster@earth3d.org
 */
public interface ModelTree extends TreeNode {
	public Vector<ModuleNode> getModules();

	/**
	 * Returns the first ClassNode node for the given classname.
	 * 
	 * @param classname
	 * @return
	 */
	public ClassNode getClassNode(String classname);

	/**
	 * Returns the node that contains the unassigned packages.
	 * @return "unassigned" ModuleNode
	 */
	public ModuleNode getUnassignedModule();
}
