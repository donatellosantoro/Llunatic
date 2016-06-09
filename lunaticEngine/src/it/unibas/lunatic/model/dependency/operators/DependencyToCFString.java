package it.unibas.lunatic.model.dependency.operators;

import it.unibas.lunatic.model.dependency.*;
import java.util.Stack;
import speedy.SpeedyConstants;

public class DependencyToCFString {

    public String toCFString(Dependency dependency) {
        DependencyToCFStringVisitor visitor = new DependencyToCFStringVisitor("");
        dependency.accept(visitor);
        return visitor.getResult().toString();
    }
}

class DependencyToCFStringVisitor implements IFormulaVisitor {

    private Stack<String> indentStack = new Stack<String>();
    private boolean premise = true;
    private StringBuilder result = new StringBuilder();

    public DependencyToCFStringVisitor(String indent) {
        this.indentStack.push(indent);
    }

    public void visitDependency(Dependency dependency) {
        if (dependency.getConclusion().getAtoms().get(0) instanceof QueryAtom) {
            this.premise = true;
            dependency.getConclusion().accept(this);
            result.append(" <- ");
            this.premise = false;
            dependency.getPremise().accept(this);
        } else {
            this.premise = true;
            dependency.getPremise().accept(this);
            result.append(" -> ");
            this.premise = false;
            dependency.getConclusion().accept(this);
        }
        result.append(".");
    }

    public void visitPositiveFormula(PositiveFormula formula) {
        if (premise) {
            result.append("\n").append(getIndent());
        }
        if (formula instanceof NullFormula) {
            result.append("fail");
            return;
        }
        if (!isRootFormula(formula)) {
            result.append(getIndent()).append(" !(");
        }
        result.append(formula.toCFString());
    }

    public void visitFormulaWithNegations(FormulaWithNegations formula) {
        formula.getPositiveFormula().accept(this);
        for (IFormula negatedFormula : formula.getNegatedSubFormulas()) {
            this.indentStack.push(SpeedyConstants.SECONDARY_INDENT);
            negatedFormula.accept(this);
            this.indentStack.pop();
        }
        if (formula.getFather() != null) {
            result.append(")");
        }
    }

    private String getIndent() {
        StringBuilder indentString = new StringBuilder();
        for (String string : indentStack) {
            indentString.append(string);
        }
        return indentString.toString();
    }

    private boolean isRootFormula(IFormula formula) {
        return formula.getFather() == null || (formula.getFather() != null && formula.getFather().getFather() == null);
    }

    public Object getResult() {
        return result.toString();
    }
}
