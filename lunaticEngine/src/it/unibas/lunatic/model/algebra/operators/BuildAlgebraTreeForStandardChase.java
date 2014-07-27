package it.unibas.lunatic.model.algebra.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.algebra.Difference;
import it.unibas.lunatic.model.algebra.IAlgebraOperator;
import it.unibas.lunatic.model.algebra.Limit;
import it.unibas.lunatic.model.algebra.ProjectWithoutOIDs;
import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.utility.DependencyUtility;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuildAlgebraTreeForStandardChase {

    private static Logger logger = LoggerFactory.getLogger(BuildAlgebraTreeForStandardChase.class);

    private BuildAlgebraTree treeBuilder = new BuildAlgebraTree();

    public IAlgebraOperator generate(Dependency extTGD, Scenario scenario) {
        if (logger.isDebugEnabled()) logger.debug("Generating standard insert for dependency " + extTGD);
        List<FormulaVariable> universalVariables = DependencyUtility.getUniversalVariablesInConclusion(extTGD);
        if (logger.isDebugEnabled()) logger.debug("Universal variables: " + universalVariables);
        IAlgebraOperator premiseOperator = buildPremiseOperator(extTGD, scenario, universalVariables);
        if (logger.isDebugEnabled()) logger.debug("Premise operator\n" + premiseOperator);
        IAlgebraOperator conclusionOperator = buildConclusionOperator(extTGD, scenario, universalVariables);
        if (logger.isDebugEnabled()) logger.debug("Conclusion operator\n" + conclusionOperator);
        Difference difference = new Difference();
        difference.addChild(premiseOperator);
        difference.addChild(conclusionOperator);
        if (logger.isDebugEnabled()) logger.debug("Difference operator: " + difference);
        IAlgebraOperator root = difference;
        if (scenario.getConfiguration().isUseLimit1()) {
            Limit limit = new Limit(1);
            limit.addChild(root);
            if (logger.isDebugEnabled()) logger.debug("Adding limit operator. " + limit);
            root = limit;
        }
        return root;
    }

    private IAlgebraOperator buildPremiseOperator(Dependency dependency, Scenario scenario, List<FormulaVariable> universalVariables) {
        IAlgebraOperator premiseOperator = treeBuilder.buildTreeForPremise(dependency, scenario);
        List<AttributeRef> universalAttributes = DependencyUtility.getUniversalAttributesInPremise(universalVariables);
        if (logger.isDebugEnabled()) logger.debug("Universal attributes in premise: " + universalAttributes);
        ProjectWithoutOIDs root = new ProjectWithoutOIDs(universalAttributes);
        root.addChild(premiseOperator);
        return root;
    }

    private IAlgebraOperator buildConclusionOperator(Dependency dependency, Scenario scenario, List<FormulaVariable> universalVariables) {
        IAlgebraOperator conclusion = treeBuilder.buildTreeForConclusion(dependency, scenario);
        List<AttributeRef> universalAttributes = DependencyUtility.getUniversalAttributesInConclusion(universalVariables);
        if (logger.isDebugEnabled()) logger.debug("Universal attributes in conclusion: " + universalAttributes);
        ProjectWithoutOIDs root = new ProjectWithoutOIDs(universalAttributes);
        root.addChild(conclusion);
        return root;
    }
}
