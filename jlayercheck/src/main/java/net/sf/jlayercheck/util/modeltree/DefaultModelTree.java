package net.sf.jlayercheck.util.modeltree;

import java.util.Enumeration;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * @see ModelTree
 * @author webmaster@earth3d.org
 */
public class DefaultModelTree extends DefaultMutableTreeNode implements ModelTree {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1419905413709544489L;

	public DefaultModelTree() {
		setAllowsChildren(true);
	}

	public Vector<ModuleNode> getModules() {
		if (getChildCount() > 0) {
			Vector<ModuleNode> result = new Vector<ModuleNode>();
			result.addAll(children);
			return result;
		}
		
		return new Vector<ModuleNode>();
	}

	/**
	 * Searches for the ModuleNode with the given name.
	 * 
	 * @param modulename
	 * @return
	 */
	public ModuleNode getModule(String modulename) {
		if (getModules() == null) return null;
		
		for(ModuleNode node : getModules()) {
			if (node.getModuleName().equals(modulename)) {
				return node;
			}
		}
		
		return null;
	}

	/**
	 * Returns the first ClassNode node for the given classname.
	 * 
	 * @param classname
	 * @return
	 */
	public ClassNode getClassNode(String classname) {
		return (ClassNode) findNode(this, ClassNode.class, classname);
	}

	/**
	 * Searches for the first occurence of a node that is an instance of
	 * the given class and has the given name.
	 * 
	 * @param node root node of the tree
	 * @param clazz to search for
	 * @param name name that the NamedTreeNode should have
	 * @return NamedTreeNode
	 */
	protected TreeNode findNode(TreeNode node, Class<? extends NamedTreeNode> clazz, String name) {
		Enumeration<TreeNode> e = node.children();
		while(e.hasMoreElements()) {
			TreeNode currentNode = e.nextElement();
			if (clazz.isInstance(currentNode)) {
				if (((NamedTreeNode) currentNode).getName().equals(name)) {
					return currentNode;
				}
			}
			
			TreeNode childResult = findNode(currentNode, clazz, name);
			if (childResult != null) {
				return childResult;
			}
		}
		
		return null;
	}

	/**
	 * Sorts nodes and sorts the "unassigned" node to the end.
	 */
	public synchronized void sortNodes() {
		SortedSet<ModuleNode> sort = new TreeSet<ModuleNode>();
		sort.addAll(getModules());
		removeAllChildren();

		// re-add all nodes except the "unassigned" node
		ModuleNode unassignedModule = null;
		for(ModuleNode mn : sort) {
			if (mn.isUnassignedModule()) {
				assert(unassignedModule == null);
				unassignedModule = mn;
			} else {
				add(mn);
			}
		}
		
		// add the "unassigned" node at the end
		if (unassignedModule != null) {
			add(unassignedModule);
		}
	}

	/**
	 * Finds and returns the ModuleNode that is named "unassigned" and contains all
	 * packages that do not belong to any module. If it is not
	 * found, null is returned.
	 * 
	 * @return "unassigned" ModuleNode or null
	 */
	public ModuleNode getUnassignedModule() {
		for(ModuleNode mn : getModules()) {
			if (mn.isUnassignedModule()) {
				return mn;
			}
		}
		
		return null;
	}

	public void merge(ModelTree additionalModelTree) {
		for(ModuleNode mn : additionalModelTree.getModules()) {
			for(PackageNode pn : mn.getPackages()) {
				for(ClassNode cn : pn.getClasses()) {
					ModuleNode thisMn = getModule(mn.getModuleName());
					if (thisMn == null) {
						thisMn = new DependentModuleNode(mn.getModuleName());
						add(thisMn);
					}
					PackageNode thisPn = thisMn.getPackage(pn.getPackagename());
					if (thisPn == null) {
						thisPn = new DependentPackageNode(pn.getPackagename());
						thisMn.add(thisPn);
					}
					thisPn.add(cn);
				}
			}
		}
	}
}
