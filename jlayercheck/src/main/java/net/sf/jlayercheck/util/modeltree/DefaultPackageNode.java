package net.sf.jlayercheck.util.modeltree;

import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @see PackageNode
 * @author webmaster@earth3d.org
 *
 */
public class DefaultPackageNode extends DefaultMutableTreeNode implements PackageNode {

	protected String packagename;
	
	public DefaultPackageNode(String packagename) {
		super();
		
		this.packagename = packagename;
		setAllowsChildren(true);
	}
		
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

	public String getPackagename() {
		return packagename;
	}
	
	public String toString() {
		return getPackagename().replace("/", ".");
	}
}
