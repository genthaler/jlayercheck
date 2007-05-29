package net.sf.jlayercheck.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

import net.sf.jlayercheck.util.modeltree.ClassNode;
import net.sf.jlayercheck.util.modeltree.PackageNode;
import net.sf.jlayercheck.util.modeltree.UnallowedOrAllowedDependency;

/**
 * Draws labels for allowed dependencies green, for unallowed red.
 *  
 * @author webmaster@earth3d.org
 */
public class DependenciesTreeCellRenderer extends ModelPackageClassRenderer
		implements TreeCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5484285023121201745L;

	protected Icon classIcon = new ImageIcon(getClass().getResource("/class.png"));
	protected Icon packageIcon = new ImageIcon(getClass().getResource("/package.png"));
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		if (value instanceof UnallowedOrAllowedDependency) {
			UnallowedOrAllowedDependency dcn = (UnallowedOrAllowedDependency) value;
			
			if (dcn.isUnallowedDependency()) {
				setTextNonSelectionColor(Color.RED);
			} else {
				setTextNonSelectionColor(new Color(0, 128, 0));
			}
		} else {
			setTextNonSelectionColor(null);
		}

		Component result = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

		if (value instanceof ClassNode) {
			setIcon(classIcon);
		}
		if (value instanceof PackageNode) {
			setIcon(packageIcon);
		}
		
		return result;
	}
}
