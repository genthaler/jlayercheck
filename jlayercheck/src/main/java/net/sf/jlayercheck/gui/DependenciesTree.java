package net.sf.jlayercheck.gui;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;

/**
 * A JTree to visualize dependencies for a single ClassNode.
 * 
 * @author webmaster@earth3d.org
 */
public class DependenciesTree extends JTree {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5462092050779265005L;

	public DependenciesTree() {
		setModel(new DefaultTreeModel(null));
		setCellRenderer(new DependenciesTreeCellRenderer());
	}
	
	public void expandAll() {
		for (int i = 0; i < getRowCount(); i++) {
	         expandRow(i);
		}		
	}
}
