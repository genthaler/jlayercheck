package net.sf.jlayercheck.gui;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;

import net.sf.jlayercheck.util.model.ClassDependency;
import net.sf.jlayercheck.util.modeltree.ClassNode;
import net.sf.jlayercheck.util.modeltree.DefaultModelTree;
import net.sf.jlayercheck.util.modeltree.DefaultModuleNode;
import net.sf.jlayercheck.util.modeltree.DefaultPackageNode;
import net.sf.jlayercheck.util.modeltree.DependentClassNode;
import net.sf.jlayercheck.util.modeltree.DependentModuleNode;
import net.sf.jlayercheck.util.modeltree.DependentPackageNode;
import net.sf.jlayercheck.util.modeltree.ModelTree;
import net.sf.jlayercheck.util.modeltree.ModuleNode;
import net.sf.jlayercheck.util.modeltree.PackageNode;

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

	protected static final String UNASSIGNED = "unassigned";
	
	protected ModuleNode unassignedModule;
	
	public DependenciesTree() {
		setModel(new DefaultTreeModel(null));
		setCellRenderer(new DependenciesTreeCellRenderer());
	}
	
	/**
	 * Generates a dependency representation model from the given input
	 * and uses it as model for this tree.
	 * 
	 * @param node ClassNode to show the dependencies for
	 * @param treemodel ModelTree that contains the given ClassNode
	 */
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
				depTree.add(new DependentModuleNode(mn.getModuleName()));
			}
			
			ModuleNode destModule = depTree.getModule(mn.getModuleName());
			
			// create package if necessary
			if (destModule.getPackage(pn.getPackagename()) == null) {
				destModule.add(new DependentPackageNode(pn.getPackagename()));
			}
		
			PackageNode destPackage = destModule.getPackage(pn.getPackagename());
			
			// create ClassNode
			destPackage.add(new DependentClassNode(cd));
		}
		
		// compute unallowed dependency marks
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
		
		// sort nodes and sort the "unassigned" node to the end
		sortNodes(depTree);
		
		setModel(new DefaultTreeModel(depTree));
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

	public void expandAll() {
		for (int i = 0; i < getRowCount(); i++) {
	         expandRow(i);
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
}
