package net.sf.jlayercheck.gui;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import net.sf.jlayercheck.util.model.ClassDependency;
import net.sf.jlayercheck.util.modeltree.ClassNode;
import net.sf.jlayercheck.util.modeltree.DefaultModelTree;
import net.sf.jlayercheck.util.modeltree.DefaultModuleNode;
import net.sf.jlayercheck.util.modeltree.DefaultPackageNode;
import net.sf.jlayercheck.util.modeltree.DependentClassNode;
import net.sf.jlayercheck.util.modeltree.ModelTree;
import net.sf.jlayercheck.util.modeltree.ModuleNode;
import net.sf.jlayercheck.util.modeltree.PackageNode;

public class DependenciesTree extends JTree {

	protected static final String UNASSIGNED = "unassigned";
	
	public DependenciesTree() {
		setModel(new DefaultTreeModel(null));
		setCellRenderer(new DependenciesTreeCellRenderer());
	}
	
	public void showDependencies(ClassNode node, ModelTree treemodel) {
		DefaultModelTree depTree = new DefaultModelTree();
		
		for(ClassDependency cd : node.getClassDependencies()) {
			
			ClassNode depClass = treemodel.getClassNode(cd.getDependency());
			
			PackageNode pn = null;
			ModuleNode mn = null;
			
			if (depClass != null) {
				pn = (PackageNode) depClass.getParent();
				mn = (ModuleNode) pn.getParent();
			} else {
				pn = new DefaultPackageNode(UNASSIGNED);
				mn = new DefaultModuleNode(UNASSIGNED);
			}
			
			// create module if necessary
			if (depTree.getModule(mn.getModuleName()) == null) {
				depTree.add(new DefaultModuleNode(mn.getModuleName()));
			}
			
			ModuleNode destModule = depTree.getModule(mn.getModuleName());
			
			// create package if necessary
			if (destModule.getPackage(pn.getPackagename()) == null) {
				destModule.add(new DefaultPackageNode(pn.getPackagename()));
			}
		
			PackageNode destPackage = destModule.getPackage(pn.getPackagename());
			
			// create ClassNode
			destPackage.add(new DependentClassNode(cd));
		}
		
		// sort nodes and sort the "unassigned" node to the end
		SortedSet<ModuleNode> sort = new TreeSet<ModuleNode>();
		sort.addAll(depTree.getModules());
		depTree.removeAllChildren();
		
		ModuleNode unassigned = null;
		for(ModuleNode mn : sort) {
			if (mn.getModuleName().equals(UNASSIGNED)) {
				unassigned = mn;
			} else {
				depTree.add(mn);
			}
		}
		if (unassigned != null) {
			depTree.add(unassigned);
		}
		
		setModel(new DefaultTreeModel(depTree));
		
		expandAll();
		
		// collapse "unassigned"
		if (unassigned != null) {
			collapsePath(new TreePath(new Object[] {depTree, unassigned}));
		}
	}

	protected void expandAll() {
		for (int i = 0; i < getRowCount(); i++) {
	         expandRow(i);
		}		
	}
}
