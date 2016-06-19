package it.unibas.lunatic.model.chase.chasede.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.algebra.operators.BuildAlgebraTreeForTGD;
import it.unibas.lunatic.model.chase.commons.operators.ChaseUtility;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.algebra.operators.ITupleIterator;
import speedy.model.database.IDatabase;
import speedy.model.database.operators.IRunQuery;

public class CheckUnsatisfiedDependenciesDE {

    private static Logger logger = LoggerFactory.getLogger(CheckUnsatisfiedDependenciesDE.class);

    private BuildAlgebraTreeForTGD treeBuilderForTGD = new BuildAlgebraTreeForTGD();
    private IBuildDatabaseForDE databaseBuilder;
    private final IRunQuery queryRunner;

    public CheckUnsatisfiedDependenciesDE(IBuildDatabaseForDE databaseBuilder, IRunQuery queryRunner) {
        this.databaseBuilder = databaseBuilder;
        this.queryRunner = queryRunner;
    }

    ///////        DURING CHASE
    public List<Dependency> findUnsatisfiedTGDs(DeltaChaseStep currentNode, List<Dependency> dependencies, Scenario scenario) {
        if (logger.isDebugEnabled()) logger.debug("Searching satisfied dependencies among " + LunaticUtility.printDependencyIds(dependencies));
        Map<Dependency, IAlgebraOperator> tgdTreeMap = treeBuilderForTGD.buildAlgebraTreesForTGDViolationsCheck(scenario.getExtTGDs(), scenario);
        List<Dependency> unsatisfiedDepdendencies = new ArrayList<Dependency>();
        for (Dependency dependency : dependencies) {
            IDatabase databaseForStep = databaseBuilder.extractDatabase(currentNode.getDeltaDB(), currentNode.getOriginalDB(), dependency, scenario);
//            IDatabase databaseForStep = databaseBuilder.extractDatabase(currentNode.getId(), currentNode.getDeltaDB(), currentNode.getOriginalDB());
            if (isTGDSatisfied(dependency, currentNode, tgdTreeMap, databaseForStep, scenario)) {
                if (logger.isDebugEnabled()) logger.debug("Dependency " + dependency.getId() + " is satisfied, skipping...");
                continue;
            }
            unsatisfiedDepdendencies.add(dependency);
        }
        if (logger.isDebugEnabled()) logger.info("Unsatisfied:  " + LunaticUtility.printDependencyIds(unsatisfiedDepdendencies));
        return unsatisfiedDepdendencies;
    }

    private boolean isTGDSatisfied(Dependency eTgd, DeltaChaseStep currentNode, Map<Dependency, IAlgebraOperator> tgdTreeMap, IDatabase databaseForStep, Scenario scenario) {
        IAlgebraOperator tgdQuery = tgdTreeMap.get(eTgd);
        ITupleIterator it = queryRunner.run(tgdQuery, scenario.getSource(), databaseForStep);
        boolean existsViolation = it.hasNext();
        it.close();
        if (existsViolation) {
            if (logger.isDebugEnabled()) logger.debug("TGD " + eTgd + " is violated... Node " + currentNode.getId() + " is not a solution");
            return false;
        }
        return true;
    }

    ///////        POST CHASE
    public void checkEGDSatisfactionWithQuery(DeltaChaseStep currentNode, Scenario scenario) {
        logger.debug("Checking solution with query...");
        for (Dependency egd : currentNode.getSatisfiedEGDs()) {
            IDatabase databaseForStep = databaseBuilder.extractDatabase(currentNode.getDeltaDB(), currentNode.getOriginalDB(), egd, scenario);
            boolean satisfied = isEGDSatisfiedQuery(egd, currentNode, databaseForStep, scenario);
            if (!satisfied) {
                if (scenario.isDBMS()) {
//                    logger.error("Dependency " + egd + "\nis not satisfied in node " + currentNode.toShortStringWithSort());
                    throw new ChaseException("Dependency " + egd + "\nis not satisfied in node " + currentNode.toShortStringWithSort());
                } else {
//                    logger.error("Dependency " + egd + "\nis not satisfied in node " + currentNode.toShortStringWithSort());
                    throw new ChaseException("Dependency " + egd + "\nis not satisfied in node " + currentNode.toStringWithSort() + "\nDelta db: " + currentNode.getDeltaDB());
                }
            }
        }
    }

    public List<Dependency> findUnsatisfiedEGDsQuery(DeltaChaseStep currentNode, List<Dependency> dependencies, Scenario scenario) {
        if (logger.isDebugEnabled()) logger.debug("Searching satisfied dependencies among " + LunaticUtility.printDependencyIds(dependencies));
        List<Dependency> unsatisfiedDepdendencies = new ArrayList<Dependency>();
        for (Dependency dependency : dependencies) {
            IDatabase databaseForStep = databaseBuilder.extractDatabase(currentNode.getDeltaDB(), currentNode.getOriginalDB(), dependency, scenario);
            if (isEGDSatisfiedQuery(dependency, currentNode, databaseForStep, scenario)) {
                if (logger.isDebugEnabled()) logger.debug("Dependency " + dependency.getId() + " is satisfied, skipping...");
                continue;
            }
            unsatisfiedDepdendencies.add(dependency);
        }
        if (logger.isDebugEnabled()) logger.info("Unsatisfied:  " + LunaticUtility.printDependencyIds(unsatisfiedDepdendencies));
        return unsatisfiedDepdendencies;
    }

    public boolean isEGDSatisfiedQuery(Dependency egd, DeltaChaseStep currentNode, IDatabase databaseForStep, Scenario scenario) {
        return ChaseUtility.checkEGDSatisfactionWithQuery(egd, databaseForStep, scenario);
    }

    public List<Dependency> findUnsatisfiedEGDsNoQuery(DeltaChaseStep rootNode, List<Dependency> dependencies) {
        if (logger.isDebugEnabled()) logger.debug("Searching satisfied dependencies among " + LunaticUtility.printDependencyIds(dependencies));
        List<Dependency> unsatisfiedDepdendencies = new ArrayList<Dependency>(dependencies);
        unsatisfiedDepdendencies.removeAll(rootNode.getSatisfiedEGDs());
        return unsatisfiedDepdendencies;
    }

}
