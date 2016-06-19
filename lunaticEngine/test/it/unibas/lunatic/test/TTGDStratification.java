package it.unibas.lunatic.test;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.commons.operators.ChaseUtility;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.DependencyStratification;
import it.unibas.lunatic.model.dependency.EGDStratum;
import it.unibas.lunatic.model.dependency.TGDStratum;
import it.unibas.lunatic.model.dependency.operators.AnalyzeDependencies;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import junit.framework.TestCase;
import org.jgrapht.DirectedGraph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TTGDStratification extends TestCase {

    private static Logger logger = LoggerFactory.getLogger(TTGDStratification.class);
    private final AnalyzeDependencies dependencyAnalyzer = new AnalyzeDependencies();

//    public void testTGD0() throws Exception {
//        Scenario scenario = UtilityTest.loadScenarioFromResources("/de/tgd/tgd0-mcscenario.xml");
//        dependencyAnalyzer.analyzeDependencies(scenario);
//        DependencyStratification stratification = scenario.getStratification();
//        List<TGDStratum> tgdStrata = stratification.getTGDStrata();
//        if (logger.isDebugEnabled()) logger.debug(tgdStrata.toString());
//        assertEquals(3, tgdStrata.size());
//        for (TGDStratum tGDStratum : tgdStrata) {
//            System.out.println(tGDStratum.toLongString());
//        }
//        showTGDGraph(stratification.getTgdStrataGraph());
//        while (true) {
//        }
//    }
//    public void testTGD1() throws Exception {
//        Scenario scenario = UtilityTest.loadScenarioFromResources("/de/tgd/tgd1-mcscenario.xml");
//        dependencyAnalyzer.analyzeDependencies(scenario);
//        DependencyStratification stratification = scenario.getStratification();
//        List<TGDStratum> tgdStrata = stratification.getTGDStrata();
//        for (TGDStratum tGDStratum : tgdStrata) {
//            System.out.println(tGDStratum.toLongString());
//        }
//        if (logger.isDebugEnabled()) logger.debug(tgdStrata.toString());
//        assertEquals(5, tgdStrata.size());
//        if (logger.isDebugEnabled()) logger.debug(stratification.getTgdStrataGraph().toString());
//        showTGDGraph(stratification.getTgdStrataGraph());
//        while (true) {
//        }
//    }
//    public void testTGDSynth() throws Exception {
//        Scenario scenario = UtilityTest.loadScenarioFromAbsolutePath("");
//        dependencyAnalyzer.analyzeDependencies(scenario);
//        DependencyStratification stratification = scenario.getStratification();
//        List<TGDStratum> tgdStrata = stratification.getTGDStrata();
//        for (TGDStratum tGDStratum : tgdStrata) {
//            System.out.println(tGDStratum.toLongString());
//        }
//        if (logger.isDebugEnabled()) logger.debug(tgdStrata.toString());
//        if (logger.isDebugEnabled()) logger.debug(stratification.getTgdStrataGraph().toString());
//        showTGDGraph(stratification.getTgdStrataGraph());
//        while (true) {
//        }
//    }
    public void testEGD1() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromAbsolutePath("/chasebench/tools/llunatic/input/Ontology-256/Ontology-256-mcscenario-dbms.xml");
//        Scenario scenario = UtilityTest.loadScenarioFromAbsolutePath("/chasebench/tools/llunatic/input/doctors/doctors-10k-mcscenario-dbms.xml");
        ChaseUtility.copyEGDsToExtEGDs(scenario);
        dependencyAnalyzer.analyzeDependencies(scenario);
        DependencyStratification stratification = scenario.getStratification();
        List<EGDStratum> egdStrata = stratification.getEGDStrata();
        for (EGDStratum egdStratum : egdStrata) {
            System.out.println(egdStratum.toLongString());
        }
        if (logger.isDebugEnabled()) logger.debug("EGD Stata: " + egdStrata.toString());
//        assertEquals(5, egdStrata.size());
        if (logger.isDebugEnabled()) logger.debug(stratification.getEgdStrataGraph().toString());
        showEGDGraph(stratification.getEgdStrataGraph());
        while (true) {
        }
    }

    private void showTGDGraph(DirectedGraph<TGDStratum, DefaultEdge> strataGraph) {
        JGraphXAdapter<TGDStratum, DefaultEdge> jgxAdapterContext = new JGraphXAdapter<TGDStratum, DefaultEdge>(strataGraph);
        jgxAdapterContext.getStylesheet().getDefaultEdgeStyle().put(mxConstants.STYLE_NOLABEL, "1");
//        jgxAdapterContext.getStylesheet().getDefaultEdgeStyle().put(mxConstants.STYLE_ENDARROW, "0");
        jgxAdapterContext.setCellsEditable(false);
        jgxAdapterContext.setCellsMovable(false);
        jgxAdapterContext.setEdgeLabelsMovable(false);
        jgxAdapterContext.setCellsDeletable(false);
        jgxAdapterContext.setCellsDisconnectable(false);
        jgxAdapterContext.setCellsResizable(false);
        jgxAdapterContext.setCellsBendable(false);
        JFrame frame = new JFrame();
        mxGraphComponent mxGraphComponent = new mxGraphComponent(jgxAdapterContext);
        frame.getContentPane().add(mxGraphComponent, BorderLayout.CENTER);
        mxHierarchicalLayout layout = new mxHierarchicalLayout(jgxAdapterContext);
        layout.execute(jgxAdapterContext.getDefaultParent());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Graph");
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        try {
            BufferedImage image = mxCellRenderer.createBufferedImage(jgxAdapterContext, null, 1, Color.WHITE, true, null);
            ImageIO.write(image, "PNG", new File("/Temp/llunatic/tgd-graph.png"));
        } catch (IOException ex) {
            logger.error("Unable to save graph image: " + ex.getLocalizedMessage());
        }
    }

    private void showEGDGraph(DirectedGraph<EGDStratum, DefaultEdge> strataGraph) {
        JGraphXAdapter<EGDStratum, DefaultEdge> jgxAdapterContext = new JGraphXAdapter<EGDStratum, DefaultEdge>(strataGraph);
        jgxAdapterContext.getStylesheet().getDefaultEdgeStyle().put(mxConstants.STYLE_NOLABEL, "1");
//        jgxAdapterContext.getStylesheet().getDefaultEdgeStyle().put(mxConstants.STYLE_ENDARROW, "0");
        jgxAdapterContext.setCellsEditable(false);
        jgxAdapterContext.setCellsMovable(false);
        jgxAdapterContext.setEdgeLabelsMovable(false);
        jgxAdapterContext.setCellsDeletable(false);
        jgxAdapterContext.setCellsDisconnectable(false);
        jgxAdapterContext.setCellsResizable(false);
        jgxAdapterContext.setCellsBendable(false);
        JFrame frame = new JFrame();
        mxGraphComponent mxGraphComponent = new mxGraphComponent(jgxAdapterContext);
        frame.getContentPane().add(mxGraphComponent, BorderLayout.CENTER);
        mxHierarchicalLayout layout = new mxHierarchicalLayout(jgxAdapterContext);
        layout.execute(jgxAdapterContext.getDefaultParent());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Graph");
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        try {
            BufferedImage image = mxCellRenderer.createBufferedImage(jgxAdapterContext, null, 1, Color.WHITE, true, null);
            ImageIO.write(image, "PNG", new File("/Temp/llunatic/egd-graph.png"));
        } catch (IOException ex) {
            logger.error("Unable to save graph image: " + ex.getLocalizedMessage());
        }
    }
}
