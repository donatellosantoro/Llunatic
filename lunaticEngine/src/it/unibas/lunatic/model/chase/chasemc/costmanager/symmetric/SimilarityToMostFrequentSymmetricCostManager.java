package it.unibas.lunatic.model.chase.chasemc.costmanager.symmetric;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.chase.chasemc.BackwardAttribute;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.ChangeDescription;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClassForSymmetricEGD;
import it.unibas.lunatic.model.chase.chasemc.Repair;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClassForEGDProxy;
import it.unibas.lunatic.model.chase.chasemc.costmanager.CostManagerConfiguration;
import it.unibas.lunatic.model.chase.chasemc.costmanager.TupleGroupComparator;
import it.unibas.lunatic.model.chase.chasemc.costmanager.CostManagerUtility;
import it.unibas.lunatic.model.chase.chasemc.costmanager.ICostManager;
import it.unibas.lunatic.model.chase.chasemc.operators.CellGroupIDGenerator;
import it.unibas.lunatic.model.chase.chasemc.operators.OccurrenceHandlerMC;
import it.unibas.lunatic.model.chase.chasemc.partialorder.FrequencyPartialOrder;
import it.unibas.lunatic.model.chase.chasemc.partialorder.StandardPartialOrder;
import speedy.model.database.ConstantValue;
import speedy.model.database.IDatabase;
import speedy.model.database.IValue;
import it.unibas.lunatic.utility.DependencyUtility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.Cell;
import speedy.model.database.LLUNValue;

public class SimilarityToMostFrequentSymmetricCostManager implements ICostManager {

    private static Logger logger = LoggerFactory.getLogger(SimilarityToMostFrequentSymmetricCostManager.class);

    public List<Repair> chooseRepairStrategy(EquivalenceClassForEGDProxy equivalenceClass, DeltaChaseStep chaseTreeRoot, List<Repair> repairsForDependency, Scenario scenario, String stepId, OccurrenceHandlerMC occurrenceHandler) {
        throw new UnsupportedOperationException("Not supported yet."); //TODO Implement method
    }
//    @SuppressWarnings("unchecked")
//    public List<Repair> chooseRepairStrategy(EquivalenceClassForEGDProxy equivalenceClassProxy, DeltaChaseStep chaseTreeRoot,
//            List<Repair> repairsForDependency, Scenario scenario, String stepId,
//            OccurrenceHandlerMC occurrenceHandler) {
//        if (!(scenario.getPartialOrder() instanceof FrequencyPartialOrder)) {
//            logger.warn("##################################################################################");
//            logger.warn("   SimilarityToMostFrequentCostManager should be used with FrequencyPartialOrder");
//            logger.warn("##################################################################################");
//            throw new ChaseException("SimilarityToMostFrequentCostManager requires FrequencyPartialOrder");
//        }
//        EquivalenceClassForSymmetricEGD equivalenceClass = (EquivalenceClassForSymmetricEGD) equivalenceClassProxy.getEquivalenceClass();
//        if (logger.isDebugEnabled()) logger.debug("Chasing dependency " + equivalenceClass.getEGD().getId() + " with cost manager " + this.getClass().getSimpleName() + " and partial order " + scenario.getPartialOrder().getClass().getSimpleName());
//        if (logger.isInfoEnabled()) logger.info("######## Choosing repair strategy for equivalence class: " + equivalenceClass);
//        List<EGDEquivalenceClassTupleCellsOLD> tupleGroups = equivalenceClass.getTupleGroups();
//        Collections.sort(tupleGroups, new TupleGroupComparator());
//        Collections.reverse(tupleGroups);
//        if (logger.isDebugEnabled()) logger.debug("Sorted tuple groups: " + tupleGroups);
//        if (DependencyUtility.hasSourceSymbols(equivalenceClass.getEGD()) && satisfactionChecker.isSatisfiedAfterUpgrades(tupleGroups, scenario)) {
//            if (logger.isDebugEnabled()) logger.debug("No violations... Returning empty list");
//            return Collections.EMPTY_LIST;
//        }
//        List<Repair> result = new ArrayList<Repair>();
//        Repair standardRepair = generateConstantRepairWithStandardPartialOrder(equivalenceClass, tupleGroups, scenario);
//        if (standardRepair != null) {
//            if (logger.isDebugEnabled()) logger.debug("Returning standard repair " + standardRepair);
//            result.add(standardRepair);
//            // if lub is a constant, no need to search for frequency/similarity
//            return result;
//        }
//        // search similarity/frequency
//        List<EGDEquivalenceClassTupleCellsOLD> forwardGroups = new ArrayList<EGDEquivalenceClassTupleCellsOLD>();
//        List<EGDEquivalenceClassTupleCellsOLD> backwardGroups = new ArrayList<EGDEquivalenceClassTupleCellsOLD>();
//        //TODO++ check: we need a single backward attribute per group of cells
//        Map<EGDEquivalenceClassTupleCellsOLD, BackwardAttribute> backwardAttributes = partitionGroups(tupleGroups, forwardGroups, backwardGroups, scenario);
//        if (logger.isDebugEnabled()) logger.debug("Forward groups: " + forwardGroups);
//        if (logger.isDebugEnabled()) logger.debug("Backward groups: " + backwardGroups);
//        Repair repair = generateRepairWithBackwards(equivalenceClass, forwardGroups, backwardGroups, backwardAttributes, scenario, chaseTreeRoot.getDeltaDB(), stepId);
//        if (allSuspicious(backwardGroups)) {
//            if (logger.isDebugEnabled()) logger.debug("CostManager generates a repair with all suspicious changes\n" + repair);
//            repair = generateSymmetricForwardRepair(tupleGroups, scenario, chaseTreeRoot.getDeltaDB(), stepId);
//        }
//        if (repair != null) {
//            if (logger.isDebugEnabled()) logger.debug("Returning repair " + repair);
//            result.add(repair);
//        }
//        if (logger.isInfoEnabled()) logger.info("Returning repair " + repair);
//        return result;
//    }
//
//    private Repair generateConstantRepairWithStandardPartialOrder(EquivalenceClassForSymmetricEGD equivalenceClass, List<EGDEquivalenceClassTupleCellsOLD> tupleGroups, Scenario scenario) {
//        List<CellGroup> cellGroups = extractForwardCellGroups(tupleGroups);
//        CellGroup cellGroup = new StandardPartialOrder().findLUB(cellGroups, scenario);
//        IValue poValue = cellGroup.getValue();
//        if (!(poValue instanceof ConstantValue)) {
//            return null;
//        }
//        Repair repair = new Repair();
//        ChangeDescription forwardChanges = new ChangeDescription(cellGroup, LunaticConstants.CHASE_FORWARD, buildWitnessCells(tupleGroups));
//        repair.addViolationContext(forwardChanges);
//        return repair;
//    }
//
//    private Map<EGDEquivalenceClassTupleCellsOLD, BackwardAttribute> partitionGroups(List<EGDEquivalenceClassTupleCellsOLD> tupleGroups, List<EGDEquivalenceClassTupleCellsOLD> forwardGroups, List<EGDEquivalenceClassTupleCellsOLD> backwardGroups, Scenario scenario) {
//        Map<EGDEquivalenceClassTupleCellsOLD, BackwardAttribute> result = new HashMap<EGDEquivalenceClassTupleCellsOLD, BackwardAttribute>();
//        EGDEquivalenceClassTupleCellsOLD tupleGroup0 = tupleGroups.get(0);
//        forwardGroups.add(tupleGroup0);
//        for (int j = 1; j < tupleGroups.size(); j++) {
//            EGDEquivalenceClassTupleCellsOLD tupleGroupj = tupleGroups.get(j);
//            if (tupleGroupj.getOccurrenceSize() == 0 || areSimilar(tupleGroup0, tupleGroupj, scenario.getCostManagerConfiguration())) {
//                forwardGroups.add(tupleGroupj);
//                continue;
//            }
//            BackwardAttribute backwardAttribute = canDoBackward(tupleGroupj);
//            if (backwardAttribute == null) {
//                forwardGroups.add(tupleGroupj);
//                continue;
//            }
//            backwardGroups.add(tupleGroupj);
//            result.put(tupleGroupj, backwardAttribute);
//        }
//        return result;
//    }
//
//    private Repair generateRepairWithBackwards(EquivalenceClassForSymmetricEGD equivalenceClass, List<EGDEquivalenceClassTupleCellsOLD> forwardTupleGroups,
//            List<EGDEquivalenceClassTupleCellsOLD> backwardTupleGroups, Map<EGDEquivalenceClassTupleCellsOLD, BackwardAttribute> backwardAttributes, Scenario scenario, IDatabase deltaDB, String stepId) {
//        Repair repair = new Repair();
//        if (forwardTupleGroups.size() > 1) {
//            ChangeDescription forwardChanges = generateChangeDescriptionForForwardRepair(forwardTupleGroups, scenario, deltaDB, stepId);
//            repair.addViolationContext(forwardChanges);
//        }
//        for (EGDEquivalenceClassTupleCellsOLD backwardTupleGroup : backwardTupleGroups) {
//            BackwardAttribute backwardAttribute = backwardAttributes.get(backwardTupleGroup);
//            Set<CellGroup> backwardCellGroups = backwardTupleGroup.getCellGroupsForBackwardRepair().get(backwardAttribute);
//            for (CellGroup backwardCellGroup : backwardCellGroups) {
//                LLUNValue llunValue = CellGroupIDGenerator.getNextLLUNID();
//                backwardCellGroup.setValue(llunValue);
//                backwardCellGroup.setInvalidCell(CellGroupIDGenerator.getNextInvalidCell());
//                ChangeDescription backwardChangesForGroup = new ChangeDescription(backwardCellGroup, LunaticConstants.CHASE_BACKWARD, buildWitnessCells(backwardTupleGroups));
//                repair.addViolationContext(backwardChangesForGroup);
//                if (scenario.getConfiguration().isRemoveSuspiciousSolutions() && isSuspicious(backwardCellGroup, backwardAttribute, equivalenceClass)) {
//                    backwardTupleGroup.setSuspicious(true);
////                    repair.setSuspicious(true);
//                }
//            }
//        }
//        if (repair.getChangeDescriptions().isEmpty()) {
//            return null;
//        }
//        return repair;
//    }
//
//    private boolean areSimilar(EGDEquivalenceClassTupleCellsOLD t1, EGDEquivalenceClassTupleCellsOLD t2, CostManagerConfiguration costManagerConfiguration) {
//        IValue v1 = t1.getCellGroupForForwardRepair().getValue();
//        IValue v2 = t2.getCellGroupForForwardRepair().getValue();
//        return CostManagerUtility.areSimilar(v1, v2, costManagerConfiguration.getSimilarityStrategy(), costManagerConfiguration.getSimilarityThreshold());
//    }
//
//    private BackwardAttribute canDoBackward(EGDEquivalenceClassTupleCellsOLD tupleGroup) {
//        List<BackwardAttribute> sortedBackwardAttributes = sortBackAttributes(tupleGroup);
//        for (BackwardAttribute backwardAttribute : sortedBackwardAttributes) {
//            Set<CellGroup> backwardCellGroups = tupleGroup.getCellGroupsForBackwardRepair().get(backwardAttribute);
//            if (CostManagerUtility.backwardIsAllowed(backwardCellGroups)) {
//                return backwardAttribute;
//            }
//        }
//        return null;
//    }
//
//    private List<BackwardAttribute> sortBackAttributes(EGDEquivalenceClassTupleCellsOLD tupleGroup) {
////        return tupleGroup.getSortedWitnessCellGroups();
//        Map<BackwardAttribute, Set<CellGroup>> backwardCellGroups = tupleGroup.getCellGroupsForBackwardRepair();
//        BackwardAttributeComparator comparator = new BackwardAttributeComparator(backwardCellGroups);
//        List<BackwardAttribute> result = new ArrayList<BackwardAttribute>(tupleGroup.getWitnessCells().keySet());
//        Collections.sort(result, comparator);
//        return result;
//    }
//
//    private boolean allSuspicious(List<EGDEquivalenceClassTupleCellsOLD> backwardGroups) {
//        if (backwardGroups.isEmpty()) {
//            return false;
//        }
//        for (EGDEquivalenceClassTupleCellsOLD targetCellsToChange : backwardGroups) {
//            if (!targetCellsToChange.isSuspicious()) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    @Override
//    public String toString() {
//        return "Similarity To Most Frequent";
//    }
//
//    private static class BackwardAttributeComparator implements Comparator<BackwardAttribute> {
//
//        private Map<BackwardAttribute, Set<CellGroup>> backwardCellGroups;
//
//        public BackwardAttributeComparator(Map<BackwardAttribute, Set<CellGroup>> backwardCellGroups) {
//            this.backwardCellGroups = backwardCellGroups;
//        }
//
//        public int compare(BackwardAttribute o1, BackwardAttribute o2) {
//            int occ1 = backwardCellGroups.get(o1).size();
//            int occ2 = backwardCellGroups.get(o2).size();
//            if (occ1 == occ2) {
//                return o1.toString().compareTo(o2.toString());
//            }
//            return occ2 - occ1;
//        }
//
//    }


}
