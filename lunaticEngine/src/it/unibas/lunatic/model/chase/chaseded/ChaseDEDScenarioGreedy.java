package it.unibas.lunatic.model.chase.chaseded;

import it.unibas.lunatic.LunaticConfiguration;
import it.unibas.lunatic.OperatorFactory;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.exceptions.ChaseFailedException;
import it.unibas.lunatic.model.chase.chasede.IDEChaser;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.chase.commons.IChaseSTTGDs;
import it.unibas.lunatic.model.chase.commons.control.IChaseState;
import it.unibas.lunatic.model.chase.commons.control.ImmutableChaseState;
import speedy.model.database.IDatabase;
import it.unibas.lunatic.model.dependency.DED;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.operators.FindTargetGenerators;
import it.unibas.lunatic.model.dependency.operators.NormalizeConclusionsInTGDs;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.utility.combinatorics.GenericListGenerator;

public class ChaseDEDScenarioGreedy implements IDEDChaser {

    private static Logger logger = LoggerFactory.getLogger(ChaseDEDScenarioGreedy.class);
    //
    private IChaseSTTGDs stChaser;
    private IDEChaser deChaser;
    private IDEDDatabaseManager databaseManager;
    //
    private ImmutableChaseState immutableChaseState = ImmutableChaseState.getInstance();
    private NormalizeConclusionsInTGDs dependencyNormalizer = new NormalizeConclusionsInTGDs();
    private FindTargetGenerators generatorFinder = new FindTargetGenerators();

    public ChaseDEDScenarioGreedy(IChaseSTTGDs stChaser, IDEChaser deChaser, IDEDDatabaseManager databaseManager) {
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
        ChaseStats.getInstance().addStat(ChaseStats.NUMBER_OF_EXECUTED_GREEDY_SCENARIOS, 0);
        if (logger.isDebugEnabled()) printDEDScenarios(dedScenarios);
        stChaser.doChase(scenario, true);
        IDatabase originalTarget = databaseManager.cloneTarget(scenario);
        IDatabase result = null;
        int executedGreedyScenarios = 0;
        for (GreedyDEDScenario dedScenario : dedScenarios) {
            if (logger.isDebugEnabled()) logger.debug("Executing DED Scenario " + dedScenario.getId());
            if (LunaticConfiguration.isPrintSteps()) System.out.println("*** Executing DED Scenario " + dedScenario.getId());
            if (LunaticConfiguration.isPrintSteps()) System.out.println(dedScenario.toString());
            executedGreedyScenarios++;
            try {
                result = doChase(scenario, dedScenario, chaseState);
                //Solution found
                if (logger.isDebugEnabled()) logger.debug("DED Scenario " + dedScenario.getId() + " generates a solution!");
                if (LunaticConfiguration.isPrintSteps()) System.out.println("*** DED Scenario " + dedScenario.getId() + " generates a solution!");
                if (scenario.getConfiguration().isChaseDEDGreedyExecuteAllScenarios()) {
                    rollbackChase(originalTarget, scenario);
                    continue;
                }
                if (logger.isDebugEnabled()) logger.debug("No more scenarios to chase. Returning");
                break;
            } catch (ChaseFailedException ex) {
                if (logger.isDebugEnabled()) logger.debug("Chase fail: " + ex);
                if (logger.isDebugEnabled()) logger.debug("DED Scenario " + dedScenario.getId() + " failed!");
                if (LunaticConfiguration.isPrintSteps()) System.out.println("*** DED Scenario " + dedScenario.getId() + " failed!");
                ChaseStats.getInstance().addStat(ChaseStats.NUMBER_OF_FAILED_GREEDY_SCENARIOS, 1);
                rollbackChase(originalTarget, scenario);
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.error(ex.getLocalizedMessage());
                throw new RuntimeException(ex.getLocalizedMessage());
            }
        }
        databaseManager.removeClone(originalTarget, scenario);
        ChaseStats.getInstance().addStat(ChaseStats.NUMBER_OF_EXECUTED_GREEDY_SCENARIOS, executedGreedyScenarios);
        if (result == null) {
            throw new ChaseException("Unable to find solution...");
        }
        if (logger.isDebugEnabled()) ChaseStats.getInstance().printStatistics();
        return result;
    }

    private void rollbackChase(IDatabase originalTarget, Scenario scenario) {
        databaseManager.restoreTarget(originalTarget, scenario);
        OperatorFactory.getInstance().reset();
    }

    private IDatabase doChase(Scenario scenario, GreedyDEDScenario dedScenario, IChaseState chaseState) {
        if (logger.isDebugEnabled()) logger.debug("Chasing DED Scenario " + dedScenario);
        // Generating a new standard Scenario starting from the DED one
        Scenario deScenario = new Scenario(scenario.getFileName(), null);
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
        for (DED ded : scenario.getDEDEGDs()) {
            deScenario.getEGDs().add(dedScenario.getDependencyForDED(ded));
        }
        List<Dependency> newExtTGDs = processDEDTGDs(scenario, dedScenario);
        deScenario.getExtTGDs().addAll(newExtTGDs);
        if (logger.isDebugEnabled()) logger.debug("DE Scenario associated to the DED Scenario " + dedScenario.getId() + ":\n" + deScenario);
        // Chasing the new standard scenario
        return deChaser.doChase(deScenario, chaseState);
    }

    private List<Dependency> processDEDTGDs(Scenario scenario, GreedyDEDScenario dedScenario) {
        List<Dependency> newExtTGDs = new ArrayList<Dependency>();
        for (DED ded : scenario.getDEDextTGDs()) {
            newExtTGDs.add(dedScenario.getDependencyForDED(ded));
        }
        if (logger.isDebugEnabled()) logger.debug("Generated TGDs from DED: \n" + LunaticUtility.printCollection(newExtTGDs));
        // needed because DEDs cannot be normalized earlier on
        newExtTGDs = dependencyNormalizer.normalizeTGDs(newExtTGDs);
        for (Dependency eTGD : newExtTGDs) {
            generatorFinder.findGenerators(eTGD);
        }
        if (logger.isDebugEnabled()) logger.debug("Normalized generated TGDs: \n" + LunaticUtility.printCollection(newExtTGDs));
        return newExtTGDs;
    }

    private List<GreedyDEDScenario> generateGreedyDEDScenarios(Scenario scenario) {
        List<DED> deds = new ArrayList<DED>();
        deds.addAll(scenario.getDEDstTGDs());
        deds.addAll(scenario.getDEDextTGDs());
        deds.addAll(scenario.getDEDEGDs());
        return generateCombinations(deds, scenario);
    }

    private List<GreedyDEDScenario> generateCombinations(List<DED> deds, Scenario scenario) {
        List<List<Dependency>> lists = new ArrayList<List<Dependency>>();
        for (DED ded : deds) {
            lists.add(ded.getAssociatedDependencies());
        }
        List<List<Dependency>> result = new GenericListGenerator<Dependency>().generateListsOfElements(lists);
        List<GreedyDEDScenario> scenarios = new ArrayList<GreedyDEDScenario>();
        for (int i = 0; i < result.size(); i++) {
            List<Dependency> combinations = result.get(i);
            GreedyDEDScenario greedyScenario = generateScenario(deds, combinations, i);
            scenarios.add(greedyScenario);
        }
        if (scenario.getConfiguration().isChaseDEDGreedyRandomScenarios()) {
            Collections.shuffle(scenarios);
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
