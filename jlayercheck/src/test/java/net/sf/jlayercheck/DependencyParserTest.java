package net.sf.jlayercheck;

import net.sf.jlayercheck.util.DependencyParser;
import net.sf.jlayercheck.util.DependencyVisitor;
import junit.framework.TestCase;

public class DependencyParserTest extends TestCase {
	/**
	 * Tests the parser using a test jar file named test.jar 
	 * (which is a copy of asm.jar).
	 * 
	 * @throws Exception
	 */
	public void testParserJar() throws Exception {
		String testfile = getClass().getResource("/test.jar").getFile();
		
		DependencyVisitor v = new DependencyVisitor();
		
		DependencyParser.callForZipFile(testfile, v);
		
		assertTrue(v.getPackages().containsKey("org/objectweb/asm"));
		assertTrue(v.getPackages().get("org/objectweb/asm").contains("org/objectweb/asm/ClassReader"));
		
		assertTrue(v.getDependencies().containsKey("org/objectweb/asm/ClassReader"));
		assertTrue(v.getDependencies().get("org/objectweb/asm/ClassReader").containsKey("java/io/InputStream"));
	}

	/**
	 * Tests the parser by letting the project parse itself.
	 * 
	 * @throws Exception
	 */
	public void testParserClasses() throws Exception {
		DependencyVisitor v = new DependencyVisitor();
		
		DependencyParser.callForFilesystem(new String[]{"target/test-classes"}, v);
		
		assertTrue(v.getPackages().get("net/sf/jlayercheck/ant").contains("net/sf/jlayercheck/ant/JLCTaskTest"));
	}
}
