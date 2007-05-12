package net.sf.jlayercheck.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;

/**
 * This class is a simple visitor object that collects all dependency information
 * for the given classes. The collected information can be retrieved by calling
 * {@link #getDependencies()} and {{@link #getPackages()}. 
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
    
    protected Map<String, Set<MethodCall>> constructorCalls = new HashMap<String, Set<MethodCall>>();
    
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
     * Returns a Map containing package names as keys and another Set
     * as values. This second Set contains class names that belong to
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

//        System.out.println("Visit class "+name);
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
    	if (opcode == Opcodes.NEW) {
    		addConstuctorCall(desc);
    	}
    	
        if (desc.charAt(0) == '[') {
            addDesc(desc);
        } else {
            addName(desc);
        }
    }

    /**
     * Called when a "new" command is found. Useful to see where implementations are needed
     * and where interfaces can replace a reference.
     * 
     * @param desc
     */
    protected void addConstuctorCall(String desc) {
    	Set<MethodCall> methodCallList = constructorCalls.get(currentClass);
    	
    	if (methodCallList == null) {
    		methodCallList = new HashSet<MethodCall>();
    		constructorCalls.put(currentClass, methodCallList);
    	}
    	
    	methodCallList.add(new MethodCall(desc, null, MethodCall.Type.CONSTRUCTOR, currentLineNumber));
//    	System.out.println("Add constructor call to "+desc+" from "+currentClass);
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
    	String packagename = StringUtils.getPackageName(name);
        
        // add this class to the package
        Set<String> packageclasses = packages.get(packagename);
        if (packageclasses == null) {
        	packageclasses = new TreeSet<String>();
            packages.put(packagename, packageclasses);
        }
        packageclasses.add(name);
        
        return name;
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
     * Returns a Map of class -> constructor calls. Can be used to determine
     * where implementations are needed and where an interface would be sufficient.
     * 
     * @return Map class -> constructor calls
     */
	public Map<String, Set<MethodCall>> getConstructorCalls() {
		return constructorCalls;
	}
}
