package it.unibas.lunatic.model.algebra.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.dependency.ComparisonAtom;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaWithNegations;
import it.unibas.lunatic.model.dependency.IFormula;
import it.unibas.lunatic.model.dependency.IFormulaAtom;
import it.unibas.lunatic.utility.DependencyUtility;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.algebra.Limit;
import speedy.model.algebra.OrderBy;
import speedy.model.algebra.Select;
import speedy.model.database.AttributeRef;
import speedy.model.expressions.Expression;

public class BuildAlgebraTreeForEGD {

    private static Logger logger = LoggerFactory.getLogger(BuildAlgebraTreeForEGD.class);
    private BuildAlgebraTree treeBuilder = new BuildAlgebraTree();
    private BuildAlgebraTreeForSymmetricEGD builderForSymmetricEGD = new BuildAlgebraTreeForSymmetricEGD();
    private BuildAlgebraTreeForPositiveFormula builderForPositiveFormula = new BuildAlgebraTreeForPositiveFormula();

    public Map<Dependency, IAlgebraOperator> buildPremiseAlgebraTreesForEGDs(List<Dependency> dependencies, Scenario scenario) {
        Map<Dependency, IAlgebraOperator> premiseTreeMap = new HashMap<Dependency, IAlgebraOperator>();
        for (Dependency dependency : dependencies) {
            IAlgebraOperator premiseRoot = buildTreeForExtEGDPremise(dependency, scenario);
//            if (scenario.getConfiguration().isUseLimit1() || dependency.isOverlapBetweenAffectedAndQueried()) {
            if (scenario.getConfiguration().isUseLimit1ForEGDs()) {
                Limit limit1 = new Limit(1);
                limit1.addChild(premiseRoot);
                premiseRoot = limit1;
            }
            premiseTreeMap.put(dependency, premiseRoot);
            if (logger.isDebugEnabled()) logger.debug("Algebra tree for dependency\n" + dependency + "\n" + premiseRoot);
        }
        return premiseTreeMap;
    }

    public IAlgebraOperator buildTreeForExtEGDPremise(Dependency dependency, Scenario scenario) {
        return buildTreeForExtEGDPremise(dependency, true, scenario);
    }

    public IAlgebraOperator buildTreeForExtEGDPremise(Dependency dependency, boolean useSymmetry, Scenario scenario) {
        IAlgebraOperator premiseRoot;
        if (!dependency.hasSymmetricChase() || !useSymmetry) {
            if (logger.isDebugEnabled()) logger.debug("Building tree for non-symmetric dependency...");
            premiseRoot = builderForPositiveFormula.buildTreeForPositiveFormula(dependency, dependency.getPremise().getPositiveFormula(), true, true);
            IAlgebraOperator select = addSelectionsForViolations(dependency);
            select.addChild(premiseRoot);
            premiseRoot = select;
            for (IFormula negatedFormula : dependency.getPremise().getNegatedSubFormulas()) {
                premiseRoot = treeBuilder.addDeltaDifferences(dependency, premiseRoot, (FormulaWithNegations) negatedFormula, scenario, true);
            }
            premiseRoot = addOrderBy(dependency, premiseRoot);
        } else {
            premiseRoot = builderForSymmetricEGD.buildTreeForSymmetricEGD(dependency);
        }
        if (logger.isDebugEnabled()) logger.debug("Premise query for extended dependency: \n" + dependency + "\n" + premiseRoot);
        return premiseRoot;
    }

    ////////////////////////////////
    private Select addSelectionsForViolations(Dependency dependency) {
        StringBuilder stringExpression = new StringBuilder();
        Map<String, Object> variableDescriptions = new HashMap<String, Object>();
        for (IFormulaAtom atom : dependency.getConclusion().getAtoms()) {
            if (!(atom instanceof ComparisonAtom)) {
                throw new IllegalArgumentException("Only comparisons are allowed in egd conclusions: " + dependency);
            }
            ComparisonAtom comparison = (ComparisonAtom) atom;
            stringExpression.append("!").append(comparison.getExpression().toString());
            stringExpression.append(" || ");
            if (comparison.getExpression().getVariables().size() == 2) {
                String variable1 = comparison.getExpression().getVariables().get(0);
                String variable2 = comparison.getExpression().getVariables().get(1);
                stringExpression.append("(isNull(").append(variable1).append(") && isNotNull(").append(variable2).append("))");
                stringExpression.append(" || ");
                stringExpression.append("(isNull(").append(variable2).append(") && isNotNull(").append(variable1).append("))");
                stringExpression.append(" || ");
            } else {
                String variable1 = comparison.getExpression().getVariables().get(0);
                stringExpression.append("isNull(").append(variable1).append(")");
                stringExpression.append(" || ");
            }
            for (String variable : comparison.getExpression().getVariables()) {
                variableDescriptions.put(variable, comparison.getExpression().getJepExpression().getVar(variable).getDescription());
            }
        }
        LunaticUtility.removeChars(" || ".length(), stringExpression);
        Expression expression = new Expression(stringExpression.toString());
        for (String variable : variableDescriptions.keySet()) {
            expression.changeVariableDescription(variable, variableDescriptions.get(variable));
        }
        if (logger.isTraceEnabled()) logger.debug("Expression string for dependency " + dependency + "\n" + expression);
        List<Expression> selections = new ArrayList<Expression>();
        selections.add(expression);
        Select select = new Select(selections);
        return select;
    }

    private IAlgebraOperator addOrderBy(Dependency dependency, IAlgebraOperator premiseRoot) {
        List<AttributeRef> targetJoinAttributes = DependencyUtility.findTargetJoinAttributesInPositiveFormula(dependency);
        if (targetJoinAttributes.isEmpty()) {
            return premiseRoot;
        }
        List<AttributeRef> attributesForOrderBy = new ArrayList<AttributeRef>();
        for (AttributeRef targetJoinAttribute : targetJoinAttributes) {
            AttributeRef attributeForOrderBy = targetJoinAttribute;
            if (!attributesForOrderBy.contains(attributeForOrderBy)) {
                attributesForOrderBy.add(attributeForOrderBy);
            }
        }
        OrderBy orderBy = new OrderBy(attributesForOrderBy);
        orderBy.addChild(premiseRoot);
        return orderBy;
    }
}
