package net.sf.jlayercheck;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import junit.framework.TestCase;
import net.sf.jlayercheck.util.DependencyVisitor;
import net.sf.jlayercheck.util.XMLConfigurationParser;
import net.sf.jlayercheck.util.model.ClassDependency;

public class XMLConfigurationParserTest extends TestCase {
	/**
	 * Tests the basic parsing functionality.
	 * 
	 * @throws Exception
	 */
	public void testParser() throws Exception {
		InputStream is = getClass().getResource("/jlayercheck_test.xml").openStream();
		
		XMLConfigurationParser xcp = new XMLConfigurationParser(is);
		
		// test if loading of the configuration from the XML file works
		assertTrue(xcp.getModulePackages().containsKey("util"));
		assertTrue(xcp.getModulePackages().containsKey("main"));
		assertTrue(xcp.getModuleDependencies().containsKey("main"));
		assertTrue(xcp.getModuleDependencies().get("main").contains("util"));
		assertTrue(xcp.getExcludeList().contains("java.**"));
		
		// test if the ClassSource correctly implements calling a DependencyVisitor
		DependencyVisitor dv = new DependencyVisitor();
		xcp.getClassSources().get(0).call(dv);
		assertTrue(dv.getPackages().containsKey("net/sf/jlayercheck"));

		// test if the java sources are correctly loaded
		Map<String, URL> javaSources = new TreeMap<String, URL>();
		javaSources.putAll(xcp.getClassSources().get(0).getSourceFiles());
		assertTrue(javaSources.containsKey("net/sf/jlayercheck/DependencyParser"));
	}
	
	/**
	 * Tests if dependency violations are found.
	 * 
	 * @throws Exception
	 */
	public void testFindViolations() throws Exception {
		// load and parse configuration, class and java files
		InputStream is = getClass().getResource("/jlayercheck_test.xml").openStream();
		XMLConfigurationParser xcp = new XMLConfigurationParser(is);
		DependencyVisitor dv = new DependencyVisitor();
		xcp.getClassSources().get(0).call(dv);
		Map<String, URL> javaSources = new TreeMap<String, URL>();
		javaSources.putAll(xcp.getClassSources().get(0).getSourceFiles());

		Set<String> unspecifiedPackages = dv.getUnspecifiedPackages(xcp);
		Map<String, Map<String, ClassDependency>> unallowedDependencies = dv.getUnallowedDependencies(xcp); 
		
		// find violations
		
		for(String classPackageName : unspecifiedPackages) {
			System.out.println("Warning: Package "+classPackageName+" has no module.");
		}
		
		for(String classname : unallowedDependencies.keySet()) {
			for(String dependency : unallowedDependencies.get(classname).keySet()) {
				String classPackageName = DependencyVisitor.getPackageName(classname);
				String dependencyPackageName = DependencyVisitor.getPackageName(dependency);

				String classmodule = xcp.getPackageModules().get(classPackageName);
				String dependencymodule = xcp.getPackageModules().get(dependencyPackageName);

				System.out.print("Class "+classname+" ("+classmodule+") must not use class "+dependency+" ("+dependencymodule+") in line ");
				
				for(int line : unallowedDependencies.get(classname).get(dependency).getLineNumbers()) {
					System.out.print(" "+line);
				}
				System.out.println();
			}
		}
		
		assertTrue(unspecifiedPackages.contains("net/sf/jlayercheck/util/model"));
		assertTrue(unallowedDependencies.get("net/sf/jlayercheck/util/XMLConfigurationParser").containsKey("org/w3c/dom/NodeList"));
	}
}
