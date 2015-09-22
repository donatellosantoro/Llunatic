package it.unibas.lunatic.model.algebra.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.commons.ChaseUtility;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.model.dependency.RelationalAtom;
import it.unibas.lunatic.persistence.relational.DBMSUtility;
import it.unibas.lunatic.utility.DependencyUtility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.algebra.Join;
import speedy.model.algebra.Scan;
import speedy.model.database.AttributeRef;
import speedy.model.database.TableAlias;

public class BuildAlgebraTreeForTGD {

    private static Logger logger = LoggerFactory.getLogger(BuildAlgebraTreeForTGD.class);
    private BuildAlgebraTree treeBuilder = new BuildAlgebraTree();
    private BuildAlgebraTreeForStandardChase treeBuilderForStandardChase = new BuildAlgebraTreeForStandardChase();

    public Map<Dependency, IAlgebraOperator> buildAlgebraTreesForTGDViolationsCheck(List<Dependency> extTGDs, Scenario scenario) {
        Map<Dependency, IAlgebraOperator> result = new HashMap<Dependency, IAlgebraOperator>();
        for (Dependency extTGD : extTGDs) {
            IAlgebraOperator standardQuery = treeBuilderForStandardChase.generate(extTGD, scenario);
            if (logger.isDebugEnabled()) logger.debug("Operator for dependency " + extTGD + "\n" + standardQuery);
            result.put(extTGD, standardQuery);
        }
        return result;
    }

    public Map<Dependency, IAlgebraOperator> buildAlgebraTreesForTGDViolationsChase(List<Dependency> extTGDs, Scenario scenario) {
        Map<Dependency, IAlgebraOperator> result = new HashMap<Dependency, IAlgebraOperator>();
        for (Dependency extTGD : extTGDs) {
            IAlgebraOperator standardQuery = treeBuilderForStandardChase.generate(extTGD, scenario);
            if (logger.isDebugEnabled()) logger.debug("Operator for dependency " + extTGD + "\n" + standardQuery);
            List<FormulaVariable> universalVariables = DependencyUtility.getUniversalVariablesInConclusion(extTGD);
            IAlgebraOperator premiseOperator = treeBuilder.buildTreeForPremise(extTGD, scenario);
            List<AttributeRef> universalAttributes = DependencyUtility.getUniversalAttributesInPremise(universalVariables);
            Join join = new Join(universalAttributes, universalAttributes);
            join.addChild(premiseOperator);
            join.addChild(standardQuery);
            result.put(extTGD, join);
        }
        return result;
    }

    public Map<Dependency, IAlgebraOperator> buildAlgebraTreesForTGDSatisfaction(List<Dependency> extTGDs, Scenario scenario) {
        Map<Dependency, IAlgebraOperator> result = new HashMap<Dependency, IAlgebraOperator>();
        for (Dependency extTGD : extTGDs) {
            IAlgebraOperator queryOperator = buildCheckSatisfactionAlgebraTreesForTGD(extTGD, scenario, true);
            if (logger.isDebugEnabled()) logger.debug("Operator for dependency " + extTGD + "\n" + queryOperator);
            result.put(extTGD, queryOperator);
        }
        return result;
    }

    public IAlgebraOperator buildCheckSatisfactionAlgebraTreesForTGD(Dependency extTGD, Scenario scenario, boolean usePreviousViolationTable) {
        if (logger.isDebugEnabled()) logger.debug("Building tree for TGD \n" + extTGD);
        IAlgebraOperator premiseRoot = treeBuilder.buildTreeForPremise(extTGD, scenario);
        if (logger.isDebugEnabled()) logger.debug("Premise query:\n" + premiseRoot);
        if (scenario.isDBMS() && usePreviousViolationTable) {
            IAlgebraOperator violationRoot = buildTreeForViolationTable(extTGD, scenario);
            IAlgebraOperator violationJoin = addJoinBetweenPremiseAndViolations(premiseRoot, violationRoot, extTGD);
            premiseRoot = violationJoin;
        }
        IAlgebraOperator conclusionRoot = treeBuilder.buildTreeForConclusion(extTGD, scenario);
        IAlgebraOperator joinRoot = addJoinBetweenPremiseAndConclusion(premiseRoot, conclusionRoot, extTGD);
//        IAlgebraOperator joinRoot = addJoinBetweenPremiseAndConclusion(premiseRoot, conclusionRoot, extTGD);
//        if (scenario.isDBMS()) {
//            IAlgebraOperator violationRoot = buildTreeForViolationTable(extTGD, scenario);
//            IAlgebraOperator violationJoin = addJoinBetweenPremiseAndViolations(joinRoot, violationRoot, extTGD);
//            joinRoot = violationJoin;
//        }
        if (logger.isDebugEnabled()) logger.debug("Conclusion query:\n" + conclusionRoot);
        if (logger.isDebugEnabled()) logger.debug("Join query:\n" + joinRoot);
        return joinRoot;
//        IAlgebraOperator orderByRoot = addOrderBy(joinRoot, extTGD);
//        if (logger.isDebugEnabled()) logger.debug("OrderBy query:\n" + orderByRoot);
//        return orderByRoot;
    }

    public Map<Dependency, IAlgebraOperator> buildAlgebraTreesForDTGD(List<Dependency> denialTGDs, Scenario scenario) {
        Map<Dependency, IAlgebraOperator> result = new HashMap<Dependency, IAlgebraOperator>();
        for (Dependency dTGD : denialTGDs) {
            IAlgebraOperator premiseRoot = treeBuilder.buildTreeForPremise(dTGD, scenario);
            if (logger.isDebugEnabled()) logger.debug("Operator for dependency " + dTGD + "\n" + premiseRoot);
            result.put(dTGD, premiseRoot);
        }
        return result;
    }

    private IAlgebraOperator addJoinBetweenPremiseAndConclusion(IAlgebraOperator premise, IAlgebraOperator conclusion, Dependency extTGD) {
        List<DifferenceEquality> differenceEqualities = findUniversalVariablesEqualities(extTGD);
        List<AttributeRef> leftAttributes = new ArrayList<AttributeRef>();
        List<AttributeRef> rightAttributes = new ArrayList<AttributeRef>();
        for (DifferenceEquality equality : differenceEqualities) {
            leftAttributes.add(equality.leftAttribute);
            rightAttributes.add(equality.rightAttribute);
        }
        Join join = new Join(leftAttributes, rightAttributes);
        join.addChild(premise);
        join.addChild(conclusion);
        return join;
    }

    private IAlgebraOperator buildTreeForViolationTable(Dependency extTGD, Scenario scenario) {
        RelationalAtom relationalAtom = (RelationalAtom) extTGD.getConclusion().getAtoms().get(0);
        Scan scan = new Scan(new TableAlias(ChaseUtility.getTmpTableForTGDViolations(extTGD, relationalAtom.getTableName(), false)));
        if (logger.isDebugEnabled()) logger.debug("Scan for violation table: " + scan);
        return scan;
    }

    private IAlgebraOperator addJoinBetweenPremiseAndViolations(IAlgebraOperator premise, IAlgebraOperator conclusion, Dependency extTGD) {
        List<DifferenceEquality> differenceEqualities = findUniversalVariablesEqualities(extTGD);
        List<AttributeRef> leftAttributes = new ArrayList<AttributeRef>();
        List<AttributeRef> rightAttributes = new ArrayList<AttributeRef>();
        for (DifferenceEquality equality : differenceEqualities) {
            leftAttributes.add(equality.leftAttribute);
            String tableName = ChaseUtility.getTmpTableForTGDViolations(extTGD, equality.rightAttribute.getTableName(), false);
            String attributeName = DBMSUtility.attributeRefToSQL(equality.rightAttribute);
            AttributeRef violationAttribute = new AttributeRef(tableName, attributeName);
            if (logger.isDebugEnabled()) logger.debug("Violation attribute: " + violationAttribute);
            rightAttributes.add(violationAttribute);
        }
        Join join = new Join(leftAttributes, rightAttributes);
        join.addChild(premise);
        join.addChild(conclusion);
        return join;
    }

    private List<DifferenceEquality> findUniversalVariablesEqualities(Dependency dependency) {
        List<DifferenceEquality> result = new ArrayList<DifferenceEquality>();
        List<FormulaVariable> universalVariable = DependencyUtility.getUniversalVariablesInConclusion(dependency);
        if (logger.isDebugEnabled()) logger.debug("Universal variables: " + universalVariable);
        for (FormulaVariable formulaVariable : universalVariable) {
            AttributeRef leftAttribute = DependencyUtility.findFirstOccurrenceInFormula(dependency.getPremise(), formulaVariable.getPremiseRelationalOccurrences());
            AttributeRef rightAttribute = DependencyUtility.findFirstOccurrenceInFormula(dependency.getConclusion(), formulaVariable.getConclusionRelationalOccurrences());
            if (logger.isDebugEnabled()) logger.debug("Adding equality on " + leftAttribute + " and " + rightAttribute);
            result.add(new DifferenceEquality(leftAttribute, rightAttribute));
        }
        return result;
    }
}
