package it.unibas.lunatic.gui.visualdeps.generator.vmd;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import it.unibas.lunatic.gui.visualdeps.SceneUtils;
import it.unibas.lunatic.model.dependency.ComparisonAtom;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaAttribute;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.model.dependency.FormulaVariableOccurrence;
import it.unibas.lunatic.model.dependency.FormulaWithNegations;
import it.unibas.lunatic.model.dependency.IFormula;
import it.unibas.lunatic.model.dependency.IFormulaAtom;
import it.unibas.lunatic.model.dependency.NullFormula;
import it.unibas.lunatic.model.dependency.PositiveFormula;
import it.unibas.lunatic.model.dependency.RelationalAtom;
import it.unibas.lunatic.model.dependency.operators.IFormulaVisitor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.api.visual.border.Border;
import org.netbeans.api.visual.vmd.VMDGraphScene;
import org.netbeans.api.visual.vmd.VMDNodeWidget;
import org.netbeans.api.visual.vmd.VMDPinWidget;


class VmdEgdSceneGenerator implements IVmdSceneGenerator {

    private Multimap<String, String> commonValues = HashMultimap.create();
    private final Dependency dependency;
    private EgdPremiseVisitor premiseVisitor = new EgdPremiseVisitor(commonValues);
    private EgdConclusionVisitor conclusionVisitor = new EgdConclusionVisitor();
    private Border premiseBorder = SceneUtils.getPremiseBorder();
    private Border conclusionBorder = SceneUtils.getConclusionBorder();

    public VmdEgdSceneGenerator(Dependency dependency) {
        this.dependency = dependency;
    }

    @Override
    public void populateScene(VMDGraphScene scene) {
        premiseVisitor.visitDependency(dependency);
        conclusionVisitor.visitDependency(dependency);
        ComparisonAtom conclusion = (ComparisonAtom) conclusionVisitor.getResult();
        List<VMDNodeWidget> premiseNodes = createPremiseNodes(scene);
        VMDNodeWidget conclusionNode = createConclusionNode(scene, conclusion);
//        int i = 0;
//        for (VMDNodeWidget premiseNode : premiseNodes) {
//            String edge = "node"+i;
//            scene.addEdge(edge);
//            scene.setEdgeSource(edge, premiseNode.getNodeName() + VMDGraphScene.PIN_ID_DEFAULT_SUFFIX);
//            scene.setEdgeTarget(edge, conclusionNode.getNodeName() + VMDGraphScene.PIN_ID_DEFAULT_SUFFIX);
//            i++;
//        }
    }

    private List<VMDNodeWidget> createPremiseNodes(VMDGraphScene scene) {
        List<VMDNodeWidget> result = new ArrayList<VMDNodeWidget>();
        for (String commonValue : commonValues.keySet()) {
            Collection<String> cells = commonValues.get(commonValue);
            if (cells.size() > 1) {
                VMDNodeWidget node = (VMDNodeWidget) scene.addNode(commonValue);
                node.setNodeName(commonValue);
                node.setBorder(premiseBorder);
                createNodePin(scene, node);
                result.add(node);
                for (String cell : cells) {
                    VMDPinWidget pin = (VMDPinWidget) scene.addPin(commonValue, cell + ":" + commonValue);
                    pin.setPinName(cell);
                }
            }
        }
        return result;
    }

    private VMDNodeWidget createConclusionNode(VMDGraphScene scene, ComparisonAtom conclusion) {
        FormulaVariable leftVar = conclusion.getVariables().get(0);
        FormulaVariable rightVar = conclusion.getVariables().get(1);
        VMDNodeWidget node = (VMDNodeWidget) scene.addNode(conclusion.toString());
        node.setNodeName(conclusion.toString());
        node.setBorder(conclusionBorder);
        for (FormulaVariableOccurrence occ : leftVar.getPremiseOccurrences()) {
            VMDPinWidget pin = (VMDPinWidget) scene.addPin(conclusion.toString(), occ.toString());
            pin.setPinName(occ.toLongString());
        }
        for (FormulaVariableOccurrence occ : rightVar.getPremiseOccurrences()) {
            VMDPinWidget pin = (VMDPinWidget) scene.addPin(conclusion.toString(), occ.toString());
            pin.setPinName(occ.toLongString());
        }
        return node;
    }

    private void createNodePin(VMDGraphScene scene, VMDNodeWidget node) {
//        scene.addPin(node.getNodeName(), node.getNodeName() + VMDGraphScene.PIN_ID_DEFAULT_SUFFIX);
    }
}
class EgdPremiseVisitor implements IFormulaVisitor {

    private Log logger = LogFactory.getLog(getClass());
    private final Multimap<String, String> constraints;

    public EgdPremiseVisitor(Multimap<String, String> constraints) {
        this.constraints = constraints;
    }

    @Override
    public void visitDependency(Dependency dependency) {
        dependency.getPremise().accept(this);
    }

    @Override
    public void visitPositiveFormula(PositiveFormula formula) {
        if (formula instanceof NullFormula) {
            return;
        }
        for (IFormulaAtom atom : formula.getAtoms()) {
            if (atom instanceof RelationalAtom) {
                addAtom((RelationalAtom) atom);
            }
        }
    }

    @Override
    public void visitFormulaWithNegations(FormulaWithNegations formula) {
        formula.getPositiveFormula().accept(this);
        for (IFormula negatedFormula : formula.getNegatedSubFormulas()) {
            negatedFormula.accept(this);
        }
    }

    private void addAtom(RelationalAtom atom) {
        logger.debug("FORMULA ATOM: " + atom.toString());
        String atomId = atom.getTableNameWithAlias();
        for (FormulaAttribute atomAttribute : atom.getAttributes()) {
            logger.debug("ATOM ATTRIBUTE: " + atomAttribute.toString());
            constraints.put(atomAttribute.getValue().toString(), atomId + "." + atomAttribute.getAttributeName());
        }
    }

    @Override
    public Object getResult() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

class EgdConclusionVisitor implements IFormulaVisitor {

    private ComparisonAtom result;

    @Override
    public void visitDependency(Dependency dependency) {
        dependency.getConclusion().accept(this);
    }

    @Override
    public void visitPositiveFormula(PositiveFormula formula) {
        if (formula instanceof NullFormula) {
            return;
        }
        result = (ComparisonAtom) formula.getAtoms().get(0);
    }

    @Override
    public Object getResult() {
        return result;
    }

    @Override
    public void visitFormulaWithNegations(FormulaWithNegations formula) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}