package it.unibas.lunatic.test.algebra;

import it.unibas.lunatic.utility.LunaticUtility;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.algebra.IAlgebraOperator;
import it.unibas.lunatic.model.algebra.operators.BuildAlgebraTree;
import it.unibas.lunatic.model.algebra.operators.ITupleIterator;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import java.util.List;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestBuildAlgebraTree extends TestCase {

    private static Logger logger = LoggerFactory.getLogger(TestBuildAlgebraTree.class);

    private BuildAlgebraTree buildAlgebraTree = new BuildAlgebraTree();

    public void testBuiltInCondition() {
        Scenario scenario = UtilityTest.loadScenario(References.employees_rew);
        List<Dependency> dependencies = scenario.getSTTgds();
        dependencies.addAll(scenario.getExtTGDs());
        dependencies.addAll(scenario.getExtEGDs());
        for (Dependency dependency : dependencies) {
            IAlgebraOperator operator = buildAlgebraTree.buildTreeForPremise(dependency, scenario);
            if (logger.isDebugEnabled()) logger.debug(operator.toString());
            ITupleIterator result = operator.execute(scenario.getSource(), scenario.getTarget());
            String stringResult = LunaticUtility.printTupleIterator(result);
            if (logger.isDebugEnabled()) logger.debug(stringResult);
        }
    }

    public void testJoinNegation() {
        Scenario scenario = UtilityTest.loadScenario(References.companies_rew);
        List<Dependency> dependencies = scenario.getSTTgds();
        dependencies.addAll(scenario.getExtTGDs());
        dependencies.addAll(scenario.getExtEGDs());
        for (Dependency dependency : dependencies) {
            IAlgebraOperator operator = buildAlgebraTree.buildTreeForPremise(dependency, scenario);
            if (logger.isDebugEnabled()) logger.debug(operator.toString());
            ITupleIterator result = operator.execute(scenario.getSource(), scenario.getTarget());
            String stringResult = LunaticUtility.printTupleIterator(result);
            if (logger.isDebugEnabled()) logger.debug(stringResult);
        }
    }

    public void testRSETGD() {
        Scenario scenario = UtilityTest.loadScenario(References.testRS);
        List<Dependency> eTGD = scenario.getExtTGDs();
        for (Dependency dependency : eTGD) {
            IAlgebraOperator operator = buildAlgebraTree.buildTreeForPremise(dependency, scenario);
            if (logger.isDebugEnabled()) logger.debug(operator.toString());
            ITupleIterator result = operator.execute(scenario.getSource(), scenario.getTarget());
            String stringResult = LunaticUtility.printTupleIterator(result);
            if (logger.isDebugEnabled()) logger.debug(stringResult);
        }
    }
}
