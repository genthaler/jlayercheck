package net.sf.jlayercheck;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

import junit.framework.TestCase;
import net.sf.jlayercheck.util.DependencyVisitor;
import net.sf.jlayercheck.util.HTMLOutput;
import net.sf.jlayercheck.util.XMLConfigurationParser;
import net.sf.jlayercheck.util.model.ClassSource;

/**
 * Tests if the html output is generated correctly for an example configuration.
 *  
 * @author webmaster@earth3d.org
 */
public class HTMLOutputTest extends TestCase {
	public void testWrite() throws Exception {
		// load and parse configuration, class and java files
		InputStream is = getClass().getResource("/jlayercheck_test.xml").openStream();
		XMLConfigurationParser xcp = new XMLConfigurationParser(is);
		DependencyVisitor dv = new DependencyVisitor();
		Map<String, URL> javaSources = new TreeMap<String, URL>();
        for(ClassSource source : xcp.getClassSources()) {
            source.call(dv);
            javaSources.putAll(source.getSourceFiles());
        }

		HTMLOutput html = new HTMLOutput("target/tmp");
		html.write(dv, xcp);
		
		assertTrue(new File("target/tmp/unspecified.html").exists());
		assertTrue(new File("target/tmp/violations.html").exists());
	}
}
