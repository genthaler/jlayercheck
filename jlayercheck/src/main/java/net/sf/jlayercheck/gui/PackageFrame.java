package net.sf.jlayercheck.gui;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.jlayercheck.util.DependencyVisitor;
import net.sf.jlayercheck.util.XMLConfigurationParser;
import net.sf.jlayercheck.util.exceptions.ConfigurationException;
import net.sf.jlayercheck.util.exceptions.OverlappingModulesDefinitionException;
import net.sf.jlayercheck.util.model.ClassSource;
import net.sf.jlayercheck.util.modeltree.ClassNode;
import net.sf.jlayercheck.util.modeltree.DependenciesTreeModel;
import net.sf.jlayercheck.util.modeltree.ModelTree;

import org.xml.sax.SAXException;

/**
 * PackageFrame is part of a GUI that is used to test other
 * dependencies by rearranging the classes and packages in the
 * modules interactively in a treeview. 
 *  
 * @author webmaster@earth3d.org
 */
public class PackageFrame extends JFrame implements TreeSelectionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8443924981257381579L;

	protected DependenciesTree list;
	
	protected ModelTree modeltree;
	
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

        modeltree = xcp.getModelTree(dv);
		DefaultTreeModel treemodel = new DefaultTreeModel(modeltree);

		getContentPane().setLayout(new BorderLayout());
		
		JTree testtree = new ModelPackageClassTree();
		testtree.setModel(treemodel);
		JScrollPane scroll = new JScrollPane(testtree);
		testtree.getSelectionModel().addTreeSelectionListener(this);

		list = new DependenciesTree();
		JScrollPane scrollDep = new JScrollPane(list);
		
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scroll, scrollDep);
		getContentPane().add(split);
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

	public void valueChanged(TreeSelectionEvent e) {
		if (e.getNewLeadSelectionPath() != null) {
			Object selected = e.getNewLeadSelectionPath().getLastPathComponent();
			if (selected instanceof ClassNode) {
				DependenciesTreeModel model = new DependenciesTreeModel((ClassNode) selected, modeltree);
				list.setModel(model);
				
				list.expandAll();
				
				// collapse "unassigned"
				if (model.getUnassignedModule() != null) {
					list.collapsePath(new TreePath(new Object[] {list.getModel().getRoot(), model.getUnassignedModule()}));
				}
			}
		}
	}
}
