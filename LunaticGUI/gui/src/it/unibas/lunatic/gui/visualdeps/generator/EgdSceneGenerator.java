package it.unibas.lunatic.gui.visualdeps.generator;

import it.unibas.lunatic.gui.visualdeps.model.GraphNode;
import it.unibas.lunatic.gui.visualdeps.model.PinNode;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import it.unibas.lunatic.gui.visualdeps.IDependencySceneGenerator;
import it.unibas.lunatic.gui.visualdeps.IVmdScene;
import it.unibas.lunatic.gui.visualdeps.SceneUtils;
import it.unibas.lunatic.gui.visualdeps.model.EdgeNode;
import it.unibas.lunatic.model.dependency.ComparisonAtom;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.model.dependency.FormulaVariableOccurrence;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.netbeans.api.visual.border.Border;
import org.netbeans.api.visual.graph.layout.GraphLayout;
import org.netbeans.api.visual.graph.layout.GridGraphLayout;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.layout.SceneLayout;
import org.netbeans.api.visual.vmd.VMDNodeWidget;
import org.netbeans.api.visual.widget.Scene;

public class EgdSceneGenerator implements IDependencySceneGenerator {

    private Multimap<String, String> commonValues = HashMultimap.create();
    private EgdPremiseVisitor premiseVisitor = new EgdPremiseVisitor(commonValues);
    private EgdConclusionVisitor conclusionVisitor = new EgdConclusionVisitor();
    private Border premiseBorder = SceneUtils.getPremiseBorder();
    private Border conclusionBorder = SceneUtils.getConclusionBorder();
    private GraphLayout<GraphNode, EdgeNode> graphLayout;

    public EgdSceneGenerator() {
        this.graphLayout = new GridGraphLayout<GraphNode, EdgeNode>();
        graphLayout.setAnimated(false);
    }

    @Override
    public Scene createScene(Dependency dependency) {
        LunaticDepScene scene = new LunaticDepScene();
        SceneLayout sceneLayout = LayoutFactory.createSceneGraphLayout(scene, graphLayout);
        scene.setSceneLayout(sceneLayout);
        populateScene(dependency, scene);
        return scene;
    }

    private List<GraphNode> createPremiseNodes(IVmdScene scene) {
        List<GraphNode> result = new ArrayList<GraphNode>();
        for (String commonValue : commonValues.keySet()) {
            Collection<String> cells = commonValues.get(commonValue);
            if (cells.size() > 1) {
                GraphNode graphNode = new GraphNode(commonValue);
                VMDNodeWidget node = scene.createNode(graphNode, true);
                node.setBorder(premiseBorder);
                result.add(graphNode);
                for (String cell : cells) {
                    scene.createPin(new PinNode(graphNode, cell + ":" + commonValue));
                }
            }
        }
        return result;
    }

    private GraphNode createConclusionNode(IVmdScene scene, ComparisonAtom conclusion) {
        FormulaVariable leftVar = conclusion.getVariables().get(0);
        FormulaVariable rightVar = conclusion.getVariables().get(1);
        GraphNode graphNode = new GraphNode(conclusion.toString());
        VMDNodeWidget node = scene.createNode(graphNode, true);
        node.setBorder(conclusionBorder);
        for (FormulaVariableOccurrence occ : leftVar.getPremiseRelationalOccurrences()) {
            scene.createPin(new PinNode(graphNode, occ.toString(), occ.toLongString()));
        }
        for (FormulaVariableOccurrence occ : rightVar.getPremiseRelationalOccurrences()) {
            scene.createPin(new PinNode(graphNode, occ.toString(), occ.toLongString()));
        }
        return graphNode;
    }

    public void populateScene(Dependency dependency, LunaticDepScene scene) {
        premiseVisitor.visitDependency(dependency);
        conclusionVisitor.visitDependency(dependency);
        ComparisonAtom conclusion = (ComparisonAtom) conclusionVisitor.getResult();
        GraphNode conclusionNode = createConclusionNode(scene, conclusion);
        List<GraphNode> premiseNodes = createPremiseNodes(scene);
        PinNode targetPin = conclusionNode.getDefaultPin();
        for (GraphNode premiseNode : premiseNodes) {
            PinNode sourcePin = premiseNode.getDefaultPin();
            EdgeNode edge = new EdgeNode(sourcePin, targetPin);
            scene.createEdge(edge);
        }
    }
}
