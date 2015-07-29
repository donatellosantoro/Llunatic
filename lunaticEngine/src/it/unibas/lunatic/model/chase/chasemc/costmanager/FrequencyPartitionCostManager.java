package it.unibas.lunatic.model.chase.chasemc.costmanager;

import it.unibas.lunatic.LunaticConfiguration;
import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.utility.LunaticUtility;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.chase.chasemc.BackwardAttribute;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.ViolationContext;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClassForEGD;
import it.unibas.lunatic.model.chase.chasemc.Repair;
import it.unibas.lunatic.model.chase.chasemc.TargetCellsToChangeForEGD;
import it.unibas.lunatic.model.chase.chasemc.operators.CellGroupIDGenerator;
import it.unibas.lunatic.model.chase.chasemc.operators.OccurrenceHandlerMC;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.LLUNValue;
import it.unibas.lunatic.utility.DependencyUtility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrequencyPartitionCostManager extends AbstractCostManager {

    private static Logger logger = LoggerFactory.getLogger(FrequencyPartitionCostManager.class);

    private double lowFrequencyThreshold = 0.1;
    private int lowFrequencyValue = 1;
    private double highFrequencyThreshold = 0.5;
    private double highFrequencyDifferenceThreshold = 0.2;

    @SuppressWarnings("unchecked")
    public List<Repair> chooseRepairStrategy(EquivalenceClassForEGD equivalenceClass, DeltaChaseStep chaseTreeRoot,
            List<Repair> repairsForDependency, Scenario scenario, String stepId,
            OccurrenceHandlerMC occurrenceHandler) {
        if (isDoBackward() && !isDoPermutations()) {
            throw new ChaseException("SinglePermutation CostManager works must return singleton repairs. Configuration with doBackward and not doPermutations is not allowed with StandardCostManager");
        }
        if (logger.isDebugEnabled()) logger.debug("########?Choosing repair strategy for equivalence class: " + equivalenceClass);
        List<TargetCellsToChangeForEGD> tupleGroups = equivalenceClass.getTupleGroups();
        if (DependencyUtility.hasSourceSymbols(equivalenceClass.getEGD()) && satisfactionChecker.isSatisfiedAfterUpgrades(tupleGroups, scenario)) {
            return Collections.EMPTY_LIST;
        }
        List<Repair> result = new ArrayList<Repair>();
        // generate forward repair for all groups
        ViolationContext changesForForwardRepair = generateForwardRepair(equivalenceClass.getTupleGroups(), scenario, chaseTreeRoot.getDeltaDB(), stepId);
        Repair forwardRepair = new Repair();
        forwardRepair.addViolationContext(changesForForwardRepair);
        if (logger.isDebugEnabled()) logger.debug("########Forward repair: " + forwardRepair);
        result.add(forwardRepair);
        if (isDoBackward()) {
            // check if repairs with backward chasing are possible
            int chaseBranching = chaseTreeRoot.getNumberOfLeaves();
//            int repairsForDependenciesSize = repairsForDependency.size();
            int potentialSolutions = chaseTreeRoot.getPotentialSolutions();
//            int databaseSize = scenario.getTarget().getSize();
//            if (isTreeSizeBelowThreshold(chaseTreeSize, potentialSolutions, repairsForDependenciesSize)) {
            if (isTreeSizeBelowThreshold(chaseBranching, potentialSolutions)) {
                int equivalenceClassSize = calculateSize(equivalenceClass);
                List<TargetCellsToChangeForEGD> lowFrequencyGroups = new ArrayList<TargetCellsToChangeForEGD>();
                List<TargetCellsToChangeForEGD> mediumFrequencyGroups = new ArrayList<TargetCellsToChangeForEGD>();
                List<TargetCellsToChangeForEGD> highFrequencyGroups = new ArrayList<TargetCellsToChangeForEGD>();
                partitionGroups(equivalenceClass, equivalenceClassSize, scenario, lowFrequencyGroups, mediumFrequencyGroups, highFrequencyGroups);
                List<Repair> backwardRepairs = generateBackwardRepairs(equivalenceClass, lowFrequencyGroups, mediumFrequencyGroups, highFrequencyGroups,
                        scenario, chaseTreeRoot.getDeltaDB(), stepId, occurrenceHandler);
                result.addAll(backwardRepairs);
                if (logger.isDebugEnabled()) logger.debug("########Backward repairs: " + backwardRepairs);
            }
        }
        return result;
    }

    private int calculateSize(EquivalenceClassForEGD equivalenceClass) {
        int size = 0;
        for (TargetCellsToChangeForEGD tupleGroup : equivalenceClass.getTupleGroups()) {
            size += tupleGroup.getOccurrenceSize();
        }
        return size;
    }

    private void partitionGroups(EquivalenceClassForEGD equivalenceClass, int equivalenceClassSize, Scenario scenario,
            List<TargetCellsToChangeForEGD> lowFrequencyGroups, List<TargetCellsToChangeForEGD> mediumFrequencyGroups, List<TargetCellsToChangeForEGD> highFrequencyGroups) {
        for (int i = 0; i < equivalenceClass.getTupleGroups().size(); i++) {
            TargetCellsToChangeForEGD tupleGroup = equivalenceClass.getTupleGroups().get(i);
            if (isLowFrequency(tupleGroup, equivalenceClassSize, scenario.getConfiguration())) {
                lowFrequencyGroups.add(tupleGroup);
                continue;
            }
            if (i != 0) {
                TargetCellsToChangeForEGD previousGroup = equivalenceClass.getTupleGroups().get(i - 1);
                if (isHighFrequency(tupleGroup, previousGroup, equivalenceClassSize, scenario.getConfiguration())) {
                    highFrequencyGroups.add(tupleGroup);
                    continue;
                }
            }
            mediumFrequencyGroups.add(tupleGroup);
        }
        if (logger.isDebugEnabled()) logger.debug("Low frequency groups: " + LunaticUtility.printCollection(lowFrequencyGroups));
        if (logger.isDebugEnabled()) logger.debug("Medium frequency groups: " + LunaticUtility.printCollection(mediumFrequencyGroups));
        if (logger.isDebugEnabled()) logger.debug("High frequency groups: " + LunaticUtility.printCollection(highFrequencyGroups));
    }

    private boolean isLowFrequency(TargetCellsToChangeForEGD tupleGroup, int equivalenceClassSize, LunaticConfiguration configuration) {
        double frequency = ((double) tupleGroup.getOccurrenceSize()) / equivalenceClassSize;
        return (frequency < lowFrequencyThreshold || tupleGroup.getOccurrenceSize() <= lowFrequencyValue);
    }

    private boolean isHighFrequency(TargetCellsToChangeForEGD tupleGroup, TargetCellsToChangeForEGD previousGroup, int equivalenceClassSize, LunaticConfiguration configuration) {
        double frequency = ((double) tupleGroup.getOccurrenceSize()) / equivalenceClassSize;
        double previousFrequency = ((double) previousGroup.getOccurrenceSize()) / equivalenceClassSize;
        double increase = frequency - previousFrequency;
        return (frequency >= highFrequencyThreshold && increase > highFrequencyDifferenceThreshold);
    }

    private List<Repair> generateBackwardRepairs(EquivalenceClassForEGD equivalenceClass, List<TargetCellsToChangeForEGD> lowFrequencyGroups, List<TargetCellsToChangeForEGD> mediumFrequencyGroups, List<TargetCellsToChangeForEGD> highFrequencyGroups, Scenario scenario, IDatabase deltaDB, String stepId, OccurrenceHandlerMC occurrenceHandler) {
        List<Repair> result = new ArrayList<Repair>();
        if (logger.isDebugEnabled()) logger.debug("Generating backward repairs for groups:\n" + "High frequency:\n" + highFrequencyGroups + "Medium frequency:\n" + mediumFrequencyGroups + "Low frequency:\n" + lowFrequencyGroups);
        for (TargetCellsToChangeForEGD lowFrequencyGroup : lowFrequencyGroups) {
            for (BackwardAttribute premiseAttribute : lowFrequencyGroup.getCellGroupsForBackwardRepairs().keySet()) {
                List<TargetCellsToChangeForEGD> forwardGroups = new ArrayList<TargetCellsToChangeForEGD>();
                forwardGroups.addAll(mediumFrequencyGroups);
                forwardGroups.addAll(highFrequencyGroups);
                forwardGroups.addAll(lowFrequencyGroups);
                List<TargetCellsToChangeForEGD> backwardGroups = new ArrayList<TargetCellsToChangeForEGD>();
                CellGroup cellGroup = lowFrequencyGroup.getCellGroupsForBackwardRepairs().get(premiseAttribute);
                if (backwardIsAllowed(cellGroup)) {
                    backwardGroups.add(lowFrequencyGroup);
                    forwardGroups.remove(lowFrequencyGroup);
                }
                if (backwardGroups.isEmpty()) {
                    continue;
                }
                Repair repair = generateRepairWithBackwards(equivalenceClass, forwardGroups, backwardGroups, premiseAttribute, scenario, deltaDB, stepId);
                result.add(repair);
            }
        }
        return result;
    }

    private Repair generateRepairWithBackwards(EquivalenceClassForEGD equivalenceClass, List<TargetCellsToChangeForEGD> forwardGroups, List<TargetCellsToChangeForEGD> backwardGroups, BackwardAttribute premiseAttribute,
            Scenario scenario, IDatabase deltaDB, String stepId) {
        Repair repair = new Repair();
        if (forwardGroups.size() > 1) {
            ViolationContext forwardChanges = generateForwardRepair(forwardGroups, scenario, deltaDB, stepId);
            repair.addViolationContext(forwardChanges);
        }
        for (TargetCellsToChangeForEGD backwardTupleGroup : backwardGroups) {
            CellGroup backwardCellGroup = backwardTupleGroup.getCellGroupsForBackwardRepairs().get(premiseAttribute).clone();
            LLUNValue llunValue = CellGroupIDGenerator.getNextLLUNID();
            backwardCellGroup.setValue(llunValue);
            backwardCellGroup.setInvalidCell(CellGroupIDGenerator.getNextInvalidCell());
            ViolationContext backwardChangesForGroup = new ViolationContext(backwardCellGroup, LunaticConstants.CHASE_BACKWARD, buildWitnessCellGroups(backwardGroups));
            repair.addViolationContext(backwardChangesForGroup);
        }
        return repair;
    }

    @Override
    public String toString() {
        return "Frequency partition";
    }
}
