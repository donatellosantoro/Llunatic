/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.visualdeps.generator;

import com.google.common.collect.Multimap;
import it.unibas.lunatic.model.dependency.ComparisonAtom;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaAttribute;
import it.unibas.lunatic.model.dependency.FormulaWithNegations;
import it.unibas.lunatic.model.dependency.IFormula;
import it.unibas.lunatic.model.dependency.IFormulaAtom;
import it.unibas.lunatic.model.dependency.NullFormula;
import it.unibas.lunatic.model.dependency.PositiveFormula;
import it.unibas.lunatic.model.dependency.RelationalAtom;
import it.unibas.lunatic.model.dependency.operators.IFormulaVisitor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Antonio Galotta
 */
public class EgdVisitor {
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