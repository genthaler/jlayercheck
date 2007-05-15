package net.sf.jlayercheck.uti.modeltreel;

import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @see ModuleNode
 * @author webmaster@earth3d.org
 *
 */
public class DefaultModuleNode extends DefaultMutableTreeNode implements ModuleNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1419905413709544489L;

	public DefaultModuleNode() {
		setAllowsChildren(true);
	}

	public Vector<PackageNode> getPackages() {
		return (Vector<PackageNode>) children();
	}
}
