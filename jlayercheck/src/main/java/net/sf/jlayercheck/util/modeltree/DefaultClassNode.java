package net.sf.jlayercheck.util.modeltree;

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

	protected String classname;
	
	public DefaultClassNode(String classname) {
		this.classname = classname;
		
		setAllowsChildren(false);
	}

	public String getClassname() {
		return classname;
	}
	
	public String toString() {
		return getClassname().replace("/", ".");
	}
}
