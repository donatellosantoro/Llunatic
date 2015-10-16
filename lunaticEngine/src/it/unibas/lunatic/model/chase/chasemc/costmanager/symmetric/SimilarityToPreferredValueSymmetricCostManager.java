package it.unibas.lunatic.model.chase.chasemc.costmanager.symmetric;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.BackwardAttribute;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClassForSymmetricEGD;
import it.unibas.lunatic.model.chase.chasemc.Repair;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.EGDEquivalenceClassTuple;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClassForEGDProxy;
import it.unibas.lunatic.model.chase.chasemc.ViolationContext;
import it.unibas.lunatic.model.chase.chasemc.costmanager.CellGroupScore;
import it.unibas.lunatic.model.chase.chasemc.costmanager.CostManagerUtility;
import it.unibas.lunatic.model.chase.chasemc.costmanager.ICostManager;
import it.unibas.lunatic.model.chase.chasemc.operators.OccurrenceHandlerMC;
import it.unibas.lunatic.model.chase.chasemc.partialorder.FrequencyPartialOrder;
import speedy.model.database.IValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.LLUNValue;
import speedy.model.database.NullValue;
import speedy.utility.SpeedyUtility;

public class SimilarityToPreferredValueSymmetricCostManager implements ICostManager {

    private static Logger logger = LoggerFactory.getLogger(SimilarityToPreferredValueSymmetricCostManager.class);

    @SuppressWarnings("unchecked")
    public List<Repair> chooseRepairStrategy(EquivalenceClassForEGDProxy equivalenceClassProxy, DeltaChaseStep chaseTreeRoot,
            List<Repair> repairsForDependency, Scenario scenario, String stepId,
            OccurrenceHandlerMC occurrenceHandler) {
        if (!(scenario.getPartialOrder() instanceof FrequencyPartialOrder)) {
            logger.warn("#### SimilarityToPreferredValueCostManager is usually used with a FrequencyPartialOrder ####");
        }
        EquivalenceClassForSymmetricEGD equivalenceClass = (EquivalenceClassForSymmetricEGD) equivalenceClassProxy.getEquivalenceClass();
        if (logger.isDebugEnabled()) logger.debug("Chasing dependency " + equivalenceClass.getEGD().getId() + " with cost manager " + this.getClass().getSimpleName() + " and partial order " + scenario.getPartialOrder().getClass().getSimpleName());
        if (logger.isInfoEnabled()) logger.info("######## Choosing repair strategy for equivalence class: " + equivalenceClass);
        List<EGDEquivalenceClassTuple> tuples = equivalenceClass.getAllTupleCells();
        List<CellGroup> forwardCellGroups = CostManagerUtility.extractForwardCellGroups(tuples);
        IValue preferredValue = CostManagerUtility.findPreferredValue(forwardCellGroups, scenario);
        if (isDebug(equivalenceClass)) logger.info("Preferred values: " + preferredValue);
        logger.info("Preferred values: " + preferredValue);
        Repair repair;
        if (preferredValue instanceof LLUNValue || preferredValue instanceof NullValue) {
            repair = CostManagerUtility.generateSymmetricForwardRepair(tuples, scenario);
        } else {
            repair = generateRepairForConstantPreferredValue(preferredValue, equivalenceClass, scenario);
        }
        if (logger.isInfoEnabled()) logger.info("Returning repair " + repair);
        return new ArrayList<Repair>(Arrays.asList(new Repair[]{repair}));
    }

    private boolean isDebug(EquivalenceClassForSymmetricEGD equivalenceClass) {
//        for (IValue conclusionValue : equivalenceClass.getAllConclusionValues()) {
//            if (conclusionValue.toString().equals("FORDYCE-*")) {
//                return true;
//            }
//        }
        return false;
    }

    private Repair generateRepairForConstantPreferredValue(IValue preferredValue, EquivalenceClassForSymmetricEGD equivalenceClass, Scenario scenario) {
        Set<IValue> forwardValues = CostManagerUtility.findForwardValues(preferredValue, equivalenceClass.getAllConclusionValues(), scenario.getCostManagerConfiguration());
        List<EGDEquivalenceClassTuple> forwardTuples = extractForwardTuples(forwardValues, equivalenceClass);
        boolean debug = isDebug(equivalenceClass);
        if (debug) logger.info("Forward values: " + forwardValues);
        if (logger.isDebugEnabled()) logger.debug("Forward values: " + forwardValues);
        if (logger.isDebugEnabled()) logger.debug("Forward tuples: " + forwardTuples);
        Map<CellGroup, Set<EGDEquivalenceClassTuple>> backwardCellGroupsMap = new HashMap<CellGroup, Set<EGDEquivalenceClassTuple>>();
        for (EGDEquivalenceClassTuple tuple : equivalenceClass.getAllTupleCells()) {
            handleTuple(tuple, forwardTuples, backwardCellGroupsMap, equivalenceClass);
        }
        if (debug) logger.info("Backward groups map: " + SpeedyUtility.printMap(backwardCellGroupsMap));
        Set<EGDEquivalenceClassTuple> backwardTuplesToHandle = addAllTuples(backwardCellGroupsMap);
        List<CellGroup> backwardCellGroups = findBackwardCellGroupsToChange(backwardTuplesToHandle, backwardCellGroupsMap);
        Repair repair = CostManagerUtility.generateSymmetricRepairWithBackwards(forwardTuples, backwardTuplesToHandle, backwardCellGroups, scenario);
        return repair;
    }

    @SuppressWarnings("unchecked")
    private List<CellGroup> findBackwardCellGroupsToChange(Set<EGDEquivalenceClassTuple> backwardTuplesToHandle, Map<CellGroup, Set<EGDEquivalenceClassTuple>> backwardCellGroupsMap) {
        Set<EGDEquivalenceClassTuple> handledTuples = new HashSet<EGDEquivalenceClassTuple>();
        List<CellGroup> backwardCellGroups = new ArrayList<CellGroup>();
        if (logger.isDebugEnabled()) logger.debug("Finding backward cell groups to change " + backwardTuplesToHandle.size() + " backward contexts");
        int iterations = 0;
        while (handledTuples.size() < backwardTuplesToHandle.size()) {
            List<CellGroupScore> cellGroupScores = buildCellGroupScores(backwardCellGroupsMap, handledTuples);
            Collections.sort(cellGroupScores);
            CellGroupScore maxScore = cellGroupScores.get(0);
            handledTuples.addAll(maxScore.getViolationContexts());
            backwardCellGroups.add(maxScore.getCellGroup());
            iterations++;
            if (iterations > backwardTuplesToHandle.size()) {
                throw new IllegalArgumentException("Unable to find backward cellgroups to change for backward contexts \n" + SpeedyUtility.printCollection(backwardTuplesToHandle, "\t") + "\n Handled contexts: \n" + SpeedyUtility.printCollection(handledTuples, "\t") + "\n CellGroupScores: " + SpeedyUtility.printCollection(cellGroupScores, "\t"));
            }
            if (logger.isDebugEnabled()) logger.debug("So far handled " + handledTuples.size() + " backward contexts");
        }
        return backwardCellGroups;
    }

    private List<CellGroupScore> buildCellGroupScores(Map<CellGroup, Set<EGDEquivalenceClassTuple>> backwardCellGroupsMap, Set<EGDEquivalenceClassTuple> handledTuples) {
        List<CellGroupScore> result = new ArrayList<CellGroupScore>();
        for (CellGroup cellGroup : backwardCellGroupsMap.keySet()) {
            Set<EGDEquivalenceClassTuple> contexts = backwardCellGroupsMap.get(cellGroup);
            contexts.removeAll(handledTuples);
            result.add(new CellGroupScore(cellGroup, contexts));
        }
        return result;
    }

    private List<EGDEquivalenceClassTuple> extractForwardTuples(Set<IValue> forwardValues, EquivalenceClassForSymmetricEGD equivalenceClass) {
        List<EGDEquivalenceClassTuple> result = new ArrayList<EGDEquivalenceClassTuple>();
        for (EGDEquivalenceClassTuple tuple : equivalenceClass.getAllTupleCells()) {
            if (forwardValues.contains(tuple.getConclusionGroup().getValue())) {
                result.add(tuple);
            }
        }
        return result;
    }

    private void handleTuple(EGDEquivalenceClassTuple tuple, List<EGDEquivalenceClassTuple> forwardTuples, Map<CellGroup, Set<EGDEquivalenceClassTuple>> backwardCellGroupsMap, EquivalenceClassForSymmetricEGD equivalenceClass) {
        if (forwardTuples.contains(tuple)) {
            return;
        }
        List<CellGroup> backwardCellGroups = extractPossibleBackwardCellGroups(tuple, forwardTuples, equivalenceClass);
        if (backwardCellGroups.isEmpty()) {
            forwardTuples.add(tuple);
            return;
        }
        for (CellGroup backwardCellGroup : backwardCellGroups) {
            Set<EGDEquivalenceClassTuple> tuples = backwardCellGroupsMap.get(backwardCellGroup);
            if (tuples == null) {
                tuples = new HashSet<EGDEquivalenceClassTuple>();
                backwardCellGroupsMap.put(backwardCellGroup, tuples);
            }
            tuples.add(tuple);
        }
    }

    private List<CellGroup> extractPossibleBackwardCellGroups(EGDEquivalenceClassTuple tuple, List<EGDEquivalenceClassTuple> forwardTuples, EquivalenceClassForSymmetricEGD equivalenceClass) {
        boolean debug = isDebug(equivalenceClass);
        List<CellGroup> result = new ArrayList<CellGroup>();
        for (CellGroup witnessCellGroup : tuple.getBackwardCellGroups()) {
            if (hasOccurrencesInForwardTuples(witnessCellGroup, forwardTuples)) {
                continue;
            }
            if (!CostManagerUtility.backwardIsAllowed(witnessCellGroup)) {
                if (debug) logger.error("Cannot do backward on group " + witnessCellGroup + " for tuple " + tuple);
                continue;
            }
            result.add(witnessCellGroup);
        }
        return result;
    }

    //Changing backward this cell group will also change a forward tuple, and therefore a conflict remains (was suspicious)
    private boolean hasOccurrencesInForwardTuples(CellGroup witnessCellGroup, List<EGDEquivalenceClassTuple> forwardTuples) {
        for (CellGroupCell occurrence : witnessCellGroup.getOccurrences()) {
            for (EGDEquivalenceClassTuple tuple : forwardTuples) {
                if (occurrence.getTupleOID().equals(tuple.getTupleOID())) {
                    return true;
                }
            }
        }
        return false;
    }

    private Set<EGDEquivalenceClassTuple> addAllTuples(Map<CellGroup, Set<EGDEquivalenceClassTuple>> backwardCellGroupsMap) {
        Set<EGDEquivalenceClassTuple> result = new HashSet<EGDEquivalenceClassTuple>();
        for (Set<EGDEquivalenceClassTuple> value : backwardCellGroupsMap.values()) {
            result.addAll(value);
        }
        return result;
    }

    @Override
    public String toString() {
        return "Similarity To Most Frequent";
    }

}
