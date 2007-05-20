package net.sf.jlayercheck.gui;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;

import net.sf.jlayercheck.util.model.ClassDependency;
import net.sf.jlayercheck.util.modeltree.ClassNode;
import net.sf.jlayercheck.util.modeltree.DefaultClassNode;
import net.sf.jlayercheck.util.modeltree.DefaultModelTree;
import net.sf.jlayercheck.util.modeltree.DefaultModuleNode;
import net.sf.jlayercheck.util.modeltree.DefaultPackageNode;
import net.sf.jlayercheck.util.modeltree.ModelTree;
import net.sf.jlayercheck.util.modeltree.ModuleNode;
import net.sf.jlayercheck.util.modeltree.PackageNode;

public class DependenciesTree extends JTree {

	public DependenciesTree() {
		setModel(new DefaultTreeModel(null));
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
				pn = new DefaultPackageNode("unassigned");
				mn = new DefaultModuleNode("unassigned");
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
			destPackage.add(new DefaultClassNode(cd.getDependency()));
		}
		
		setModel(new DefaultTreeModel(depTree));
	}
}
