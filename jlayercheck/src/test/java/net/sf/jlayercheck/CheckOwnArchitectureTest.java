package net.sf.jlayercheck;

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
 * Checks if the architecture of JLayerCheck is valid. Uses jlayercheck.xml
 * as definition file.
 * 
 * @author webmaster@earth3d.org
 */
public class CheckOwnArchitectureTest extends TestCase {
	public void testCheckOwn() throws Exception {
		// load and parse configuration, class and java files
		InputStream is = getClass().getResource("/jlayercheck.xml").openStream();
		XMLConfigurationParser xcp = new XMLConfigurationParser(is);
		DependencyVisitor dv = new DependencyVisitor();
		Map<String, URL> javaSources = new TreeMap<String, URL>();
        for(ClassSource source : xcp.getClassSources()) {
            source.call(dv);
            javaSources.putAll(source.getSourceFiles());
        }

		HTMLOutput html = new HTMLOutput("target/tmp_jlayercheck");
		html.write(dv, xcp);
	}
}
