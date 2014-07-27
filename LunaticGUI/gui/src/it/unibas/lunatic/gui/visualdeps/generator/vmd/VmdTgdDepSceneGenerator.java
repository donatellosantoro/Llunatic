/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.visualdeps.generator.vmd;

import it.unibas.lunatic.gui.visualdeps.SceneUtils;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaAttribute;
import it.unibas.lunatic.model.dependency.FormulaWithNegations;
import it.unibas.lunatic.model.dependency.IFormula;
import it.unibas.lunatic.model.dependency.IFormulaAtom;
import it.unibas.lunatic.model.dependency.NullFormula;
import it.unibas.lunatic.model.dependency.PositiveFormula;
import it.unibas.lunatic.model.dependency.RelationalAtom;
import it.unibas.lunatic.model.dependency.operators.IFormulaVisitor;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.api.visual.border.Border;
import org.netbeans.api.visual.vmd.VMDGraphScene;
import org.netbeans.api.visual.vmd.VMDNodeWidget;
import org.netbeans.api.visual.vmd.VMDPinWidget;

/**
 *
 * @author Antonio Galotta
 */
class VmdTgdDepSceneGenerator implements IVmdSceneGenerator {

    private Dependency dep;
    private Border premiseBorder = SceneUtils.getPremiseBorder();
    private Border conclusionBorder = SceneUtils.getConclusionBorder();

    public VmdTgdDepSceneGenerator(Dependency dep) {
        this.dep = dep;
    }

    @Override
    public void populateScene(VMDGraphScene scene) {
        FormulaVisitor premiseVisitor = new FormulaVisitor(scene);
        FormulaVisitor conclusionVisitor = new FormulaVisitor(scene);
        dep.getPremise().accept(premiseVisitor);
        dep.getConclusion().accept(conclusionVisitor);
        List<VMDNodeWidget> premiseNodes = premiseVisitor.getResult();
        List<VMDNodeWidget> conclusionNodes = conclusionVisitor.getResult();
        for (VMDNodeWidget nodeWidget : premiseNodes) {
            nodeWidget.setBorder(premiseBorder);
            nodeWidget.getChildren();
        }
        for (VMDNodeWidget nodeWidget : conclusionNodes) {
            nodeWidget.setBorder(conclusionBorder);
        }
    }
}

class FormulaVisitor implements IFormulaVisitor {

    private Log logger = LogFactory.getLog(getClass());
    private VMDGraphScene scene;
    private List<VMDNodeWidget> nodes = new ArrayList<VMDNodeWidget>();

    public FormulaVisitor(VMDGraphScene scene) {
        this.scene = scene;
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
        VMDNodeWidget n1 = (VMDNodeWidget) scene.addNode(atomId);
        n1.setNodeName(atom.getTableNameWithAlias());
        nodes.add(n1);
        for (FormulaAttribute formulaVariable : atom.getAttributes()) {
            logger.debug("FORMULA ATTRIBUTE PIN: " + formulaVariable.toString());
            VMDPinWidget pin = (VMDPinWidget) scene.addPin(atomId, formulaVariable.hashCode() + "");
            pin.setPinName(formulaVariable.toString());
        }
    }

    private boolean isRootFormula(IFormula formula) {
        return formula.getFather() == null || (formula.getFather() != null && formula.getFather().getFather() == null);
    }

    @Override
    public List<VMDNodeWidget> getResult() {
        return nodes;
    }

    @Override
    public void visitDependency(Dependency dependency) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}