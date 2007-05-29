package net.sf.jlayercheck;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

import junit.framework.TestCase;
import net.sf.jlayercheck.util.DependencyVisitor;
import net.sf.jlayercheck.util.StringUtils;
import net.sf.jlayercheck.util.XMLConfiguration;
import net.sf.jlayercheck.util.XMLConfigurationParser;
import net.sf.jlayercheck.util.model.ClassDependency;
import net.sf.jlayercheck.util.modeltree.ClassNode;
import net.sf.jlayercheck.util.modeltree.ModelTree;
import net.sf.jlayercheck.util.modeltree.ModuleNode;
import net.sf.jlayercheck.util.modeltree.PackageNode;

public class XMLConfigurationParserTest extends TestCase {
	protected static Logger logger = Logger.getLogger("JLayerCheck");
	
	/**
	 * Tests the basic parsing functionality.
	 * 
	 * @throws Exception
	 */
	public void testParser() throws Exception {
		InputStream is = getClass().getResource("/jlayercheck_test.xml").openStream();
		
		XMLConfiguration xcp = new XMLConfigurationParser().parse(is);
		
		// test if loading of the configuration from the XML file works
		assertTrue(xcp.getModulePackages().containsKey("util"));
		assertTrue(xcp.getModulePackages().containsKey("main"));
		assertTrue(xcp.getModuleDependencies().containsKey("main"));
		assertTrue(xcp.getModuleDependencies().get("main").contains("util"));
		assertTrue(xcp.getExcludeList().contains("java.**"));
		
		// test if the ClassSource correctly implements calling a DependencyVisitor
		DependencyVisitor dv = new DependencyVisitor();
		xcp.getClassSources().get(0).call(dv);
		assertTrue(dv.getPackages().containsKey("net/sf/jlayercheck/util"));

		// test if the java sources are correctly loaded
		Map<String, URL> javaSources = new TreeMap<String, URL>();
		javaSources.putAll(xcp.getClassSources().get(0).getSourceFiles());
		assertTrue(javaSources.containsKey("net/sf/jlayercheck/util/DependencyParser"));
	}
	
	/**
	 * Tests if dependency violations are found.
	 * 
	 * @throws Exception
	 */
	public void testFindViolations() throws Exception {
		// load and parse configuration, class and java files
		InputStream is = getClass().getResource("/jlayercheck_test.xml").openStream();
		XMLConfiguration xcp = new XMLConfigurationParser().parse(is);
		DependencyVisitor dv = new DependencyVisitor();
		xcp.getClassSources().get(0).call(dv);
		Map<String, URL> javaSources = new TreeMap<String, URL>();
		javaSources.putAll(xcp.getClassSources().get(0).getSourceFiles());

		Set<String> unspecifiedPackages = xcp.getUnspecifiedPackages(dv.getDependencies());
		Map<String, Map<String, ClassDependency>> unallowedDependencies = xcp.getUnallowedDependencies(dv.getDependencies()); 
		
		// find violations
		for(String classPackageName : unspecifiedPackages) {
			logger.fine("Warning: Package "+classPackageName+" has no module.");
		}
		
		for(String classname : unallowedDependencies.keySet()) {
			for(String dependency : unallowedDependencies.get(classname).keySet()) {
				String classPackageName = StringUtils.getPackageName(classname);
				String dependencyPackageName = StringUtils.getPackageName(dependency);

				String classmodule = xcp.getPackageModules().get(classPackageName);
				String dependencymodule = xcp.getPackageModules().get(dependencyPackageName);

				logger.finer("Class "+classname+" ("+classmodule+") must not use class "+dependency+" ("+dependencymodule+") in line ");
				
				for(int line : unallowedDependencies.get(classname).get(dependency).getLineNumbers()) {
					logger.finest(" "+line);
				}
				logger.finest("");
			}
		}
		
		assertTrue(unspecifiedPackages.contains("net/sf/jlayercheck/util/model"));
		assertTrue(unallowedDependencies.get("net/sf/jlayercheck/util/XMLConfigurationParser").containsKey("org/w3c/dom/NodeList"));
	}

	/**
	 * Test if the creation of a dependency model works.
	 * 
	 * @throws Exception
	 */
	public void testModel() throws Exception {
		// load and parse configuration, class and java files
		InputStream is = getClass().getResource("/jlayercheck_test.xml").openStream();
		XMLConfiguration xcp = new XMLConfigurationParser().parse(is);
		DependencyVisitor dv = new DependencyVisitor();
		xcp.getClassSources().get(0).call(dv);

		ModelTree mt = xcp.getModelTree(dv);
		
		boolean moduleUtilFound = false;
		boolean packageJLayerCheckFound = false;
		boolean classXMLConfigurationFound = false;
		
		for(ModuleNode mn : mt.getModules()) {
			if (mn.getName().equals("util")) {
				moduleUtilFound = true;
			}
			
			for(PackageNode pn : mn.getPackages()) {
				if (pn.getName().equals("net/sf/jlayercheck/gui")) {
					assertEquals("main", mn.getName());
					packageJLayerCheckFound = true;
				}
				
				for(ClassNode cn: pn.getClasses()) {
					if (cn.getName().equals("net/sf/jlayercheck/util/XMLConfiguration")) {
						assertEquals("util", mn.getName());
						assertEquals("net/sf/jlayercheck/util", pn.getName());
						classXMLConfigurationFound = true;
					}
				}
			}
		}
		
		assertTrue(moduleUtilFound);
		assertTrue(packageJLayerCheckFound);
		assertTrue(classXMLConfigurationFound);
	}
}
