package it.unibas.lunatic.model.chase.chasemc.costmanager.symmetric;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.EGDEquivalenceClassTuple;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClassForEGDProxy;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClassForSymmetricEGD;
import it.unibas.lunatic.model.chase.chasemc.Repair;
import it.unibas.lunatic.model.chase.chasemc.costmanager.CostManagerUtility;
import it.unibas.lunatic.model.chase.chasemc.costmanager.ICostManager;
import it.unibas.lunatic.model.chase.chasemc.operators.OccurrenceHandlerMC;
import it.unibas.lunatic.model.chase.chasemc.partialorder.GreedyPartialOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GreedySymmetricCostManager implements ICostManager {

    private static final Logger logger = LoggerFactory.getLogger(GreedySymmetricCostManager.class.getName());

    public List<Repair> chooseRepairStrategy(EquivalenceClassForEGDProxy equivalenceClassProxy, DeltaChaseStep chaseTreeRoot, List<Repair> repairsForDependency, Scenario scenario, String stepId, OccurrenceHandlerMC occurrenceHandler) {
        assert (scenario.getPartialOrder() instanceof GreedyPartialOrder && scenario.getScriptPartialOrder() == null) : "The Greedy cost manager requires a Greedy partial order " + scenario;
        assert (scenario.getCostManagerConfiguration().isDoPermutations() == false) : "No permutations allowed in greedy repair cost manager " + scenario;
        assert (scenario.getCostManagerConfiguration().isDoBackwardForAllDependencies() == false) : "No backward allowed in greedy repair cost manager " + scenario;
        EquivalenceClassForSymmetricEGD equivalenceClass = (EquivalenceClassForSymmetricEGD) equivalenceClassProxy.getEquivalenceClass();
        if (logger.isInfoEnabled()) logger.info("Chasing dependency " + equivalenceClass.getEGD().getId() + " with cost manager " + this.getClass().getSimpleName() + " and partial order " + scenario.getPartialOrder().getClass().getSimpleName() + " in step " + stepId);
        if (logger.isTraceEnabled()) logger.trace("######## Current node: " + chaseTreeRoot.toStringWithSort());
        if (logger.isInfoEnabled()) logger.info("######## Choosing repair strategy for equivalence class: " + equivalenceClass);
        List<EGDEquivalenceClassTuple> tuples = equivalenceClass.getAllTupleCells();
        Repair forwardRepair = CostManagerUtility.generateSymmetricForwardRepair(tuples, scenario);
        if (logger.isInfoEnabled()) logger.info("Returning repair " + forwardRepair);
        return new ArrayList<Repair>(Arrays.asList(new Repair[]{forwardRepair}));
    }

}
