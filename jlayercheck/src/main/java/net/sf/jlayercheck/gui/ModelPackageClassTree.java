package net.sf.jlayercheck.gui;

import javax.swing.JTree;
import javax.swing.tree.TreeNode;

import net.antonioshome.swing.treewrapper.DnDVetoException;
import net.antonioshome.swing.treewrapper.TreeTreeDnDEvent;
import net.antonioshome.swing.treewrapper.TreeTreeDnDListener;
import net.antonioshome.swing.treewrapper.TreeWrapper;
import net.sf.jlayercheck.util.modeltree.ClassNode;
import net.sf.jlayercheck.util.modeltree.DefaultModelTree;
import net.sf.jlayercheck.util.modeltree.ModuleNode;
import net.sf.jlayercheck.util.modeltree.PackageNode;

/**
 * A tree view of the modules, packages and classes that can be
 * reordered (e.g. put packages and classes into other modules i.e. packages).
 * 
 * @author webmaster@earth3d.org
 */
public class ModelPackageClassTree extends JTree {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8017813199550063002L;

	public ModelPackageClassTree() {
		setCellRenderer(new DependenciesTreeCellRenderer());
		
		TreeWrapper tw = new TreeWrapper(this);
		
		tw.addTreeTreeDnDListener(new TreeTreeDnDListener() {
			
			public void mayDrop(TreeTreeDnDEvent arg0) throws DnDVetoException {
				TreeNode source = arg0.getSourceNode();
				TreeNode target = arg0.getTargetNode();
				if (source instanceof PackageNode && target instanceof ModuleNode) {
					return;
				}
				if (source instanceof ClassNode && target instanceof PackageNode) {
					return;
				}
				if (source instanceof ClassNode && target instanceof ClassNode) {
					return;
				}
				throw new DnDVetoException("");
			}
		
			public void drop(TreeTreeDnDEvent arg0) throws DnDVetoException {
				((DefaultModelTree) getModel().getRoot()).cumulateDependencyViolations();
			}
		
		});
	}
}
