package net.sf.jlayercheck.gui;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import net.antonioshome.swing.treewrapper.DnDVetoException;
import net.antonioshome.swing.treewrapper.TreeTreeDnDEvent;
import net.antonioshome.swing.treewrapper.TreeTreeDnDListener;
import net.antonioshome.swing.treewrapper.TreeWrapper;

/**
 * PackageFrame is part of a GUI that is used to test other
 * dependencies by rearranging the classes and packages in the
 * modules interactively in a treeview. 
 *  
 * @author webmaster@earth3d.org
 */
public class PackageFrame extends JFrame {

	public PackageFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		DefaultMutableTreeNode p1,p2,p3;
		DefaultMutableTreeNode treeroot = new DefaultMutableTreeNode("Root");
		treeroot.add(p1 = new DefaultMutableTreeNode("package1"));
		treeroot.add(p2 = new DefaultMutableTreeNode("package2"));
		treeroot.add(p3 = new DefaultMutableTreeNode("package3"));
		
		p1.add(new DefaultMutableTreeNode("class1"));
		p1.add(new DefaultMutableTreeNode("class2"));
		p2.add(new DefaultMutableTreeNode("class3"));
		p2.add(new DefaultMutableTreeNode("class4"));
		p3.add(new DefaultMutableTreeNode("class5j"));
		
		DefaultTreeModel treemodel = new DefaultTreeModel(treeroot);
	
		JTree testtree = new JTree();
		testtree.setModel(treemodel);
		getContentPane().add(testtree);
		TreeWrapper tw = new TreeWrapper(testtree);
		
		tw.addTreeTreeDnDListener(new TreeTreeDnDListener() {
		
			public void mayDrop(TreeTreeDnDEvent arg0) throws DnDVetoException {
				String source = arg0.getSourceNode().toString();
				String target = arg0.getTargetNode().toString();
				if (source.startsWith("package") && target.startsWith("Root")) {
					return;
				}
				if (source.startsWith("class") && target.startsWith("package")) {
					return;
				}
				if (source.startsWith("class") && target.startsWith("class")) {
					return;
				}
				throw new DnDVetoException("");
			}
		
			public void drop(TreeTreeDnDEvent arg0) throws DnDVetoException {
			}
		
		});
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new PackageFrame().setVisible(true);
	}
}
