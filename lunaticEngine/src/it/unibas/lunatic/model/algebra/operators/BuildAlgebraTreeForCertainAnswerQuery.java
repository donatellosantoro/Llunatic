package it.unibas.lunatic.model.algebra.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaAttribute;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.model.dependency.FormulaVariableOccurrence;
import it.unibas.lunatic.model.dependency.QueryAtom;
import it.unibas.lunatic.model.dependency.operators.DependencyUtility;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
        List<String> orderedVariableIdsInConclusion = extractOrderedVariableIdsInConclusion(dependency);
        if (logger.isDebugEnabled()) logger.debug("Ordered variable ids: " + orderedVariableIdsInConclusion);
        Collections.sort(universalAttributes, new AttributeComparatorInQueryAtom(orderedVariableIdsInConclusion, dependency));
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

    private List<String> extractOrderedVariableIdsInConclusion(Dependency dependency) {
        List<String> result = new ArrayList<String>();
        QueryAtom queryAtom = (QueryAtom) dependency.getConclusion().getAtoms().get(0);
        for (int i = 0; i < queryAtom.getAttributes().size(); i++) {
            FormulaAttribute formulaAttribute = queryAtom.getAttributes().get(i);
            FormulaVariableOccurrence occurrence = (FormulaVariableOccurrence) formulaAttribute.getValue();
            result.add(occurrence.getVariableId());
        }
        return result;
    }

    class AttributeComparatorInQueryAtom implements Comparator<AttributeRef> {

        private List<String> orderedVariableIdsInConclusion;
        private Dependency dependency;

        public AttributeComparatorInQueryAtom(List<String> orderedVariableIdsInConclusion, Dependency dependency) {
            this.orderedVariableIdsInConclusion = orderedVariableIdsInConclusion;
            this.dependency = dependency;
        }

        public int compare(AttributeRef o1, AttributeRef o2) {
            int pos1 = findPosition(o1);
            int pos2 = findPosition(o2);
            return pos1 - pos2;
        }

        private int findPosition(AttributeRef attribute) {
            for (FormulaVariable variable : dependency.getPremise().getLocalVariables()) {
                if (!variable.getAttributeRefs().contains(attribute)) {
                    continue;
                }
                return orderedVariableIdsInConclusion.indexOf(variable.getId());
            }
            throw new IllegalArgumentException("Unable to find attribute " + attribute + " query atom");
        }

    }

}
