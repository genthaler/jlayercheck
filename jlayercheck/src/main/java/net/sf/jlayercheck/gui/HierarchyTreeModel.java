package net.sf.jlayercheck.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import net.sf.jlayercheck.util.DependencyParser;
import net.sf.jlayercheck.util.XMLConfigurationParser;
import net.sf.jlayercheck.util.modeltree.ModelTree;

/**
 * Offers a TreeModel interface to the model-package-class
 * hierarchy that is computed by the {@link XMLConfigurationParser}
 * and the {@link DependencyParser}.
 * 
 * @author webmaster@earth3d.org
 */
public class HierarchyTreeModel implements TreeModel {

	protected List<TreeModelListener> listenerList = new ArrayList<TreeModelListener>();
	
	public HierarchyTreeModel(ModelTree mt) {
		
	}
	
	public void addTreeModelListener(TreeModelListener l) {
		listenerList.add(l);
	}

	public Object getChild(Object parent, int index) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getChildCount(Object parent) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getIndexOfChild(Object parent, Object child) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Object getRoot() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isLeaf(Object node) {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeTreeModelListener(TreeModelListener l) {
		listenerList.remove(l);
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
		// TODO Auto-generated method stub

	}

}
