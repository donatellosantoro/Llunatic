package it.unibas.lunatic.model.algebra.operators;

import it.unibas.lunatic.model.chase.commons.operators.ChaseUtility;
import it.unibas.lunatic.model.dependency.ComparisonAtom;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.model.dependency.FormulaVariableOccurrence;
import it.unibas.lunatic.model.dependency.PositiveFormula;
import it.unibas.lunatic.model.dependency.operators.GenerateSymmetricPremise;
import speedy.model.expressions.Expression;
import it.unibas.lunatic.model.dependency.SymmetricAtoms;
import it.unibas.lunatic.model.dependency.operators.DependencyUtility;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.SpeedyConstants;
import speedy.model.algebra.GroupBy;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.algebra.OrderBy;
import speedy.model.algebra.Project;
import speedy.model.algebra.Select;
import speedy.model.algebra.SelectIn;
import speedy.model.algebra.aggregatefunctions.CountAggregateFunction;
import speedy.model.algebra.aggregatefunctions.IAggregateFunction;
import speedy.model.algebra.aggregatefunctions.ValueAggregateFunction;
import speedy.model.database.AttributeRef;
import speedy.model.database.TableAlias;
import speedy.utility.SpeedyUtility;

public class BuildAlgebraTreeForSymmetricEGD {

    private static Logger logger = LoggerFactory.getLogger(BuildAlgebraTreeForSymmetricEGD.class);

    private BuildAlgebraTreeForPositiveFormula builderForPositiveFormula = new BuildAlgebraTreeForPositiveFormula();
    private GenerateSymmetricPremise symmetricPremiseGenerator = new GenerateSymmetricPremise();

    public IAlgebraOperator buildTreeForSymmetricEGD(Dependency dependency) {
        if (logger.isDebugEnabled()) logger.debug("Building tree for symmetric dependency...");
        PositiveFormula symmetricFormula = symmetricPremiseGenerator.generateSymmetricPremise(dependency);
        if (logger.isDebugEnabled()) logger.debug("Symmetric formula: " + symmetricFormula);
        IAlgebraOperator premiseRoot = builderForPositiveFormula.buildTreeForPositiveFormula(dependency, symmetricFormula, true, true);
        IAlgebraOperator violationValues = generateViolationValues(dependency, premiseRoot);
        IAlgebraOperator selectIn = generateSelectIn(dependency, violationValues);
        selectIn.addChild(premiseRoot);
        IAlgebraOperator groupBy = addOrderBy(dependency, selectIn);
        return groupBy;
    }

    private IAlgebraOperator generateViolationValues(Dependency dependency, IAlgebraOperator premiseRoot) {
        if (logger.isDebugEnabled()) logger.debug("Generating violation values for " + premiseRoot);
        List<IAggregateFunction> aggreatesForQueriedAttributes = new ArrayList<IAggregateFunction>();
//        List<AttributeRef> symmetricQueriedAttributes = filterAttributesForSymmetricPremise(dependency.getQueriedAttributes(), dependency);
        List<AttributeRef> symmetricQueriedAttributes = dependency.getQueriedAttributes();
        if (logger.isDebugEnabled()) logger.debug("Symmetric queried attributes: " + symmetricQueriedAttributes);
        for (AttributeRef queriedAttribute : symmetricQueriedAttributes) {
            aggreatesForQueriedAttributes.add(new ValueAggregateFunction(queriedAttribute));
        }
        AttributeRef countAttribute = new AttributeRef(new TableAlias(SpeedyConstants.AGGR), SpeedyConstants.COUNT);
        GroupBy groupByQueriedAttributes = new GroupBy(symmetricQueriedAttributes, aggreatesForQueriedAttributes);
        groupByQueriedAttributes.addChild(premiseRoot);

        List<IAggregateFunction> aggregatesForWitness = new ArrayList<IAggregateFunction>();
        List<AttributeRef> targetJoinAttributes = DependencyUtility.findTargetJoinAttributesInPositiveFormula(dependency);
        List<AttributeRef> symmetricWitnessAttributes = filterAttributesForSymmetricPremise(targetJoinAttributes, dependency);
        if (logger.isDebugEnabled()) logger.debug("Symmetric witness attributes: " + symmetricWitnessAttributes);
        symmetricWitnessAttributes = filterConclusionOccurrences(symmetricWitnessAttributes, dependency);
        for (AttributeRef witnessAttribute : symmetricWitnessAttributes) {
            aggregatesForWitness.add(new ValueAggregateFunction(witnessAttribute));
        }
        aggregatesForWitness.add(new CountAggregateFunction(countAttribute));
        GroupBy secondGroupBy = new GroupBy(symmetricWitnessAttributes, aggregatesForWitness);
        secondGroupBy.addChild(groupByQueriedAttributes);

        Expression expression = new Expression("count > 1");
        expression.changeVariableDescription("count", countAttribute);
        Select select = new Select(expression);
        select.addChild(secondGroupBy);
        Project violationProject = new Project(SpeedyUtility.createProjectionAttributes(symmetricWitnessAttributes));
        violationProject.addChild(select);
        return violationProject;
    }

    private IAlgebraOperator generateSelectIn(Dependency dependency, IAlgebraOperator violationValues) {
        List<AttributeRef> targetJoinAttributes = DependencyUtility.findTargetJoinAttributesInPositiveFormula(dependency);
        List<AttributeRef> symmetricWitnessAttributes = filterAttributesForSymmetricPremise(targetJoinAttributes, dependency);
        symmetricWitnessAttributes = filterConclusionOccurrences(symmetricWitnessAttributes, dependency);
        List<IAlgebraOperator> selectionOperators = new ArrayList<IAlgebraOperator>();
        selectionOperators.add(violationValues);
        SelectIn selectIn = new SelectIn(symmetricWitnessAttributes, selectionOperators);
        return selectIn;
    }

    private IAlgebraOperator addOrderBy(Dependency dependency, IAlgebraOperator premiseRoot) {
        List<AttributeRef> targetJoinAttributes = DependencyUtility.findTargetJoinAttributesInPositiveFormula(dependency);
        List<AttributeRef> symmetricWitnessAttributes = filterAttributesForSymmetricPremise(targetJoinAttributes, dependency);
        symmetricWitnessAttributes = filterConclusionOccurrences(symmetricWitnessAttributes, dependency);
        List<AttributeRef> attributesForOrderBy = new ArrayList<AttributeRef>();
        for (AttributeRef targetJoinAttribute : symmetricWitnessAttributes) {
            targetJoinAttribute = ChaseUtility.unAlias(targetJoinAttribute);
            if (!attributesForOrderBy.contains(targetJoinAttribute)) {
                attributesForOrderBy.add(targetJoinAttribute);
            }
        }
        OrderBy orderBy = new OrderBy(attributesForOrderBy);
        orderBy.addChild(premiseRoot);
        return orderBy;
    }

    private List<AttributeRef> filterAttributesForSymmetricPremise(List<AttributeRef> attributes, Dependency dependency) {
//        String tableName = dependency.getTableNameForSymmetricAtom();
        if (logger.isDebugEnabled()) logger.debug("Filtering attributes for symmetric premise: " + attributes + " - " + dependency.getSymmetricAtoms());
        SymmetricAtoms symmetricAtoms = dependency.getSymmetricAtoms();
        List<AttributeRef> result = new ArrayList<AttributeRef>();
        for (AttributeRef attribute : attributes) {
            if (!symmetricAtoms.getSymmetricAliases().contains(attribute.getTableAlias())) {
                continue;
            }
//            if (!attribute.getTableName().equals(tableName)) {
//                continue;
//            }
            AttributeRef unaliasedAttribute = ChaseUtility.unAlias(attribute);
            if (result.contains(unaliasedAttribute)) {
                continue;
            }
            result.add(unaliasedAttribute);
        }
        if (logger.isDebugEnabled()) logger.debug("Result: " + result);
        return result;
    }

    public List<AttributeRef> filterConclusionOccurrences(List<AttributeRef> attributes, Dependency dependency) {
        if (logger.isDebugEnabled()) logger.debug("Filtering conclusion occurrences of attributes: " + attributes + " in dependency " + dependency);
        List<AttributeRef> result = new ArrayList<AttributeRef>();
        ComparisonAtom comparisonAtom = (ComparisonAtom) dependency.getConclusion().getAtoms().get(0);
        FormulaVariable v1 = comparisonAtom.getVariables().get(0);
        FormulaVariable v2 = comparisonAtom.getVariables().get(1);
        for (AttributeRef attributeRef : attributes) {
            if (!containsOccurrences(attributeRef, v1, dependency.getSymmetricAtoms()) && !containsOccurrences(attributeRef, v2, dependency.getSymmetricAtoms())) {
                result.add(attributeRef);
            }
        }
        return result;
    }

    public boolean containsOccurrences(AttributeRef attribute, FormulaVariable v, SymmetricAtoms symmetricAtoms) {
        for (FormulaVariableOccurrence formulaVariableOccurrence : v.getPremiseRelationalOccurrences()) {
            if (!symmetricAtoms.getSymmetricAliases().contains(formulaVariableOccurrence.getAttributeRef().getTableAlias())) {
                continue;
            }
            AttributeRef unaliasedOccurrence = ChaseUtility.unAlias(formulaVariableOccurrence.getAttributeRef());
            if (unaliasedOccurrence.equals(attribute)) {
                return true;
            }
        }
        return false;
    }

}
