package net.sf.jlayercheck.uti.modeltreel;

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
		return (Vector<ModuleNode>) children();
	}
}
