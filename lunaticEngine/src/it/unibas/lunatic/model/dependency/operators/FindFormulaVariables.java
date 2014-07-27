package it.unibas.lunatic.model.dependency.operators;

import it.unibas.lunatic.utility.LunaticUtility;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.dependency.*;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FindFormulaVariables {
    
    public void findVariables(Dependency dependency, Scenario scenario) {
        FindFormulaVariablesVisitor visitor = new FindFormulaVariablesVisitor(scenario);
        dependency.accept(visitor);
    }
}

class FindFormulaVariablesVisitor implements IFormulaVisitor {
    
    private static Logger logger = LoggerFactory.getLogger(FindFormulaVariablesVisitor.class);

    private boolean inConclusion = false;
    private IFormula premise;
    private Scenario scenario;
    
    public FindFormulaVariablesVisitor(Scenario scenario) {
        this.scenario = scenario;
    }
    
    public void visitDependency(Dependency dependency) {
        dependency.getPremise().accept(this);
        this.premise = dependency.getPremise();
        this.inConclusion = true;
        dependency.getConclusion().accept(this);
        if (logger.isDebugEnabled()) logger.debug("Variables found in dependency: " + dependency.getId() + "\n" + LunaticUtility.printVariablesWithOccurrences(dependency.getPremise().getLocalVariables()) + "\n" + LunaticUtility.printVariablesWithOccurrences(dependency.getConclusion().getLocalVariables()));
    }
    
    public void visitPositiveFormula(PositiveFormula formula) {
        List<String> sourceTables = scenario.getSource().getTableNames();
        for (IFormulaAtom atom : formula.getAtoms()) {
            if (!(atom instanceof RelationalAtom)) {
                continue;
            }
            for (FormulaAttribute attribute : ((RelationalAtom) atom).getAttributes()) {
                if (!attribute.getValue().isVariable()) {
                    continue;
                }
                FormulaVariableOccurrence occurrence = (FormulaVariableOccurrence) attribute.getValue();
                FormulaVariable variable = findVariable(occurrence.getVariableId(), formula);
                if (variable == null) {
                    variable = new FormulaVariable(occurrence.getVariableId());
                    formula.addLocalVariable(variable);
                }
                if (!inConclusion) {
                    variable.addPremiseOccurrence(occurrence);
                } else {
                    variable.addConclusionOccurrence(occurrence);
                }
                AttributeRef attributeRef = occurrence.getAttributeRef();
                if (sourceTables.contains(attributeRef.getTableName())) {
                    attributeRef.getTableAlias().setSource(true);
                }
                if (scenario.getAuthoritativeSources().contains(attributeRef.getTableName())) {
                    attributeRef.getTableAlias().setAuthoritative(true);
                }
            }
        }
        if (logger.isDebugEnabled()) logger.debug("Variables found in formula: " + formula.getId() + "\n" + LunaticUtility.printVariablesWithOccurrences(formula.getLocalVariables()));
    }
    
    public void visitFormulaWithNegations(FormulaWithNegations formula) {
        formula.getPositiveFormula().accept(this);
        for (IFormula negatedFormula : formula.getNegatedSubFormulas()) {
            negatedFormula.accept(this);
        }
    }
    
    public Object getResult() {
        return null;
    }
    
    private FormulaVariable findVariable(String variableId, IFormula formula) {
        FormulaVariable variable = findVariableInList(variableId, formula.getLocalVariables());
        if (variable != null) {
            return variable;
        }
        if (formula.getFather() == null) {
            if (inConclusion) {
                return findVariableInList(variableId, premise.getLocalVariables());
            }
            return null;
        }
        return findVariable(variableId, formula.getFather());
    }
    
    private FormulaVariable findVariableInList(String variableId, List<FormulaVariable> variables) {
        for (FormulaVariable variable : variables) {
            if (variable.getId().equals(variableId)) {
                return variable;
            }
        }
        return null;
    }
}