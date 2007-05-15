package net.sf.jlayercheck.uti.modeltreel;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @see ClassNode
 * @author webmaster@earth3d.org
 *
 */
public class DefaultClassNode extends DefaultMutableTreeNode implements ClassNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -478278521385757708L;

	public DefaultClassNode() {
		setAllowsChildren(false);
	}
}
