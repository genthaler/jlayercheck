package net.sf.jlayercheck.gui;

import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import junit.framework.TestCase;
import net.sf.jlayercheck.util.exceptions.CycleFoundException;
import net.sf.jlayercheck.util.graph.GraphModuleDependencies;

/**
 * Tests the class that generates the module hierarchy graphic.
 * 
 * @author webmaster@earth3d.org
 */
public class GraphModuleDependenciesTest extends TestCase {

	/**
	 * Checks the cycle detection of the JGraphT library.
	 * 
	 * @throws Exception
	 */
	public void testCycleException() throws Exception {
    	Map<String, Set<String>> testmap = new TreeMap<String, Set<String>>();
    	
    	Set<String> guiset = new TreeSet<String>();
    	guiset.add("domain");
    	guiset.add("persistency");
    	testmap.put("gui3", guiset);
    	
    	Set<String> pset = new TreeSet<String>();
    	pset.add("domain");
    	testmap.put("persistency", pset);

    	Set<String> dset = new TreeSet<String>();
    	dset.add("gui3");
    	testmap.put("domain", dset);

    	try {
    		new GraphModuleDependencies(testmap);
    		assertTrue(false); // if this line was reached, an error
    		// occured, because a CycleFoundException should have
    		// been thrown
    	} catch(CycleFoundException e) {
    		
    	}
	}
	
	public void testNormalCase() throws Exception {
    	Map<String, Set<String>> testmap = new TreeMap<String, Set<String>>();
    	
    	Set<String> guiset = new TreeSet<String>();
    	guiset.add("domain");
    	guiset.add("persistency");
    	testmap.put("gui", guiset);
    	
    	Set<String> pset = new TreeSet<String>();
    	pset.add("domain");
    	testmap.put("persistency", pset);

    	Set<String> dset = new TreeSet<String>();
    	testmap.put("domain", dset);

    	BufferedImage bi = new GraphModuleDependencies(testmap).getImage();
    	
    	assertTrue(bi.getWidth()>100);
    	assertTrue(bi.getHeight()>100);
	}
}
