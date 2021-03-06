package jlayercheckbuilder.builder;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import net.sf.jlayercheck.util.DependencyVisitor;
import net.sf.jlayercheck.util.XMLConfiguration;
import net.sf.jlayercheck.util.XMLConfigurationParser;
import net.sf.jlayercheck.util.exceptions.ConfigurationException;
import net.sf.jlayercheck.util.exceptions.OverlappingModulesDefinitionException;
import net.sf.jlayercheck.util.model.ClassDependency;
import net.sf.jlayercheck.util.model.ClassSource;
import net.sf.jlayercheck.util.model.FilesystemClassSource;
import net.sf.jlayercheck.util.modeltree.ClassNode;
import net.sf.jlayercheck.util.modeltree.ModelTree;
import net.sf.jlayercheck.util.modeltree.ModuleNode;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.xml.sax.SAXException;

/**
 * This is the builder class that checks the dependencies of changed
 * java files and adds markers to lines that violate the architecture.
 * 
 * @author webmaster@earth3d.org
 */
public class JLayerCheckBuilder extends IncrementalProjectBuilder {

	/**
	 * Contains the dependency informations of all classes.
	 */
	protected ModelTree mt;
	
	/**
	 * The list of source directories from which the sources should
	 * be retrieved and marked.
	 */
	protected List<ClassSource> classSources;
	
	class JLayerCheckDeltaVisitor implements IResourceDeltaVisitor {
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
		 */
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			switch (delta.getKind()) {
			case IResourceDelta.ADDED:
				// handle added resource
				check(resource);
				break;
			case IResourceDelta.REMOVED:
				// handle removed resource
				break;
			case IResourceDelta.CHANGED:
				// handle changed resource
				check(resource);
				break;
			}
			//return true to continue visiting children.
			return true;
		}
	}

	class JLayerCheckResourceVisitor implements IResourceVisitor {
		public boolean visit(IResource resource) {
			check(resource);
			//return true to continue visiting children.
			return true;
		}
	}

	public static final String BUILDER_ID = "net.sf.jlayercheck.builder";

	private static final String MARKER_TYPE = "net.sf.jlayercheck.dependency";

	private void addMarker(IFile file, String message, int lineNumber, int severity) {
		try {
			IMarker marker = file.createMarker(MARKER_TYPE);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			if (lineNumber == -1) {
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
	 *      java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
	throws CoreException {
		if (kind == FULL_BUILD) {
			fullBuild(monitor);
		} else {
			IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				fullBuild(monitor);
			} else {
				incrementalBuild(delta, monitor);
			}
		}
		return null;
	}

	/**
	 * Implements the functionality to check for dependency violations.
	 * 
	 * @param resource to be checked, only java files are checked and changed to the jlayercheck.xml are recognized.
	 */
	protected void check(IResource resource) {
		if (resource instanceof IFile && resource.getName().endsWith("jlayercheck.xml")) {
			IFile file = (IFile) resource;
			
			refreshArchitecture(file);
			
			// refresh all files in the project
			refreshFiles(file.getProject());
		}
		if (resource instanceof IFile && resource.getName().endsWith(".java") && isInClassSource(resource.getProjectRelativePath())) {
			IFile file = (IFile) resource;
			deleteMarkers(file);

			if (mt == null) {
				refreshArchitecture(file);
			}
			
			ICompilationUnit compilationUnit = (ICompilationUnit) JavaCore.createCompilationUnitFrom(file);
			try {
				IPackageDeclaration pd[] = compilationUnit.getPackageDeclarations();
				String p = "";
				if (pd.length>0) {
					p = pd[0].getElementName();
				}

				String classname = p.replace(".", "/").concat("/").concat(file.getName()).replaceAll(".java$", "");

				File classOutputPath = JavaCore.create(file.getProject()).getOutputLocation().toFile();
				File basepath = file.getProject().getParent().getLocation().toFile();
				File classfilename = new File(new File(basepath, classOutputPath.toString()), classname+".class");
				
				// refresh architectural model
				refreshArchitectureIncremental(file, classfilename);

				// create markers for dependency violations
				ClassNode cn = mt.getClassNode(classname);

				if (cn != null) {
					String modulename = ((ModuleNode) cn.getParent().getParent()).getModuleName();
					for(ClassDependency cd : cn.getClassDependencies()) {
						if (cd.isUnallowedDependency()) {

							ClassNode cndest = mt.getClassNode(cd.getDependency());
							String moduledest = "";
							if (cndest != null) {
								moduledest = ((ModuleNode) cndest.getParent().getParent()).getModuleName();
							}

							// add markers
							for(Integer linenumber : cd.getLineNumbers()) {

								String msg = "Module " + modulename + " should not access " + cd.getDependency().replace("/", ".") + " (" + moduledest +")";
								addMarker(file, msg, linenumber.intValue(), IMarker.SEVERITY_WARNING);
							}						
						}
					}
				}

			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Returns true, if this path is specified in the configuration as source path.
	 * 
	 * @param projectRelativePath
	 * @return
	 */
	protected boolean isInClassSource(IPath projectRelativePath) {
		boolean result = false;
		if (classSources != null) {
			for(ClassSource cs : classSources) {
				if (cs instanceof FilesystemClassSource) {
					FilesystemClassSource fcs = (FilesystemClassSource) cs;
					String fcssrc = fcs.getSrc().replaceAll("\\\\", "/");
					if (!fcssrc.endsWith("/")) fcssrc = fcssrc + "/";
					
					if (projectRelativePath.toString().startsWith(fcssrc)) {
						result = true;
						break;
					}
				}
			}
		}
		return result;
	}

	/**
	 * Checks all files recursively from the given root. It is used to rescan the whole
	 * project when the configuration file was modified.
	 * 
	 * @param root the container to start from, e.g. the project root
	 */
	protected void refreshFiles(IContainer root) {
		IResource allfiles[];
		try {
			allfiles = root.members();
			for(int i=0; i<allfiles.length; i++) {
				IResource resource = allfiles[i];
				if (resource instanceof IFile && !resource.getName().endsWith("jlayercheck.xml")) { // prevent recursion loop
					deleteMarkers((IFile) resource);
					
					check(resource);
				}
				if (resource instanceof IContainer) {
					refreshFiles((IContainer) resource);
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	protected void refreshArchitectureIncremental(IFile file, File classfilename) {
		File fi = file.getProject().getFile("/jlayercheck.xml").getRawLocation().toFile();
		XMLConfiguration xcp;
		try {
			xcp = new XMLConfigurationParser().parse(fi);
			xcp.updateModelTree(mt, classfilename);
		} catch (ConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (OverlappingModulesDefinitionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reloads all dependencies.
	 * 
	 * @param file
	 */
	protected void refreshArchitecture(IFile file) {
		try {
			File fi = file.getProject().getFile("/jlayercheck.xml").getRawLocation().toFile();
			XMLConfiguration xcp = new XMLConfigurationParser().parse(fi);
			classSources = xcp.getClassSources();
			DependencyVisitor dv = new DependencyVisitor();
			for(ClassSource source : xcp.getClassSources()) {
				source.call(dv);
			}
			mt = xcp.getModelTree(dv);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (OverlappingModulesDefinitionException e) {
			e.printStackTrace();
		} catch (net.sf.jlayercheck.util.exceptions.ConfigurationException e) {
			e.printStackTrace();
		}
	}

	private void deleteMarkers(IFile file) {
		try {
			file.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
		} catch (CoreException ce) {
		}
	}

	protected void fullBuild(final IProgressMonitor monitor)
	throws CoreException {
		try {
			getProject().accept(new JLayerCheckResourceVisitor());
		} catch (CoreException e) {
		}
	}

	protected void incrementalBuild(IResourceDelta delta,
			IProgressMonitor monitor) throws CoreException {
		// the visitor does the work.
		delta.accept(new JLayerCheckDeltaVisitor());
	}
}
