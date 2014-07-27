package it.unibas.lunatic.model.dependency.operators;

import it.unibas.lunatic.model.dependency.*;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckRecursion {
    
    private static Logger logger = LoggerFactory.getLogger(CheckRecursion.class);

    public void checkRecursion(Dependency dependency) {
//        if (!dependency.getType().equals(LunaticConstants.ExtTGD)) {
//            return;
//        }
//        CheckRecursionVisitor visitor = new CheckRecursionVisitor();
//        dependency.getPremise().accept(visitor);
//        Set<String> premiseTables = visitor.getResult();
//        if (logger.isDebugEnabled()) logger.debug("Premise tables: " + premiseTables);
//        visitor = new CheckRecursionVisitor();
//        dependency.getConclusion().accept(visitor);
//        Set<String> conclusionTables = visitor.getResult();
//        if (logger.isDebugEnabled()) logger.debug("Conclusion tables: " + conclusionTables);
//        if (haveIntersection(premiseTables, conclusionTables)) {
//            dependency.setRecursive(true);
//            if (logger.isDebugEnabled()) logger.debug("Recursive dependency: " + dependency);
//        }
    }

    private boolean haveIntersection(Set<String> premiseTables, Set<String> conclusionTables) {
        Set<String> intersection = new HashSet<String>(premiseTables);
        intersection.retainAll(conclusionTables);
        return !intersection.isEmpty();
    }
}

class CheckRecursionVisitor implements IFormulaVisitor {

    private Set<String> tableNames = new HashSet<String>();
    
    public void visitPositiveFormula(PositiveFormula formula) {
        for (IFormulaAtom atom : formula.getAtoms()) {
            if (atom instanceof RelationalAtom) {
                RelationalAtom relationalAtom = (RelationalAtom) atom;
                tableNames.add(relationalAtom.getTableName());
            }
        }
    }

    public void visitFormulaWithNegations(FormulaWithNegations formula) {
        formula.getPositiveFormula().accept(this);
        for (IFormula negatedFormula : formula.getNegatedSubFormulas()) {
            negatedFormula.accept(this);
        }
    }

    public Set<String> getResult() {
        return tableNames;
    }

    public void visitDependency(Dependency dependency) {
    }
}
