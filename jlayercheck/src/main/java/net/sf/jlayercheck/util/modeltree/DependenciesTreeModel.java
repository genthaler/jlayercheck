package net.sf.jlayercheck.util.modeltree;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

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
	
	protected ModuleNode unassignedModule;
	
	/**
	 * Generates a dependency representation model from the given input.
	 * 
	 * @param node ClassNode to show the dependencies for
	 * @param treemodel ModelTree that contains the given ClassNode
	 */
	public DependenciesTreeModel(ClassNode node, ModelTree treemodel) {
		super(null);
		setRoot(createModel(node, treemodel));
	}

	/**
	 * Generates an empty dependency representation model.
	 * 
	 */
	public DependenciesTreeModel() {
		super(null);
		setRoot(new DependentModelTree());
	}
	
	/**
	 * Creates a dependency tree model for the given node of the given ModelTree.
	 * 
	 * @param node
	 * @param treemodel
	 * @return
	 */
	protected TreeNode createModel(ClassNode node, ModelTree treemodel) {
		DependentModelTree depTree = new DependentModelTree();
		
		for(ClassDependency cd : node.getClassDependencies()) {
			
			ClassNode depClass = treemodel.getClassNode(cd.getDependency());
			
			addClassNodeToDependentModelTree(depTree, cd, depClass);
		}
		
		// compute unallowed dependency marks
		boolean treeUnallowed = false;
		for(ModuleNode mn : depTree.getModules()) {
			boolean mnUnallowed = false;
			for(PackageNode pn : mn.getPackages()) {
				boolean pnUnallowed = false;
				for (ClassNode cn : pn.getClasses()) {
					if (cn instanceof DependentClassNode) {
						DependentClassNode dcn = (DependentClassNode) cn;
						
						if (dcn.getClassDependency().isUnallowedDependency()) {
							pnUnallowed = true;
							mnUnallowed = true;
							treeUnallowed = true;
						}
					}
				}
				
				if (pn instanceof DependentPackageNode) {
					((DependentPackageNode) pn).setUnallowedDependency(pnUnallowed);
				}
			}

			if (mn instanceof DependentModuleNode) {
				((DependentModuleNode) mn).setUnallowedDependency(mnUnallowed);
			}
		}
		depTree.setUnallowedDependency(treeUnallowed);
		
		// sort nodes and sort the "unassigned" node to the end
		sortNodes(depTree);
		
		return depTree;
	}

	/**
	 * Adds the given ClassNode to the given DependentModelTree.
	 * 
	 * @param depTree
	 * @param cd
	 * @param depClass
	 */
	protected void addClassNodeToDependentModelTree(DependentModelTree depTree, ClassDependency cd, ClassNode depClass) {
		
		// find the package and class node names where it should be inserted
		
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
			depTree.add(new DependentModuleNode(mn.getModuleName()));
		}
		
		ModuleNode destModule = depTree.getModule(mn.getModuleName());
		
		// create package if necessary
		if (destModule.getPackage(pn.getPackagename()) == null) {
			destModule.add(new DependentPackageNode(pn.getPackagename()));
		}

		PackageNode destPackage = destModule.getPackage(pn.getPackagename());
		
		// create ClassNode if necessary
		if (destPackage.getClass(cd.getDependency()) == null) {
			destPackage.add(new DependentClassNode(cd));
		}
	}
	
	/**
	 * Sorts nodes and sorts the "unassigned" node to the end.
	 * 
	 * @param depTree
	 */
	protected void sortNodes(DefaultModelTree depTree) {
		SortedSet<ModuleNode> sort = new TreeSet<ModuleNode>();
		sort.addAll(depTree.getModules());
		depTree.removeAllChildren();

		unassignedModule = null;
		for(ModuleNode mn : sort) {
			if (mn.getModuleName().equals(UNASSIGNED)) {
				unassignedModule = mn;
			} else {
				depTree.add(mn);
			}
		}
		
		if (unassignedModule != null) {
			depTree.add(unassignedModule);
		}
	}

	/**
	 * Returns the ModuleNode that is named "unassigned" and contains all
	 * packages that do not belong to any module.
	 * 
	 * @return "unassigned" ModuleNode
	 */
	public ModuleNode getUnassignedModule() {
		return unassignedModule;
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
		
		sortNodes((DependentModelTree) getRoot());
	}
}
