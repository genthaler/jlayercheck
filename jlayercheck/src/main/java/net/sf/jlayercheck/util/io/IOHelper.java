/*
 * $Author$
 * $Id$ 
 */
package net.sf.jlayercheck.util.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;

/**
 * Some helper methods for file and io handling.
 * 
 * @author timo
 * @author $Author$
 * @version $Id$
 */
public class IOHelper {

	/** Buffer size for IO. 2KB for most systems. */
	public static final int BUFFER_SIZE = 2048;

	/**
	 * Close stream, catching all exceptions.
	 * 
	 * @param stream
	 */
	public static void close(OutputStream stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				// Ignore
			}
		}
	}

	/**
	 * Close stream, catching all exceptions.
	 * 
	 * @param stream
	 */
	public static void close(InputStream stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				// Ignore
			}
		}
	}

	/**
	 * Close reader, catching all exceptions.
	 * 
	 * @param reader
	 */
	public static void close(Reader reader) {
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				// Ignore
			}
		}
	}

	/**
	 * Close writer, catching all exceptions.
	 * 
	 * @param writer
	 */
	public static void close(Writer writer) {
		if (writer != null) {
			try {
				writer.close();
			} catch (IOException e) {
				// Ignore
			}
		}
	}

	// ----------------------------------------------------------------------

	/**
	 * Copy content from in to out.
	 * 
	 * @param in
	 *            The stream to read from
	 * @param out
	 *            The stream to write to
	 * @throws IOException
	 */
	public static void copy(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[BUFFER_SIZE];
		int len = in.read(buffer);
		while (len > 0) {
			out.write(buffer, 0, len);
			len = in.read(buffer);
		}
	}

	/**
	 * Copy content from in to out.
	 * 
	 * @param in
	 *            The reader to read from
	 * @param out
	 *            The writer to write to
	 * @throws IOException
	 */
	public static void copy(Reader in, Writer out) throws IOException {
		char[] buffer = new char[BUFFER_SIZE];
		int len = in.read(buffer);
		while (len > 0) {
			out.write(buffer, 0, len);
			len = in.read(buffer);
		}
	}

	/**
	 * Copy file to another file
	 * 
	 * @param from
	 *            source file
	 * @param to
	 *            destination file
	 */
	public static void copy(File from, File to) {
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			in = new FileInputStream(from);
			out = new FileOutputStream(to);
			copy(in, out);
		} catch (IOException e) {
			throw new RuntimeException("Error copying " + from + " to " + to, e);
		} finally {
			close(in);
			close(out);
		}
	}

	/**
	 * Copy content from URL to a file
	 * 
	 * @param from
	 *            source
	 * @param to
	 *            destination file
	 */
	public static void copy(URL from, File to) {
		InputStream in = null;
		FileOutputStream out = null;
		try {
			in = from.openStream();
			out = new FileOutputStream(to);
			copy(in, out);
		} catch (IOException e) {
			throw new RuntimeException("Error copying " + from + " to " + to, e);
		} finally {
			close(in);
			close(out);
		}
	}

	// ----------------------------------------------------------------------

	/**
	 * Read content from URL into string.
	 * 
	 * @param url
	 *            URL to read
	 * @return a String containing complete content
	 */
	public static String readContent(URL url) {
		StringWriter writer = new StringWriter();
		Reader reader = null;
		try {
			reader = new InputStreamReader(url.openStream());
			copy(reader, writer);
		} catch (IOException e) {
			throw new RuntimeException("Cannot read " + url, e);
		} finally {
			close(reader);
		}
		return writer.toString();
	}

}
