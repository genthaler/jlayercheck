package net.sf.jlayercheck.gui;

import java.awt.BorderLayout;
import java.io.FileInputStream;
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
import net.sf.jlayercheck.util.XMLConfiguration;
import net.sf.jlayercheck.util.XMLConfigurationParser;
import net.sf.jlayercheck.util.exceptions.ConfigurationException;
import net.sf.jlayercheck.util.exceptions.OverlappingModulesDefinitionException;
import net.sf.jlayercheck.util.model.ClassSource;
import net.sf.jlayercheck.util.modeltree.ClassNode;
import net.sf.jlayercheck.util.modeltree.DefaultModelTree;
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

	protected DependenciesTree outgoingList;
	protected DependenciesTree incomingList;
	
	protected ModelTree modeltree;
	
	public PackageFrame(String filename) throws IOException, SAXException, ParserConfigurationException, ConfigurationException, OverlappingModulesDefinitionException {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("JLayerCheck - jlayercheck.sf.net");
		setSize(500, 500);

		// load and parse configuration, class and java files
		InputStream is = null;
		if (filename == null) {
			is = getClass().getResource("/jlayercheck.xml").openStream();
		} else {
			is = new FileInputStream(filename);
		}
		XMLConfiguration xcp = new XMLConfigurationParser().parse(is);
		DependencyVisitor dv = new DependencyVisitor();
		Map<String, URL> javaSources = new TreeMap<String, URL>();
        for(ClassSource source : xcp.getClassSources()) {
            source.call(dv);
            javaSources.putAll(source.getSourceFiles());
        }

        modeltree = xcp.getModelTree(dv);
        if (modeltree instanceof DefaultModelTree) {
        	((DefaultModelTree) modeltree).sortNodes();
        }
        
		DefaultTreeModel treemodel = new DefaultTreeModel(modeltree);

		getContentPane().setLayout(new BorderLayout());
		
		JTree testtree = new ModelPackageClassTree(xcp);
		testtree.setModel(treemodel);
		JScrollPane scroll = new JScrollPane(testtree);
		testtree.getSelectionModel().addTreeSelectionListener(this);

		outgoingList = new DependenciesTree();
		JScrollPane scrollDepOut = new JScrollPane(outgoingList);
		
		incomingList = new DependenciesTree();
		JScrollPane scrollDepIn = new JScrollPane(incomingList);
		
		JSplitPane splitDep = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollDepIn, scrollDepOut);
		splitDep.setDividerLocation(250);
		
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scroll, splitDep);
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
		String filename = null;
		if (args.length != 1) {
			System.out.println("USAGE: PackageFrame jlayercheck.xml");
		} else {
			filename = args[0];
		}
		new PackageFrame(filename).setVisible(true);
	}

	public void valueChanged(TreeSelectionEvent e) {
		if (e.getNewLeadSelectionPath() != null) {
			Object selected = e.getNewLeadSelectionPath().getLastPathComponent();
			
			DependenciesTreeModel model = null;
			
			// outgoing dependencies
			model = getOutgoingModel(selected, model);
			
			// show the created model
			if (model != null) {
				outgoingList.setModel(model);
				
				outgoingList.expandAll();
				
				// collapse "unassigned"
				if (((ModelTree) model.getRoot()).getUnassignedModule() != null) {
					outgoingList.collapsePath(new TreePath(new Object[] {outgoingList.getModel().getRoot(), ((ModelTree) model.getRoot()).getUnassignedModule()}));
				}
			}
			
			// incoming dependencies
			model = getIncomingModel(selected, model);
			
			// show the created model
			if (model != null) {
				incomingList.setModel(model);
				
				incomingList.expandAll();
				
				// collapse "unassigned"
				if (((ModelTree) model.getRoot()).getUnassignedModule() != null) {
					incomingList.collapsePath(new TreePath(new Object[] {incomingList.getModel().getRoot(), ((ModelTree) model.getRoot()).getUnassignedModule()}));
				}
			}
		}
	}

	protected DependenciesTreeModel getIncomingModel(Object selected, DependenciesTreeModel model) {
		if (selected instanceof DependentClassNode) {
			model = ((DependentClassNode) selected).getIncomingDependenciesTreeModel();
		}
		if (selected instanceof DependentPackageNode) {
			model = new DependenciesTreeModel();
			
			// cumulate all dependencies from all contained classes
			mergeIncomingPackage((DependentPackageNode) selected, model);
		}
		if (selected instanceof DependentModuleNode) {
			model = new DependenciesTreeModel();
			
			// cumulate all dependencies from all contained classes
			DependentModuleNode dmn = (DependentModuleNode) selected;

			for(PackageNode pn : dmn.getPackages()) {
				if (pn instanceof DependentPackageNode) {
					DependentPackageNode dpn = (DependentPackageNode) pn;
					
					mergeIncomingPackage(dpn, model);
				}
			}
		}
		return model;
	}

	protected DependenciesTreeModel getOutgoingModel(Object selected, DependenciesTreeModel model) {
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
		return model;
	}

	protected void mergePackage(DependentPackageNode dpn, DependenciesTreeModel model) {
		for(ClassNode cn : dpn.getClasses()) {
			if (cn instanceof DependentClassNode) {
				DependentClassNode dcn = (DependentClassNode) cn;
				
				model.merge((DependentModelTree) dcn.getDependenciesTreeModel().getRoot());
			}
		}
	}

	protected void mergeIncomingPackage(DependentPackageNode dpn, DependenciesTreeModel model) {
		for(ClassNode cn : dpn.getClasses()) {
			if (cn instanceof DependentClassNode) {
				DependentClassNode dcn = (DependentClassNode) cn;
				
				model.merge((DependentModelTree) dcn.getIncomingDependenciesTreeModel().getRoot());
			}
		}
	}
}
