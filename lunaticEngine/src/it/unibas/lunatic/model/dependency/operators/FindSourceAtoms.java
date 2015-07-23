package it.unibas.lunatic.model.dependency.operators;

import it.unibas.lunatic.model.dependency.*;

public class FindSourceAtoms {

    public boolean hasSourceAtoms(Dependency dependency) {
        SourceAtomFinderVisitor visitor = new SourceAtomFinderVisitor();
        dependency.accept(visitor);
        return (Boolean) visitor.getResult();
    }
}

class SourceAtomFinderVisitor implements IFormulaVisitor {

    private boolean sourceAtomInPremise = false;
    private boolean sourceAtomInConclusion = false;
    private boolean inPremise = true;

    public void visitDependency(Dependency dependency) {
        dependency.getPremise().accept(this);
        this.inPremise = false;
        dependency.getConclusion().accept(this);
    }

    public void visitPositiveFormula(PositiveFormula formula) {
        for (IFormulaAtom atom : formula.getAtoms()) {
            visitAtom(atom);
        }
    }

    public void visitFormulaWithNegations(FormulaWithNegations formula) {
        formula.getPositiveFormula().accept(this);
        for (IFormula negatedFormula : formula.getNegatedSubFormulas()) {
            negatedFormula.accept(this);
        }
    }

    private void visitAtom(IFormulaAtom atom) {
        if (!(atom instanceof RelationalAtom)) {
            return;
        }
        RelationalAtom relationalAtom = (RelationalAtom) atom;
        if (!relationalAtom.isSource()) {
            return;
        }
        if (inPremise) {
            sourceAtomInPremise = true;
        } else {
            sourceAtomInConclusion = true;
        }
    }

    public Object getResult() {
        return sourceAtomInPremise || sourceAtomInConclusion;
    }
}
