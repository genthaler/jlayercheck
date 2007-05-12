package net.sf.jlayercheck.uti.modeltreel;

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
}
