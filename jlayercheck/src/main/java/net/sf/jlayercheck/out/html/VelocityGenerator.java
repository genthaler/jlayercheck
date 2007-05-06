package net.sf.jlayercheck.out.html;

import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

/**
 * Prepares velocity and an initial context for a template.
 * 
 * @author timo
 * @author $Author: timo $
 * @version $Id: VelocityGenerator.java,v 1.1 2007/05/04 20:13:50 timo Exp $
 */
public class VelocityGenerator {

	/** Use UTF-8 as default encoding for templates. */
	public static final String DEFAULT_ENCODING = "UTF-8";

	static {
		// Initialize velocity at least once
		try {
			Velocity.init();
		} catch (Exception e) {
			throw new InternalError("Cannot initialize velocity: " + e);
		}
	}

	// fields

	/** The name of the template */
	protected String template;

	/** The {@link VelocityContext} for the template. */
	protected VelocityContext context;

	// construction

	/**
	 * Create a new generator.
	 */
	public VelocityGenerator() {
		super();
	}

	/**
	 * Create a new generator for the given template
	 * 
	 * @param template
	 *            The name of the template to use.
	 * @param context
	 *            A Map with
	 */
	public VelocityGenerator(String template, Map<String, Object> addCtx) {
		this();
		setTemplate(template);
		addToContext(addCtx);
	}

	// getter and setter

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
		// simply put template name into context too
		getContext().put("templateName", template);

	}

	// context

	/**
	 * Get the current {@link #context} or create a new empty one.
	 */
	public VelocityContext getContext() {
		if (context == null) {
			// create inital empty context
			context = new VelocityContext();

			// default entries
			Date now = new Date();
			context.put("now", now);
			context.put("nowIso", new SimpleDateFormat("yyyy-MM-dd_HH:mm:ssZ")
					.format(now));
			context
					.put("today", SimpleDateFormat.getDateInstance()
							.format(now));
			context.put("todayIso", new SimpleDateFormat("yyyy-MM-dd")
					.format(now));

			// self-reference the context
			context.put("context", context);

			// add this
			context.put("generator", this);

		}
		return context;
	}

	/**
	 * Set the {@link #context}.
	 * 
	 * @param context
	 *            the new context
	 */
	public void setContext(VelocityContext context) {
		this.context = context;
	}

	/**
	 * Add all entries to the {@link #context}.
	 * 
	 * @param addCtx
	 *            The map containing the entries
	 */
	public void addToContext(Map<String, Object> addCtx) {
		if (context == null) {
			// initialize context
			getContext();
		}
		for (Map.Entry<String, Object> entry : addCtx.entrySet()) {
			context.put(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Start generation and write output.
	 * 
	 * @param writer
	 *            write output into this writer
	 */
	public void generate(Writer writer) {
		if (writer == null)
			throw new NullPointerException("writer must not be null!");

		String templateName = getTemplate();
		if (templateName == null)
			throw new NullPointerException("template must not be null!");

		try {
			// let the party start
			Velocity.mergeTemplate(templateName, DEFAULT_ENCODING, //
					getContext(), writer);
		} catch (ResourceNotFoundException e) {
			// wrong template name?
			throw new RuntimeException(e);
		} catch (ParseErrorException e) {
			// syntax error: problem parsing the template
			throw new RuntimeException(e);
		} catch (MethodInvocationException e) {
			// something threw an exception in the template
			throw new RuntimeException(e);
		} catch (Exception e) {
			// Something weird happend
			throw new RuntimeException(e);
		}

	}

}
