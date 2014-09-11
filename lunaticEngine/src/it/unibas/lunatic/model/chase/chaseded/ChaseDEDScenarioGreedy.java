package it.unibas.lunatic.model.chase.chaseded;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.exceptions.ChaseFailedException;
import it.unibas.lunatic.model.chase.chasede.IDEChaser;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.chase.commons.IChaseSTTGDs;
import it.unibas.lunatic.model.chase.commons.control.IChaseState;
import it.unibas.lunatic.model.chase.commons.control.ImmutableChaseState;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.dependency.DED;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.utility.combinatorial.GenericListGenerator;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChaseDEDScenarioGreedy implements IDEDChaser {

    private static Logger logger = LoggerFactory.getLogger(ChaseDEDScenarioGreedy.class);
    //
    private IChaseSTTGDs stChaser;
    private IDEChaser deChaser;
    private IDatabaseManager databaseManager;
    //
    private ImmutableChaseState immutableChaseState = ImmutableChaseState.getInstance();

    public ChaseDEDScenarioGreedy(IChaseSTTGDs stChaser, IDEChaser deChaser, IDatabaseManager databaseManager) {
        this.stChaser = stChaser;
        this.deChaser = deChaser;
        this.databaseManager = databaseManager;
    }

    public IDatabase doChase(Scenario scenario) {
        return doChase(scenario, immutableChaseState);
    }

    public IDatabase doChase(Scenario scenario, IChaseState chaseState) {
        if (!scenario.getSTTgds().isEmpty() && !scenario.getDEDstTGDs().isEmpty()) {
            throw new ChaseException("A DED scenario cannot contain both standard ST-TGDs and DED ST-TGDs..");
        }
        List<GreedyDEDScenario> dedScenarios = generateGreedyDEDScenarios(scenario);
        if (dedScenarios.isEmpty()) {
            throw new ChaseException("The given scenario does not contains any DED dependency");
        }
        ChaseStats.getInstance().addStat(ChaseStats.NUMBER_OF_DED_STTGDS, scenario.getDEDstTGDs().size());
        ChaseStats.getInstance().addStat(ChaseStats.NUMBER_OF_DED_EGDS, scenario.getDEDEGDs().size());
        ChaseStats.getInstance().addStat(ChaseStats.NUMBER_OF_DED_EXTGDS, scenario.getDEDextTGDs().size());
        ChaseStats.getInstance().addStat(ChaseStats.NUMBER_OF_GREEDY_SCENARIOS, dedScenarios.size());
        if (logger.isDebugEnabled()) printDEDScenarios(dedScenarios);
        stChaser.doChase(scenario, true);
        IDatabase originalTarget = databaseManager.cloneTarget(scenario);
        IDatabase result = null;
        for (GreedyDEDScenario dedScenario : dedScenarios) {
            try {
                result = doChase(scenario, dedScenario, chaseState);
                //Solution found
                if (logger.isDebugEnabled()) logger.debug("DED Scenario " + dedScenario.getId() + " generates a solution!");
                break;
            } catch (ChaseFailedException ex) {
                if (logger.isDebugEnabled()) logger.debug("DED Scenario " + dedScenario.getId() + " failed!");
                databaseManager.restoreTarget(originalTarget, scenario);
            }
        }
        databaseManager.removeClone(originalTarget, scenario);
        if (result == null) {
            throw new ChaseException("Unable to find solution...");
        }
        ChaseStats.getInstance().printStats();
        return result;
    }

    private IDatabase doChase(Scenario scenario, GreedyDEDScenario dedScenario, IChaseState chaseState) {
        if (logger.isDebugEnabled()) logger.debug("Chasing DED Scenario " + dedScenario);
        // Generating a new standard Scenario starting from the DED one
        Scenario deScenario = new Scenario(scenario.getFileName());
        deScenario.setSource(scenario.getSource());
        deScenario.setTarget(scenario.getTarget());
        deScenario.setConfiguration(scenario.getConfiguration());
        deScenario.getSTTgds().addAll(scenario.getSTTgds());
        deScenario.getExtTGDs().addAll(scenario.getExtTGDs());
        deScenario.getEGDs().addAll(scenario.getEGDs());
        deScenario.getDCs().addAll(scenario.getDCs());
        for (DED ded : scenario.getDEDstTGDs()) {
            deScenario.getSTTgds().add(dedScenario.getDependencyForDED(ded));
        }
        for (DED ded : scenario.getDEDextTGDs()) {
            deScenario.getExtTGDs().add(dedScenario.getDependencyForDED(ded));
        }
        for (DED ded : scenario.getDEDEGDs()) {
            deScenario.getEGDs().add(dedScenario.getDependencyForDED(ded));
        }
        if (logger.isDebugEnabled()) logger.debug("DE Scenario associated to the DED Scenario " + dedScenario.getId() + ":\n" + deScenario);
        // Chasing the new standard scenario
        return deChaser.doChase(deScenario, chaseState);
    }

    private List<GreedyDEDScenario> generateGreedyDEDScenarios(Scenario scenario) {
        List<DED> deds = new ArrayList<DED>();
        deds.addAll(scenario.getDEDstTGDs());
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
        for (int i = 0; i < result.size(); i++) {
            List<Dependency> combinations = result.get(i);
            GreedyDEDScenario scenario = generateScenario(deds, combinations, i);
            scenarios.add(scenario);
        }
        return scenarios;
    }

    private GreedyDEDScenario generateScenario(List<DED> deds, List<Dependency> combinations, int id) {
        GreedyDEDScenario scenario = new GreedyDEDScenario(id);
        for (int i = 0; i < deds.size(); i++) {
            scenario.addDEDInstantiation(deds.get(i), combinations.get(i));
        }
        return scenario;
    }

    private void printDEDScenarios(List<GreedyDEDScenario> dedScenarios) {
        StringBuilder sb = new StringBuilder();
        sb.append("DED Scenarios: ").append(dedScenarios.size()).append("\n");
        for (int i = 0; i < dedScenarios.size(); i++) {
            sb.append(dedScenarios.get(i));
        }
        if (logger.isDebugEnabled()) logger.debug(sb.toString());
    }
}
