package net.sf.jlayercheck.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.jlayercheck.util.exceptions.ConfigurationException;
import net.sf.jlayercheck.util.exceptions.OrphanedSearchException;
import net.sf.jlayercheck.util.exceptions.OverlappingModulesDefinitionException;
import net.sf.jlayercheck.util.model.ClassDependency;
import net.sf.jlayercheck.util.model.ClassSource;
import net.sf.jlayercheck.util.model.FilesystemClassSource;
import net.sf.jlayercheck.util.modeltree.ClassNode;
import net.sf.jlayercheck.util.modeltree.DefaultModelTree;
import net.sf.jlayercheck.util.modeltree.DefaultPackageNode;
import net.sf.jlayercheck.util.modeltree.DependenciesTreeModel;
import net.sf.jlayercheck.util.modeltree.DependentClassNode;
import net.sf.jlayercheck.util.modeltree.DependentModuleNode;
import net.sf.jlayercheck.util.modeltree.DependentPackageNode;
import net.sf.jlayercheck.util.modeltree.ModelTree;
import net.sf.jlayercheck.util.modeltree.ModuleNode;
import net.sf.jlayercheck.util.modeltree.PackageNode;
import net.sf.jlayercheck.util.modeltree.UnallowedOrAllowedDependency;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Parses the given configuration file/input stream and returns the specified
 * structure using its getter methods.
 * 
 * @author webmaster@earth3d.org
 */
public class XMLConfigurationParser {
	protected static Logger logger = Logger.getLogger("JLayerCheck"); 
	
	/**
	 * Contains the packages that belong to one module.
	 */
	protected Map<String, Set<String>> modulePackages = new TreeMap<String, Set<String>>();

	/**
	 * Contains the dependencies that belong to one module.
	 */
	protected Map<String, Set<String>> moduleDependencies = new TreeMap<String, Set<String>>();

	/**
	 * Contains the sources defined in the configuration file.
	 */
	protected List<ClassSource> classSources = new ArrayList<ClassSource>();
	
	/**
	 * Contains the packages that are excluded from the analysis (like java.**).
	 */
	protected Set<String> excludeList = new TreeSet<String>();
	
    /**
     * Contains classnames of classes that are used as program entries for the
     * orphaned classes search.
     */
    protected Set<String> entryClasses = new TreeSet<String>();
    
    /**
     * Parses the given configuration file.
     * 
     * @param is InputStream that points to an XML configuration file
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException 
     * @throws ParserConfigurationException
     * @throws ConfigurationException 
     */
	public XMLConfigurationParser(InputStream is) throws SAXException, IOException, ParserConfigurationException, ConfigurationException {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
		Element docElem = doc.getDocumentElement();

		// get the architecture tag
		if (!docElem.getNodeName().equalsIgnoreCase("jlayercheck")) {
			throw new ConfigurationException("Tag jlayercheck not found.");
		}

		// get the tag sources and architecture
		NodeList configNodeList = docElem.getChildNodes();
		for(int i=0; i<configNodeList.getLength(); i++) {
			Node configNode = configNodeList.item(i);
			if (configNode.getNodeType() == Node.ELEMENT_NODE) {
				// Element elemConfig = (Element) configNode;

				if (configNode.getNodeName().equals("sources")) {
					parseSourcesTag(configNode);
				}
				if (configNode.getNodeName().equals("architecture")) {
					parseArchitectureTag(configNode);
				}
			}
		}
	}

	protected void parseSourcesTag(Node configNode) {
		// get the tag module
		NodeList sourcesNodeList = configNode.getChildNodes();
		for(int i2=0; i2<sourcesNodeList.getLength(); i2++) {
			Node sourceNode = sourcesNodeList.item(i2);
			if (sourceNode.getNodeType() == Node.ELEMENT_NODE) {
				Element elemSource = (Element) sourceNode;
				
				if (sourceNode.getNodeName().equals("filesystem")) {
					logger.finer("Filesystem: "+elemSource.getAttribute("bin"));
					
					classSources.add(new FilesystemClassSource(elemSource.getAttribute("bin"), elemSource.getAttribute("src")));
				}
			}
		}
	}
	
	protected void parseArchitectureTag(Node configNode) {
		// get the tag module
		NodeList modulesNodeList = configNode.getChildNodes();
		for(int i2=0; i2<modulesNodeList.getLength(); i2++) {
			Node moduleNode = modulesNodeList.item(i2);
			if (moduleNode.getNodeType() == Node.ELEMENT_NODE) {
				if (moduleNode.getNodeName().equals("module")) {
					parseModule(moduleNode);
				}
				if (moduleNode.getNodeName().equals("exclude")) {
					parseExclude(moduleNode);
				}
                if (moduleNode.getNodeName().equals("entry")) {
                    parseEntry(moduleNode);
                }
			}
		}
	}

	protected void parseModule(Node moduleNode) {
		Element elemModule = (Element) moduleNode;
		String moduleName = elemModule.getAttribute("name");
		moduleDependencies.put(moduleName, new TreeSet<String>());

		// get the tag package and dependency
		NodeList nodeList = moduleNode.getChildNodes();
		for(int i3=0; i3<nodeList.getLength(); i3++) {
			Node packageDepNode = nodeList.item(i3);
			if (packageDepNode.getNodeType() == Node.ELEMENT_NODE) {
				Element elemPackageDep = (Element) packageDepNode;

				// add a new package to the current module
				if (elemPackageDep.getNodeName().equalsIgnoreCase("package")) {
					String packageName = elemPackageDep.getAttribute("name");

					addPackageToModule(moduleName, packageName);
				}

				// add a new dependency to the current module
				if (elemPackageDep.getNodeName().equalsIgnoreCase("dependency")) {
					String dependencyName = elemPackageDep.getAttribute("name");

					addDependencyToModule(moduleName, dependencyName);
				}
			}
		}
	}

	protected void parseExclude(Node excludeNode) {
		// get the tag package and dependency
		NodeList nodeList = excludeNode.getChildNodes();
		for(int i3=0; i3<nodeList.getLength(); i3++) {
			Node packageDepNode = nodeList.item(i3);
			if (packageDepNode.getNodeType() == Node.ELEMENT_NODE) {
				Element elemPackageDep = (Element) packageDepNode;

				// add a new package to the current module
				if (elemPackageDep.getNodeName().equalsIgnoreCase("package")) {
					String packageName = elemPackageDep.getAttribute("name");

					addPackageToExcludeList(packageName);
				}
			}
		}
	}

    /**
     * Parses the entry tag.
     * 
     * @param entryNode
     */
    protected void parseEntry(Node entryNode) {
        Element elemEntry = (Element) entryNode;
        entryClasses.add(elemEntry.getAttribute("name").replaceAll("\\.", "/"));
    }

	protected void addPackageToExcludeList(String packageName) {
		excludeList.add(packageName);
	}

	protected void addDependencyToModule(String moduleName, String dependencyName) {
		logger.finer("Add dependency "+dependencyName+" to module "+moduleName);
		
		Set<String> deps = moduleDependencies.get(moduleName);
		
		if (deps == null) {
			deps = new TreeSet<String>();
			moduleDependencies.put(moduleName, deps);
		}
		
		deps.add(dependencyName);
	}

	protected void addPackageToModule(String moduleName, String packageName) {
		logger.finer("Add package "+packageName+" to module "+moduleName);
		packageName = packageName.replaceAll("\\.", "/");

		Set<String> packs = modulePackages.get(moduleName);
		
		if (packs == null) {
			packs = new TreeSet<String>();
			modulePackages.put(moduleName, packs);
		}
		
		packs.add(packageName);
	}
	
	public static void main(String args[]) throws FileNotFoundException, SAXException, IOException, ParserConfigurationException, ConfigurationException {
		new XMLConfigurationParser(new FileInputStream("jlayercheck_test.xml"));
	}

	/**
	 * Returns the dependencies of all modules. The key is the element
	 * that depends from all values in the given set. E.g. a dataset
	 * a -> (b,c,d) means, that a depends on the modules b, c and d. This
	 * normally means that b, c and d are on a lower layer in the
	 * architecture.
	 * 
	 * @return Map with all module dependencies
	 */
	public Map<String, Set<String>> getModuleDependencies() {
		return moduleDependencies;
	}

	/**
	 * Returns the packages contained in every module. The key of the
	 * Map is the name of the module and the Set contains all package
	 * names.
	 * 
	 * @return Map with all modules and their packages
	 */
	public Map<String, Set<String>> getModulePackages() {
		return modulePackages;
	}

	/**
	 * <p>Returns the matching module for the given classname or null. If more than
	 * one module matches, an exception is thrown, because it is a configuration
	 * error.
	 * <p>The input must be a classname, not a package name. The last part is
	 * expected to be the name of the class. 
	 * 
	 * @return the matching module or null if none was found
	 * @throws OverlappingModulesDefinitionException 
	 */
	public String getMatchingModule(String classname) throws OverlappingModulesDefinitionException {
		String result = null;
		
		for(String modulename : getModulePackages().keySet()) {
			for(String packagename : getModulePackages().get(modulename)) {
				packagename = convertToRegularExpression(packagename);
				packagename = packagename + "/[^/]*"; // allow an appended classname 

				if (classname.matches(packagename)) {
					if (result == null) {
						result = modulename;
					} else {
						throw new OverlappingModulesDefinitionException("Class "+classname+" is matched by more than one module definition.");
					}
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Returns a map that contains the mapping from packages
	 * to modules.
	 * 
	 * @return
	 */
	public Map<String, String> getPackageModules() {
		Map<String, String> result = new TreeMap<String, String>();
		
		for(String modulename : getModulePackages().keySet()) {
			for(String packagename : getModulePackages().get(modulename)) {
				result.put(packagename, modulename);
			}
		}
		
		return result;
	}
	
	/**
	 * Returns the list of sources for the class and java files.
	 * 
	 * @return list of sources
	 */
	public List<ClassSource> getClassSources() {
		return classSources;
	}

    /**
     * Returns a map containing all java class names and an URL
     * that points to the source file.
     * 
     * @return
     */
    public Map<String, URL> getAllClassSources() {
        Map<String, URL> result = new TreeMap<String, URL>();
        for(ClassSource source : getClassSources()) {
            result.putAll(source.getSourceFiles());
        }
        return result;
    }

    /**
     * Returns all package entries that are specified to exclude
     * in the configuration file.
     * 
     * @return
     */
	public Set<String> getExcludeList() {
		return excludeList;
	}

	/**
	 * Returns true if the given class is excluded from the analysis.
	 * 
	 * @param dependency
	 * @return
	 */
	public boolean isExcluded(String classname) {
		for(String exclude : getExcludeList()) {
			exclude = convertToRegularExpression(exclude);
			
			if (classname.matches(exclude)) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Used internally to convert a string from the wildcard format used
	 * in the configuration file into a regular expression.
	 * 
	 * @param wildcardstring
	 * @return wildcardstring converted to regular expression
	 */
	protected String convertToRegularExpression(String wildcardstring) {
		wildcardstring = wildcardstring.replaceAll("\\.", "/");
//		wildcardstring = wildcardstring.replaceAll("/\\*[^\\*]", "/[^\\.]*");
		wildcardstring = wildcardstring.replaceAll("\\*", ".*");
		return wildcardstring;
	}

    /**
     * Returns a Set of classes that are named as entry classes in the
     * configuration file. These classes are points where the execution
     * of the system can start. All classes that are directly or indirectly
     * referenced from these named entry classes are marked as used, all
     * others are marked as unused/orphaned. 
     * 
     * @return the entryClasses
     */
    public Set<String> getEntryClasses() {
        return entryClasses;
    }

    /**
     * Calculates the orphaned classes based on the entry points of the configuration
     * file and the dependency data from the DependencyVisitor.
     * 
     * @param dv the dependency data to use
     * @throws OrphanedSearchException 
     */
    public Set<String> getOrphanedClasses(Map<String, Map<String, Set<Integer>>> dependencies) throws OrphanedSearchException {
        Set<String> visitedClasses = new HashSet<String>();
        
        // add the entry points
        visitedClasses.addAll(entryClasses);
        
        // add all dependend classes
        Set<String> unvisited = null;
        Map<String, URL> allClassSources = getAllClassSources();
        do {
            unvisited = getUnvisitedDependendClasses(visitedClasses, dependencies, allClassSources);
            visitedClasses.addAll(unvisited);
        } while(unvisited.size()>0);
        
        // find the missing classes and return them as orphaned
        Set<String> orphanedClasses = new TreeSet<String>();
        
        for(String classname : dependencies.keySet()) {
            if (!visitedClasses.contains(classname)) {
                orphanedClasses.add(classname);
            }
        }
        
        return orphanedClasses;
    }

    /**
     * Returns all classnames that are directly referenced by the visited classes
     * but not yet contained.
     * 
     * @param visitedClasses
     * @param dv the dependencies to use
     * @param allClassSources a map containing all classnames for which java source files are available
     * @return Set containing class names
     * @throws OrphanedSearchException 
     */
    protected Set<String> getUnvisitedDependendClasses(Set<String> visitedClasses, Map<String, Map<String, Set<Integer>>> dependencies, Map<String, URL> allClassSources) throws OrphanedSearchException {
        Set<String> result = new HashSet<String>();
        
        for(String visitedClass : visitedClasses) {
            if (dependencies.get(visitedClass) != null) {
                for(String classname : dependencies.get(visitedClass).keySet()) {
                    if (!visitedClasses.contains(classname)) {
                        result.add(classname);
                    }
                }
            } else {
                if (allClassSources.containsKey(visitedClass)) {
                    // a class file for a source file is missing!
                    throw new OrphanedSearchException("Class file for "+visitedClass+" is not available! (Source file is "+allClassSources.get(visitedClass).toExternalForm());
                } else {
                    logger.fine("Dependencies for "+visitedClass+" not found!");
                }
            }
        }
        
        return result;
    }
    
    /**
     * Returns a map containing the dependencies (from class, to class) that
     * are not allowed by the rules.
     * 
     * @param xcp the configuration to use
     * @return
     * @throws OverlappingModulesDefinitionException 
     */
    public Map<String, Map<String, ClassDependency>> getUnallowedDependencies(Map<String, Map<String, Set<Integer>>> dependencies) throws OverlappingModulesDefinitionException {
		Map<String, Map<String, ClassDependency>> unallowedDependencies = new TreeMap<String, Map<String, ClassDependency>>();
		
		for(String classname : dependencies.keySet()) {
			String classPackageName = StringUtils.getPackageName(classname);
			for(String dependency : dependencies.get(classname).keySet()) {
				String dependencyPackageName = StringUtils.getPackageName(dependency);
				
				// check if packagename is an allowed dependency for classname
				if (isUnallowedDependency(classname, dependency)) {
					Map<String, ClassDependency> depList = unallowedDependencies.get(classname);
					if (depList == null) {
						depList = new TreeMap<String, ClassDependency>();
						unallowedDependencies.put(classname, depList);
					}
					ClassDependency cd = depList.get(dependency);
					if (cd == null) {
						cd = new ClassDependency(dependency);
						depList.put(dependency, cd);
					}
					for(Integer lineNumber : dependencies.get(classname).get(dependency)) {
						cd.addLineNumber(lineNumber);
					}
				}
			}
		}
		
		return unallowedDependencies;
    }

    /**
     * Returns true when the dependency from fromClass to toClass is not allowed,
     * otherwise false.
     * 
     * @param fromClass
     * @param toClass
     * @return
     * @throws OverlappingModulesDefinitionException
     */
    public boolean isUnallowedDependency(String fromClass, String toClass) throws OverlappingModulesDefinitionException {
		String classmodule = getMatchingModule(fromClass);
		String dependencymodule = getMatchingModule(toClass);
		
		if (classmodule == null) {
			// unspecified package
			return false;
		} else {
			if (!classmodule.equals(dependencymodule)) {
				if (!(isExcluded(fromClass) || isExcluded(toClass))) {
					if (getModuleDependencies().get(classmodule) == null || dependencymodule == null || 
							!getModuleDependencies().get(classmodule).contains(dependencymodule)) {

						return true;
					}
				}
			}
		}
	
		return false;
    }
    
    /**
     * Returns a list of packages that are not assigned to
     * a module in the given configuration.
     * 
     * @param xcp
     * @return
     * @throws OverlappingModulesDefinitionException 
     */
    public Set<String> getUnspecifiedPackages(Map<String, Map<String, Set<Integer>>> dependencies) throws OverlappingModulesDefinitionException {
		Set<String> unspecifiedPackages = new TreeSet<String>();
		
		for(String classname : dependencies.keySet()) {
			String classPackageName = StringUtils.getPackageName(classname);
			
			// check if packagename is an allowed dependency for classname
			String classmodule = getMatchingModule(classPackageName+"/Dummy");

			if (classmodule == null) {
				unspecifiedPackages.add(classPackageName);
			}
		}
		
		return unspecifiedPackages;
    }
    
    /**
     * Builds a model that contains all dependency information that was retrieved
     * from the class files. It can be used for displaying the dependencies in a 
     * tree view.
     * 
     * @param dv
     * @return
     * @throws OverlappingModulesDefinitionException
     */
    public ModelTree getModelTree(DependencyVisitor dv) throws OverlappingModulesDefinitionException {
    	DefaultModelTree result = new DefaultModelTree();
    	
    	// build ModelTree
    	for(String packagename : dv.getPackages().keySet()) {

    		// get the module for this package
			String packagemodule = getMatchingModule(packagename+"/Dummy");
			
			if (packagemodule == null) {
				packagemodule = "unassigned";
			}
			
			ModuleNode module = result.getModule(packagemodule);
			
			if (module == null) {
				module = new DependentModuleNode(packagemodule);
				result.add(module);
			}
			
			// add the new package node
    		DefaultPackageNode packagenode = new DependentPackageNode(packagename);
			module.add(packagenode);

    		for(String classname : dv.getPackages().get(packagename)) {
    			// add class nodes for all packages
    			packagenode.add(new DependentClassNode(new ClassDependency(classname)));
    		}
    	}

    	// add dependencies
    	for(String classname : dv.getDependencies().keySet()) {
    		for(String dep : dv.getDependencies().get(classname).keySet()) {
    			// create ClassDependency
    			ClassDependency cd = new ClassDependency(dep);
    			
    			for(Integer line : dv.getDependencies().get(classname).get(dep)) {
    				cd.addLineNumber(line);
    			}

    			cd.setUnallowedDependency(isUnallowedDependency(classname, dep));
    			
    			// and add it to the ClassNode
				ClassNode cn = result.getClassNode(classname);
				if (cn != null) {
					cn.addClassDependency(cd);
				} else {
					System.out.println("Class "+classname+" not found (to "+dep+")!");
				}
    		}
    	}

    	// calculate state unallowed/allowed dependencies for the tree
		// compute unallowed dependency marks
		for(ModuleNode mn : result.getModules()) {
			boolean mnUnallowed = false;
			for(PackageNode pn : mn.getPackages()) {
				boolean pnUnallowed = false;
				for (ClassNode cn : pn.getClasses()) {
					if (cn instanceof DependentClassNode) {
						DependentClassNode dcn = (DependentClassNode) cn;
						
						DependenciesTreeModel dtm = new DependenciesTreeModel(dcn, result);
						dcn.getClassDependency().setUnallowedDependency(((UnallowedOrAllowedDependency) dtm.getRoot()).isUnallowedDependency()); 

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

    	
    	return result;
    }
}
