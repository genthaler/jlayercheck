package net.sf.jlayercheck.gui;

import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import net.antonioshome.swing.treewrapper.DnDVetoException;
import net.antonioshome.swing.treewrapper.TreeTreeDnDEvent;
import net.antonioshome.swing.treewrapper.TreeTreeDnDListener;
import net.antonioshome.swing.treewrapper.TreeWrapper;
import net.sf.jlayercheck.util.XMLConfiguration;
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

	protected XMLConfiguration xmlConfiguration;
	
	public ModelPackageClassTree(XMLConfiguration xmlconf) {
		this.xmlConfiguration = xmlconf;
		
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
			}
		
		});
	}

	/**
	 * Updates the status if nodes are unallowed dependencies.
	 */
	protected void updateTreeStatus() {
		getXmlConfiguration().cumulateDependencyViolations(((DefaultModelTree) getModel().getRoot()));
	}
	
	public XMLConfiguration getXmlConfiguration() {
		return xmlConfiguration;
	}

	public void setXmlConfiguration(XMLConfiguration xmlConfiguration) {
		this.xmlConfiguration = xmlConfiguration;
	}

	@Override
	public void setModel(TreeModel arg0) {
		super.setModel(arg0);
		
		getModel().addTreeModelListener(new TreeModelListener() {
			
			public void treeStructureChanged(TreeModelEvent arg0) {
				updateTreeStatus();
			}
		
			public void treeNodesRemoved(TreeModelEvent arg0) {
				updateTreeStatus();
			}
		
			public void treeNodesInserted(TreeModelEvent arg0) {
				updateTreeStatus();
			}
		
			public void treeNodesChanged(TreeModelEvent arg0) {
				updateTreeStatus();
			}
		
		});
	}
}
