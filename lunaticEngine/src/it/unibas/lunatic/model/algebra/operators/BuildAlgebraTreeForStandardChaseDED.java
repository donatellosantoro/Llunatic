package it.unibas.lunatic.model.algebra.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.dependency.DED;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.model.dependency.operators.DependencyUtility;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.Difference;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.algebra.Join;
import speedy.model.algebra.Project;
import speedy.model.database.AttributeRef;
import speedy.utility.SpeedyUtility;

public class BuildAlgebraTreeForStandardChaseDED {

    private static Logger logger = LoggerFactory.getLogger(BuildAlgebraTreeForStandardChaseDED.class);

    private BuildAlgebraTree algebraTreeBuilder = new BuildAlgebraTree();

    public IAlgebraOperator generate(DED ded, Dependency dependency, Scenario scenario) {
        if (logger.isDebugEnabled()) logger.debug("Building AlgebraTree for DED " + ded + " and dependency " + dependency);
        IAlgebraOperator premiseOperator = algebraTreeBuilder.buildTreeForPremise(dependency, scenario);
        if (logger.isDebugEnabled()) logger.debug("Original  Algebra tree:\n" + premiseOperator);
//        List<Dependency> negatedDependencies = extractNegatedDependencies(ded, dependency);
        List<Dependency> negatedDependencies = ded.getAssociatedDependencies();
        IAlgebraOperator operator = premiseOperator;
        for (Dependency negatedDependency : negatedDependencies) {
            operator = addNegatedDependency(negatedDependency, operator, premiseOperator, scenario);
        }
        if (logger.isDebugEnabled()) logger.debug("DED Algebra tree:\n" + operator);
        return operator;
    }

//    private List<Dependency> extractNegatedDependencies(DED ded, Dependency dependency) {
//        List<Dependency> result = new ArrayList<Dependency>();
//        for (Dependency d : ded.getAssociatedDependencies()) {
//            if (d.equals(dependency)) {
//                continue;
//            }
//            result.add(d);
//        }
//        return result;
//    }

    private IAlgebraOperator addNegatedDependency(Dependency negatedDependency, IAlgebraOperator root, IAlgebraOperator premiseOperator, Scenario scenario) {
        IAlgebraOperator negatedRoot = algebraTreeBuilder.buildTreeForConclusion(negatedDependency, scenario);
        if (logger.isDebugEnabled()) logger.debug("Adding negated tree:\n" + negatedRoot);
        IAlgebraOperator joinRoot = addJoinForDifference(premiseOperator, negatedRoot, negatedDependency);
        IAlgebraOperator project = new Project(SpeedyUtility.createProjectionAttributes(root.getAttributes(scenario.getSource(), scenario.getTarget())));
        project.addChild(joinRoot);
        IAlgebraOperator difference = new Difference();
        difference.addChild(root);
        difference.addChild(project);
        if (logger.isDebugEnabled()) logger.debug("Resulting difference:\n" + difference);
        return difference;
    }

    private IAlgebraOperator addJoinForDifference(IAlgebraOperator root, IAlgebraOperator negatedRoot, Dependency negatedDependency) {
        List<DifferenceEquality> differenceEqualities = findDifferenceEqualities(negatedDependency);
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

    private List<DifferenceEquality> findDifferenceEqualities(Dependency negatedDependency) {
        List<DifferenceEquality> result = new ArrayList<DifferenceEquality>();
        List<FormulaVariable> universalVariable = DependencyUtility.getUniversalVariablesInConclusion(negatedDependency);
        if (logger.isDebugEnabled()) logger.debug("Universal variables: " + universalVariable);
        for (FormulaVariable formulaVariable : universalVariable) {
            AttributeRef leftAttribute = DependencyUtility.findFirstOccurrenceInFormula(negatedDependency.getPremise(), formulaVariable.getPremiseRelationalOccurrences());
            AttributeRef rightAttribute = DependencyUtility.findFirstOccurrenceInFormula(negatedDependency.getConclusion(), formulaVariable.getConclusionRelationalOccurrences());
            if (logger.isDebugEnabled()) logger.debug("Adding equality on " + leftAttribute + " and " + rightAttribute);
            result.add(new DifferenceEquality(leftAttribute, rightAttribute));
        }
        return result;
    }
}
