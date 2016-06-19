package it.unibas.lunatic.gui.visualdeps.generator;

import it.unibas.lunatic.gui.visualdeps.IDependencySceneGenerator;
import it.unibas.lunatic.gui.visualdeps.SceneUtils;
import it.unibas.lunatic.gui.visualdeps.components.GraphBiLayout;
import it.unibas.lunatic.gui.visualdeps.model.EdgeNode;
import it.unibas.lunatic.gui.visualdeps.model.GraphNode;
import it.unibas.lunatic.gui.visualdeps.model.PinNode;
import it.unibas.lunatic.model.dependency.ComparisonAtom;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.IFormulaAtom;
import java.awt.Color;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.api.visual.graph.layout.GraphLayout;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.layout.SceneLayout;
import org.netbeans.api.visual.vmd.VMDConnectionWidget;
import org.netbeans.api.visual.widget.Scene;

public class TgdDepSceneGenerator implements IDependencySceneGenerator {

    private Log logger = LogFactory.getLog(getClass());
    public static final String ATOM = "atom";
    public static final String VARIABLE_VALUE = "variableValue";
    private GraphLayout<GraphNode, EdgeNode> graphLayout = new GraphBiLayout();

    public void populateScene(Dependency dependency, LunaticDepScene scene) {
        TgdVisitor premiseVisitor = new TgdVisitor(scene, true);
        TgdVisitor conclusionVisitor = new TgdVisitor(scene, false);
        premiseVisitor.visitDependency(dependency);
        conclusionVisitor.visitDependency(dependency);
        List<GraphNode> premiseRelationalAtoms = premiseVisitor.getRelationalAtomsNodes();
        List<GraphNode> premiseComparisonAtomsNodes = premiseVisitor.getComparisonAtomsNodes();
        List<GraphNode> premiseBuiltinAtomsNodes = premiseVisitor.getBuiltinAtomsNodes();
        List<GraphNode> conclusionNodes = conclusionVisitor.getResult();
        for (GraphNode node : premiseRelationalAtoms) {
            createConnections(scene, node, conclusionNodes, Color.BLACK, true);
        }
        for (GraphNode node : premiseRelationalAtoms) {
            createConnections(scene, node, premiseBuiltinAtomsNodes, SceneUtils.PREMISE_COLOR, false);
            createConnections(scene, node, premiseComparisonAtomsNodes, SceneUtils.PREMISE_COLOR, false);
            createNotExistingConnections(scene, node, premiseRelationalAtoms, SceneUtils.PREMISE_COLOR, false);
        }
        for (GraphNode node : conclusionNodes) {
            createConnections(scene, node, conclusionNodes, SceneUtils.CONCLUSION_COLOR, false);
        }
    }

    @Override
    public Scene createScene(Dependency dependency) {
        LunaticDepScene scene = new LunaticDepScene();
        SceneLayout sceneLayout = LayoutFactory.createSceneGraphLayout(scene, graphLayout);
        scene.setSceneLayout(sceneLayout);
        populateScene(dependency, scene);
        return scene;
    }

    private void createConnections(LunaticDepScene scene, GraphNode source, List<GraphNode> targetNodes, Color color, boolean premiseConlcusionMapping) {
        createConnections(scene, source, targetNodes, true, color, premiseConlcusionMapping);
    }

     private void createNotExistingConnections(LunaticDepScene scene, GraphNode source, List<GraphNode> targetNodes, Color color, boolean premiseConlcusionMapping) {
        createConnections(scene, source, targetNodes, false, color, premiseConlcusionMapping);
    }

    private void createConnections(LunaticDepScene scene, GraphNode node, List<GraphNode> nodes, boolean alwaysCreate, Color connectionColor, boolean premiseConlcusionMapping) {
        Collection<PinNode> nodePins = scene.getNodePins(node);
        for (GraphNode graphNode : nodes) {
            if (!graphNode.equals(node)) {
                Collection<PinNode> otherPins = scene.getNodePins(graphNode);
                createPinsConnections(scene, nodePins, otherPins, alwaysCreate, connectionColor, premiseConlcusionMapping);
            }
        }
    }

    private void createPinsConnections(LunaticDepScene scene, Collection<PinNode> nodePins, Collection<PinNode> otherPins, boolean alwaysCreate, Color color, boolean premiseConlcusionMapping) {
        for (PinNode source : nodePins) {
            String sourceValue = (String) source.getValue(VARIABLE_VALUE);
            Collection<EdgeNode> pinEdges = scene.findPinEdges(source, true, true);
            if (alwaysCreate || pinEdges.isEmpty()) {
                linkValues(scene, source, otherPins, sourceValue, color, premiseConlcusionMapping);
            }
        }
    }

    private void linkValues(LunaticDepScene scene, PinNode source, Collection<PinNode> otherPins, String sourceValue, Color color, boolean mapping) {
        for (PinNode target : otherPins) {
            String targetValue = (String) target.getValue(VARIABLE_VALUE);
            if (sourceValue.equals(targetValue)) {
                createEdge(scene, source, target, color, mapping);
            }
        }
    }

    private void createEdge(LunaticDepScene scene, PinNode source, PinNode target, Color color, boolean mapping) {
        Collection<EdgeNode> sourceTargetEdges = scene.findEdgesBetween(target, source);
        logger.debug("EDGES BETWEEN " + source.getId() + " and " + target.getId());
        logger.debug(sourceTargetEdges);
        if (sourceTargetEdges.isEmpty()) {
            EdgeNode edge = new EdgeNode(source, target, color);
            VMDConnectionWidget edgeWidget = scene.createEdge(edge, mapping);
            if (!mapping) {
                edgeWidget.setRouter(scene.getOrthogonalRouter());
            }
        }
    }
}

class AtomOrderComparator implements Comparator<GraphNode> {

    @Override
    public int compare(GraphNode o1, GraphNode o2) {
        IFormulaAtom thisAtom = (IFormulaAtom) o1.getValue(TgdDepSceneGenerator.ATOM);
        IFormulaAtom otherAtom = (IFormulaAtom) o2.getValue(TgdDepSceneGenerator.ATOM);
        int result = 0;
        if (thisAtom instanceof ComparisonAtom && !(otherAtom instanceof ComparisonAtom)) {
            result = -1;
        } else if (otherAtom instanceof ComparisonAtom && !(thisAtom instanceof ComparisonAtom)) {
            result = 1;
        }
        return result;
    }
}