package it.unibas.lunatic.model.chase.chasede.operators;

import it.unibas.lunatic.LunaticConfiguration;
import it.unibas.lunatic.OperatorFactory;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.algebra.operators.BuildAlgebraTreeForStandardChase;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.chase.commons.operators.IChaseSTTGDs;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.operators.DependencyUtility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.database.IDatabase;
import speedy.model.database.operators.IDatabaseManager;
import speedy.utility.comparator.StringComparator;

public class ChaseSTTGDsRestrictedFreshNulls implements IChaseSTTGDs {

    private final static Logger logger = LoggerFactory.getLogger(ChaseSTTGDsRestrictedFreshNulls.class);
    private BuildAlgebraTreeForStandardChase treeBuilderForStandardChase = new BuildAlgebraTreeForStandardChase();
    private IDatabaseManager databaseManager;

    public ChaseSTTGDsRestrictedFreshNulls(IDatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void doChase(Scenario scenario, boolean cleanTarget) {
        long start = new Date().getTime();
        IDatabase source = scenario.getSource();
        IDatabase target = scenario.getTarget();
        databaseManager.initDatabase(source, target, cleanTarget, scenario.getConfiguration().isPreventInsertDuplicateTuples());
        if (scenario.getSTTgds().isEmpty()) {
            return;
        }
        if (LunaticConfiguration.isPrintSteps()) System.out.println("****Chasing scenario for s-t tgds...");
        List<Dependency> stTGDs = scenario.getSTTgds();
        Map<Dependency, IAlgebraOperator> treeMap = buildAlgebraTrees(stTGDs, scenario);
        Set<Dependency> unsatisfiedSTTGDs = new HashSet<Dependency>(stTGDs);
        while (!unsatisfiedSTTGDs.isEmpty()) {
            Dependency stTGD = pickNextDependency(unsatisfiedSTTGDs, scenario);
            if (logger.isDebugEnabled()) logger.debug("Executing ST-TGD " + stTGD.getId());
            IAlgebraOperator treeRoot = treeMap.get(stTGD);
            IInsertFromSelectNaive naiveInsert = OperatorFactory.getInstance().getInsertFromSelectNaive(scenario);
            boolean newTuples = naiveInsert.execute(stTGD, treeRoot, source, target, scenario);
            if (!newTuples || !DependencyUtility.hasUniversalVariablesInConclusion(stTGD)) {
                if (logger.isDebugEnabled()) logger.debug("No new tuples. ST-TGD " + stTGD + " is satisfied.");
                unsatisfiedSTTGDs.remove(stTGD);
            }
        }
        long end = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.STTGD_TIME, end - start);
        if (LunaticConfiguration.isPrintSteps()) System.out.println("****Chase for s-t tgds completed in " + (end - start) + "ms");
    }

    private Map<Dependency, IAlgebraOperator> buildAlgebraTrees(List<Dependency> extTGDs, Scenario scenario) {
        Map<Dependency, IAlgebraOperator> result = new HashMap<Dependency, IAlgebraOperator>();
        for (Dependency dependency : extTGDs) {
            IAlgebraOperator standardInsert = treeBuilderForStandardChase.generateAlgebraForSourceToTargetTGD(dependency, scenario);
            if (logger.isDebugEnabled()) logger.debug("Operator for dependency " + dependency + "\n" + standardInsert);
            result.put(dependency, standardInsert);
        }
        return result;
    }

    private Dependency pickNextDependency(Set<Dependency> unsatisfiedSTTGDs, Scenario scenario) {
        List<Dependency> sortedList = new ArrayList<Dependency>(unsatisfiedSTTGDs);
        Collections.sort(sortedList, new StringComparator());
        //TODO: Add strategies
        //First one
//        return sortedList.get(0);
//        Last one
//        return sortedList.get(sortedList.size() - 1);
        //Random
        return new ArrayList<Dependency>(unsatisfiedSTTGDs).get(new Random().nextInt(unsatisfiedSTTGDs.size()));
    }

}
