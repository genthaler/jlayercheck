package net.sf.jlayercheck.util;

public class StringUtils {
    /**
     * Returns the package name part of the given class name. E.g.
     * if the given classname is "java/lang/System", it returns "java/lang".
     * 
     * @param classname
     * @return
     */
    public static String getPackageName(String classname) {
    	String packagename = "";
    	
        int n = classname.lastIndexOf('/');
        if (n > -1) {
            packagename = classname.substring(0, n);
        }
        
        return packagename;
	}
}
