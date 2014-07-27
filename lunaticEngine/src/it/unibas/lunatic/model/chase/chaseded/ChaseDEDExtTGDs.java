package it.unibas.lunatic.model.chase.chaseded;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.algebra.IAlgebraOperator;
import it.unibas.lunatic.model.algebra.operators.BuildAlgebraTreeForStandardChaseDED;
import it.unibas.lunatic.model.chase.chasede.operators.IInsertFromSelectNaive;
import it.unibas.lunatic.model.dependency.DED;
import it.unibas.lunatic.model.dependency.Dependency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChaseDEDExtTGDs {

    private static Logger logger = LoggerFactory.getLogger(ChaseDEDExtTGDs.class);

    private BuildAlgebraTreeForStandardChaseDED insertGenerator = new BuildAlgebraTreeForStandardChaseDED();
    private IInsertFromSelectNaive naiveInsert;

    public ChaseDEDExtTGDs(IInsertFromSelectNaive naiveInsert) {
        this.naiveInsert = naiveInsert;
    }

    public boolean doChase(Scenario scenario, Map<DED, Dependency> dedScenario) {
        if (logger.isDebugEnabled()) logger.debug("Chasing st tgds on scenario: " + scenario);
        Map<DED, IAlgebraOperator> treeMap = buildAlgebraTrees(scenario.getDEDextTGDs(), scenario, dedScenario);
        boolean modified = false;
        int iterations = 0;
        while (true) {
            boolean insertedTuples = false;
            for (DED dedExtTgd : scenario.getDEDextTGDs()) {
                Dependency eTgd = dedScenario.get(dedExtTgd);
                if (logger.isDebugEnabled()) logger.debug("----Chasing tgd: " + eTgd);
                IAlgebraOperator treeRoot = treeMap.get(dedExtTgd);
                insertedTuples = naiveInsert.execute(eTgd, treeRoot, scenario.getSource(), scenario.getTarget()) || insertedTuples;
            }
            if (!insertedTuples) {
                break;
            } else {
                iterations++;
                if (scenario.getConfiguration().getIterationLimit() != null && iterations > scenario.getConfiguration().getIterationLimit()) {
                    throw new ChaseException("Iteration limit reached in chasing extTgds. Stopping after " + scenario.getConfiguration().getIterationLimit() + " iterations");
                }
                modified = true;
            }
        }
        return modified;
    }

    private Map<DED, IAlgebraOperator> buildAlgebraTrees(List<DED> extTGDs, Scenario scenario, Map<DED, Dependency> dedScenario) {
        Map<DED, IAlgebraOperator> result = new HashMap<DED, IAlgebraOperator>();
        for (DED ded : extTGDs) {
            IAlgebraOperator standardInsert = insertGenerator.generate(ded, dedScenario.get(ded), scenario);
            if (logger.isDebugEnabled()) logger.debug("Operator for dependency " + ded + "\n" + standardInsert);
            result.put(ded, standardInsert);
        }
        return result;
    }
}
