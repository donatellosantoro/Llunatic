package it.unibas.lunatic.model.algebra.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.utility.DependencyUtility;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.Distinct;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.algebra.Select;
import speedy.model.database.AttributeRef;
import speedy.model.expressions.Expression;

public class BuildAlgebraTreeForCertainAnswerQuery {

    private final static Logger logger = LoggerFactory.getLogger(BuildAlgebraTreeForCertainAnswerQuery.class);
    private BuildAlgebraTreeForStandardChase treeBuilder = new BuildAlgebraTreeForStandardChase();

    public IAlgebraOperator generateOperator(Dependency extTGD, Scenario scenario) {
        if (logger.isDebugEnabled()) logger.debug("Generating standard query for dependency " + extTGD);
        List<FormulaVariable> universalVariables = DependencyUtility.getUniversalVariablesInConclusion(extTGD);
        if (logger.isDebugEnabled()) logger.debug("Universal variables: " + universalVariables);
        List<AttributeRef> universalAttributes = DependencyUtility.getUniversalAttributesInPremise(universalVariables);
        if (logger.isDebugEnabled()) logger.debug("Universal attributes: " + universalVariables);
        IAlgebraOperator premiseOperator = treeBuilder.buildPremiseOperator(extTGD, scenario, universalVariables);
        if (logger.isDebugEnabled()) logger.debug("Premise operator\n" + premiseOperator);
        List<Expression> expressions = buildExpression(universalAttributes);
        Select certainSelect = new Select(expressions);
        certainSelect.addChild(premiseOperator);
        Distinct distinct = new Distinct();
        distinct.addChild(certainSelect);
        return distinct;
    }

    private List<Expression> buildExpression(List<AttributeRef> universalAttributes) {
        List<Expression> expressions = new ArrayList<Expression>();
        for (AttributeRef universalAttribute : universalAttributes) {
            StringBuilder stringExpression = new StringBuilder();
            stringExpression.append("(isNotNull(").append(universalAttribute.toString()).append("))");
            stringExpression.append(" && ");
            stringExpression.append("!(startswith(").append(universalAttribute.toString()).append(",\"_SK\"))");
            Expression expression = new Expression(stringExpression.toString());
            expression.setVariableDescription(universalAttribute.toString(), universalAttribute);
            expressions.add(expression);
        }
        return expressions;
    }
}
