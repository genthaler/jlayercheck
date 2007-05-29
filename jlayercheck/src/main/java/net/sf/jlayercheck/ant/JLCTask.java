package net.sf.jlayercheck.ant;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import net.sf.jlayercheck.util.DependencyVisitor;
import net.sf.jlayercheck.util.HTMLOutput;
import net.sf.jlayercheck.util.XMLConfiguration;
import net.sf.jlayercheck.util.XMLConfigurationParser;
import net.sf.jlayercheck.util.model.ClassSource;

/**
 * Configure and start HTML-output generation as ant task.
 * 
 * @author timo
 * @author $Author$
 * @version $Id$
 */
public class JLCTask {

	/** Default name for JLayerCheck configuration file */
	public static final String CONFIG = "jlayercheck_test.xml"; //$NON-NLS-1$

	/** Default name for JLayerCheck output dir */
	public static final String OUTDIR = "jlayercheck-out"; //$NON-NLS-1$

	/** The configuration file */
	protected File config = new File(CONFIG);

	/** the output directory */
	protected File outDir = new File(OUTDIR);

	// methods

	// getter and setter
	public File getConfig() {
		return config;
	}

	public void setConfig(File config) {
		this.config = config;
	}

	public File getOutDir() {
		return outDir;
	}

	public void setOutDir(File outDir) {
		this.outDir = outDir;
	}

	// ant entry point

	/**
	 * Start execution. {@link #config} and {@link #outDir} must be set prior.
	 */
	public void execute() {
		// check params
		if (getConfig() == null || getOutDir() == null) {
			throw new IllegalArgumentException("Config and outDir must be set!");
		}
		File config = getConfig();
		if (!config.exists()) {
			throw new RuntimeException("Cannot read " + config);
		}

		try {
			// read config
			InputStream confIn = new FileInputStream(config);
			XMLConfiguration xcp = new XMLConfigurationParser().parse(confIn);

			// prepare outdir
			File outDir = getOutDir();
			outDir.mkdirs();

			// start processing
			DependencyVisitor dv = new DependencyVisitor();
			Map<String, URL> javaSources = new TreeMap<String, URL>();

			// visit all classes
			for (ClassSource cs : xcp.getClassSources()) {
				cs.call(dv);
				javaSources.putAll(cs.getSourceFiles());
			}

			// create output
			HTMLOutput html = new HTMLOutput(outDir.getAbsolutePath());
			html.write(dv, xcp);

		} catch (IOException ioe) {
			throw new RuntimeException("Error reading configuration " + config
					+ ": " + ioe, ioe);
		} catch (SAXException e) {
			throw new RuntimeException("Error reading configuration " + config
					+ ": " + e, e);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException("Error reading configuration " + config
					+ ": " + e, e);
		} catch (Exception e) {
			throw new RuntimeException("unexpected Exception caught: " + e, e);
		}

	}

}
