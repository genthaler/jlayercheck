package net.sf.jlayercheck.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import net.sf.jlayercheck.util.modeltree.DependentClassNode;

/**
 * Draws labels for allowed dependencies green, for unallowed red.
 *  
 * @author webmaster@earth3d.org
 */
public class DependenciesTreeCellRenderer extends DefaultTreeCellRenderer
		implements TreeCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5484285023121201745L;

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		if (value instanceof DependentClassNode) {
			DependentClassNode dcn = (DependentClassNode) value;
			
			if (dcn.getClassDependency().isUnallowedDependency()) {
				setTextNonSelectionColor(Color.RED);
			} else {
				setTextNonSelectionColor(Color.GREEN);
			}
		} else {
			setTextNonSelectionColor(null);
		}
		
		return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
				row, hasFocus);
	}
}
