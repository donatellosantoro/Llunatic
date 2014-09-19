package it.unibas.lunatic.model.chase.chasede.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.chase.commons.ChaseUtility;
import it.unibas.lunatic.model.chase.commons.IChaseSTTGDs;
import it.unibas.lunatic.model.chase.commons.control.IChaseState;
import it.unibas.lunatic.model.chase.commons.control.ImmutableChaseState;
import it.unibas.lunatic.model.chase.chasede.IDEChaser;
import it.unibas.lunatic.model.chase.chasemc.operators.IRunQuery;
import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.extendedegdanalysis.operators.FindSymmetricAtoms;
import it.unibas.lunatic.utility.DependencyUtility;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChaseDEScenario implements IDEChaser {

    public static final int ITERATION_LIMIT = 10;
    private static Logger logger = LoggerFactory.getLogger(ChaseDEScenario.class);
    //
    private IChaseSTTGDs stChaser;
    private ChaseExtTGDs extTgdChaser;
    private ChaseEGDs egdChaser;
    private ChaseDCs dChaser;
    private FindSymmetricAtoms symmetryFinder = new FindSymmetricAtoms();
    private ImmutableChaseState immutableChaseState = ImmutableChaseState.getInstance();

    public ChaseDEScenario(IChaseSTTGDs stChaser, IInsertFromSelectNaive naiveInsert, IRemoveDuplicates duplicateRemover,
            IValueOccurrenceHandlerDE valueOccurrenceHandler, IRunQuery queryRunner, IUpdateCell cellUpdater) {
        this.stChaser = stChaser;
        this.extTgdChaser = new ChaseExtTGDs(naiveInsert);
        this.egdChaser = new ChaseEGDs(valueOccurrenceHandler, duplicateRemover, queryRunner, cellUpdater);
        this.dChaser = new ChaseDCs(queryRunner);
    }

    public IDatabase doChase(Scenario scenario) {
        return doChase(scenario, immutableChaseState);
    }

    public IDatabase doChase(Scenario scenario, IChaseState chaseState) {
        if (!scenario.isDEScenario()) {
            throw new ChaseException("Unable to execute scenario. Only tgds and egds are allowed. " + scenario);
        }
        if (logger.isDebugEnabled()) logger.debug("Chasing dependencies on de scenario: " + scenario);
        stChaser.doChase(scenario, true);
        addQueriedAttributes(scenario.getEGDs());
        symmetryFinder.findSymmetricAtoms(scenario.getEGDs(), scenario);
        int iterations = 0;
        while (iterations < ITERATION_LIMIT) {
            if (chaseState.isCancelled()) ChaseUtility.stopChase(chaseState);
            boolean newTuples = extTgdChaser.doChase(scenario, chaseState);
            boolean cellChanges = egdChaser.doChase(scenario, chaseState);
            if (!newTuples && !cellChanges) {
                break;
            } else {
                iterations++;
            }
        }
        dChaser.doChase(scenario, chaseState);
        IDatabase target = scenario.getTarget();
        if (logger.isDebugEnabled()) logger.debug("----Result of chase: " + target);
        return target;
    }

    private void addQueriedAttributes(List<Dependency> dependencies) {
        for (Dependency dependency : dependencies) {
            List<AttributeRef> queriedAttributes = DependencyUtility.findQueriedAttributesInPremise(dependency);
            dependency.setQueriedAttributes(queriedAttributes);
        }
    }
}
