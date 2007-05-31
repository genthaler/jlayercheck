package net.sf.jlayercheck.util.modeltree;

import javax.swing.tree.DefaultTreeModel;

import net.sf.jlayercheck.util.model.ClassDependency;

/**
 * Generates a tree model for the dependencies of a single ClassNode.
 * 
 * @author webmaster@earth3d.org
 */
public class DependenciesTreeModel extends DefaultTreeModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3719586478900307613L;

	protected static final String UNASSIGNED = "unassigned";
	
	/**
	 * Generates an empty dependency representation model.
	 * 
	 */
	public DependenciesTreeModel() {
		super(null);
		setRoot(new DependentModelTree());
	}
	
	/**
	 * Adds the given ClassNode to the given DependentModelTree.
	 * 
	 * @param depTree the tree to update
	 * @param cd the dependency to add
	 * @param depClass the node that has the dependency to add and is used to retrieve the package and model node
	 */
	public void addClassNodeToDependentModelTree(DependentModelTree depTree, ClassDependency cd, ClassNode depClass) {
		
		// find the package and class node names where it should be inserted
		
		PackageNode pn = null;
		ModuleNode mn = null;
		
		if (depClass != null) {
			pn = (PackageNode) depClass.getParent();
			mn = (ModuleNode) pn.getParent();
		} else {
			pn = new DefaultPackageNode(UNASSIGNED);
			((DefaultPackageNode) pn).setUnassignedPackage(true);
			mn = new DefaultModuleNode(UNASSIGNED);
			((DefaultModuleNode) mn).setUnassignedModule(true);
		}
		
		// create module if necessary
		if (depTree.getModule(mn.getModuleName()) == null) {
			DependentModuleNode newModuleNode = new DependentModuleNode(mn.getModuleName(), mn.isUnassignedModule());
			if (mn instanceof DependentModuleNode) {
				newModuleNode.setUnallowedDependency(((DependentModuleNode) mn).isUnallowedDependency());
			}
			depTree.add(newModuleNode);
		}
		
		ModuleNode destModule = depTree.getModule(mn.getModuleName());
		
		// create package if necessary
		if (destModule.getPackage(pn.getPackagename()) == null) {
			DependentPackageNode newPackageNode = new DependentPackageNode(pn.getPackagename());
			if (pn instanceof DependentPackageNode) {
				newPackageNode.setUnallowedDependency(((DependentPackageNode) pn).isUnallowedDependency());
			}
			destModule.add(newPackageNode);
		}

		PackageNode destPackage = destModule.getPackage(pn.getPackagename());
		
		// create ClassNode if necessary
		if (destPackage.getClass(cd.getDependency()) == null) {
			DependentClassNode newClassNode = new DependentClassNode(cd);
			if (depClass instanceof DependentClassNode) {
				newClassNode.getClassDependency().setUnallowedDependency(((DependentClassNode) depClass).isUnallowedDependency());
			}
			destPackage.add(newClassNode);
		}
	}
	
	/**
	 * Merges the other tree into this tree.
	 * @param dependenciesTreeModel
	 */
	public void merge(DependentModelTree depTree) {
		for(ModuleNode mn : depTree.getModules()) {
			for(PackageNode pn : mn.getPackages()) {
				for(ClassNode cn : pn.getClasses()) {
					addClassNodeToDependentModelTree((DependentModelTree) getRoot(), new ClassDependency(cn.getName()), cn);
				}
			}
		}
		
		((DependentModelTree) getRoot()).sortNodes();
	}
}
