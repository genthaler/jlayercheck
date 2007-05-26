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
import net.sf.jlayercheck.util.modeltree.DependentClassNode;
import net.sf.jlayercheck.util.modeltree.DependentModelTree;
import net.sf.jlayercheck.util.modeltree.DependentModuleNode;
import net.sf.jlayercheck.util.modeltree.DependentPackageNode;
import net.sf.jlayercheck.util.modeltree.ModelTree;
import net.sf.jlayercheck.util.modeltree.PackageNode;

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
			DependenciesTreeModel model = null;
			if (selected instanceof DependentClassNode) {
				model = ((DependentClassNode) selected).getDependenciesTreeModel();
			}
			if (selected instanceof DependentPackageNode) {
				model = new DependenciesTreeModel();
				
				// cumulate all dependencies from all contained classes
				mergePackage((DependentPackageNode) selected, model);
			}
			if (selected instanceof DependentModuleNode) {
				model = new DependenciesTreeModel();
				
				// cumulate all dependencies from all contained classes
				DependentModuleNode dmn = (DependentModuleNode) selected;

				for(PackageNode pn : dmn.getPackages()) {
					if (pn instanceof DependentPackageNode) {
						DependentPackageNode dpn = (DependentPackageNode) pn;
						
						mergePackage(dpn, model);
					}
				}
			}
			
			// show the created model
			if (model != null) {
				list.setModel(model);
				
				list.expandAll();
				
				// collapse "unassigned"
				if (model.getUnassignedModule() != null) {
					list.collapsePath(new TreePath(new Object[] {list.getModel().getRoot(), model.getUnassignedModule()}));
				}
			}
		}
	}

	protected void mergePackage(DependentPackageNode dpn, DependenciesTreeModel model) {
		for(ClassNode cn : dpn.getClasses()) {
			if (cn instanceof DependentClassNode) {
				DependentClassNode dcn = (DependentClassNode) cn;
				
				model.merge((DependentModelTree) dcn.getDependenciesTreeModel().getRoot());
			}
		}
	}
}
