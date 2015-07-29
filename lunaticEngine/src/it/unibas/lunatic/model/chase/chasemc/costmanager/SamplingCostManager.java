package it.unibas.lunatic.model.chase.chasemc.costmanager;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.BackwardAttribute;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import it.unibas.lunatic.model.chase.chasemc.ViolationContext;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClassForEGD;
import it.unibas.lunatic.model.chase.chasemc.Repair;
import it.unibas.lunatic.model.chase.chasemc.TargetCellsToChangeForEGD;
import it.unibas.lunatic.model.chase.chasemc.operators.OccurrenceHandlerMC;
import it.unibas.lunatic.model.database.Cell;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.utility.DependencyUtility;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SamplingCostManager extends StandardCostManager {

    private static Logger logger = LoggerFactory.getLogger(SamplingCostManager.class);

    private int maxRepairsForStep = 3;
    private Random random = new Random();

    public SamplingCostManager() {
    }

    public SamplingCostManager(int maxRepairsForStep) {
        this.maxRepairsForStep = maxRepairsForStep;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Repair> chooseRepairStrategy(EquivalenceClassForEGD equivalenceClass, DeltaChaseStep chaseTreeRoot,
            List<Repair> repairsForDependency, Scenario scenario, String stepId,
            OccurrenceHandlerMC occurrenceHandler) {
        if (logger.isDebugEnabled()) logger.debug("########Current node: " + chaseTreeRoot.toStringWithSort());
        if (logger.isDebugEnabled()) logger.debug("########Sampling repair strategy for equivalence class: " + equivalenceClass);
        List<TargetCellsToChangeForEGD> tupleGroupsWithSameConclusionValue = equivalenceClass.getTupleGroups();
        if (DependencyUtility.hasSourceSymbols(equivalenceClass.getEGD()) && satisfactionChecker.isSatisfiedAfterUpgrades(tupleGroupsWithSameConclusionValue, scenario)) {
            return Collections.EMPTY_LIST;
        }
        while (true) {
            List<Repair> result = new ArrayList<Repair>();
            int repairsToChoose = selectNumberOfRepairs(chaseTreeRoot, repairsForDependency);
            if (logger.isDebugEnabled()) logger.debug("Repairs to choose: " + repairsToChoose);
            for (int i = 0; i < repairsToChoose; i++) {
                String chaseMode = selectChaseMode();
                if (chaseMode.equals(LunaticConstants.CHASE_FORWARD)) {
                    Repair forwardRepair = generateRandomForwardRepair(equivalenceClass, scenario, chaseTreeRoot.getDeltaDB(), stepId);
                    if (logger.isDebugEnabled()) logger.debug("Random selection of forward repair: " + forwardRepair);
                    LunaticUtility.addIfNotContained(result, forwardRepair);
                } else {
                    Repair backwardRepair = generateRandomBackwardRepair(equivalenceClass.getTupleGroups(), scenario, chaseTreeRoot.getDeltaDB(), stepId, equivalenceClass);
                    if (logger.isDebugEnabled()) logger.debug("Random selection of backward repair: " + backwardRepair);
                    if (backwardRepair != null) {
                        LunaticUtility.addIfNotContained(result, backwardRepair);
                    }
                }
            }
            if (!allRepairsAreSuspicious(result)) {
                if (logger.isDebugEnabled()) logger.debug("Final result: " + LunaticUtility.printCollection(result));
                return result;
            }
        }
    }

    private String selectChaseMode() {
        double chaseMode = random.nextDouble();
        if (chaseMode < 0.5) {
            return LunaticConstants.CHASE_FORWARD;
        }
        return LunaticConstants.CHASE_BACKWARD;
    }

    private Repair generateRandomForwardRepair(EquivalenceClassForEGD equivalenceClass, Scenario scenario, IDatabase deltaDB, String stepId) {
        ViolationContext changesForForwardRepair = super.generateForwardRepair(equivalenceClass.getTupleGroups(), scenario, deltaDB, stepId);
        Repair forwardRepair = new Repair();
        forwardRepair.addViolationContext(changesForForwardRepair);
        correctValuesInRepair(forwardRepair, equivalenceClass);
        return forwardRepair;
    }

    private Repair generateRandomBackwardRepair(List<TargetCellsToChangeForEGD> tupleGroups, Scenario scenario, IDatabase deltaDB, String stepId, EquivalenceClassForEGD equivalenceClass) {
        if (logger.isDebugEnabled()) logger.debug("Generating backward repair for groups:\n" + LunaticUtility.printCollection(tupleGroups));
        List<TargetCellsToChangeForEGD> subset = generateRandomSubset(tupleGroups);
        BackwardAttribute backwardAttribute = selectRandomAttribute(equivalenceClass);
        if (logger.isDebugEnabled()) logger.debug("Generating backward repairs for subset of size: " + subset.size() + "\n" + LunaticUtility.printCollection(subset));
        if (!super.allGroupsCanBeBackwardChasedForAttribute(subset, backwardAttribute)) {
            return null;
        }
        List<TargetCellsToChangeForEGD> forwardGroups = new ArrayList<TargetCellsToChangeForEGD>(tupleGroups);
        List<TargetCellsToChangeForEGD> backwardGroups = new ArrayList<TargetCellsToChangeForEGD>();
        for (TargetCellsToChangeForEGD tupleGroup : subset) {
            CellGroup cellGroup = tupleGroup.getCellGroupsForBackwardRepairs().get(backwardAttribute);
            if (backwardIsAllowed(cellGroup)) {
                backwardGroups.add(tupleGroup);
                forwardGroups.remove(tupleGroup);
            }
        }
        if (backwardGroups.isEmpty() || forwardGroups.isEmpty()) {
            return null;
        }
        if (logger.isDebugEnabled()) logger.debug("Generating repair for: \nForward groups: " + forwardGroups + "\nBackward groups: " + backwardGroups);
        Repair repair = super.generateRepairWithBackwards(equivalenceClass, forwardGroups, backwardGroups, backwardAttribute, scenario, deltaDB, stepId);
        correctValuesInRepair(repair, equivalenceClass);
        return repair;
    }

    private List<TargetCellsToChangeForEGD> generateRandomSubset(List<TargetCellsToChangeForEGD> tupleGroups) {
        int subsetSize = Math.max(1, random.nextInt(tupleGroups.size()));
        List<Integer> subsetPositions = new ArrayList<Integer>();
        for (int i = 0; i < tupleGroups.size(); i++) {
            subsetPositions.add(i);
        }
        Collections.shuffle(subsetPositions);
        List<TargetCellsToChangeForEGD> subset = new ArrayList<TargetCellsToChangeForEGD>();
        for (int i = 0; i < subsetSize; i++) {
            subset.add(tupleGroups.get(subsetPositions.get(i)));
        }
        return subset;
    }

    private BackwardAttribute selectRandomAttribute(EquivalenceClassForEGD equivalenceClass) {
        int position = random.nextInt(equivalenceClass.getAttributesToChangeForBackwardChasing().size());
        return equivalenceClass.getAttributesToChangeForBackwardChasing().get(position);
    }

    private int selectNumberOfRepairs(DeltaChaseStep chaseTreeRoot, List<Repair> repairsForDependency) {
        int chaseBranching = chaseTreeRoot.getNumberOfLeaves();
        int repairsForDependenciesSize = repairsForDependency.size();
        int potentialSolutions = chaseTreeRoot.getPotentialSolutions();
//        if (!isTreeSizeBelowThreshold(chaseTreeSize, potentialSolutions, repairsForDependenciesSize)) {
        if (!isTreeSizeBelowThreshold(chaseBranching, potentialSolutions)) {
            return 1;
        }
        return random.nextInt(maxRepairsForStep) + 1;
    }

    private void correctValuesInRepair(Repair repair, EquivalenceClassForEGD equivalenceClass) {
        for (ViolationContext changeSet : repair.getViolationContexts()) {
            if (!changeSet.getChaseMode().equals(LunaticConstants.CHASE_FORWARD)) {
                continue;
            }
            CellGroup cellGroup = changeSet.getCellGroup();
            CellGroupCell randomCell = selectRandomCell(cellGroup.getOccurrences());
            IValue originalValue = findOriginalValue(randomCell, equivalenceClass);
            cellGroup.setValue(originalValue);
        }
    }

    private CellGroupCell selectRandomCell(Set<CellGroupCell> occurrences) {
        return new ArrayList<CellGroupCell>(occurrences).get(random.nextInt(occurrences.size()));
    }

    private IValue findOriginalValue(Cell randomCell, EquivalenceClassForEGD equivalenceClass) {
        for (IValue value : equivalenceClass.getTupleGroupsWithSameConclusionValue().keySet()) {
            TargetCellsToChangeForEGD targetCells = equivalenceClass.getTupleGroupsWithSameConclusionValue().get(value);
            if (targetCells.getCellGroupForForwardRepair().getOccurrences().contains(randomCell)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unable to find random cell " + randomCell + " in equivalence class\n" + equivalenceClass);
    }

    @Override
    public void setDoBackward(boolean doBackward) {
        if (!doBackward) {
            throw new IllegalArgumentException("Sampling cost manager needs backward");
        }
        super.setDoBackward(doBackward);
    }

    public int getMaxRepairsForStep() {
        return maxRepairsForStep;
    }

    public void setMaxRepairsForStep(int maxRepairsForStep) {
        this.maxRepairsForStep = maxRepairsForStep;
    }

    private boolean allRepairsAreSuspicious(List<Repair> result) {
        for (Repair repair : result) {
            if (!repair.isSuspicious()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "Sampling";
    }
}
