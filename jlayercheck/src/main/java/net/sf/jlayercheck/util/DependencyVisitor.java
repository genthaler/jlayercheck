package net.sf.jlayercheck.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import net.sf.jlayercheck.util.model.ClassDependency;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;

/**
 * DependencyVisitor (based on an example class from the ASM package).
 * 
 * @author Eugene Kuleshov
 */
public class DependencyVisitor implements
        AnnotationVisitor,
        SignatureVisitor,
        ClassVisitor,
        FieldVisitor,
        MethodVisitor
{
    protected Map<String, Set<String>> packages = new HashMap<String, Set<String>>();

    protected Map<String, Map<String, Set<Integer>>> classDependencies = new HashMap<String, Map<String, Set<Integer>>>();

    protected String currentClass;
    
    /**
     * Contains the dependencies for the current class.
     */
    protected Map<String, Set<Integer>> currentClassDependencies;

    protected int currentLineNumber;
    
    /**
     * Returns a Map containing class names as keys and another Map
     * as values. This second Map contains the dependend class and a
     * count, how often it was referenced.
     *  
     * @return Map containing class dependencies
     */
    public Map<String, Map<String, Set<Integer>>> getDependencies() {
        return classDependencies;
    }

    /**
     * Returns a Map containing package names as keys and another Map
     * as values. This second Map contains class names that belong to
     * the given package.
     *   
     * @return
     */
    public Map<String, Set<String>> getPackages() {
        return packages;
    }

    // ClassVisitor

    /**
     * Called when a new class file is loaded.
     */
    public void visit(final int version, final int access, final String name,
			final String signature, final String superName,
			final String[] interfaces) {
		currentClass = name;
        currentClassDependencies = classDependencies.get(currentClass);
        if (currentClassDependencies == null) {
            currentClassDependencies = new HashMap<String, Set<Integer>>();
            classDependencies.put(currentClass, currentClassDependencies);
        }
        currentLineNumber = 0;

        System.out.println("Visit class "+name);
        if (signature == null) {
            addName(superName);
            addNames(interfaces);
        } else {
            addSignature(signature);
        }
    }

    public AnnotationVisitor visitAnnotation(
        final String desc,
        final boolean visible)
    {
        addDesc(desc);
        return this;
    }

    public void visitAttribute(final Attribute attr) {
    }

    public FieldVisitor visitField(
        final int access,
        final String name,
        final String desc,
        final String signature,
        final Object value)
    {
//    	if (currentClass.indexOf("HTMLOutput")>0) System.out.println("Field: "+name+", "+signature);
        if (signature == null) {
            addDesc(desc);
        } else {
            addTypeSignature(signature);
        }
        if (value instanceof Type) {
            addType((Type) value);
        }
        return this;
    }

    public MethodVisitor visitMethod(
        final int access,
        final String name,
        final String desc,
        final String signature,
        final String[] exceptions)
    {
        if (signature == null) {
            addMethodDesc(desc);
        } else {
            addSignature(signature);
        }
        addNames(exceptions);
        return this;
    }

    public void visitSource(final String source, final String debug) {
    }

    public void visitInnerClass(
        final String name,
        final String outerName,
        final String innerName,
        final int access)
    {
        // addName( outerName);
        // addName( innerName);
    }

    public void visitOuterClass(
        final String owner,
        final String name,
        final String desc)
    {
        // addName(owner);
        // addMethodDesc(desc);
    }

    // MethodVisitor

    public AnnotationVisitor visitParameterAnnotation(
        final int parameter,
        final String desc,
        final boolean visible)
    {
        addDesc(desc);
        return this;
    }

    public void visitTypeInsn(final int opcode, final String desc) {
//    	if (currentClass.indexOf("HTMLOutput")>0) System.out.println("TypeInsn: "+desc);
        if (desc.charAt(0) == '[') {
            addDesc(desc);
        } else {
            addName(desc);
        }
    }

    public void visitFieldInsn(
        final int opcode,
        final String owner,
        final String name,
        final String desc)
    {
//    	if (currentClass.indexOf("HTMLOutput")>0) System.out.println("FieldInsn: "+name+", "+owner);
        addName(owner);
        addDesc(desc);
    }

    public void visitMethodInsn(
        final int opcode,
        final String owner,
        final String name,
        final String desc)
    {
//    	if (currentClass.indexOf("HTMLOutput")>0) System.out.println("MethodInsn: "+name+", "+desc);
    	
        addName(owner);
        addMethodDesc(desc);
    }

    public void visitLdcInsn(final Object cst) {
        if (cst instanceof Type) {
            addType((Type) cst);
        }
    }

    public void visitMultiANewArrayInsn(final String desc, final int dims) {
        addDesc(desc);
    }

    public void visitLocalVariable(
        final String name,
        final String desc,
        final String signature,
        final Label start,
        final Label end,
        final int index)
    {
//    	if (currentClass.indexOf("HTMLOutput")>0) System.out.println("LocalVariable: "+name+", "+signature);
        addTypeSignature(signature);
    }

    public AnnotationVisitor visitAnnotationDefault() {
        return this;
    }

    public void visitCode() {
    }

    public void visitFrame(
        final int type,
        final int nLocal,
        final Object[] local,
        final int nStack,
        final Object[] stack)
    {
    }

    public void visitInsn(final int opcode) {
    }

    public void visitIntInsn(final int opcode, final int operand) {
    }

    public void visitVarInsn(final int opcode, final int var) {
    }

    public void visitJumpInsn(final int opcode, final Label label) {
    }

    public void visitLabel(final Label label) {
    }

    public void visitIincInsn(final int var, final int increment) {
    }

    public void visitTableSwitchInsn(
        final int min,
        final int max,
        final Label dflt,
        final Label[] labels)
    {
    }

    public void visitLookupSwitchInsn(
        final Label dflt,
        final int[] keys,
        final Label[] labels)
    {
    }

    public void visitTryCatchBlock(
        final Label start,
        final Label end,
        final Label handler,
        final String type)
    {
        addName(type);
    }

    public void visitLineNumber(final int line, final Label start) {
//    	if (currentClass.indexOf("HTMLOutput")>0) System.out.println("Line number: "+line);
    	currentLineNumber = line;
    }

    public void visitMaxs(final int maxStack, final int maxLocals) {
    }

    // AnnotationVisitor

    public void visit(final String name, final Object value) {
        if (value instanceof Type) {
            addType((Type) value);
        }
    }

    public void visitEnum(
        final String name,
        final String desc,
        final String value)
    {
        addDesc(desc);
    }

    public AnnotationVisitor visitAnnotation(
        final String name,
        final String desc)
    {
        addDesc(desc);
        return this;
    }

    public AnnotationVisitor visitArray(final String name) {
        return this;
    }

    // SignatureVisitor

    public void visitFormalTypeParameter(final String name) {
    }

    public SignatureVisitor visitClassBound() {
        return this;
    }

    public SignatureVisitor visitInterfaceBound() {
        return this;
    }

    public SignatureVisitor visitSuperclass() {
        return this;
    }

    public SignatureVisitor visitInterface() {
        return this;
    }

    public SignatureVisitor visitParameterType() {
        return this;
    }

    public SignatureVisitor visitReturnType() {
        return this;
    }

    public SignatureVisitor visitExceptionType() {
        return this;
    }

    public void visitBaseType(final char descriptor) {
    }

    public void visitTypeVariable(final String name) {
//    	if (currentClass.indexOf("HTMLOutput")>0) System.out.println("Type variable: "+name);
        // TODO verify
    }

    public SignatureVisitor visitArrayType() {
        return this;
    }

    public void visitClassType(final String name) {
//    	if (currentClass.indexOf("HTMLOutput")>0) System.out.println("Class type: "+name);
        addName(name);
    }

    public void visitInnerClassType(final String name) {
        addName(name);
    }

    public void visitTypeArgument() {
    }

    public SignatureVisitor visitTypeArgument(final char wildcard) {
        return this;
    }

    // common

    public void visitEnd() {
    }

    // ---------------------------------------------

    protected String getGroupKey(String name) {
    	// retrieve the package name
    	String packagename = getPackageName(name);
        
        // add this class to the package
        Set<String> packageclasses = packages.get(packagename);
        if (packageclasses == null) {
        	packageclasses = new TreeSet<String>();
            packages.put(packagename, packageclasses);
        }
        packageclasses.add(name);
        
        return name;
    }

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

	protected void addName(final String name) {
        if (name == null) {
            return;
        }
        String p = getGroupKey(name);
//        if (currentClass.indexOf("ClassReader")>0) System.out.println("Key="+p+" name="+name);
        if (!currentClassDependencies.containsKey(p)) {
            currentClassDependencies.put(p, new TreeSet<Integer>());
        }
//        assert(currentLineNumber>0);
        currentClassDependencies.get(p).add(currentLineNumber);
    }

    protected void addNames(final String[] names) {
        for (int i = 0; names != null && i < names.length; i++) {
            addName(names[i]);
        }
    }

    protected void addDesc(final String desc) {
        addType(Type.getType(desc));
    }

    protected void addMethodDesc(final String desc) {
        addType(Type.getReturnType(desc));
        Type[] types = Type.getArgumentTypes(desc);
        for (int i = 0; i < types.length; i++) {
            addType(types[i]);
        }
    }

    protected void addType(final Type t) {
        switch (t.getSort()) {
            case Type.ARRAY:
                addType(t.getElementType());
                break;
            case Type.OBJECT:
                addName(t.getClassName().replace('.', '/'));
                break;
        }
    }

    protected void addSignature(final String signature) {
        if (signature != null) {
            new SignatureReader(signature).accept(this);
        }
    }

    protected void addTypeSignature(final String signature) {
        if (signature != null) {
//        	if (currentClass.indexOf("HTMLOutput")>0) System.out.println("Type signature: "+signature+" line: "+currentLineNumber);
            new SignatureReader(signature).acceptType(this);
        }
    }
    
    /**
     * Returns a list of packages that are not assigned to
     * a module in the given configuration.
     * 
     * @param xcp
     * @return
     * @throws OverlappingModulesDefinitionException 
     */
    public Set<String> getUnspecifiedPackages(XMLConfigurationParser xcp) throws OverlappingModulesDefinitionException {
		Set<String> unspecifiedPackages = new TreeSet<String>();
		
		for(String classname : getDependencies().keySet()) {
			String classPackageName = DependencyVisitor.getPackageName(classname);
			
			// check if packagename is an allowed dependency for classname
			String classmodule = xcp.getMatchingModule(classPackageName+"/Dummy");

			if (classmodule == null) {
				unspecifiedPackages.add(classPackageName);
			}
		}
		
		return unspecifiedPackages;
    }
    
    /**
     * Returns a map containing the dependencies (from class, to class) that
     * are not allowed by the rules.
     * 
     * @param xcp the configuration to use
     * @return
     * @throws OverlappingModulesDefinitionException 
     */
    public Map<String, Map<String, ClassDependency>> getUnallowedDependencies(XMLConfigurationParser xcp) throws OverlappingModulesDefinitionException {
		Map<String, Map<String, ClassDependency>> unallowedDependencies = new TreeMap<String, Map<String, ClassDependency>>();
		
		for(String classname : getDependencies().keySet()) {
			String classPackageName = DependencyVisitor.getPackageName(classname);
			for(String dependency : getDependencies().get(classname).keySet()) {
				String dependencyPackageName = DependencyVisitor.getPackageName(dependency);
				
				// check if packagename is an allowed dependency for classname
				String classmodule = xcp.getMatchingModule(classname);
				String dependencymodule = xcp.getMatchingModule(dependency);
				
				if (classmodule == null) {
					// unspecified package
				} else {
					if (!classmodule.equals(dependencymodule)) {
						if (!(xcp.isExcluded(classname) || xcp.isExcluded(dependency))) {
							if (xcp.getModuleDependencies().get(classmodule) == null || dependencymodule == null || 
									!xcp.getModuleDependencies().get(classmodule).contains(dependencymodule)) {

								Map<String, ClassDependency> depList = unallowedDependencies.get(classname);
								if (depList == null) {
									depList = new TreeMap<String, ClassDependency>();
									unallowedDependencies.put(classname, depList);
								}
								ClassDependency cd = depList.get(dependency);
								if (cd == null) {
									cd = new ClassDependency(dependency);
									depList.put(dependency, cd);
								}
								for(Integer lineNumber : getDependencies().get(classname).get(dependency)) {
									cd.addLineNumber(lineNumber);
								}
							}
						}
					}
				}
			}
		}
		
		return unallowedDependencies;
    }
}
