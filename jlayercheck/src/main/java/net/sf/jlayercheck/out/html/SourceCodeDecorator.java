package net.sf.jlayercheck.out.html;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import net.sf.jlayercheck.util.io.IOHelper;
import de.java2html.Java2Html;
import de.java2html.options.JavaSourceConversionOptions;

/**
 * Generates the decorated source code as HTML.
 */
public class SourceCodeDecorator {
	
	/** Name of this package */
	public static final String PACKAGE = SourceCodeDecorator.class.getPackage().getName();
	
	/** Template to use */
	public static final String TEMPLATENAME = PACKAGE + ".decoratedSource";
	
	// fields
	// ----------------------------------------------------------------------
	
	/** The generator to use */
	protected VelocityGenerator generator;
	
	/** The output directory */
	protected File outputDir;
	
	/** The output file, not necessarily in {@link #outputDir} */
	protected File outputFile;

	// construction
	// ----------------------------------------------------------------------

	public SourceCodeDecorator(File outputDir, File outputFile) {
		super();
		setOutputDir(outputDir);
		setOutputFile(outputFile);
		
		generator = new VelocityGenerator();
		prepareContext();
	}
	
	// methods
	// ----------------------------------------------------------------------

	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}

	public File getOutputFile() {
		return outputFile;
	}

	public void setOutputDir(File outputDir) {
		this.outputDir = outputDir;
	}

	public File getOutputDir() {
		return outputDir;
	}

	// template methods
	// ----------------------------------------------------------------------
	
	/**
	 * Prepare velocity context for generator and template.
	 */
	protected void prepareContext() {
		Map<String, Object> addCtx = new HashMap<String, Object>();
		
		// make this object available in template
		addCtx.put("tool", this); //$NON-NLS-1$
		
		addCtx.put("outputDir", getOutputDir());
		addCtx.put("outputFile", getOutputFile());
		addCtx.put("filename", getOutputFile().getName());
		
		generator.addToContext(addCtx);
	}

	/**
	 * Use java2html to parse the source.
	 * 
	 * @param sourcefile
	 *            The {@link URL} of the java source file
	 * @return The source as HTML
	 */
	public String convert2HTML(URL sourcefile) throws IOException {
		String ret = null;
		// read source file completely into String
		String content = IOHelper.readContent(sourcefile);
		JavaSourceConversionOptions options = JavaSourceConversionOptions
				.getDefault();
		options.setShowLineNumbers(true);
		// convert source code to html 
		ret = Java2Html.convertToHtml(content, options);
		return ret;
	}
	
	/**
	 * Separates source lines from java2html's HTML code.
	 * @param htmlCode the HTML code
	 * @return A list of source lines as HTML fragments
	 */
	public List<String> parseHTML(String htmlCode) {
		if (htmlCode == null) {
			throw new NullPointerException("htmlCode must not be null!");
		}
		if ("".equals(htmlCode)) {
			throw new IllegalArgumentException("htmlCode must be set properly!");
		}
		
		List<String> ret = new ArrayList<String>();
		
		// All source code lines are embraced by <code>
		String content = htmlCode;
		int startPos = content.indexOf("<code"); //$NON-NLS-1$
		int endPos = content.lastIndexOf("</code", startPos + 1); //$NON-NLS-1$
		if (startPos < 0 || endPos < 0 || endPos <= startPos) {
			throw new IllegalArgumentException("Cannot parse htmlCode!");
		}
		// start after closing <code ... > tag
		startPos = content.indexOf('>', startPos);
		
		// parse source code lines only
		content = content.substring(startPos, endPos - 1);
		// lines are split by <br />
		StringTokenizer tokens = new StringTokenizer(content, "<br"); //$NON-NLS-1$
		while (tokens.hasMoreTokens()) {
			String token = tokens.nextToken();
			// use fragment after closed tag
			int bPos = token.indexOf('>');
			if (bPos < 0) {
				throw new IllegalArgumentException("Cannot parse htmlCode in line " + ret.size());
			}
			token = token.substring(bPos);
			ret.add(token);
		}
		
		return ret;
	}
	
	public void generate() {
		
	}

	// template methods
	// ----------------------------------------------------------------------

	/**
	 * Copy resource to {@link #outputDir}.
	 * @param resourcename the resource
	 * @return always <code>null</code>
	 */
	public Object copyResource(String resourcename) {
		File out = new File(outputDir, resourcename);
		IOHelper.copy(getClass().getResource(resourcename), out);
		return null;
	}
}
