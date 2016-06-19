package it.unibas.lunatic.model.chase.chasemc.costmanager.nonsymmetric;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClassForEGD;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClassForEGDProxy;
import it.unibas.lunatic.model.chase.chasemc.Repair;
import it.unibas.lunatic.model.chase.chasemc.ViolationContext;
import it.unibas.lunatic.model.chase.chasemc.costmanager.CostManagerUtility;
import it.unibas.lunatic.model.chase.chasemc.operators.CheckSatisfactionAfterUpgradesEGD;
import it.unibas.lunatic.model.chase.chasemc.partialorder.GreedyPartialOrder;
import it.unibas.lunatic.model.dependency.operators.DependencyUtility;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.unibas.lunatic.model.chase.chasemc.costmanager.ICostManagerMC;
import it.unibas.lunatic.model.chase.chasemc.operators.OccurrenceHandlerMC;

public class GreedyCostManager implements ICostManagerMC {

    private static final Logger logger = LoggerFactory.getLogger(GreedyCostManager.class.getName());
    private final CheckSatisfactionAfterUpgradesEGD satisfactionChecker = new CheckSatisfactionAfterUpgradesEGD();

    @SuppressWarnings("unchecked")
    public List<Repair> chooseRepairStrategy(EquivalenceClassForEGDProxy equivalenceClassProxy, DeltaChaseStep chaseTreeRoot, List<Repair> repairsForDependency, Scenario scenario, String stepId, OccurrenceHandlerMC occurrenceHandler) {
        assert (scenario.getPartialOrder() instanceof GreedyPartialOrder && scenario.getScriptPartialOrder() == null) : "The Greedy cost manager requires a Greedy partial order " + scenario;
        assert (scenario.getCostManagerConfiguration().isDoPermutations() == false) : "No permutations allowed in greedy repair cost manager " + scenario;
        assert (scenario.getCostManagerConfiguration().isDoBackwardForAllDependencies() == false) : "No backward allowed in greedy repair cost manager " + scenario;
        EquivalenceClassForEGD equivalenceClass = (EquivalenceClassForEGD) equivalenceClassProxy.getEquivalenceClass();
        if (logger.isInfoEnabled()) logger.info("Chasing dependency " + equivalenceClass.getEGD().getId() + " with cost manager " + this.getClass().getSimpleName() + " and partial order " + scenario.getPartialOrder().getClass().getSimpleName() + " in step " + stepId);
        if (logger.isTraceEnabled()) logger.trace("######## Current node: " + chaseTreeRoot.toStringWithSort());
        if (logger.isInfoEnabled()) logger.info("######## Choosing repair strategy for equivalence class: " + equivalenceClass);
        List<CellGroup> conclusionCellGroups = equivalenceClass.getAllConclusionCellGroups();
        if (DependencyUtility.hasSourceSymbols(equivalenceClass.getEGD()) && satisfactionChecker.isSatisfiedAfterUpgrades(conclusionCellGroups)) {
            if (logger.isDebugEnabled()) logger.debug("Dependency " + equivalenceClass.getEGD() + " is satisfied after upgrades in step " + stepId);
            return Collections.EMPTY_LIST;
        }
        List<ViolationContext> forwardContexts = equivalenceClass.getViolationContexts();
        Repair forwardRepair = CostManagerUtility.generateForwardRepair(forwardContexts, scenario);
        if (logger.isInfoEnabled()) logger.info("Returning repair " + forwardRepair);
        return new ArrayList<Repair>(Arrays.asList(new Repair[]{forwardRepair}));
    }

}
