/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.visualdeps.generator;

import it.unibas.lunatic.gui.visualdeps.SceneUtils;
import it.unibas.lunatic.gui.visualdeps.components.GraphBiLayout;
import it.unibas.lunatic.gui.visualdeps.model.GraphNode;
import it.unibas.lunatic.gui.visualdeps.model.PinNode;
import it.unibas.lunatic.model.dependency.BuiltInAtom;
import it.unibas.lunatic.model.dependency.ComparisonAtom;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaAttribute;
import it.unibas.lunatic.model.dependency.FormulaVariable;
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
import org.netbeans.api.visual.vmd.VMDNodeWidget;

/**
 *
 * @author Antonio Galotta
 */
class TgdVisitor implements IFormulaVisitor {

    private Log logger = LogFactory.getLog(getClass());
    private Border premiseBorder = SceneUtils.getPremiseBorder();
    private Border conclusionBorder = SceneUtils.getConclusionBorder();
    private LunaticDepScene scene;
    private List<GraphNode> relationalAtomsNodes = new ArrayList<GraphNode>();
    private List<GraphNode> comparisonAtomsNodes = new ArrayList<GraphNode>();
    private List<GraphNode> builtinAtomsNodes = new ArrayList<GraphNode>();
    private List<GraphNode> result = new ArrayList<GraphNode>();
    private final boolean premise;

    TgdVisitor(LunaticDepScene scene, boolean b) {
        this.scene = scene;
        this.premise = b;
    }

    @Override
    public void visitPositiveFormula(PositiveFormula formula) {
        if (formula instanceof NullFormula) {
            return;
        }
        for (IFormulaAtom atom : formula.getAtoms()) {
            if (atom instanceof RelationalAtom) {
                addRelationalAtom((RelationalAtom) atom);
            } else if (atom instanceof ComparisonAtom) {
                addComparisonAtom((ComparisonAtom) atom);
            }else if (atom instanceof BuiltInAtom){
                addBuiltinAtom((BuiltInAtom) atom);
            }
        }
    }

    //TODO visitor formula negato
    @Override
    public void visitFormulaWithNegations(FormulaWithNegations formula) {
        formula.getPositiveFormula().accept(this);
        for (IFormula negatedFormula : formula.getNegatedSubFormulas()) {
            negatedFormula.accept(this);
        }
    }

    private void addRelationalAtom(RelationalAtom atom) {
        logger.debug("RELATIONAL ATOM: " + atom.toString());
        String atomId = atom.getTableNameWithAlias();
        GraphNode graphNode = new GraphNode(atomId, atom.getTableNameWithAlias());
        graphNode.setValue(TgdDepSceneGenerator.ATOM, atom);
        VMDNodeWidget nodeWidget = scene.createNode(graphNode, false);
        relationalAtomsNodes.add(graphNode);
        result.add(graphNode);
        configureNodeView(graphNode);
        setNodeBorder(nodeWidget);
        for (FormulaAttribute formulaAttribute : atom.getAttributes()) {
            logger.debug("FORMULA ATTRIBUTE PIN: " + formulaAttribute.toString());
            PinNode pinNode = new PinNode(graphNode, formulaAttribute.getAttributeName(), formulaAttribute.toString());
            pinNode.setValue(TgdDepSceneGenerator.VARIABLE_VALUE, formulaAttribute.getValue().toString());
            scene.createPin(pinNode);
        }
    }

    private void addComparisonAtom(ComparisonAtom atom) {
        logger.debug("COMPARISON ATOM: " + atom.toString());
        String atomId = atom.toString();
        GraphNode graphNode = new GraphNode(atomId);
        graphNode.setValue(TgdDepSceneGenerator.ATOM, atom);
        VMDNodeWidget nodeWidget = scene.createNode(graphNode, false);
        comparisonAtomsNodes.add(graphNode);
        result.add(graphNode);
        setNodeBorder(nodeWidget);
        for (int i = 0; i < atom.getVariables().size(); i++) {
            FormulaVariable var = atom.getVariables().get(i);
            logger.debug("FORMULA VAR PIN: " + var.toString());
            PinNode pinNode = new PinNode(graphNode, var.getId());
            pinNode.setValue(TgdDepSceneGenerator.VARIABLE_VALUE, var.getId());
            scene.createHiddenPin(pinNode);
        }
    }
    
    private void addBuiltinAtom(BuiltInAtom atom) {
        logger.debug("BUILTIN ATOM: " + atom.toString());
        String atomId = atom.toString();
        GraphNode graphNode = new GraphNode(atomId);
        graphNode.setValue(TgdDepSceneGenerator.ATOM, atom);
        VMDNodeWidget nodeWidget = scene.createNode(graphNode, false);
        builtinAtomsNodes.add(graphNode);
        result.add(graphNode);
        setNodeBorder(nodeWidget);
        for (int i = 0; i < atom.getVariables().size(); i++) {
            FormulaVariable var = atom.getVariables().get(i);
            logger.debug("FORMULA VAR PIN: " + var.toString());
            PinNode pinNode = new PinNode(graphNode, var.getId());
            pinNode.setValue(TgdDepSceneGenerator.VARIABLE_VALUE, var.getId());
            scene.createHiddenPin(pinNode);
        }
    }

    @Override
    public List<GraphNode> getResult() {
        return result;
    }

    public List<GraphNode> getBuiltinAtomsNodes() {
        return builtinAtomsNodes;
    }

    public List<GraphNode> getRelationalAtomsNodes() {
        return relationalAtomsNodes;
    }

    public List<GraphNode> getComparisonAtomsNodes() {
        return comparisonAtomsNodes;
    }

    @Override
    public void visitDependency(Dependency dependency) {
        if (premise) {
            dependency.getPremise().accept(this);
        } else {
            dependency.getConclusion().accept(this);
        }
    }

    private void setNodeBorder(VMDNodeWidget nodeWidget) {
        if (premise) {
            nodeWidget.setBorder(premiseBorder);
        } else {
            nodeWidget.setBorder(conclusionBorder);
        }
    }

    private void configureNodeView(GraphNode graphNode) {
        if (!premise) {
            graphNode.setValue(GraphBiLayout.LAYOUT_NODE_LOCATION, GraphBiLayout.LOCATION_RIGHT);
        }
    }
}