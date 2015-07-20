package it.unibas.lunatic.model.chase.chasemc.costmanager;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.OperatorFactory;
import it.unibas.lunatic.utility.LunaticUtility;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.algebra.operators.IInsertTuple;
import it.unibas.lunatic.model.chase.commons.ChaseUtility;
import it.unibas.lunatic.model.chase.commons.IChaseSTTGDs;
import it.unibas.lunatic.model.chase.chasemc.BackwardAttribute;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import it.unibas.lunatic.model.chase.chasemc.ChangeSet;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.TargetCellsToChange;
import it.unibas.lunatic.model.chase.chasemc.ChaseMCScenario;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClass;
import it.unibas.lunatic.model.chase.chasemc.operators.ChaseDeltaExtEGDs;
import it.unibas.lunatic.model.chase.chasemc.operators.CheckSolution;
import it.unibas.lunatic.model.chase.chasemc.operators.IBuildDatabaseForChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.IBuildDeltaDB;
import it.unibas.lunatic.model.chase.chasemc.operators.IChaseDeltaExtTGDs;
import it.unibas.lunatic.model.chase.chasemc.operators.IInsertTuplesForTGDs;
import it.unibas.lunatic.model.chase.chasemc.operators.IRunQuery;
import it.unibas.lunatic.model.chase.chasemc.operators.OccurrenceHandlerMC;
import it.unibas.lunatic.model.chase.chasemc.partialorder.IPartialOrder;
import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.database.Cell;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.model.database.LLUNValue;
import it.unibas.lunatic.model.database.NullValue;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.model.dependency.FormulaVariableOccurrence;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractCostManager implements ICostManager {

    private static Logger logger = LoggerFactory.getLogger(AbstractCostManager.class);

    private boolean doBackward = true;
    private boolean doPermutations = true;
    private int chaseBranchingThreshold = 50;
    private int potentialSolutionsThreshold = 50;
    private int dependencyLimit = -1;

    public ChaseMCScenario getChaser(Scenario scenario) {
        IChaseSTTGDs stChaser = OperatorFactory.getInstance().getSTChaser(scenario);
        IBuildDeltaDB deltaBuilder = OperatorFactory.getInstance().getDeltaDBBuilder(scenario);
        IBuildDatabaseForChaseStep stepBuilder = OperatorFactory.getInstance().getDatabaseBuilder(scenario);
        IRunQuery queryRunner = OperatorFactory.getInstance().getQueryRunner(scenario);
        IInsertTuple insertOperatorForEgds = OperatorFactory.getInstance().getInsertTuple(scenario);
        IInsertTuplesForTGDs insertOperatorForTgds = OperatorFactory.getInstance().getInsertTuplesForTgds(scenario);
        OccurrenceHandlerMC occurrenceHandler = OperatorFactory.getInstance().getOccurrenceHandlerMC(scenario);
        IChaseDeltaExtTGDs extTgdChaser = OperatorFactory.getInstance().getExtTgdChaser(scenario);
        CheckSolution solutionChecker = OperatorFactory.getInstance().getSolutionChecker(scenario);
        ChaseDeltaExtEGDs egdChaser = OperatorFactory.getInstance().getEGDChaser(scenario);
        return new ChaseMCScenario(stChaser, extTgdChaser, deltaBuilder, stepBuilder, queryRunner, insertOperatorForEgds, insertOperatorForTgds, occurrenceHandler, egdChaser, solutionChecker);
    }

    protected boolean isNotViolation(List<TargetCellsToChange> tupleGroups, Scenario scenario) {
        if (logger.isDebugEnabled()) logger.debug("Checking violations between tuple groups\n" + LunaticUtility.printCollection(tupleGroups));
        List<CellGroup> cellGroups = extractCellGroups(tupleGroups);
        Set<IValue> differentValues = findDifferentValuesInCellGroupsWithOccurrences(cellGroups);
        if (differentValues.size() > 1) {
            return false;
        }
        return checkContainment(cellGroups, scenario);
    }

    public boolean checkContainment(List<CellGroup> cellGroups, Scenario scenario) {
        Set<CellGroupCell> allCells = new HashSet<CellGroupCell>();
        for (CellGroup cellGroup : cellGroups) {
            allCells.addAll(cellGroup.getAllCells());
        }
        for (CellGroup cellGroup : cellGroups) {
            if (cellGroup.getAllCells().equals(allCells)) {
                return true;
            }
        }
        return false;
    }

//    public boolean checkIfLUBIsIdempotent(List<CellGroup> cellGroups, Scenario scenario) {
//    public boolean checkContainment(List<CellGroup> cellGroups, Scenario scenario) {
//        CellGroup scriptLUB = getLUB(cellGroups, scenario.getScriptPartialOrder(), scenario);
//        CellGroup baseLUB = getLUB(cellGroups, scenario.getPartialOrder(), scenario);
//        if (logger.isDebugEnabled()) logger.debug("Base LUB: " + baseLUB + "\nScript LUB: " + scriptLUB);
//        if (cellGroups.contains(scriptLUB) || cellGroups.contains(baseLUB)) {
//            if (logger.isDebugEnabled()) logger.debug("Tuple groups are not a violation.");
//            return true;
//        }
//        return false;
//    }
    protected Set<IValue> findDifferentValuesInCellGroupsWithOccurrences(List<CellGroup> cellGroups) {
        Set<IValue> result = new HashSet<IValue>();
        for (CellGroup cellGroup : cellGroups) {
            if (cellGroup.getOccurrences().isEmpty()) {
                continue;
            }
            result.add(cellGroup.getValue());
        }
        return result;
    }

    protected boolean isSuspicious(CellGroup cellGroup, BackwardAttribute backwardAttribute, EquivalenceClass equivalenceClass) {
        Dependency egd = equivalenceClass.getDependency();
        FormulaVariable variable = backwardAttribute.getVariable();
        if (logger.isDebugEnabled()) logger.debug("Checking if cell group is suspicious:\n" + cellGroup + "\nEGD: " + egd + "\nVariable: " + variable + "\nVariable occurrence " + variable.getPremiseRelationalOccurrences());
        if (variable.getPremiseRelationalOccurrences().size() > cellGroup.getOccurrences().size()) {
            return false;
        }
        List<Cell> occurrences = new ArrayList<Cell>(cellGroup.getOccurrences());
        for (FormulaVariableOccurrence occurrence : variable.getPremiseRelationalOccurrences()) {
            if (logger.isDebugEnabled()) logger.debug("Variable occurrence: " + occurrence.toLongString());
            if (occurrence.getAttributeRef().isSource()) {
                continue;
            }
            AttributeRef attributeRef = ChaseUtility.unAlias(occurrence.getAttributeRef());
            if (!containsAndRemove(occurrences, attributeRef)) {
                if (logger.isDebugEnabled()) logger.debug("Cell groups occurrences " + cellGroup.getOccurrences() + " doesn't contains " + attributeRef + " so it disruts a join");
                return false;
            }
        }
        if (logger.isDebugEnabled()) logger.debug("Cell group is suspicious, returing true...");
        return true;
    }

    private boolean containsAndRemove(List<Cell> occurrences, AttributeRef attributeRef) {
        for (Iterator<Cell> it = occurrences.iterator(); it.hasNext();) {
            Cell cell = it.next();
            if (cell.getAttributeRef().equals(attributeRef)) {
                it.remove();
                return true;
            }
        }
        return false;
    }

//    protected boolean isTreeSizeBelowThreshold(int chaseTreeSize, int potentialSolutions, int repairsForDependenciesSize) {
//        return Math.max(chaseTreeSize, repairsForDependenciesSize) < getChaseTreeSizeThreshold();
//}
    protected boolean isTreeSizeBelowThreshold(int chaseTreeSize, int potentialSolutions) {
        boolean isBelow = (chaseTreeSize < this.chaseBranchingThreshold && potentialSolutions < this.potentialSolutionsThreshold);
        if (!isBelow && logger.isDebugEnabled()) {
            logger.debug("chaseTreeSize: " + chaseTreeSize + "\nchaseTreeSizeThreshold: "
                    + chaseBranchingThreshold + "\npotentialSolutions: "
                    + potentialSolutions + "\npotentialSolutionsThreshold: "
                    + potentialSolutionsThreshold);
        }
        return isBelow;
    }

    protected ChangeSet generateForwardRepair(List<TargetCellsToChange> tupleGroups, Scenario scenario, IDatabase deltaDB, String stepId) {
        List<CellGroup> cellGroups = extractCellGroups(tupleGroups);
        // give preference to the script partial order, that may have additional rules to solve the violation
        CellGroup lub = getLUB(cellGroups, scenario.getScriptPartialOrder(), scenario);
        if (lub == null) {
            lub = getLUB(cellGroups, scenario.getPartialOrder(), scenario);
        }
        ChangeSet changeSet = new ChangeSet(lub, LunaticConstants.CHASE_FORWARD, buildWitnessCellGroups(tupleGroups));
        return changeSet;
    }

    protected List<CellGroup> extractCellGroups(List<TargetCellsToChange> tupleGroups) {
        List<CellGroup> cellGroups = new ArrayList<CellGroup>();
        for (TargetCellsToChange tupleGroup : tupleGroups) {
            cellGroups.add(tupleGroup.getCellGroupForForwardRepair().clone());
        }
        return cellGroups;
    }

    protected CellGroup getLUB(List<CellGroup> cellGroups, IPartialOrder po, Scenario scenario) {
        if (po == null) {
            return null;
        }
        if (!po.canHandleAttributes(LunaticUtility.extractAttributesInCellGroups(cellGroups))) {
            return null;
        }
        return po.findLUB(cellGroups, scenario);
    }

    protected List<CellGroup> buildWitnessCellGroups(List<TargetCellsToChange> tupleGroups) {
        List<CellGroup> witnessCellGroups = new ArrayList<CellGroup>();
        for (TargetCellsToChange targetCellsToChange : tupleGroups) {
            LunaticUtility.addAllIfNotContained(witnessCellGroups, targetCellsToChange.getCellGroupsForBackwardRepairs().values());
        }
        List<CellGroup> result = new ArrayList<CellGroup>();
        for (CellGroup cellGroup : witnessCellGroups) {
            result.add(cellGroup.clone());
        }
        return result;
    }

    public List<Dependency> selectDependenciesToChase(List<Dependency> unsatisfiedDependencies, DeltaChaseStep chaseRoot) {
        if (logger.isDebugEnabled()) logger.debug("Selecting dependencies to chase - Unsatisfied " + LunaticUtility.printDependencyIds(unsatisfiedDependencies));
        if (unsatisfiedDependencies.isEmpty()) {
            return unsatisfiedDependencies;
        }
        List<Dependency> result = new ArrayList<Dependency>();
//        int chaseTreeSize = chaseRoot.getPotentialSolutions();
        int numberOfLeaves = chaseRoot.getNumberOfLeaves();
        int potentialSolutions = chaseRoot.getPotentialSolutions();
        if (dependencyLimit == 1 || !isTreeSizeBelowThreshold(numberOfLeaves, potentialSolutions)) {
            result.add(unsatisfiedDependencies.get(0));
            if (logger.isDebugEnabled()) logger.debug("To chase: " + LunaticUtility.printDependencyIds(result));
            return result;
        }
        if (dependencyLimit == -1) {
            if (logger.isDebugEnabled()) logger.debug("Returning all...");
            return unsatisfiedDependencies;
        }
        int dependencies = Math.min(unsatisfiedDependencies.size(), dependencyLimit);
        for (int i = 0; i < dependencies; i++) {
            result.add(unsatisfiedDependencies.get(i));
        }
        if (logger.isDebugEnabled()) logger.debug("To chase: " + LunaticUtility.printDependencyIds(result));
        return result;
    }

    protected boolean backwardIsAllowed(CellGroup cellGroup) {
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

    public boolean isDoBackward() {
        return doBackward;
    }

    public void setDoBackward(boolean doBackward) {
        this.doBackward = doBackward;
    }

    public boolean isDoPermutations() {
        return doPermutations;
    }

    public void setDoPermutations(boolean doPermutations) {
        this.doPermutations = doPermutations;
        if (doPermutations == false) {
            this.dependencyLimit = 1;
        }
    }

    public int getDependencyLimit() {
        return dependencyLimit;
    }

    public void setDependencyLimit(int dependencyLimit) {
        this.dependencyLimit = dependencyLimit;
    }

    public int getChaseBranchingThreshold() {
        return chaseBranchingThreshold;
    }

    public void setChaseBranchingThreshold(int chaseTreeThreshold) {
        this.chaseBranchingThreshold = chaseTreeThreshold;
    }

    public int getPotentialSolutionsThreshold() {
        return potentialSolutionsThreshold;
    }

    public void setPotentialSolutionsThreshold(int potentialSolutionsThreshold) {
        this.potentialSolutionsThreshold = potentialSolutionsThreshold;
    }

    @Override
    public String toLongString() {
        return toString() + "\n\tdoBackward=" + doBackward
                + "\n\tdoPermutations=" + doPermutations
                + "\n\tchaseTreeSizeThreshold=" + chaseBranchingThreshold
                + "\n\tpotentialSolutionsThreshold=" + potentialSolutionsThreshold
                + "\n\tdependencyLimit=" + dependencyLimit;
    }
}
