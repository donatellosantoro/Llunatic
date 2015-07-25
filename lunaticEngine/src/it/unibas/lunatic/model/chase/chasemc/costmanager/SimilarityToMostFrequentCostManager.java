package it.unibas.lunatic.model.chase.chasemc.costmanager;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.chase.chasemc.BackwardAttribute;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.ChangeSet;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClassForEGD;
import it.unibas.lunatic.model.chase.chasemc.Repair;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.TargetCellsToChangeForEGD;
import it.unibas.lunatic.model.chase.chasemc.operators.CellGroupIDGenerator;
import it.unibas.lunatic.model.chase.chasemc.operators.OccurrenceHandlerMC;
import it.unibas.lunatic.model.chase.chasemc.partialorder.FrequencyPartialOrder;
import it.unibas.lunatic.model.chase.chasemc.partialorder.IPartialOrder;
import it.unibas.lunatic.model.chase.chasemc.partialorder.StandardPartialOrder;
import it.unibas.lunatic.model.database.ConstantValue;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.model.database.LLUNValue;
import it.unibas.lunatic.model.database.NullValue;
import it.unibas.lunatic.model.similarity.SimilarityFactory;
import it.unibas.lunatic.utility.DependencyUtility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimilarityToMostFrequentCostManager extends AbstractCostManager {

    private static Logger logger = LoggerFactory.getLogger(SimilarityToMostFrequentCostManager.class);

    private double similarityThreshold = 0.8;
//    private String similarityStrategy = SimilarityFactory.SIMPLE_EDITS;
    private String similarityStrategy = SimilarityFactory.LEVENSHTEIN_STRATEGY;

    @SuppressWarnings("unchecked")
    public List<Repair> chooseRepairStrategy(EquivalenceClassForEGD equivalenceClass, DeltaChaseStep chaseTreeRoot,
            List<Repair> repairsForDependency, Scenario scenario, String stepId,
            OccurrenceHandlerMC occurrenceHandler) {
        if (!(scenario.getPartialOrder() instanceof FrequencyPartialOrder)) {
            logger.warn("##################################################################################");
            logger.warn("   SimilarityToMostFrequentCostManager should be used with FrequencyPartialOrder");
            logger.warn("##################################################################################");
            throw new ChaseException("SimilarityToMostFrequentCostManager requires FrequencyPartialOrder");
        }
        List<TargetCellsToChangeForEGD> tupleGroups = equivalenceClass.getTupleGroups();
        Collections.sort(tupleGroups, new TupleGroupComparator());
        Collections.reverse(tupleGroups);
        if (DependencyUtility.hasSourceSymbols(equivalenceClass.getEGD()) && isNotViolation(tupleGroups, scenario)) {
            if (logger.isDebugEnabled()) logger.debug("No violations... Returning empty list");
            return Collections.EMPTY_LIST;
        }
        List<Repair> result = new ArrayList<Repair>();
        Repair standardRepair = generateConstantRepairWithPartialOrder(equivalenceClass, tupleGroups, new StandardPartialOrder(), scenario);
        if (standardRepair != null) {
            if (logger.isDebugEnabled()) logger.debug("Returning standard repair " + standardRepair);
            result.add(standardRepair);
            return result;
        }
        Repair scriptRepair = generateConstantRepairWithPartialOrder(equivalenceClass, tupleGroups, scenario.getScriptPartialOrder(), scenario);
        if (scriptRepair != null) {
            if (logger.isDebugEnabled()) logger.debug("Returning script repair " + scriptRepair);
            result.add(scriptRepair);
            return result;
        }
        List<TargetCellsToChangeForEGD> forwardGroups = new ArrayList<TargetCellsToChangeForEGD>();
        List<TargetCellsToChangeForEGD> backwardGroups = new ArrayList<TargetCellsToChangeForEGD>();
        partitionGroups(tupleGroups, forwardGroups, backwardGroups, chaseTreeRoot, scenario);
        if (logger.isDebugEnabled()) logger.debug("Forward groups: " + forwardGroups);
        if (logger.isDebugEnabled()) logger.debug("Backward groups: " + backwardGroups);
        Repair repair = generateRepairWithBackwards(forwardGroups, backwardGroups, scenario, chaseTreeRoot.getDeltaDB(), stepId, equivalenceClass);
        if (allSuspicious(backwardGroups)) {
            if (logger.isDebugEnabled()) logger.debug("CostManager generates a repair with all suspicious changes\n" + repair);
            repair = generateForwardRepair(tupleGroups, scenario, chaseTreeRoot.getDeltaDB(), stepId, equivalenceClass);
        }
        if (repair != null) {
            if (logger.isDebugEnabled()) logger.debug("Returning repair " + repair);
            result.add(repair);
        }
        return result;
    }

    private Repair generateConstantRepairWithPartialOrder(EquivalenceClassForEGD equivalenceClass, List<TargetCellsToChangeForEGD> tupleGroups, IPartialOrder partialOrder, Scenario scenario) {
        List<CellGroup> cellGroups = extractCellGroups(tupleGroups);
        CellGroup cellGroup = getLUB(cellGroups, partialOrder, scenario);
        if (cellGroup == null) {
            return null;
        }
        IValue poValue = cellGroup.getValue();
        if (!(poValue instanceof ConstantValue)) {
            return null;
        }
        Repair repair = new Repair();
        ChangeSet forwardChanges = new ChangeSet(cellGroup, LunaticConstants.CHASE_FORWARD, buildWitnessCellGroups(tupleGroups));
        repair.addChanges(forwardChanges);
        return repair;
    }

    private void partitionGroups(List<TargetCellsToChangeForEGD> tupleGroups, List<TargetCellsToChangeForEGD> forwardGroups, List<TargetCellsToChangeForEGD> backwardGroups, DeltaChaseStep chaseTreeRoot, Scenario scenario) {
        TargetCellsToChangeForEGD t1 = tupleGroups.get(0);
        forwardGroups.add(t1);
        for (int j = 1; j < tupleGroups.size(); j++) {
            TargetCellsToChangeForEGD ti = tupleGroups.get(j);
//            if (ti.getOccurrenceSize() > 0 && ti.getOccurrenceSize() < t1.getOccurrenceSize() && canDoBackward(ti) && !areSimilar(t1, ti)) {
            // OLD version
            if (ti.getOccurrenceSize() > 0 && canDoBackward(ti) && !areSimilar(t1, ti)) {
                backwardGroups.add(ti);
            } else {
                forwardGroups.add(ti);
            }
        }
    }

    private boolean areSimilar(TargetCellsToChangeForEGD t1, TargetCellsToChangeForEGD t2) {
        IValue v1 = t1.getCellGroupForForwardRepair().getValue();
        IValue v2 = t2.getCellGroupForForwardRepair().getValue();
        if (v1 instanceof NullValue || v2 instanceof NullValue) {
            return true;
        }
        double similarity = SimilarityFactory.getInstance().getStrategy(similarityStrategy).computeSimilarity(v1, v2);
        if (logger.isDebugEnabled()) logger.debug("Checking similarity between " + v1 + " and " + v2 + ". Result: " + similarity);
        return similarity > similarityThreshold;
    }

    protected Repair generateRepairWithBackwards(List<TargetCellsToChangeForEGD> forwardGroups, List<TargetCellsToChangeForEGD> backwardGroups, Scenario scenario, IDatabase deltaDB, String stepId, EquivalenceClassForEGD equivalenceClass) {
        if (logger.isTraceEnabled()) logger.trace("Generating repair for groups \nForward: " + forwardGroups + "\nBackward: " + backwardGroups);
        Repair repair = new Repair();
        if (forwardGroups.size() > 1) {
            ChangeSet forwardChanges = generateForwardRepair(forwardGroups, scenario, deltaDB, stepId);
            if (logger.isDebugEnabled()) logger.debug("Forward changes: " + forwardChanges);
            if (forwardChanges != null) {
                repair.addChanges(forwardChanges);
            }
        }
        for (TargetCellsToChangeForEGD backwardGroup : backwardGroups) {
            for (BackwardAttribute backwardAttribute : backwardGroup.getCellGroupsForBackwardRepairs().keySet()) {
                CellGroup backwardCellGroup = backwardGroup.getCellGroupsForBackwardRepairs().get(backwardAttribute).clone();
                LLUNValue llunValue = CellGroupIDGenerator.getNextLLUNID();
                backwardCellGroup.setValue(llunValue);
                backwardCellGroup.setInvalidCell(CellGroupIDGenerator.getNextInvalidCell());
                ChangeSet backwardChangesForGroup = new ChangeSet(backwardCellGroup, LunaticConstants.CHASE_BACKWARD, buildWitnessCellGroups(backwardGroups));
                repair.addChanges(backwardChangesForGroup);
                if (scenario.getConfiguration().isRemoveSuspiciousSolutions() && isSuspicious(backwardCellGroup, backwardAttribute, equivalenceClass)) {
                    backwardGroup.setSuspicious(true);
                }
            }
        }
        if (repair.getChanges().isEmpty()) {
            return null;
        }
        return repair;
    }

    private Repair generateForwardRepair(List<TargetCellsToChangeForEGD> tupleGroups, Scenario scenario, IDatabase deltaDB, String stepId, EquivalenceClassForEGD equivalenceClass) {
        Repair repair = new Repair();
        ChangeSet forwardChanges = generateForwardRepair(tupleGroups, scenario, deltaDB, stepId);
        if (logger.isDebugEnabled()) logger.debug("Forward changes: " + forwardChanges);
        repair.addChanges(forwardChanges);
        return repair;
    }

    private boolean canDoBackward(TargetCellsToChangeForEGD tupleGroup) {
        for (BackwardAttribute premiseAttribute : tupleGroup.getCellGroupsForBackwardRepairs().keySet()) {
            CellGroup cellGroup = tupleGroup.getCellGroupsForBackwardRepairs().get(premiseAttribute);
            if (!backwardIsAllowed(cellGroup)) {
                return false;
            }
        }
        return true;
    }

    private boolean allSuspicious(List<TargetCellsToChangeForEGD> backwardGroups) {
        if (backwardGroups.isEmpty()) {
            return false;
        }
        for (TargetCellsToChangeForEGD targetCellsToChange : backwardGroups) {
            if (!targetCellsToChange.isSuspicious()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void setDoBackward(boolean doBackward) {
        if (doBackward == false) {
            throw new IllegalArgumentException("SimilarityCostManager requires backward chase");
        }
        super.setDoBackward(doBackward);
    }

    public double getSimilarityThreshold() {
        return similarityThreshold;
    }

    public void setSimilarityThreshold(double similarityThreshold) {
        this.similarityThreshold = similarityThreshold;
    }

    public String getSimilarityStrategy() {
        return similarityStrategy;
    }

    public void setSimilarityStrategy(String similarityStrategy) {
        this.similarityStrategy = similarityStrategy;
    }

    @Override
    public String toString() {
        return "Similarity To Most Frequent";
    }

    @Override
    public String toLongString() {
        return toString()
                + "\n\tSimilarity strategy: " + similarityStrategy
                + "\n\tSimilarity threashold: " + similarityThreshold
                + "\n"
                + super.toString();
    }
}
