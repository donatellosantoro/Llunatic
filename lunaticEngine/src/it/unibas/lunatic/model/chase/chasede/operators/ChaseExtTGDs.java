package it.unibas.lunatic.model.chase.chasede.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.algebra.operators.BuildAlgebraTreeForStandardChase;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.chase.commons.ChaseUtility;
import it.unibas.lunatic.model.chase.commons.control.IChaseState;
import it.unibas.lunatic.model.dependency.Dependency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.IAlgebraOperator;

public class ChaseExtTGDs {

    private static Logger logger = LoggerFactory.getLogger(ChaseExtTGDs.class);

    private BuildAlgebraTreeForStandardChase insertGenerator = new BuildAlgebraTreeForStandardChase();
    private IInsertFromSelectNaive naiveInsert;

    public ChaseExtTGDs(IInsertFromSelectNaive naiveInsert) {
        this.naiveInsert = naiveInsert;
    }

    public boolean doChase(Scenario scenario, IChaseState chaseState) {
        if (logger.isDebugEnabled()) logger.debug("Chasing st tgds on scenario: " + scenario);
        Map<Dependency, IAlgebraOperator> treeMap = buildAlgebraTrees(scenario.getExtTGDs(), scenario);
        boolean modified = false;
        int iterations = 0;
        long start = new Date().getTime();
        while (true) {
            boolean insertedTuples = false;
            for (Dependency eTgd : scenario.getExtTGDs()) {
                long startTgd = new Date().getTime();
                if (chaseState.isCancelled()) ChaseUtility.stopChase(chaseState); //throw new ChaseException("Chase interrupted by user");
                if (logger.isDebugEnabled()) logger.debug("----Chasing tgd: " + eTgd);
                if (logger.isDebugEnabled()) logger.debug("----Target: " + scenario.getTarget().printInstances(true));
                IAlgebraOperator treeRoot = treeMap.get(eTgd);
                insertedTuples = naiveInsert.execute(eTgd, treeRoot, scenario.getSource(), scenario.getTarget()) || insertedTuples;
                long endTgd = new Date().getTime();
                ChaseStats.getInstance().addDepenendecyStat(eTgd, endTgd - startTgd);
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
        long end = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.TGD_TIME, end - start);
        return modified;
    }

    private Map<Dependency, IAlgebraOperator> buildAlgebraTrees(List<Dependency> extTGDs, Scenario scenario) {
        Map<Dependency, IAlgebraOperator> result = new HashMap<Dependency, IAlgebraOperator>();
        for (Dependency dependency : extTGDs) {
            IAlgebraOperator standardInsert = insertGenerator.generate(dependency, scenario);
            if (logger.isDebugEnabled()) logger.debug("Operator for dependency " + dependency + "\n" + standardInsert);
            result.put(dependency, standardInsert);
        }
        return result;
    }
}
