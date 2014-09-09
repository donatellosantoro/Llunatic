package it.unibas.lunatic.model.chase.chaseded;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.chase.commons.IChaseSTTGDs;
import it.unibas.lunatic.model.chase.commons.control.IChaseState;
import it.unibas.lunatic.model.chase.commons.control.ImmutableChaseState;
import it.unibas.lunatic.model.chase.chasede.operators.ChaseDCs;
import it.unibas.lunatic.model.chase.chasede.operators.IInsertFromSelectNaive;
import it.unibas.lunatic.model.chase.chasede.operators.IRemoveDuplicates;
import it.unibas.lunatic.model.chase.chasede.operators.IUpdateCell;
import it.unibas.lunatic.model.chase.chasede.operators.IValueOccurrenceHandlerDE;
import it.unibas.lunatic.model.chase.chasemc.operators.IRunQuery;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.dependency.DED;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.utility.combinatorial.GenericListGenerator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChaseDEDScenarioGreedy implements IDEDChaser {

    public static final int ITERATION_LIMIT = 10;
    private static Logger logger = LoggerFactory.getLogger(ChaseDEDScenarioGreedy.class);
    //
    private IChaseSTTGDs stChaser;
    private ChaseDEDExtTGDs extTgdChaser;
    private ChaseDCs dChaser;
//    private ChaseEGDs egdChaser;
    private IDatabaseManager databaseManager;
//    private FindSymmetricAtoms symmetryFinder = new FindSymmetricAtoms();
    private ImmutableChaseState immutableChaseState = ImmutableChaseState.getInstance();

    public ChaseDEDScenarioGreedy(IChaseSTTGDs stChaser, IInsertFromSelectNaive naiveInsert, IRemoveDuplicates duplicateRemover,
            IValueOccurrenceHandlerDE valueOccurrenceHandler, IRunQuery queryRunner, IUpdateCell cellUpdater, IDatabaseManager databaseManager) {
        this.stChaser = stChaser;
        this.extTgdChaser = new ChaseDEDExtTGDs(naiveInsert);
//        this.egdChaser = new ChaseEGDs(valueOccurrenceHandler, duplicateRemover, queryRunner, cellUpdater);
        this.dChaser = new ChaseDCs(queryRunner);
        this.databaseManager = databaseManager;
    }

    public IDatabase doChase(Scenario scenario) {
        return doChase(scenario, immutableChaseState);
    }

    public IDatabase doChase(Scenario scenario, IChaseState chaseState) {
        List<GreedyDEDScenario> dedScenarios = generateGreedyDEDScenarios(scenario);
        if (logger.isDebugEnabled()) printDEDScenarios(dedScenarios);
        Iterator<GreedyDEDScenario> it = dedScenarios.iterator();
        stChaser.doChase(scenario, true);
        IDatabase originalTarget = databaseManager.cloneTarget(scenario);
        IDatabase result = null;
        while (it.hasNext()) {
            GreedyDEDScenario dedScenario = it.next();
            result = doChase(scenario, chaseState, dedScenario);
            if (result != null) {
                //Solution found
                break;
            }
            databaseManager.restoreTarget(originalTarget, scenario);
        }
        databaseManager.removeClone(originalTarget, scenario);
        if (result != null) {
            return result;
        }
        throw new ChaseException("Unable to find solution...");
    }

    private IDatabase doChase(Scenario scenario, IChaseState chaseState, GreedyDEDScenario dedScenario) {
        if (logger.isDebugEnabled()) logger.debug("Chasing dependencies on ded scenario: " + dedScenario);
//        ChaseUtility.findTargetJoinAttributes(scenario.getEGDs());
//        ChaseUtility.findQueriedAttributes(scenario.getEGDs());
//        symmetryFinder.findSymmetricAtoms(scenario.getEGDs());
        int iterations = 0;
        while (iterations < ITERATION_LIMIT) {
            boolean newTuples = extTgdChaser.doChase(scenario, dedScenario);
            boolean cellChanges = false;
//            boolean cellChanges = egdChaser.doChase(scenario);
            if (!newTuples && !cellChanges) {
                break;
            } else {
                iterations++;
            }
        }
        try {
            dChaser.doChase(scenario,chaseState);
            IDatabase target = scenario.getTarget();
            if (logger.isDebugEnabled()) logger.debug("----Result of chase: " + target);
            return target;
        } catch (ChaseException ce) {
            return null;
        }
    }

    private List<GreedyDEDScenario> generateGreedyDEDScenarios(Scenario scenario) {
        List<DED> deds = new ArrayList<DED>();
//        deds.addAll(scenario.getDEDstTGDs());
        deds.addAll(scenario.getDEDextTGDs());
        deds.addAll(scenario.getDEDEGDs());
        return generateCombinations(deds);
    }

    private List<GreedyDEDScenario> generateCombinations(List<DED> deds) {
        List<List<Dependency>> lists = new ArrayList<List<Dependency>>();
        for (DED ded : deds) {
            lists.add(ded.getAssociatedDependencies());
        }
        List<List<Dependency>> result = new GenericListGenerator<Dependency>().generateListsOfElements(lists);
        List<GreedyDEDScenario> scenarios = new ArrayList<GreedyDEDScenario>();
        for (List<Dependency> combinations : result) {
            GreedyDEDScenario scenario = generateScenario(deds, combinations);
            scenarios.add(scenario);
        }
        return scenarios;
    }

    private GreedyDEDScenario generateScenario(List<DED> deds, List<Dependency> combinations) {
        GreedyDEDScenario scenario = new GreedyDEDScenario();
        for (int i = 0; i < deds.size(); i++) {
            scenario.addDEDInstantiation(deds.get(i), combinations.get(i));
        }
        return scenario;
    }

    private void printDEDScenarios(List<GreedyDEDScenario> dedScenarios) {
        StringBuilder sb = new StringBuilder();
        sb.append("DED Scenarios: ").append(dedScenarios.size()).append("\n");
        for (int i = 0; i < dedScenarios.size(); i++) {
            sb.append("## Scenario ").append(i).append("\n");
            sb.append(dedScenarios.get(i));
        }
        if (logger.isDebugEnabled()) logger.debug(sb.toString());
    }

}
