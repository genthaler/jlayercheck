package net.sf.jlayercheck.gui;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.xml.parsers.ParserConfigurationException;

import net.antonioshome.swing.treewrapper.DnDVetoException;
import net.antonioshome.swing.treewrapper.TreeTreeDnDEvent;
import net.antonioshome.swing.treewrapper.TreeTreeDnDListener;
import net.antonioshome.swing.treewrapper.TreeWrapper;
import net.sf.jlayercheck.util.DependencyVisitor;
import net.sf.jlayercheck.util.XMLConfigurationParser;
import net.sf.jlayercheck.util.exceptions.ConfigurationException;
import net.sf.jlayercheck.util.exceptions.OverlappingModulesDefinitionException;
import net.sf.jlayercheck.util.model.ClassSource;
import net.sf.jlayercheck.util.modeltree.ClassNode;
import net.sf.jlayercheck.util.modeltree.ModuleNode;
import net.sf.jlayercheck.util.modeltree.PackageNode;

import org.xml.sax.SAXException;

/**
 * PackageFrame is part of a GUI that is used to test other
 * dependencies by rearranging the classes and packages in the
 * modules interactively in a treeview. 
 *  
 * @author webmaster@earth3d.org
 */
public class PackageFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8443924981257381579L;

	public PackageFrame() throws IOException, SAXException, ParserConfigurationException, ConfigurationException, OverlappingModulesDefinitionException {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("JLayerCheck - jlayercheck.sf.net");
		setSize(500, 500);

		// load and parse configuration, class and java files
		InputStream is = getClass().getResource("/jlayercheck.xml").openStream();
		XMLConfigurationParser xcp = new XMLConfigurationParser(is);
		DependencyVisitor dv = new DependencyVisitor();
		Map<String, URL> javaSources = new TreeMap<String, URL>();
        for(ClassSource source : xcp.getClassSources()) {
            source.call(dv);
            javaSources.putAll(source.getSourceFiles());
        }

//		DefaultMutableTreeNode p1,p2,p3;
//		DefaultMutableTreeNode treeroot = new DefaultMutableTreeNode("Root");
//		treeroot.add(p1 = new DefaultMutableTreeNode("package1"));
//		treeroot.add(p2 = new DefaultMutableTreeNode("package2"));
//		treeroot.add(p3 = new DefaultMutableTreeNode("package3"));
//		
//		p1.add(new DefaultMutableTreeNode("class1"));
//		p1.add(new DefaultMutableTreeNode("class2"));
//		p2.add(new DefaultMutableTreeNode("class3"));
//		p2.add(new DefaultMutableTreeNode("class4"));
//		p3.add(new DefaultMutableTreeNode("class5j"));
		
//		DefaultTreeModel treemodel = new DefaultTreeModel(treeroot);

		DefaultTreeModel treemodel = new DefaultTreeModel(xcp.getModelTree(dv));

		JTree testtree = new JTree();
		testtree.setModel(treemodel);
		JScrollPane scroll = new JScrollPane(testtree);
		getContentPane().add(scroll);
		TreeWrapper tw = new TreeWrapper(testtree);
		
		tw.addTreeTreeDnDListener(new TreeTreeDnDListener() {
		
			public void mayDrop(TreeTreeDnDEvent arg0) throws DnDVetoException {
				TreeNode source = arg0.getSourceNode();
				TreeNode target = arg0.getTargetNode();
				if (source instanceof PackageNode && target instanceof ModuleNode) {
					return;
				}
				if (source instanceof ClassNode && target instanceof PackageNode) {
					return;
				}
				if (source instanceof ClassNode && target instanceof ClassNode) {
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
	 * @throws OverlappingModulesDefinitionException 
	 * @throws ConfigurationException 
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException, ConfigurationException, OverlappingModulesDefinitionException {
		new PackageFrame().setVisible(true);
	}
}
