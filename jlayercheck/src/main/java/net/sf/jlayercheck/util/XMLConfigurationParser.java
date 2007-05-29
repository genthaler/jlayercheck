package net.sf.jlayercheck.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.jlayercheck.util.exceptions.ConfigurationException;
import net.sf.jlayercheck.util.model.FilesystemClassSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Used to create XMLConfiguration objects from an xml configuration file.
 * 
 * @author webmaster@earth3d.org
 */
public class XMLConfigurationParser {
	protected static Logger logger = Logger.getLogger("JLayerCheck"); 

	/**
	 * Creates a new parser object that can be used to create XMLConfiguration objects.
	 */
	public XMLConfigurationParser() {
	}
	
    /**
     * Parses the given configuration file and creates an XMLConfiguration object.
     * 
     * @param is InputStream that points to an XML configuration file
     * @throws ConfigurationException 
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException 
     */
	public XMLConfiguration parse(InputStream is) throws ConfigurationException, SAXException, IOException, ParserConfigurationException {
		XMLConfiguration xmlConf = new XMLConfiguration();
		
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
					parseSourcesTag(configNode, xmlConf);
				}
				if (configNode.getNodeName().equals("architecture")) {
					parseArchitectureTag(configNode, xmlConf);
				}
			}
		}
		
		return xmlConf;
	}

	protected void parseArchitectureTag(Node configNode, XMLConfiguration xmlConf) {
		// get the tag module
		NodeList modulesNodeList = configNode.getChildNodes();
		for(int i2=0; i2<modulesNodeList.getLength(); i2++) {
			Node moduleNode = modulesNodeList.item(i2);
			if (moduleNode.getNodeType() == Node.ELEMENT_NODE) {
				if (moduleNode.getNodeName().equals("module")) {
					parseModule(moduleNode, xmlConf);
				}
				if (moduleNode.getNodeName().equals("exclude")) {
					parseExclude(moduleNode, xmlConf);
				}
	            if (moduleNode.getNodeName().equals("entry")) {
	                parseEntry(moduleNode, xmlConf);
	            }
			}
		}
	}

	/**
	 * Parses the entry tag.
	 * 
	 * @param entryNode
	 * @param xmlConf 
	 */
	protected void parseEntry(Node entryNode, XMLConfiguration xmlConf) {
	    Element elemEntry = (Element) entryNode;
	    xmlConf.addEntryClass(elemEntry.getAttribute("name").replaceAll("\\.", "/"));
	}

	protected void parseExclude(Node excludeNode, XMLConfiguration xmlConf) {
		// get the tag package and dependency
		NodeList nodeList = excludeNode.getChildNodes();
		for(int i3=0; i3<nodeList.getLength(); i3++) {
			Node packageDepNode = nodeList.item(i3);
			if (packageDepNode.getNodeType() == Node.ELEMENT_NODE) {
				Element elemPackageDep = (Element) packageDepNode;
	
				// add a new package to the current module
				if (elemPackageDep.getNodeName().equalsIgnoreCase("package")) {
					String packageName = elemPackageDep.getAttribute("name");
	
					xmlConf.addPackageToExcludeList(packageName);
				}
			}
		}
	}

	protected void parseModule(Node moduleNode, XMLConfiguration xmlConf) {
		Element elemModule = (Element) moduleNode;
		String moduleName = elemModule.getAttribute("name");
		xmlConf.addModuleDependency(moduleName, new TreeSet<String>());
	
		// get the tag package and dependency
		NodeList nodeList = moduleNode.getChildNodes();
		for(int i3=0; i3<nodeList.getLength(); i3++) {
			Node packageDepNode = nodeList.item(i3);
			if (packageDepNode.getNodeType() == Node.ELEMENT_NODE) {
				Element elemPackageDep = (Element) packageDepNode;
	
				// add a new package to the current module
				if (elemPackageDep.getNodeName().equalsIgnoreCase("package")) {
					String packageName = elemPackageDep.getAttribute("name");
	
					xmlConf.addPackageToModule(moduleName, packageName);
				}
	
				// add a new dependency to the current module
				if (elemPackageDep.getNodeName().equalsIgnoreCase("dependency")) {
					String dependencyName = elemPackageDep.getAttribute("name");
	
					xmlConf.addDependencyToModule(moduleName, dependencyName);
				}
			}
		}
	}

	protected void parseSourcesTag(Node configNode, XMLConfiguration xmlConf) {
		// get the tag module
		NodeList sourcesNodeList = configNode.getChildNodes();
		for(int i2=0; i2<sourcesNodeList.getLength(); i2++) {
			Node sourceNode = sourcesNodeList.item(i2);
			if (sourceNode.getNodeType() == Node.ELEMENT_NODE) {
				Element elemSource = (Element) sourceNode;
				
				if (sourceNode.getNodeName().equals("filesystem")) {
					logger.finer("Filesystem: "+elemSource.getAttribute("bin"));
					
					xmlConf.addClassSource(new FilesystemClassSource(elemSource.getAttribute("bin"), elemSource.getAttribute("src")));
				}
			}
		}
	}
}
