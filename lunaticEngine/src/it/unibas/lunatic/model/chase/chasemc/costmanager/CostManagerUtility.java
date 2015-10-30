package it.unibas.lunatic.model.chase.chasemc.costmanager;

import it.unibas.lunatic.model.similarity.SimilarityConfiguration;
import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.BackwardAttribute;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import it.unibas.lunatic.model.chase.chasemc.ChangeDescription;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.EGDEquivalenceClassTuple;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClassForSymmetricEGD;
import it.unibas.lunatic.model.chase.chasemc.Repair;
import it.unibas.lunatic.model.chase.chasemc.ViolationContext;
import it.unibas.lunatic.model.chase.chasemc.operators.CellGroupIDGenerator;
import it.unibas.lunatic.model.chase.chasemc.partialorder.IPartialOrder;
import it.unibas.lunatic.model.chase.chasemc.partialorder.StandardPartialOrder;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.VariableEquivalenceClass;
import it.unibas.lunatic.model.similarity.SimilarityFactory;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.AttributeRef;
import speedy.model.database.Cell;
import speedy.model.database.ConstantValue;
import speedy.model.database.IValue;
import speedy.model.database.LLUNValue;
import speedy.model.database.NullValue;
import speedy.model.database.TupleOID;

public class CostManagerUtility {

    private static Logger logger = LoggerFactory.getLogger(CostManagerUtility.class);

    public static List<Dependency> selectDependenciesToChase(List<Dependency> unsatisfiedDependencies, DeltaChaseStep chaseRoot, CostManagerConfiguration costManagerConfiguration) {
        if (logger.isDebugEnabled()) logger.debug("Selecting dependencies to chase - Unsatisfied " + LunaticUtility.printDependencyIds(unsatisfiedDependencies));
        if (unsatisfiedDependencies.isEmpty()) {
            return unsatisfiedDependencies;
        }
        List<Dependency> result = new ArrayList<Dependency>();
//        int chaseTreeSize = chaseRoot.getPotentialSolutions();
        int numberOfLeaves = chaseRoot.getNumberOfLeaves();
        int potentialSolutions = chaseRoot.getPotentialSolutions();
        if (costManagerConfiguration.getDependencyLimit() == 1
                || !CostManagerUtility.isTreeSizeBelowThreshold(numberOfLeaves, potentialSolutions, costManagerConfiguration)) {
            result.add(unsatisfiedDependencies.get(0));
            if (logger.isDebugEnabled()) logger.debug("To chase: " + LunaticUtility.printDependencyIds(result));
            return result;
        }
        if (costManagerConfiguration.getDependencyLimit() == -1) {
            if (logger.isDebugEnabled()) logger.debug("Returning all...");
            return unsatisfiedDependencies;
        }
        int dependencies = Math.min(unsatisfiedDependencies.size(), costManagerConfiguration.getDependencyLimit());
        for (int i = 0; i < dependencies; i++) {
            result.add(unsatisfiedDependencies.get(i));
        }
        if (logger.isDebugEnabled()) logger.debug("To chase: " + LunaticUtility.printDependencyIds(result));
        return result;
    }

    public static boolean isTreeSizeBelowThreshold(int chaseTreeSize, int potentialSolutions, CostManagerConfiguration costManagerConfiguration) {
        boolean isBelow = (chaseTreeSize < costManagerConfiguration.getChaseBranchingThreshold()
                && potentialSolutions < costManagerConfiguration.getPotentialSolutionsThreshold());
        if (!isBelow && logger.isDebugEnabled()) {
            logger.debug(costManagerConfiguration.toString());
        }
        return isBelow;
    }

    public static boolean backwardIsAllowed(Set<CellGroup> cellGroups) {
        for (CellGroup cellGroup : cellGroups) {
            if (!backwardIsAllowed(cellGroup)) {
                return false;
            }
        }
        return true;
    }

    public static boolean canDoBackwardOnGroup(CellGroup cellGroupToChange, ViolationContext backwardContext) {
        if (!CostManagerUtility.backwardIsAllowed(cellGroupToChange)) {
            if (logger.isDebugEnabled()) logger.debug("Backward is not allowed on group " + cellGroupToChange);
            return false;
        }
//        //Checking that the backward change actually disrupts a join. (Was suspicious in previous versions)
        VariableEquivalenceClass variableEQC = findVariableEquivalenceClassForCellGroup(cellGroupToChange, backwardContext);
        Set<CellGroup> cellGroups = backwardContext.getWitnessCellGroupsForVariable(variableEQC);
        for (CellGroup cellGroup : cellGroups) {
            if (cellGroup.equals(cellGroupToChange)) {
                continue;
            }
            return true;
        }
        if (logger.isDebugEnabled()) logger.debug("Backward change doesn't disrupt a join " + cellGroupToChange);
        return false;
    }

    private static VariableEquivalenceClass findVariableEquivalenceClassForCellGroup(CellGroup witnessCellGroup, ViolationContext backwardContext) {
        for (VariableEquivalenceClass witnessVariable : backwardContext.getWitnessVariables()) {
            Set<CellGroup> cellGroups = backwardContext.getWitnessCellGroupsForVariable(witnessVariable);
            if (cellGroups.contains(witnessCellGroup)) {
                return witnessVariable;
            }
        }
        throw new IllegalArgumentException("Unable to find variable equivalence class for cell group " + witnessCellGroup + "\n\t in context " + backwardContext);
    }

    public static boolean backwardIsAllowed(CellGroup cellGroup) {
        if (cellGroup.getOccurrences().isEmpty()) {
            if (logger.isDebugEnabled()) logger.debug("Backward with empty occurrences (" + cellGroup + ") is not allowed");
            return false;
        }
        // never change LLUNs backward L(L(x)) = L(x)            
        if (cellGroup.getValue() instanceof LLUNValue || cellGroup.hasInvalidCell()) {
            if (logger.isDebugEnabled()) logger.debug("Backward on LLUN (" + cellGroup.getValue() + ") is not allowed");
            return false;
        }
        // never change equal null values          
        if (cellGroup.getValue() instanceof NullValue) {
            if (logger.isDebugEnabled()) logger.debug("Backward on Null (" + cellGroup.getValue() + ") is not allowed");
            return false;
        }
        if (!cellGroup.getAuthoritativeJustifications().isEmpty()) {
            if (logger.isDebugEnabled()) logger.debug("Backward on " + cellGroup.getValue() + " with authoritative justification " + cellGroup.getAuthoritativeJustifications() + " is not allowed");
            return false;
        }
        if (!cellGroup.getUserCells().isEmpty()) {
            if (logger.isDebugEnabled()) logger.debug("Backward on " + cellGroup.getValue() + " with user cell " + cellGroup.getUserCells() + " is not allowed");
            return false;
        }
        if (logger.isDebugEnabled()) logger.debug("Backward on " + cellGroup.getValue() + " is allowed");
        return true;
    }

    //Was isSuspicious
    public static boolean joinsAreNotDisrupted(EquivalenceClassForSymmetricEGD equivalenceClass, List<EGDEquivalenceClassTuple> forwardTuples, List<EGDEquivalenceClassTuple> backwardTuples, List<BackwardAttribute> backwardAttributes) {
        for (int i = 0; i < backwardTuples.size(); i++) {
            EGDEquivalenceClassTuple backwardTuple = backwardTuples.get(i);
            BackwardAttribute backwardAttribute = backwardAttributes.get(i);
            CellGroup backwardCellGroup = backwardTuple.getCellGroupForBackwardAttribute(backwardAttribute);
            for (CellGroupCell backwardCell : backwardCellGroup.getOccurrences()) {
                for (EGDEquivalenceClassTuple tuple : equivalenceClass.getTupleCellsForCell(backwardCell)) {
                    if (forwardTuples.contains(tuple)) {
                        if (logger.isDebugEnabled()) logger.debug("Backward cellgroup " + backwardCellGroup + " do not disrupt a join. Tuples for cell group occurrences: " + tuple);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static Set<Cell> buildWitnessCells(Collection<EGDEquivalenceClassTuple> equivalenceClassTuples) {
        Set<Cell> witnessCells = new HashSet<Cell>();
        for (EGDEquivalenceClassTuple tuple : equivalenceClassTuples) {
            for (CellGroup witnessCellsInTuple : tuple.getBackwardCellGroups()) {
                witnessCells.addAll(witnessCellsInTuple.getAllCells());
            }
        }
        return witnessCells;
    }

    public static CellGroup getLUB(List<CellGroup> cellGroups, Scenario scenario) {
        CellGroup lub = null;
        IPartialOrder scriptPo = scenario.getScriptPartialOrder();
        if (scriptPo != null) {
            lub = scriptPo.findLUB(cellGroups, scenario);
        }
        if (lub == null) {
            lub = scenario.getPartialOrder().findLUB(cellGroups, scenario);
        }
        return lub;
    }

    public static boolean areSimilar(IValue v1, IValue v2, SimilarityConfiguration similarityConfiguration) {
        if (v1 instanceof NullValue || v2 instanceof NullValue) {
            return true;
        }
        if (v1.toString().equalsIgnoreCase(v2.toString())) {
            return true;
        }
        double similarity = SimilarityFactory.getInstance().getStrategy(similarityConfiguration.getStrategy(), similarityConfiguration.getParams()).computeSimilarity(v1, v2);
        if (logger.isDebugEnabled()) logger.debug("Checking similarity between " + v1 + " and " + v2 + ". Result: " + similarity + " - Similarity Configuration: " + similarityConfiguration);
        return similarity > similarityConfiguration.getThreshold();
    }

    public static Repair generateSymmetricForwardRepair(List<EGDEquivalenceClassTuple> forwardTuples, Scenario scenario) {
        Repair repair = new Repair();
        ChangeDescription forwardChanges = generateChangeDescriptionForSymmetricForwardRepair(forwardTuples, scenario);
        if (logger.isDebugEnabled()) logger.debug("Forward changes: " + forwardChanges);
        repair.addViolationContext(forwardChanges);
        return repair;
    }

    public static ChangeDescription generateChangeDescriptionForSymmetricForwardRepair(List<EGDEquivalenceClassTuple> forwardTupleGroups, Scenario scenario) {
        List<CellGroup> cellGroups = extractForwardCellGroups(forwardTupleGroups);
        // give preference to the script partial order, that may have additional rules to solve the violation
        CellGroup lub = getLUB(cellGroups, scenario);
        ChangeDescription changeSet = new ChangeDescription(lub, LunaticConstants.CHASE_FORWARD, buildWitnessCells(forwardTupleGroups));
        return changeSet;
    }

    public static List<CellGroup> extractForwardCellGroups(List<EGDEquivalenceClassTuple> allTupleCells) {
        List<CellGroup> cellGroups = new ArrayList<CellGroup>();
        for (EGDEquivalenceClassTuple tupleCells : allTupleCells) {
            cellGroups.add(tupleCells.getConclusionGroup().clone());
        }
        return cellGroups;
    }

    public static Repair generateForwardRepair(List<ViolationContext> forwardContexts, Scenario scenario) {
        Repair repair = new Repair();
        List<CellGroup> forwardCellGroups = extractConclusionCellGroupsFromContexts(forwardContexts);
        CellGroup lub = getLUB(forwardCellGroups, scenario);
        Set<Cell> contextCells = extractContextCellsFromContexts(forwardContexts);
        ChangeDescription forwardChanges = new ChangeDescription(lub, LunaticConstants.CHASE_FORWARD, contextCells);
        if (logger.isDebugEnabled()) logger.debug("Forward changes: " + forwardChanges);
        repair.addViolationContext(forwardChanges);
        return repair;
    }

    public static List<CellGroup> extractConclusionCellGroupsFromContexts(List<ViolationContext> contexts) {
        Set<CellGroup> result = new HashSet<CellGroup>();
        for (ViolationContext context : contexts) {
            result.addAll(context.getAllConclusionGroups());
        }
        return new ArrayList<CellGroup>(result);
    }

    public static Repair generateRepairWithBackwards(List<ViolationContext> forwardContexts, List<CellGroup> backwardCellGroups, List<ViolationContext> backwardContexts, Scenario scenario) {
        if (logger.isDebugEnabled()) logger.debug("Generating repair with forward contexts: " + LunaticUtility.printViolationContextIDs(forwardContexts)
                    + "\n\tbackward contexts: " + LunaticUtility.printViolationContextIDs(backwardContexts)
                    + "\n\tbackward combination: " + backwardCellGroups);
        Repair repair;
        if (!forwardContexts.isEmpty()) {
            repair = CostManagerUtility.generateForwardRepair(forwardContexts, scenario);
        } else {
            repair = new Repair();
        }
        for (CellGroup backwardGroup : backwardCellGroups) {
            if (hasBeenChanged(backwardGroup, repair)) {
                continue;
            }
            CellGroup backwardCellGroup = backwardGroup.clone();
            LLUNValue llunValue = CellGroupIDGenerator.getNextLLUNID();
            backwardCellGroup.setValue(llunValue);
            backwardCellGroup.setInvalidCell(CellGroupIDGenerator.getNextInvalidCell());
            Set<Cell> contextCells = CostManagerUtility.extractContextCellsFromContexts(backwardContexts);
            ChangeDescription backwardChangesForGroup = new ChangeDescription(backwardCellGroup, LunaticConstants.CHASE_BACKWARD, contextCells);
            repair.addViolationContext(backwardChangesForGroup);
        }
        return repair;
    }

    private static boolean hasBeenChanged(CellGroup backwardGroup, Repair repair) {
        for (ChangeDescription changeDescription : repair.getChangeDescriptions()) {
            if (changeDescription.getCellGroup().getOccurrences().containsAll(backwardGroup.getOccurrences())) {
                return true;
            }
        }
        return false;
    }

    private static Set<Cell> extractContextCellsFromContexts(List<ViolationContext> contexts) {
        Set<Cell> result = new HashSet<Cell>();
        for (ViolationContext context : contexts) {
            Set<Cell> cellsForContext = extractAllCellsFromContext(context);
            result.addAll(cellsForContext);
        }
        return result;
    }

    public static Set<Cell> extractAllCellsFromContext(ViolationContext context) {
        Set<Cell> result = new HashSet<Cell>();
        for (CellGroup conclusionGroup : context.getAllConclusionGroups()) {
            result.addAll(conclusionGroup.getOccurrences());
        }
        for (CellGroup witnessCellGroup : context.getAllWitnessCellGroups()) {
            result.addAll(witnessCellGroup.getOccurrences());
        }
        return result;
    }

    public static List<Integer> createIndexes(int size) {
        List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < size; i++) {
            result.add(i);
        }
        return result;
    }

    public static IValue findPreferredValue(List<CellGroup> forwardCellGroups, Scenario scenario) {
        List<CellGroup> validForwardCellGroups = filterValidCellGroups(forwardCellGroups);
        if (validForwardCellGroups.isEmpty()) {
            return CellGroupIDGenerator.getNextLLUNID();
        }
        // To handle User, Auth, and PI
        CellGroup standardLub = new StandardPartialOrder().findLUB(validForwardCellGroups, scenario);
        IValue standardLubValue = standardLub.getValue();
        if (logger.isDebugEnabled()) logger.debug("Standard lub value: " + standardLubValue);
        if ((standardLubValue instanceof ConstantValue)) {
            return standardLubValue;
        }
        if (hasUserOrAuthoritativeValues(standardLub)) {
            return (LLUNValue) standardLubValue;
        }
        // To handle specific partial order (i.e. frequency)
        CellGroup lub = CostManagerUtility.getLUB(validForwardCellGroups, scenario);
        IValue lubValue = lub.getValue();
        if (logger.isDebugEnabled()) logger.debug("Lub value: " + lubValue);
        return lubValue;
    }

    private static boolean hasUserOrAuthoritativeValues(CellGroup lub) {
        return !lub.getUserCells().isEmpty() || !lub.getAuthoritativeJustifications().isEmpty();
    }

    public static List<CellGroup> filterValidCellGroups(List<CellGroup> forwardCellGroups) {
        List<CellGroup> result = new ArrayList<CellGroup>();
        for (CellGroup forwardCellGroup : forwardCellGroups) {
            if (forwardCellGroup.hasInvalidCell()) {
                continue;
            }
            result.add(forwardCellGroup);
        }
        return result;
    }

    public static Set<IValue> findForwardValues(IValue preferredValue, Set<IValue> conclusionValues, SimilarityConfiguration similarityConfiguration) {
        Set<IValue> forwardValues = new HashSet<IValue>();
        for (IValue conclusionValue : conclusionValues) {
            if (CostManagerUtility.areSimilar(preferredValue, conclusionValue, similarityConfiguration)) {
                forwardValues.add(conclusionValue);
            }
        }
        return forwardValues;
    }

    public static boolean hasOccurrencesInTupleOIDs(CellGroup witnessCellGroup, Set<TupleOID> forwardTupleOIDs) {
        for (CellGroupCell occurrence : witnessCellGroup.getOccurrences()) {
            if (forwardTupleOIDs.contains(occurrence.getTupleOID())) {
                return true;
            }
        }
        return false;
    }

    public static Repair generateSymmetricRepairWithBackwards(List<EGDEquivalenceClassTuple> forwardTuples, Collection<EGDEquivalenceClassTuple> backwardTuples, List<CellGroup> backwardCellGroups, Scenario scenario) {
        Repair repair = new Repair();
        if (forwardTuples.size() > 1 && haveDifferentConclusionValues(forwardTuples)) {
//        if (forwardTupleGroups.size() > 1) {
            ChangeDescription forwardChanges = CostManagerUtility.generateChangeDescriptionForSymmetricForwardRepair(forwardTuples, scenario);
            repair.addViolationContext(forwardChanges);
        }
        for (CellGroup originalBackwardCellGroup : backwardCellGroups) {
            CellGroup backwardCellGroup = originalBackwardCellGroup.clone();
            LLUNValue llunValue = CellGroupIDGenerator.getNextLLUNID();
            backwardCellGroup.setValue(llunValue);
            backwardCellGroup.setInvalidCell(CellGroupIDGenerator.getNextInvalidCell());
            ChangeDescription backwardChangesForGroup = new ChangeDescription(backwardCellGroup, LunaticConstants.CHASE_BACKWARD, CostManagerUtility.buildWitnessCells(backwardTuples));
            repair.addViolationContext(backwardChangesForGroup);
        }
        return repair;
    }

    private static boolean haveDifferentConclusionValues(List<EGDEquivalenceClassTuple> forwardTupleGroups) {
        IValue firstValue = forwardTupleGroups.get(0).getConclusionGroup().getValue();
        for (EGDEquivalenceClassTuple forwardTupleGroup : forwardTupleGroups) {
            IValue otherValue = forwardTupleGroup.getConclusionGroup().getValue();
            if (!firstValue.equals(otherValue)) {
                return true;
            }
        }
        return false;
    }

    public static Set<Dependency> findAffectedDependencies(CellGroup cellGroup, Scenario scenario) {
        Set<Dependency> result = new HashSet<Dependency>();
        for (CellGroupCell occurrence : cellGroup.getOccurrences()) {
            AttributeRef attributeRef = occurrence.getAttributeRef();
            result.addAll(scenario.getStratification().getDependenciesForAttribute(attributeRef));
        }
        return result;
    }

    public static SimilarityConfiguration findSimilarityConfigurationForCells(Collection<CellGroupCell> cells, CostManagerConfiguration costManagerConfiguration) {
        SimilarityConfiguration similarityConfiguration = costManagerConfiguration.getSimilarityConfiguration(cells.iterator().next().getAttributeRef());
        for (CellGroupCell constantCell : cells) {
            SimilarityConfiguration similarityConfigurationForAttribute = costManagerConfiguration.getSimilarityConfiguration(constantCell.getAttributeRef());
            if (!similarityConfiguration.equals(similarityConfigurationForAttribute)) {
                throw new IllegalArgumentException("Cells that are handled together must have the same similarity configuration. " + constantCell);
            }
        }
        return similarityConfiguration;
    }

    public static SimilarityConfiguration findSimilarityConfigurationForCellGroups(List<CellGroup> cellGroups, CostManagerConfiguration costManagerConfiguration) {
        List<CellGroupCell> occurrences = new ArrayList<CellGroupCell>();
        for (CellGroup forwardCellGroup : cellGroups) {
            occurrences.addAll(forwardCellGroup.getOccurrences());
        }
        return findSimilarityConfigurationForCells(occurrences, costManagerConfiguration);
    }
}
