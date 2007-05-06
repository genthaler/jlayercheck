package net.sf.jlayercheck;

import net.sf.jlayercheck.DependencyParser;
import net.sf.jlayercheck.util.DependencyVisitor;
import junit.framework.TestCase;

public class DependencyParserTest extends TestCase {
	public void testParser() throws Exception {
		String testfile = getClass().getResource("/test.jar").getFile();
		
		DependencyVisitor v = new DependencyVisitor();
		
		DependencyParser.callForZipFile(testfile, v);
		
		assertTrue(v.getPackages().containsKey("org/objectweb/asm"));
		assertTrue(v.getPackages().get("org/objectweb/asm").contains("org/objectweb/asm/ClassReader"));
		
		assertTrue(v.getDependencies().containsKey("org/objectweb/asm/ClassReader"));
		assertTrue(v.getDependencies().get("org/objectweb/asm/ClassReader").containsKey("java/io/InputStream"));
	}
}
