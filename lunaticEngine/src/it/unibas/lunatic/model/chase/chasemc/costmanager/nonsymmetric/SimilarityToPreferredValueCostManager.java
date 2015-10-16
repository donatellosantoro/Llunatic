package it.unibas.lunatic.model.chase.chasemc.costmanager.nonsymmetric;

import it.unibas.lunatic.model.chase.chasemc.costmanager.CostManagerUtility;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import it.unibas.lunatic.model.chase.chasemc.Repair;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClassForEGD;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClassForEGDProxy;
import it.unibas.lunatic.model.chase.chasemc.ViolationContext;
import it.unibas.lunatic.model.chase.chasemc.costmanager.CellGroupScore;
import it.unibas.lunatic.model.chase.chasemc.costmanager.ICostManager;
import it.unibas.lunatic.model.chase.chasemc.operators.CheckSatisfactionAfterUpgradesEGD;
import it.unibas.lunatic.model.chase.chasemc.operators.OccurrenceHandlerMC;
import it.unibas.lunatic.model.chase.chasemc.partialorder.FrequencyPartialOrder;
import it.unibas.lunatic.model.dependency.Dependency;
import speedy.model.database.IValue;
import it.unibas.lunatic.utility.DependencyUtility;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.LLUNValue;
import speedy.model.database.NullValue;
import speedy.model.database.TupleOID;
import speedy.utility.SpeedyUtility;

public class SimilarityToPreferredValueCostManager implements ICostManager {

    private static Logger logger = LoggerFactory.getLogger(SimilarityToPreferredValueCostManager.class);
    private CheckSatisfactionAfterUpgradesEGD satisfactionChecker = new CheckSatisfactionAfterUpgradesEGD();

    @SuppressWarnings("unchecked")
    public List<Repair> chooseRepairStrategy(EquivalenceClassForEGDProxy equivalenceClassProxy, DeltaChaseStep chaseTreeRoot,
            List<Repair> repairsForDependency, Scenario scenario, String stepId,
            OccurrenceHandlerMC occurrenceHandler) {
        if (!(scenario.getPartialOrder() instanceof FrequencyPartialOrder)) {
            logger.warn("#### SimilarityToPreferredValueCostManager is usually used with a FrequencyPartialOrder ####");
        }
        EquivalenceClassForEGD equivalenceClass = (EquivalenceClassForEGD) equivalenceClassProxy.getEquivalenceClass();
        if (logger.isInfoEnabled()) logger.info("Chasing dependency " + equivalenceClass.getEGD().getId() + " with cost manager " + this.getClass().getSimpleName() + " and partial order " + scenario.getPartialOrder().getClass().getSimpleName());
        if (logger.isTraceEnabled()) logger.trace("######## Current node: " + chaseTreeRoot.toStringWithSort());
        if (logger.isInfoEnabled()) logger.info("######## Choosing repair strategy for equivalence class: " + equivalenceClass);
        List<CellGroup> conclusionCellGroups = equivalenceClass.getAllConclusionCellGroups();
        if (DependencyUtility.hasSourceSymbols(equivalenceClass.getEGD()) && satisfactionChecker.isSatisfiedAfterUpgrades(conclusionCellGroups)) {
            return Collections.EMPTY_LIST;
        }
        List<CellGroup> forwardCellGroups = equivalenceClass.getAllConclusionCellGroups();
        IValue preferredValue = CostManagerUtility.findPreferredValue(forwardCellGroups, scenario);
        if (isDebug(equivalenceClass)) logger.info("Preferred values: " + preferredValue);
        Repair repair;
        if (preferredValue instanceof LLUNValue || preferredValue instanceof NullValue) {
            List<ViolationContext> forwardContexts = equivalenceClass.getViolationContexts();
            repair = CostManagerUtility.generateStandardForwardRepair(forwardContexts, scenario);
        } else {
            repair = generateRepairForConstantPreferredValue(preferredValue, equivalenceClass, scenario);
        }
        if (logger.isInfoEnabled()) logger.info("Returning repair " + repair);
        return new ArrayList<Repair>(Arrays.asList(new Repair[]{repair}));
    }

    private boolean isDebug(EquivalenceClassForEGD equivalenceClass) {
//        for (IValue conclusionValue : equivalenceClass.getAllConclusionValues()) {
//            if (conclusionValue.toString().equals("FORDYCE-*")) {
//                return true;
//            }
//        }
        return false;
    }

    private Repair generateRepairForConstantPreferredValue(IValue preferredValue, EquivalenceClassForEGD equivalenceClass, Scenario scenario) {
        Set<IValue> forwardValues = CostManagerUtility.findForwardValues(preferredValue, equivalenceClass.getAllConclusionValues(), scenario.getCostManagerConfiguration());
        Set<TupleOID> forwardTupleOIDs = extractTupleOIDs(forwardValues, equivalenceClass);
        boolean debug = isDebug(equivalenceClass);
        if (debug) logger.info("Forward values: " + forwardValues);
        if (logger.isDebugEnabled()) logger.debug("Forward values: " + forwardValues);
        if (logger.isDebugEnabled()) logger.debug("Forward tuple oids: " + forwardTupleOIDs);
        Map<CellGroup, Set<ViolationContext>> backwardCellGroupsMap = new HashMap<CellGroup, Set<ViolationContext>>();
        for (ViolationContext violationContext : equivalenceClass.getViolationContexts()) {
            handleViolationContext(violationContext, forwardValues, forwardTupleOIDs, backwardCellGroupsMap, equivalenceClass);
        }
        if (debug) logger.info("Backward groups map: " + SpeedyUtility.printMap(backwardCellGroupsMap));
        Set<ViolationContext> backwardContextsToHandle = addAllContexts(backwardCellGroupsMap);
        List<ViolationContext> forwardContexts = extractForwardContext(backwardContextsToHandle, equivalenceClass);
        List<CellGroup> backwardCellGroups = findBackwardCellGroupsToChange(backwardContextsToHandle, backwardCellGroupsMap, scenario);
        Repair repair = CostManagerUtility.generateRepairWithBackwards(forwardContexts, backwardCellGroups, new ArrayList<ViolationContext>(backwardContextsToHandle), scenario);
        return repair;
    }

    @SuppressWarnings("unchecked")
    private List<CellGroup> findBackwardCellGroupsToChange(Set<ViolationContext> backwardContextsToHandle, Map<CellGroup, Set<ViolationContext>> backwardCellGroupsMap, Scenario scenario) {
        Set<ViolationContext> handledContexts = new HashSet<ViolationContext>();
        List<CellGroup> backwardCellGroups = new ArrayList<CellGroup>();
        if (logger.isDebugEnabled()) logger.debug("Finding backward cell groups to change " + backwardContextsToHandle.size() + " backward contexts");
//        int iterations = 0;
        while (handledContexts.size() < backwardContextsToHandle.size()) {
            List<CellGroupScore> cellGroupScores = buildCellGroupScores(backwardCellGroupsMap, handledContexts, scenario);
            Collections.sort(cellGroupScores);
            CellGroupScore maxScore = cellGroupScores.get(0);
            handledContexts.addAll(maxScore.getViolationContexts());
            backwardCellGroups.add(maxScore.getCellGroup());
//            iterations++;
//            if (iterations > backwardContextsToHandle.size()) {
//                throw new IllegalArgumentException("Unable to find backward cellgroups to change for backward contexts \n" + SpeedyUtility.printCollection(backwardContextsToHandle, "\t") + "\n Handled contexts: \n" + SpeedyUtility.printCollection(handledContexts, "\t") + "\n CellGroupScores: " + SpeedyUtility.printCollection(cellGroupScores, "\t"));
//            }
            if (logger.isDebugEnabled()) logger.debug("So far handled " + handledContexts.size() + " backward contexts");
        }
        return backwardCellGroups;
    }

    private List<ViolationContext> extractForwardContext(Collection<ViolationContext> backwardContexts, EquivalenceClassForEGD equivalenceClass) {
        List<ViolationContext> result = new ArrayList<ViolationContext>(equivalenceClass.getViolationContexts());
        result.removeAll(backwardContexts);
        return result;
    }

    private Set<TupleOID> extractTupleOIDs(Set<IValue> values, EquivalenceClassForEGD equivalenceClass) {
        Set<TupleOID> result = new HashSet<TupleOID>();
        for (IValue value : values) {
            Set<CellGroup> cellGroups = equivalenceClass.getCellGroupsForValue(value);
            for (CellGroup cellGroup : cellGroups) {
                for (CellGroupCell occurrence : cellGroup.getOccurrences()) {
                    result.add(occurrence.getTupleOID());
                }
            }
        }
        return result;
    }

    private void handleViolationContext(ViolationContext violationContext, Set<IValue> forwardValues, Set<TupleOID> forwardTupleOIDs, Map<CellGroup, Set<ViolationContext>> backwardCellGroupsMap, EquivalenceClassForEGD equivalenceClass) {
        for (CellGroup conclusionGroup : violationContext.getAllConclusionGroups()) {
            IValue conclusionValue = conclusionGroup.getValue();
            if (forwardValues.contains(conclusionValue)) {
                continue;
            }
            List<CellGroup> backwardCellGroups = extractPossibleBackwardCellGroups(violationContext, forwardTupleOIDs, equivalenceClass);
            if (backwardCellGroups.isEmpty()) {
                continue;
            }
            for (CellGroup backwardCellGroup : backwardCellGroups) {
                Set<ViolationContext> contexts = backwardCellGroupsMap.get(backwardCellGroup);
                if (contexts == null) {
                    contexts = new HashSet<ViolationContext>();
                    backwardCellGroupsMap.put(backwardCellGroup, contexts);
                }
                contexts.add(violationContext);
            }
        }
    }

    private List<CellGroup> extractPossibleBackwardCellGroups(ViolationContext violationContext, Set<TupleOID> forwardTupleOIDs, EquivalenceClassForEGD equivalenceClass) {
        boolean debug = isDebug(equivalenceClass);
        List<CellGroup> result = new ArrayList<CellGroup>();
        for (CellGroup witnessCellGroup : violationContext.getAllWitnessCellGroups()) {
            if (CostManagerUtility.hasOccurrencesInTupleOIDs(witnessCellGroup, forwardTupleOIDs)) {
                continue;
            }
            if (!CostManagerUtility.canDoBackwardOnGroup(witnessCellGroup, violationContext)) {
                if (debug) logger.error("Cannot do backward on group " + witnessCellGroup + " for context " + violationContext);
                continue;
            }
            result.add(witnessCellGroup);
        }
        return result;
    }

    private Set<ViolationContext> addAllContexts(Map<CellGroup, Set<ViolationContext>> backwardCellGroupsMap) {
        Set<ViolationContext> result = new HashSet<ViolationContext>();
        for (Set<ViolationContext> value : backwardCellGroupsMap.values()) {
            result.addAll(value);
        }
        return result;
    }

    private List<CellGroupScore> buildCellGroupScores(Map<CellGroup, Set<ViolationContext>> backwardCellGroupsMap, Set<ViolationContext> handledContexts, Scenario scenario) {
        List<CellGroupScore> result = new ArrayList<CellGroupScore>();
        for (CellGroup cellGroup : backwardCellGroupsMap.keySet()) {
            Set<ViolationContext> contexts = backwardCellGroupsMap.get(cellGroup);
            contexts.removeAll(handledContexts);
            Set<Dependency> affectedDependencies = CostManagerUtility.findAffectedDependencies(cellGroup, scenario);
            result.add(new CellGroupScore(cellGroup, contexts, affectedDependencies));
        }
        return result;
    }

    @Override
    public String toString() {
        return "Similarity To Most Frequent";
    }

}
