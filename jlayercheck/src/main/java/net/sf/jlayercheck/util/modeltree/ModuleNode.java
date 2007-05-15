package net.sf.jlayercheck.uti.modeltreel;

import java.util.Vector;

import javax.swing.tree.TreeNode;

/**
 * Describes a module that can contain java packages.
 * 
 * @author webmaster@earth3d.org
 */
public interface ModuleNode extends TreeNode {
	public Vector<PackageNode> getPackages();
}
