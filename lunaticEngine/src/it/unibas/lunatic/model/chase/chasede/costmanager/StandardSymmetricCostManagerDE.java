package it.unibas.lunatic.model.chase.chasede.costmanager;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.ChangeDescription;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.EGDEquivalenceClassTuple;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClassForSymmetricEGD;
import it.unibas.lunatic.model.chase.chasemc.Repair;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClassForEGDProxy;
import it.unibas.lunatic.model.chase.chasemc.costmanager.CostManagerUtility;
import it.unibas.lunatic.model.chase.chasemc.costmanager.ICostManager;
import it.unibas.lunatic.model.chase.chasemc.operators.IOccurrenceHandler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StandardSymmetricCostManagerDE implements ICostManager {

    private static Logger logger = LoggerFactory.getLogger(StandardSymmetricCostManagerDE.class);

    @SuppressWarnings("unchecked")
    public List<Repair> chooseRepairStrategy(EquivalenceClassForEGDProxy equivalenceClassProxy, DeltaChaseStep chaseTreeRoot,
            List<Repair> repairsForDependency, Scenario scenario, String stepId, IOccurrenceHandler occurrenceHandler) {
        EquivalenceClassForSymmetricEGD equivalenceClass = (EquivalenceClassForSymmetricEGD) equivalenceClassProxy.getEquivalenceClass();
        if (logger.isInfoEnabled()) logger.info("Chasing dependency " + equivalenceClass.getEGD().getId() + " with cost manager " + this.getClass().getSimpleName() + " and partial order " + scenario.getPartialOrder().getClass().getSimpleName());
        if (logger.isDebugEnabled()) logger.debug("########Current node: " + stepId);
        if (logger.isDebugEnabled()) logger.debug("########Choosing repair strategy for equivalence class: " + equivalenceClass.toLongString());
        List<Repair> result = new ArrayList<Repair>();
        Repair forwardRepair = generateSymmetricForwardRepair(equivalenceClass.getAllTupleCells(), scenario);
        result.add(forwardRepair);
        return result;
    }

    private Repair generateSymmetricForwardRepair(List<EGDEquivalenceClassTuple> forwardTuples, Scenario scenario) {
        Repair repair = new Repair();
        ChangeDescription forwardChanges = generateChangeDescriptionForSymmetricForwardRepair(forwardTuples, scenario);
        if (logger.isDebugEnabled()) logger.debug("Forward changes: " + forwardChanges);
        repair.addViolationContext(forwardChanges);
        return repair;
    }

    private ChangeDescription generateChangeDescriptionForSymmetricForwardRepair(Collection<EGDEquivalenceClassTuple> forwardTupleGroups, Scenario scenario) {
        List<CellGroup> cellGroups = CostManagerUtility.extractForwardCellGroups(forwardTupleGroups);
        // give preference to the script partial order, that may have additional rules to solve the violation
        CellGroup lub = CostManagerUtility.getLUB(cellGroups, scenario);
        ChangeDescription changeSet = new ChangeDescription(lub, LunaticConstants.CHASE_FORWARD);
        return changeSet;
    }

    @Override
    public String toString() {
        return "StandardDE";
    }

}
