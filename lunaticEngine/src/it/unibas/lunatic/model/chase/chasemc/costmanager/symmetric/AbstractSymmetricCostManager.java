package it.unibas.lunatic.model.chase.chasemc.costmanager.symmetric;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.OperatorFactory;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.commons.ChaseUtility;
import it.unibas.lunatic.model.chase.commons.IChaseSTTGDs;
import it.unibas.lunatic.model.chase.chasemc.BackwardAttribute;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.EGDEquivalenceClassCells;
import it.unibas.lunatic.model.chase.chasemc.ChaseMCScenario;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClassForSymmetricEGD;
import it.unibas.lunatic.model.chase.chasemc.Repair;
import it.unibas.lunatic.model.chase.chasemc.ChangeDescription;
import it.unibas.lunatic.model.chase.chasemc.costmanager.CostManagerUtility;
import it.unibas.lunatic.model.chase.chasemc.costmanager.ICostManager;
import it.unibas.lunatic.model.chase.chasemc.operators.CellGroupIDGenerator;
import it.unibas.lunatic.model.chase.chasemc.operators.ChaseDeltaExtEGDs;
import it.unibas.lunatic.model.chase.chasemc.operators.CheckSatisfactionAfterUpgradesEGD;
import it.unibas.lunatic.model.chase.chasemc.operators.CheckSolution;
import it.unibas.lunatic.model.chase.chasemc.operators.IBuildDatabaseForChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.IBuildDeltaDB;
import it.unibas.lunatic.model.chase.chasemc.operators.IChaseDeltaExtTGDs;
import it.unibas.lunatic.model.chase.chasemc.operators.OccurrenceHandlerMC;
import speedy.model.database.AttributeRef;
import speedy.model.database.Cell;
import speedy.model.database.IDatabase;
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
import speedy.model.algebra.operators.IInsertTuple;
import speedy.model.database.LLUNValue;
import speedy.model.database.operators.IRunQuery;

public abstract class AbstractSymmetricCostManager implements ICostManager {

    private static Logger logger = LoggerFactory.getLogger(AbstractSymmetricCostManager.class);

    protected CheckSatisfactionAfterUpgradesEGD satisfactionChecker = new CheckSatisfactionAfterUpgradesEGD();

    protected OccurrenceHandlerMC getOccurrenceHandler(Scenario scenario) {
        return OperatorFactory.getInstance().getOccurrenceHandlerMC(scenario);
    }

    protected boolean isSuspicious(CellGroup cellGroup, BackwardAttribute backwardAttribute, EquivalenceClassForSymmetricEGD equivalenceClass) {
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

    protected Repair generateSymmetricForwardRepair(List<EGDEquivalenceClassCells> tupleGroups, Scenario scenario, IDatabase deltaDB, String stepId) {
        Repair repair = new Repair();
        ChangeDescription forwardChanges = generateChangeDescriptionForForwardRepair(tupleGroups, scenario, deltaDB, stepId);
        if (logger.isDebugEnabled()) logger.debug("Forward changes: " + forwardChanges);
        repair.addViolationContext(forwardChanges);
        return repair;
    }

    protected ChangeDescription generateChangeDescriptionForForwardRepair(List<EGDEquivalenceClassCells> tupleGroups, Scenario scenario, IDatabase deltaDB, String stepId) {
        List<CellGroup> cellGroups = extractForwardCellGroups(tupleGroups);
        // give preference to the script partial order, that may have additional rules to solve the violation
        CellGroup lub = getLUB(cellGroups, scenario);
        ChangeDescription changeSet = new ChangeDescription(lub, LunaticConstants.CHASE_FORWARD, buildWitnessCells(tupleGroups));
        return changeSet;
    }

    protected Repair generateRepairWithBackwards(EquivalenceClassForSymmetricEGD equivalenceClass, List<EGDEquivalenceClassCells> forwardTupleGroups, List<EGDEquivalenceClassCells> backwardTupleGroups, BackwardAttribute backwardAttribute,
            Scenario scenario, IDatabase deltaDB, String stepId) {
        Repair repair = new Repair();
        if (forwardTupleGroups.size() > 1) {
            ChangeDescription forwardChanges = generateChangeDescriptionForForwardRepair(forwardTupleGroups, scenario, deltaDB, stepId);
            repair.addViolationContext(forwardChanges);
        }
        for (EGDEquivalenceClassCells backwardTupleGroup : backwardTupleGroups) {
            Set<CellGroup> backwardCellGroups = backwardTupleGroup.getCellGroupsForBackwardRepair().get(backwardAttribute);
            for (CellGroup backwardCellGroup : backwardCellGroups) {
                LLUNValue llunValue = CellGroupIDGenerator.getNextLLUNID();
                backwardCellGroup.setValue(llunValue);
                backwardCellGroup.setInvalidCell(CellGroupIDGenerator.getNextInvalidCell());
                ChangeDescription backwardChangesForGroup = new ChangeDescription(backwardCellGroup, LunaticConstants.CHASE_BACKWARD, buildWitnessCells(backwardTupleGroups));
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
        return CostManagerUtility.getLUB(cellGroups, scenario);
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


    protected List<EGDEquivalenceClassCells> extractSubset(List<Integer> subsetIndex, List<EGDEquivalenceClassCells> tupleGroups) {
        List<EGDEquivalenceClassCells> result = new ArrayList<EGDEquivalenceClassCells>();
        for (Integer index : subsetIndex) {
            result.add(tupleGroups.get(index));
        }
        return result;
    }
}
