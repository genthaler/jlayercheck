package net.sf.jlayercheck.util;

/**
 * Contains the information that a given method of a given class was called
 * in a given line. The type determines if it was a method or a constructor.
 * In case of a constructor, the method name is null.
 * 
 * @author webmaster@earth3d.org
 */
public class MethodCall {
	@Override
	public boolean equals(Object obj) {
		MethodCall other = (MethodCall) obj;
		
		if (getType() != other.getType()) return false;
		if (getLineNumber() != other.getLineNumber()) return false;

		String classname1 = getClassName();
		if (classname1 == null) classname1 = "";
		String classname2 = other.getClassName();
		if (classname2 == null) classname2 = "";
		
		if (!classname1.equals(classname2)) return false;

		String methodname1 = getMethodName();
		if (methodname1 == null) methodname1 = "";
		String methodname2 = other.getMethodName();
		if (methodname2 == null) methodname2 = "";

		if (!methodname1.equals(methodname2)) return false;
		
		return true;
	}

	@Override
	public int hashCode() {
		return (""+getClassName()+" "+getMethodName()+" "+getLineNumber()+" "+getType()).hashCode();
	}

	protected String className;
	protected String methodName;
	
	public enum Type {
		CONSTRUCTOR,
		METHOD
	};
	
	protected Type type;
	
	protected int lineNumber;
	
	public MethodCall(String className, String methodName, Type type, int lineNumber) {
		this.className = className;
		this.methodName = methodName;
		this.type = type;
		this.lineNumber = lineNumber;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public Type getType() {
		return type;
	}

	public String getMethodName() {
		return methodName;
	}

	public String getClassName() {
		return className;
	}
}
