package net.sf.jlayercheck.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


import org.objectweb.asm.ClassReader;

/**
 * Utility methods for calling the asm parser on a number of files.
 * 
 * @author webmaster@earth3d.org
 */
public class DependencyParser {

    public static void callForFilesystem(final String[] args, DependencyVisitor v) throws IOException {
		File f = new File(args[0]);

        checkDirectory(v, f);
	}

	protected static void checkDirectory(DependencyVisitor v, File f) throws IOException, FileNotFoundException {
        File files[] = f.listFiles();
        for(File file : files) {
        	if (file.isDirectory()) {
        		checkDirectory(v, file);
        	} else {
        		String name = file.getAbsolutePath();
        		if (name.endsWith(".class")) {
        			new ClassReader(new FileInputStream(file)).accept(v, 0);
        		}
        	}
        }
	}

	public static void callForZipFile(final String zipfilename, DependencyVisitor v) throws IOException {
		ZipFile f = new ZipFile(zipfilename);

        Enumeration< ? extends ZipEntry> en = f.entries();
        while (en.hasMoreElements()) {
            ZipEntry e = en.nextElement();
            String name = e.getName();
            if (name.endsWith(".class")) {
                new ClassReader(f.getInputStream(e)).accept(v, 0);
            }
        }
	}
}
