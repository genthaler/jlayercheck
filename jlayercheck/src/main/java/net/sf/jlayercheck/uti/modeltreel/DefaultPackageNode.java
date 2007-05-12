package net.sf.jlayercheck.uti.modeltreel;

import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @see PackageNode
 * @author webmaster@earth3d.org
 *
 */
public class DefaultPackageNode extends DefaultMutableTreeNode implements PackageNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3407786085047017446L;

	public DefaultPackageNode() {
		setAllowsChildren(true);
	}

	public Vector<ClassNode> getClasses() {
		return (Vector<ClassNode>) children();
	}
}
