package net.sf.jlayercheck.gui;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import net.sf.jlayercheck.util.modeltree.ClassNode;

/**
 * Cell renderer used by ModelPackageClassTree to display the tree. It
 * removes the package names from the classes. 
 */
public class ModelPackageClassRenderer extends DefaultTreeCellRenderer implements TreeCellRenderer {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3172175241849470075L;

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
				row, hasFocus);

		if (value instanceof ClassNode) {
			// do not display package name
			ClassNode cn = (ClassNode) value;
			setText(cn.getName().replaceAll(".*/", ""));
		}
		
		return this;
	}
}
