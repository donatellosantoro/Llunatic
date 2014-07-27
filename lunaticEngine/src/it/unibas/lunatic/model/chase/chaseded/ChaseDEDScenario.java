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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChaseDEDScenario implements IDEDChaser {

    public static final int ITERATION_LIMIT = 10;
    private static Logger logger = LoggerFactory.getLogger(ChaseDEDScenario.class);
    //
    private IChaseSTTGDs stChaser;
    private ChaseDEDExtTGDs extTgdChaser;
    private ChaseDCs dChaser;
//    private ChaseEGDs egdChaser;
    private IDatabaseManager databaseManager;
//    private FindSymmetricAtoms symmetryFinder = new FindSymmetricAtoms();
    private ImmutableChaseState immutableChaseState = ImmutableChaseState.getInstance();

    public ChaseDEDScenario(IChaseSTTGDs stChaser, IInsertFromSelectNaive naiveInsert, IRemoveDuplicates duplicateRemover,
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
        List<Map<DED, Dependency>> dedScenarios = generateDEDScenarios(scenario);
        if (logger.isDebugEnabled()) printDEDScenarios(dedScenarios);
        Iterator<Map<DED, Dependency>> it = dedScenarios.iterator();
        stChaser.doChase(scenario, true);
        IDatabase originalTarget = databaseManager.cloneTarget(scenario);
        IDatabase result = null;
        while (it.hasNext()) {
            Map<DED, Dependency> dedScenario = it.next();
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

    private IDatabase doChase(Scenario scenario, IChaseState chaseState, Map<DED, Dependency> dedScenario) {
        if (logger.isDebugEnabled()) logger.debug("Chasing dependencies on ded scenario: " + printDEDScenario(dedScenario));
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

    private List<Map<DED, Dependency>> generateDEDScenarios(Scenario scenario) {
        List<DED> deds = new ArrayList<DED>();
//        deds.addAll(scenario.getDEDstTGDs());
        deds.addAll(scenario.getDEDextTGDs());
        deds.addAll(scenario.getDEDEGDs());
        return generateCombinations(deds);
    }

    private List<Map<DED, Dependency>> generateCombinations(List<DED> deds) {
        List<List<Dependency>> lists = new ArrayList<List<Dependency>>();
        for (DED ded : deds) {
            lists.add(ded.getAssociatedDependencies());
        }
        List<List<Dependency>> result = new GenericListGenerator<Dependency>().generateListsOfElements(lists);
        List<Map<DED, Dependency>> scenarios = new ArrayList<Map<DED, Dependency>>();
        for (List<Dependency> combinations : result) {
            Map<DED, Dependency> scenario = generateScenario(deds, combinations);
            scenarios.add(scenario);
        }
        return scenarios;
    }

    private Map<DED, Dependency> generateScenario(List<DED> deds, List<Dependency> combinations) {
        Map<DED, Dependency> scenario = new HashMap<DED, Dependency>();
        for (int i = 0; i < deds.size(); i++) {
            scenario.put(deds.get(i), combinations.get(i));
        }
        return scenario;
    }

    private void printDEDScenarios(List<Map<DED, Dependency>> dedScenarios) {
        StringBuffer sb = new StringBuffer();
        sb.append("DED Scenarios: ").append(dedScenarios.size()).append("\n");
        for (int i = 0; i < dedScenarios.size(); i++) {
            sb.append("## Scenario ").append(i).append("\n");
            Map<DED, Dependency> dedScenario = dedScenarios.get(i);
            sb.append(printDEDScenario(dedScenario));
        }
        if (logger.isDebugEnabled()) logger.debug(sb.toString());
    }

    private String printDEDScenario(Map<DED, Dependency> dedScenario) {
        StringBuilder sb = new StringBuilder();
        for (DED ded : dedScenario.keySet()) {
            sb.append("DED ").append(ded.getId()).append(" ").append(dedScenario.get(ded)).append("\n");
        }
        return sb.toString();
    }
}
