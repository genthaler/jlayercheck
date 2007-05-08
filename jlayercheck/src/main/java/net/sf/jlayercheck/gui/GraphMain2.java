package net.sf.jlayercheck.gui;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JPanel;

import org.jgraph.JGraph;
import org.jgraph.plugins.layouts.JGraphLayoutAlgorithm;
import org.jgraph.plugins.layouts.SpringEmbeddedLayoutAlgorithm;
import org.jgrapht.ListenableGraph;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableDirectedGraph;

public class GraphMain2 extends JApplet {

	private JGraphModelAdapter m_jgAdapter;
	protected JGraph jgraph;

    protected List cells = new ArrayList();
    /**
     * @see java.applet.Applet#init().
     */
    public static void main(String args[]) throws Exception {
    	Map<String, Set<String>> testmap = new TreeMap<String, Set<String>>();
    	
    	Set<String> guiset = new TreeSet<String>();
    	guiset.add("domain");
    	guiset.add("persistency");
    	testmap.put("gui", guiset);
    	
    	Set<String> pset = new TreeSet<String>();
    	pset.add("domain");
    	testmap.put("persistency", pset);

    	Set<String> dset = new TreeSet<String>();
    	testmap.put("domain", dset);

    	BufferedImage bi = new GraphMain2(testmap).getImage();
    	ImageIO.write(bi, "png", new File("/tmp/test2.png"));
    }
    
    public GraphMain2(Map<String, Set<String>> modulemap) {
        // create a JGraphT graph
        ListenableGraph g = new ListenableDirectedGraph( DefaultEdge.class );

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

        // that's all there is to it!...
        
        JGraphLayoutAlgorithm rtla = new SpringEmbeddedLayoutAlgorithm();
        
        JGraphLayoutAlgorithm.applyLayout(jgraph, rtla, jgraph.getRoots());

        headless.setDoubleBuffered(false);
        headless.add(jgraph);
        headless.setVisible(true);
        headless.setEnabled(true);
        headless.addNotify();
        headless.validate();
    }
    
    public BufferedImage getImage() {
//        Object cells[] = jgraph.getRoots();
//        Rectangle2D bounds = jgraph.toScreen(jgraph.getCellBounds(cells));

    	BufferedImage bi = jgraph.getImage(jgraph.getBackground(), 5);

//        try {
////			ImageIO.write(bi, "png", new File("/tmp/test.png"));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    	
    	return bi;
    }
}
