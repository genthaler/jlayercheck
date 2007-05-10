package net.sf.jlayercheck.util.graph;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JPanel;

import net.sf.jlayercheck.util.exceptions.CycleFoundException;

import org.jgraph.JGraph;
import org.jgraph.plugins.layouts.AnnealingLayoutAlgorithm;
import org.jgraph.plugins.layouts.CircleGraphLayout;
import org.jgraph.plugins.layouts.GEMLayoutAlgorithm;
import org.jgraph.plugins.layouts.JGraphLayoutAlgorithm;
import org.jgraph.plugins.layouts.OrderedTreeLayoutAlgorithm;
import org.jgraph.plugins.layouts.RadialTreeLayoutAlgorithm;
import org.jgraph.plugins.layouts.SugiyamaLayoutAlgorithm;
import org.jgraph.plugins.layouts.TreeLayoutAlgorithm;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableDirectedGraph;

public class GraphModuleDependencies extends JApplet {

	private JGraphModelAdapter m_jgAdapter;
	protected JGraph jgraph;

    protected List cells = new ArrayList();

    public static void main(String args[]) throws Exception {
    	Map<String, Set<String>> testmap = new TreeMap<String, Set<String>>();
    	
    	Set<String> guiset = new TreeSet<String>();
    	guiset.add("domain");
    	guiset.add("persistency");
    	testmap.put("gui3", guiset);
    	
    	Set<String> pset = new TreeSet<String>();
    	pset.add("domain");
    	testmap.put("persistency", pset);

    	Set<String> dset = new TreeSet<String>();
    	dset.add("gui3");
    	testmap.put("domain", dset);

    	BufferedImage bi = new GraphModuleDependencies(testmap).getImage();
    	ImageIO.write(bi, "png", new File("/tmp/test2.png"));
    }
    
    /**
     * Creates a graph from the given dependencies. The dependency graph
     * must be free of cycles.
     * 
     * @param modulemap
     * @throws CycleFoundException 
     */
    public GraphModuleDependencies(Map<String, Set<String>> modulemap) throws CycleFoundException {
        // create a JGraphT graph
        ListenableDirectedGraph<String, String> g = new ListenableDirectedGraph( DefaultEdge.class );

        // create a visualization using JGraph, via an adapter
        m_jgAdapter = new JGraphModelAdapter( g );

        jgraph = new JGraph( m_jgAdapter );
        JPanel headless = new JPanel();
        
        // add some sample data (graph manipulated via JGraphT)
        for(String module : modulemap.keySet()) {
        	g.addVertex( module );
        }
        for(String module : modulemap.keySet()) {
        	for(String edgeto : modulemap.get(module)) {
        		g.addEdge( module, edgeto );
        	}
        }

        CycleDetector<String, String> cd = new CycleDetector<String, String>(g);
        
        if (cd.detectCycles()) {
        	throw new CycleFoundException();
        }
        
        JGraphLayoutAlgorithm rtla = new SugiyamaLayoutAlgorithm(); //SpringEmbeddedLayoutAlgorithm();

        JGraphLayoutAlgorithm.applyLayout(jgraph, rtla, jgraph.getRoots());

        headless.setDoubleBuffered(false);
        headless.add(jgraph);
        headless.setVisible(true);
        headless.setEnabled(true);
        headless.addNotify();
        headless.validate();
    }

    /**
     * Returns a image containing the graph.
     * 
     * @return BufferedImage
     */
    public BufferedImage getImage() {
    	BufferedImage bi = jgraph.getImage(jgraph.getBackground(), 5);
    	return bi;
    }
}
