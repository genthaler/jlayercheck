package net.sf.jlayercheck.util.modeltree;

import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @see ModelTree
 * @author webmaster@earth3d.org
 *
 */
public class DefaultModelTree extends DefaultMutableTreeNode implements ModelTree {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1419905413709544489L;

	public DefaultModelTree() {
		setAllowsChildren(true);
	}

	public Vector<ModuleNode> getModules() {
		return (Vector<ModuleNode>) children;
	}

	/**
	 * Searches for the ModuleNode with the given name.
	 * 
	 * @param modulename
	 * @return
	 */
	public ModuleNode getModule(String modulename) {
		if (getModules() == null) return null;
		
		for(ModuleNode node : getModules()) {
			if (node.getModuleName().equals(modulename)) {
				return node;
			}
		}
		
		return null;
	}
}
