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
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.EGDEquivalenceClassCells;
import it.unibas.lunatic.model.chase.chasemc.ChaseMCScenario;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClassForEGD;
import it.unibas.lunatic.model.chase.chasemc.Repair;
import it.unibas.lunatic.model.chase.chasemc.ViolationContext;
import it.unibas.lunatic.model.chase.chasemc.operators.CellGroupIDGenerator;
import it.unibas.lunatic.model.chase.chasemc.operators.ChaseDeltaExtEGDs;
import it.unibas.lunatic.model.chase.chasemc.operators.CheckSatisfactionAfterUpgradesEGD;
import it.unibas.lunatic.model.chase.chasemc.operators.CheckSolution;
import it.unibas.lunatic.model.chase.chasemc.operators.IBuildDatabaseForChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.IBuildDeltaDB;
import it.unibas.lunatic.model.chase.chasemc.operators.IChaseDeltaExtTGDs;
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

    protected CheckSatisfactionAfterUpgradesEGD satisfactionChecker = new CheckSatisfactionAfterUpgradesEGD();

    public ChaseMCScenario getChaser(Scenario scenario) {
        IChaseSTTGDs stChaser = OperatorFactory.getInstance().getSTChaser(scenario);
        IBuildDeltaDB deltaBuilder = OperatorFactory.getInstance().getDeltaDBBuilder(scenario);
        IBuildDatabaseForChaseStep stepBuilder = OperatorFactory.getInstance().getDatabaseBuilder(scenario);
        IRunQuery queryRunner = OperatorFactory.getInstance().getQueryRunner(scenario);
        IInsertTuple insertOperatorForEgds = OperatorFactory.getInstance().getInsertTuple(scenario);
        OccurrenceHandlerMC occurrenceHandler = OperatorFactory.getInstance().getOccurrenceHandlerMC(scenario);
        IChaseDeltaExtTGDs extTgdChaser = OperatorFactory.getInstance().getExtTgdChaser(scenario);
        CheckSolution solutionChecker = OperatorFactory.getInstance().getSolutionChecker(scenario);
        ChaseDeltaExtEGDs egdChaser = OperatorFactory.getInstance().getEGDChaser(scenario);
        return new ChaseMCScenario(stChaser, extTgdChaser, deltaBuilder, stepBuilder, queryRunner, insertOperatorForEgds, occurrenceHandler, egdChaser, solutionChecker);
    }

    protected OccurrenceHandlerMC getOccurrenceHandler(Scenario scenario) {
        return OperatorFactory.getInstance().getOccurrenceHandlerMC(scenario);
    }

    protected boolean isSuspicious(CellGroup cellGroup, BackwardAttribute backwardAttribute, EquivalenceClassForEGD equivalenceClass) {
        Dependency egd = equivalenceClass.getEGD();
        FormulaVariable variable = backwardAttribute.getVariable();
        if (logger.isDebugEnabled()) logger.debug("Checking if cell group is suspicious:\n" + cellGroup + "\nEGD: " + egd + "\nVariable: " + variable + "\nVariable occurrence " + variable.getPremiseRelationalOccurrences());
        // If premise contains more occurrences than the ones we are changing, it is guareanteed that the join is disrupted
        if (variable.getPremiseRelationalOccurrences().size() > cellGroup.getOccurrences().size()) {
            return false;
        }
        List<Cell> occurrences = new ArrayList<Cell>(cellGroup.getOccurrences());
        for (FormulaVariableOccurrence occurrence : variable.getPremiseRelationalOccurrences()) {
            if (logger.isDebugEnabled()) logger.debug("Variable occurrence: " + occurrence.toLongString());
            if (occurrence.getAttributeRef().isSource()) {
//                continue;
                //TODO Check
                return false;
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
    
    protected Repair generateForwardRepair(List<EGDEquivalenceClassCells> tupleGroups, Scenario scenario, IDatabase deltaDB, String stepId) {
        Repair repair = new Repair();
        ViolationContext forwardChanges = generateViolationContextForForwardRepair(tupleGroups, scenario, deltaDB, stepId);
        if (logger.isDebugEnabled()) logger.debug("Forward changes: " + forwardChanges);
        repair.addViolationContext(forwardChanges);
        return repair;
    }

    protected ViolationContext generateViolationContextForForwardRepair(List<EGDEquivalenceClassCells> tupleGroups, Scenario scenario, IDatabase deltaDB, String stepId) {
        List<CellGroup> cellGroups = extractForwardCellGroups(tupleGroups);
        // give preference to the script partial order, that may have additional rules to solve the violation
        CellGroup lub = getLUB(cellGroups, scenario);
        ViolationContext changeSet = new ViolationContext(lub, LunaticConstants.CHASE_FORWARD, buildWitnessCells(tupleGroups));
        return changeSet;
    }

    protected Repair generateRepairWithBackwards(EquivalenceClassForEGD equivalenceClass, List<EGDEquivalenceClassCells> forwardTupleGroups, List<EGDEquivalenceClassCells> backwardTupleGroups, BackwardAttribute backwardAttribute,
            Scenario scenario, IDatabase deltaDB, String stepId) {
        Repair repair = new Repair();
        if (forwardTupleGroups.size() > 1) {
            ViolationContext forwardChanges = generateViolationContextForForwardRepair(forwardTupleGroups, scenario, deltaDB, stepId);
            repair.addViolationContext(forwardChanges);
        }
        for (EGDEquivalenceClassCells backwardTupleGroup : backwardTupleGroups) {
            Set<CellGroup> backwardCellGroups = backwardTupleGroup.getCellGroupsForBackwardRepair().get(backwardAttribute);
            for (CellGroup backwardCellGroup : backwardCellGroups) {
                LLUNValue llunValue = CellGroupIDGenerator.getNextLLUNID();
                backwardCellGroup.setValue(llunValue);
                backwardCellGroup.setInvalidCell(CellGroupIDGenerator.getNextInvalidCell());
                ViolationContext backwardChangesForGroup = new ViolationContext(backwardCellGroup, LunaticConstants.CHASE_BACKWARD, buildWitnessCells(backwardTupleGroups));
                repair.addViolationContext(backwardChangesForGroup);
                if (scenario.getConfiguration().isRemoveSuspiciousSolutions() && isSuspicious(backwardCellGroup, backwardAttribute, equivalenceClass)) {
                    backwardTupleGroup.setSuspicious(true);
                    repair.setSuspicious(true);
                }
            }
        }
        return repair;
    }    
    
    protected List<CellGroup> extractForwardCellGroups(List<EGDEquivalenceClassCells> tupleGroups) {
        List<CellGroup> cellGroups = new ArrayList<CellGroup>();
        for (EGDEquivalenceClassCells tupleGroup : tupleGroups) {
            cellGroups.add(tupleGroup.getCellGroupForForwardRepair().clone());
        }
        return cellGroups;
    }

    protected CellGroup getLUB(List<CellGroup> cellGroups, Scenario scenario) {
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

    protected Set<Cell> buildWitnessCells(List<EGDEquivalenceClassCells> equivalenceClassCellList) {
        Set<Cell> witnessCells = new HashSet<Cell>();
        for (EGDEquivalenceClassCells equivalenceClassCells : equivalenceClassCellList) {
            for (Set<Cell> witnessCellsInEquivalenceClass : equivalenceClassCells.getWitnessCells().values()) {
                witnessCells.addAll(witnessCellsInEquivalenceClass);
            }
        }
        return witnessCells;
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

    protected boolean backwardIsAllowed(Set<CellGroup> cellGroups) {
        for (CellGroup cellGroup : cellGroups) {
            if (!backwardIsAllowed(cellGroup)) {
                return false;
            }
        }
        return true;
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
