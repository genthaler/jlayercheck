package net.sf.jlayercheck.out.html;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

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
	protected VelocityGenerator generator = new VelocityGenerator();
	
	/** The output dir */
	protected File outDir;

	// methods
	// ----------------------------------------------------------------------

	/**
	 * Prepare velocity context for generator and template.
	 */
	protected void prepareContext() {
		Map<String, Object> addCtx = new HashMap<String, Object>();
		
		addCtx.put("tool", this);
		
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
		String content = IOHelper.readContent(sourcefile);
		JavaSourceConversionOptions options = JavaSourceConversionOptions
				.getDefault();
		options.setShowLineNumbers(true);
		ret = Java2Html.convertToHtml(content, options);
		return ret;
	}

	// template methods
	// ----------------------------------------------------------------------

	/**
	 * Copy resource to {@link #outDir}.
	 * @param resourcename teh resource
	 * @return always <code>null</code>
	 */
	public Object copyResource(String resourcename) {
		File out = new File(outDir, resourcename);
		IOHelper.copy(getClass().getResource(resourcename), out);
		return null;
	}
}
