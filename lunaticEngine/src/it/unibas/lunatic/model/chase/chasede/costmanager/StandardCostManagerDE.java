package it.unibas.lunatic.model.chase.chasede.costmanager;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.model.chase.chasemc.costmanager.CostManagerUtility;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.ChangeDescription;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.Repair;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClassForEGD;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClassForEGDProxy;
import it.unibas.lunatic.model.chase.chasemc.ViolationContext;
import it.unibas.lunatic.model.chase.chasemc.costmanager.ICostManager;
import it.unibas.lunatic.model.chase.chasemc.operators.IOccurrenceHandler;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StandardCostManagerDE implements ICostManager {

    private static Logger logger = LoggerFactory.getLogger(StandardCostManagerDE.class);

    @SuppressWarnings("unchecked")
    public List<Repair> chooseRepairStrategy(EquivalenceClassForEGDProxy equivalenceClassProxy, DeltaChaseStep chaseTreeRoot,
            List<Repair> repairsForDependency, Scenario scenario, String stepId, IOccurrenceHandler occurrenceHandler) {
        EquivalenceClassForEGD equivalenceClass = (EquivalenceClassForEGD) equivalenceClassProxy.getEquivalenceClass();
        if (logger.isInfoEnabled()) logger.info("Chasing dependency " + equivalenceClass.getEGD().getId() + " with cost manager " + this.getClass().getSimpleName() + " and partial order " + scenario.getPartialOrder().getClass().getSimpleName() + " in step " + stepId);
        if (logger.isDebugEnabled()) logger.debug("######## Current node: " + chaseTreeRoot.toStringWithSort());
        if (logger.isDebugEnabled()) logger.debug("######## Choosing repair strategy for equivalence class: " + equivalenceClass);
        List<Repair> result = new ArrayList<Repair>();
        List<ViolationContext> allContexts = equivalenceClass.getViolationContexts();
        Repair forwardRepair = generateForwardRepair(allContexts, scenario);
        result.add(forwardRepair);
        return result;
    }

    private Repair generateForwardRepair(List<ViolationContext> forwardContexts, Scenario scenario) {
        Repair repair = new Repair();
        List<CellGroup> forwardCellGroups = extractConclusionCellGroupsFromContexts(forwardContexts);
        CellGroup lub = CostManagerUtility.getLUB(forwardCellGroups, scenario);
        ChangeDescription forwardChanges = new ChangeDescription(lub, LunaticConstants.CHASE_FORWARD);
        if (logger.isDebugEnabled()) logger.debug("Forward changes: " + forwardChanges);
        repair.addViolationContext(forwardChanges);
        return repair;
    }

    private List<CellGroup> extractConclusionCellGroupsFromContexts(List<ViolationContext> contexts) {
        Set<CellGroup> result = new HashSet<CellGroup>();
        for (ViolationContext context : contexts) {
            result.addAll(context.getAllConclusionGroups());
        }
        return new ArrayList<CellGroup>(result);
    }

    @Override
    public String toString() {
        return "StandardDE";
    }

}
