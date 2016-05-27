package it.unibas.lunatic.model.algebra.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.utility.DependencyUtility;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.SpeedyConstants;
import speedy.model.algebra.Distinct;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.algebra.ProjectWithoutOIDs;
import speedy.model.algebra.Select;
import speedy.model.database.Attribute;
import speedy.model.database.AttributeRef;
import speedy.model.expressions.Expression;
import speedy.persistence.Types;
import speedy.utility.DBMSUtility;
import speedy.utility.SpeedyUtility;

public class BuildAlgebraTreeForCertainAnswerQuery {

    private final static Logger logger = LoggerFactory.getLogger(BuildAlgebraTreeForCertainAnswerQuery.class);
    private BuildAlgebraTree treeBuilder = new BuildAlgebraTree();

    public IAlgebraOperator generateOperator(Dependency extTGD, Scenario scenario) {
        if (logger.isDebugEnabled()) logger.debug("Generating standard query for dependency " + extTGD);
        List<FormulaVariable> universalVariables = DependencyUtility.getUniversalVariablesInConclusion(extTGD);
        if (logger.isDebugEnabled()) logger.debug("Universal variables: " + universalVariables);
        List<AttributeRef> universalAttributes = DependencyUtility.getUniversalAttributesInPremise(universalVariables);
        if (logger.isDebugEnabled()) logger.debug("Universal attributes: " + universalVariables);
        IAlgebraOperator premiseOperator = buildPremiseOperator(extTGD, scenario, universalVariables);
        if (logger.isDebugEnabled()) logger.debug("Premise operator\n" + premiseOperator);
        List<Expression> expressions = buildExpression(universalAttributes, scenario);
        Select certainSelect = new Select(expressions);
        certainSelect.addChild(premiseOperator);
        Distinct distinct = new Distinct();
        distinct.addChild(certainSelect);
        return distinct;
    }

    public IAlgebraOperator buildPremiseOperator(Dependency dependency, Scenario scenario, List<FormulaVariable> universalVariables) {
        IAlgebraOperator premiseOperator = treeBuilder.buildTreeForPremiseWithNoOIDInequality(dependency, scenario);
        List<AttributeRef> universalAttributes = DependencyUtility.getUniversalAttributesInPremise(universalVariables);
        if (logger.isDebugEnabled()) logger.debug("Universal attributes in premise: " + universalAttributes);
        ProjectWithoutOIDs root = new ProjectWithoutOIDs(SpeedyUtility.createProjectionAttributes(universalAttributes));
        root.addChild(premiseOperator);
        return root;
    }

    private List<Expression> buildExpression(List<AttributeRef> universalAttributes, Scenario scenario) {
        List<Expression> expressions = new ArrayList<Expression>();
        for (AttributeRef attributeRef : universalAttributes) {
            Attribute attribute = LunaticUtility.getAttribute(attributeRef, LunaticUtility.getDatabase(attributeRef, scenario));
            String type = attribute.getType();
            StringBuilder stringExpression = new StringBuilder();
            stringExpression.append("(isNotNull(").append(attributeRef.toString()).append("))");
            if (type.equals(Types.STRING)) {
                stringExpression.append(" && ");
                stringExpression.append("!(startswith(").append(attributeRef.toString()).append(",\"" + SpeedyConstants.SKOLEM_PREFIX + "\"))");
            } else if (SpeedyUtility.isBigInt(type)) {
                stringExpression.append(" && ");
                stringExpression.append("(length(cast(").append(attributeRef.toString()).append(", text))  < ").append(SpeedyConstants.MIN_LENGTH_FOR_NUMERIC_PLACEHOLDERS);
                stringExpression.append(" || ");
                stringExpression.append("!(startswith(").append(attributeRef.toString()).append(",\"" + SpeedyConstants.BIGINT_SKOLEM_PREFIX + "\")))");
            } else if (SpeedyUtility.isDoublePrecision(type)) {
                stringExpression.append(" && ");
                stringExpression.append("(length(cast(").append(attributeRef.toString()).append(", text))  > ").append(SpeedyConstants.MIN_LENGTH_FOR_NUMERIC_PLACEHOLDERS);
                stringExpression.append(" || ");
                stringExpression.append("!(startswith(").append(attributeRef.toString()).append(",\"" + SpeedyConstants.DOUBLE_SKOLEM_PREFIX + "\")))");
            }
            Expression expression = new Expression(stringExpression.toString());
            expression.setVariableDescription(attributeRef.toString(), attributeRef);
            expressions.add(expression);
        }
        return expressions;
    }
}
