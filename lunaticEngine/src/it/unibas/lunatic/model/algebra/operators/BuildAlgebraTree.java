package it.unibas.lunatic.model.algebra.operators;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.algebra.Difference;
import it.unibas.lunatic.model.algebra.IAlgebraOperator;
import it.unibas.lunatic.model.algebra.Join;
import it.unibas.lunatic.model.algebra.Project;
import it.unibas.lunatic.model.chase.commons.ChaseUtility;
import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.database.TableAlias;
import it.unibas.lunatic.model.dependency.ComparisonAtom;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.model.dependency.FormulaVariableOccurrence;
import it.unibas.lunatic.model.dependency.FormulaWithNegations;
import it.unibas.lunatic.model.dependency.IFormula;
import it.unibas.lunatic.model.dependency.IFormulaAtom;
import it.unibas.lunatic.utility.DependencyUtility;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuildAlgebraTree {

    private static Logger logger = LoggerFactory.getLogger(BuildAlgebraTree.class);

    private BuildAlgebraTreeForPositiveFormula builderForPositiveFormula = new BuildAlgebraTreeForPositiveFormula();

    public IAlgebraOperator buildTreeForPremise(Dependency dependency, Scenario scenario) {
        return buildStandardTreeForFormulaWithNegations(dependency, dependency.getPremise(), scenario, true);
    }

    public IAlgebraOperator buildTreeForConclusion(Dependency dependency, Scenario scenario) {
        return buildStandardTreeForFormulaWithNegations(dependency, dependency.getConclusion(), scenario, false);
    }

    public IAlgebraOperator buildDeltaTreeForFormulaWithNegations(Dependency dependency, IFormula formula, Scenario scenario, boolean premise) {
        IAlgebraOperator root = builderForPositiveFormula.buildTreeForPositiveFormula(dependency, formula.getPositiveFormula(), premise);
        for (IFormula negatedFormula : formula.getNegatedSubFormulas()) {
            root = addDeltaDifferences(dependency, root, (FormulaWithNegations) negatedFormula, scenario, premise);
        }
        return root;
    }

    public IAlgebraOperator addDeltaDifferences(Dependency dependency, IAlgebraOperator root, FormulaWithNegations negatedFormula, Scenario scenario, boolean premise) {
        IAlgebraOperator negatedRoot = buildDeltaTreeForFormulaWithNegations(dependency, negatedFormula, scenario, premise);
        IAlgebraOperator joinRoot = addJoinForDifference(root, negatedRoot, negatedFormula);
        List<AttributeRef> projectionAttributes = new ArrayList<AttributeRef>();
        List<AttributeRef> requiredAttributes = DependencyUtility.extractRequestedAttributes(dependency);
        if (logger.isDebugEnabled()) logger.debug("Required attributes for dependency: " + requiredAttributes);
        if (logger.isDebugEnabled()) logger.debug("Possible projection attributes for dependency: " + root.getAttributes(scenario.getSource(), scenario.getTarget()));
        for (AttributeRef projectionAttribute : root.getAttributes(scenario.getSource(), scenario.getTarget())) {
            if (requiredAttributes.contains(ChaseUtility.unAlias(projectionAttribute)) || projectionAttribute.getName().equals(LunaticConstants.OID)) {
                projectionAttributes.add(projectionAttribute);
            }
        }
        IAlgebraOperator project = new Project(projectionAttributes);
        project.addChild(joinRoot);
        IAlgebraOperator difference = new Difference();
        difference.addChild(root);
        difference.addChild(project);
//        if (root.getAttributes(scenario.getSource(), scenario.getTarget()).size() != project.getAttributes(scenario.getSource(), scenario.getTarget()).size()) {
//            throw new IllegalArgumentException("Difference operator must have the same size and schema: "
//                    + root.getAttributes(scenario.getSource(), scenario.getTarget()) + " - "
//                    + project.getAttributes(scenario.getSource(), scenario.getTarget()));
//        }
        return difference;
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    ////////
    ////////    STANDARD QUERY WITH JOINS
    ////////
    ////////////////////////////////////////////////////////////////////////////////////////
    private IAlgebraOperator buildStandardTreeForFormulaWithNegations(Dependency dependency, IFormula formula, Scenario scenario, boolean premise) {
        IAlgebraOperator root = builderForPositiveFormula.buildTreeForPositiveFormula(dependency, formula.getPositiveFormula(), premise);
        for (IFormula negatedFormula : formula.getNegatedSubFormulas()) {
            root = addStandardDifferences(dependency, root, (FormulaWithNegations) negatedFormula, scenario, premise);
        }
        return root;
    }

    private IAlgebraOperator addStandardDifferences(Dependency dependency, IAlgebraOperator root, FormulaWithNegations negatedFormula, Scenario scenario, boolean premise) {
        IAlgebraOperator negatedRoot = buildStandardTreeForFormulaWithNegations(dependency, negatedFormula, scenario, premise);
        IAlgebraOperator joinRoot = addJoinForDifference(root, negatedRoot, negatedFormula);
        IAlgebraOperator project = new Project(root.getAttributes(scenario.getSource(), scenario.getTarget()));
        project.addChild(joinRoot);
        IAlgebraOperator difference = new Difference();
        difference.addChild(root);
        difference.addChild(project);
        return difference;
    }

    private IAlgebraOperator addJoinForDifference(IAlgebraOperator root, IAlgebraOperator negatedRoot, FormulaWithNegations negatedFormula) {
        List<DifferenceEquality> differenceEqualities = findDifferenceEqualities(negatedFormula);
        List<AttributeRef> leftAttributes = new ArrayList<AttributeRef>();
        List<AttributeRef> rightAttributes = new ArrayList<AttributeRef>();
        for (DifferenceEquality equality : differenceEqualities) {
            leftAttributes.add(equality.leftAttribute);
            rightAttributes.add(equality.rightAttribute);
        }
        Join join = new Join(leftAttributes, rightAttributes);
        join.addChild(root);
        join.addChild(negatedRoot);
        return join;
    }

    private List<DifferenceEquality> findDifferenceEqualities(FormulaWithNegations negatedFormula) {
        List<DifferenceEquality> result = new ArrayList<DifferenceEquality>();
        for (FormulaVariable formulaVariable : negatedFormula.getAllVariables()) {
            DifferenceEquality equality = isDifferenceVariable(formulaVariable, negatedFormula);
            if (equality != null) {
                result.add(equality);
            }
        }
        for (IFormulaAtom atom : negatedFormula.getAtoms()) {
            if (!(atom instanceof ComparisonAtom)) {
                continue;
            }
            ComparisonAtom comparison = (ComparisonAtom) atom;
            if (comparison.getVariables().size() < 2) {
                continue;
            }
            DifferenceEquality equality = isDifferenceComparison(comparison, negatedFormula);
            if (equality != null) {
                result.add(equality);
            }
        }
        return result;
    }

    private DifferenceEquality isDifferenceVariable(FormulaVariable formulaVariable, FormulaWithNegations negatedFormula) {
        IFormula father = negatedFormula.getFather();
        AttributeRef leftAttribute = findFirstOccurrenceInFormula(formulaVariable, father);
        AttributeRef rightAttribute = findFirstOccurrenceInFormula(formulaVariable, negatedFormula);
        if (leftAttribute != null && rightAttribute != null) {
            return new DifferenceEquality(leftAttribute, rightAttribute);
        }
        return null;
    }

    private AttributeRef findFirstOccurrenceInFormula(FormulaVariable formulaVariable, IFormula formula) {
        List<TableAlias> aliasesInFormula = AlgebraUtility.findAliasesForFormula(formula.getPositiveFormula());
        for (FormulaVariableOccurrence occurrence : formulaVariable.getPremiseRelationalOccurrences()) {
            AttributeRef attribute = occurrence.getAttributeRef();
            if (aliasesInFormula.contains(attribute.getTableAlias())) {
                return attribute;
            }
        }
        return null;
    }

    private DifferenceEquality isDifferenceComparison(ComparisonAtom comparison, FormulaWithNegations negatedFormula) {
        IFormula father = negatedFormula.getFather();
        FormulaVariable firstVariable = comparison.getVariables().get(0);
        FormulaVariable secondVariable = comparison.getVariables().get(1);
        AttributeRef leftAttribute = findFirstOccurrenceInFormula(firstVariable, father);
        AttributeRef rightAttribute = findFirstOccurrenceInFormula(secondVariable, negatedFormula);
        if (leftAttribute == null) {
            leftAttribute = findFirstOccurrenceInFormula(secondVariable, father);
            rightAttribute = findFirstOccurrenceInFormula(firstVariable, negatedFormula);
        }
        if (leftAttribute == null) {
            return null;
        }
        return new DifferenceEquality(leftAttribute, rightAttribute);

    }
}

class DifferenceEquality {

    DifferenceEquality(AttributeRef leftAttribute, AttributeRef rightAttribute) {
        this.leftAttribute = leftAttribute;
        this.rightAttribute = rightAttribute;
    }
    AttributeRef leftAttribute;
    AttributeRef rightAttribute;
}
