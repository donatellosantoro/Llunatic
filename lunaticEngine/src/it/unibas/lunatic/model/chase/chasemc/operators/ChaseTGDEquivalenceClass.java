package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.algebra.IAlgebraOperator;
import it.unibas.lunatic.model.algebra.operators.ITupleIterator;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClassForTGD;
import it.unibas.lunatic.model.chase.chasemc.TargetCellsToInsertForTGD;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.chase.commons.ChaseUtility;
import it.unibas.lunatic.model.chase.commons.control.IChaseState;
import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.database.CellRef;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.model.database.NullValue;
import it.unibas.lunatic.model.database.TableAlias;
import it.unibas.lunatic.model.database.Tuple;
import it.unibas.lunatic.model.database.TupleOID;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.model.dependency.FormulaVariableOccurrence;
import it.unibas.lunatic.model.generators.IValueGenerator;
import it.unibas.lunatic.utility.DependencyUtility;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChaseTGDEquivalenceClass {

    private static Logger logger = LoggerFactory.getLogger(ChaseTGDEquivalenceClass.class);

    private CorrectCellGroupID cellGroupIDFixer = new CorrectCellGroupID();
    private IRunQuery queryRunner;
    private IOIDGenerator oidGenerator;
    private OccurrenceHandlerMC occurrenceHandler;
    private ChangeCell cellChanger;

    public ChaseTGDEquivalenceClass(IRunQuery queryRunner, IOIDGenerator oidGenerator, OccurrenceHandlerMC occurrenceHandler, ChangeCell cellChanger) {
        this.queryRunner = queryRunner;
        this.oidGenerator = oidGenerator;
        this.occurrenceHandler = occurrenceHandler;
        this.cellChanger = cellChanger;
    }

    public boolean chaseDependency(DeltaChaseStep currentNode, Dependency tgd, IAlgebraOperator query,
            Scenario scenario, IChaseState chaseState, IDatabase databaseForStep) {
        if (logger.isDebugEnabled()) logger.debug("** Chasing dependency " + tgd);
        if (logger.isDebugEnabled()) logger.debug("Database " + databaseForStep.printInstances());
        if (logger.isDebugEnabled()) logger.debug("DeltaDB " + currentNode.getDeltaDB().printInstances());
        if (logger.isTraceEnabled()) logger.debug("Query " + query);
        long violationQueryStart = new Date().getTime();
        ITupleIterator it = queryRunner.run(query, scenario.getSource(), databaseForStep);
        long violationQueryEnd = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.TGD_VIOLATION_QUERY_TIME, violationQueryEnd - violationQueryStart);
        List<EquivalenceClassForTGD> equivalenceClasses = new ArrayList<EquivalenceClassForTGD>();
        Map<CellGroupCell, List<TargetCellsToInsertForTGD>> cellGroupMap = new HashMap<CellGroupCell, List<TargetCellsToInsertForTGD>>();
        List<IValue> lastUniversalValues = null;
        while (it.hasNext()) {
            Tuple tuple = it.next();
            List<IValue> universalValuesInConclusion = DependencyUtility.extractUniversalValuesInConclusion(tuple, tgd);
            if (logger.isDebugEnabled()) logger.debug("Detected violation on values " + universalValuesInConclusion);
            if (lastUniversalValues == null || LunaticUtility.areDifferentConsideringOrder(lastUniversalValues, universalValuesInConclusion)) {
                //New equivalence class
                if (logger.isDebugEnabled()) logger.debug("New equivalence class with universal values " + universalValuesInConclusion);
                EquivalenceClassForTGD equivalenceClass = new EquivalenceClassForTGD(tgd);
                equivalenceClasses.add(equivalenceClass);
                lastUniversalValues = universalValuesInConclusion;
            }
            EquivalenceClassForTGD equivalenceClass = equivalenceClasses.get(equivalenceClasses.size() - 1);
            addTupleInEquivalenceClass(tuple, equivalenceClass, tgd, cellGroupMap);
        }
        it.close();
        if (logger.isDebugEnabled()) logger.debug("Equivalence classes\n " + LunaticUtility.printCollection(equivalenceClasses));
        if (logger.isDebugEnabled()) logger.debug("CellGroup Map\n " + LunaticUtility.printMap(cellGroupMap));
        List<TargetCellsToInsertForTGD> updates = generateUpdates(cellGroupMap, currentNode.getDeltaDB(), currentNode.getId(), scenario);
        applyChanges(updates, currentNode.getDeltaDB(), currentNode.getId(), scenario);
        if (logger.isDebugEnabled()) logger.debug("** Updates " + LunaticUtility.printCollection(updates));
        return !updates.isEmpty();
    }

    private void addTupleInEquivalenceClass(Tuple tuple, EquivalenceClassForTGD equivalenceClass, Dependency tgd, Map<CellGroupCell, List<TargetCellsToInsertForTGD>> cellGroupMap) {
        List<FormulaVariable> universalVariables = DependencyUtility.getUniversalVariablesInConclusion(tgd);
        for (FormulaVariable universalVariable : universalVariables) {
            List<CellGroupCell> cellsForVariable = findPremiseCells(tuple, universalVariable, tgd);
            TargetCellsToInsertForTGD targetCellsToInsert = equivalenceClass.getTargetCellForVariable(universalVariable);
            if (targetCellsToInsert == null) {
                IValue cellGroupValue = cellsForVariable.get(0).getValue();
                targetCellsToInsert = new TargetCellsToInsertForTGD(cellGroupValue);
                equivalenceClass.setTargetCellToInsertForVariable(universalVariable, targetCellsToInsert);
            }
            CellGroup cellGroup = targetCellsToInsert.getCellGroup();
            for (CellGroupCell cellForVariable : cellsForVariable) {
                if (cellForVariable.getType().equals(LunaticConstants.TYPE_OCCURRENCE)) {
                    cellGroup.addOccurrenceCell(cellForVariable);
                }
                if (cellForVariable.getType().equals(LunaticConstants.TYPE_JUSTIFICATION)) {
                    cellGroup.addJustificationCell(cellForVariable);
                }
                addCellToCellGroupMap(cellForVariable, targetCellsToInsert, cellGroupMap);
            }
            if (!equivalenceClass.hasNewCellsForVariable(universalVariable)) {
                //New cells for equivalence class need to be generated only once
                addNewCells(universalVariable, cellGroup.getValue(), equivalenceClass, tgd);
            }
        }
        List<FormulaVariable> existentialVariables = DependencyUtility.getExistentialVariables(tgd);
        for (FormulaVariable existentialVariable : existentialVariables) {
            IValue nullValue = generateNullValueForVariable(existentialVariable, tgd.getTargetGenerators(), tuple);
            TargetCellsToInsertForTGD targetCellsToInsert = equivalenceClass.getTargetCellForVariable(existentialVariable);
            if (targetCellsToInsert == null) {
                targetCellsToInsert = new TargetCellsToInsertForTGD(nullValue);
                equivalenceClass.setTargetCellToInsertForVariable(existentialVariable, targetCellsToInsert);
            }
            if (!equivalenceClass.hasNewCellsForVariable(existentialVariable)) {
                //New cells for equivalence class need to be generated only once
                addNewCells(existentialVariable, nullValue, equivalenceClass, tgd);
                //Need to initialize cell groups for new cells, otherwise missing
                for (CellGroupCell nullCell : targetCellsToInsert.getNewCells()) {
                    addCellToCellGroupMap(nullCell, targetCellsToInsert, cellGroupMap);
                }
            }
        }
    }

    private List<CellGroupCell> findPremiseCells(Tuple tuple, FormulaVariable formulaVariable, Dependency tgd) {
        List<FormulaVariableOccurrence> premiseOccurrences = formulaVariable.getPremiseRelationalOccurrences();
        List<FormulaVariableOccurrence> positiveOccurrences = ChaseUtility.findPositiveOccurrences(tgd.getPremise().getPositiveFormula(), premiseOccurrences);
        List<AttributeRef> premiseAttributesForVariable = extractAttributeRefsForVariable(positiveOccurrences);
        List<CellGroupCell> premiseCells = new ArrayList<CellGroupCell>();
        for (AttributeRef attributeRef : premiseAttributesForVariable) {
            TupleOID originalOid = new TupleOID(ChaseUtility.getOriginalOid(tuple, attributeRef));
            CellRef cellRef = new CellRef(originalOid, ChaseUtility.unAlias(attributeRef));
            IValue cellValue = tuple.getCell(attributeRef).getValue();
            String type = LunaticConstants.TYPE_OCCURRENCE;
            if (cellRef.getAttributeRef().isSource()) {
                type = LunaticConstants.TYPE_JUSTIFICATION;
            }
            CellGroupCell cellGroupCell = new CellGroupCell(cellRef, cellValue, null, type, null);
            premiseCells.add(cellGroupCell);
        }
        return premiseCells;
    }

    private List<AttributeRef> extractAttributeRefsForVariable(List<FormulaVariableOccurrence> occurrences) {
        List<AttributeRef> result = new ArrayList<AttributeRef>();
        for (FormulaVariableOccurrence formulaVariableOccurrence : occurrences) {
            result.add(formulaVariableOccurrence.getAttributeRef());
        }
        return result;
    }

    private void addNewCells(FormulaVariable universalVariable, IValue value, EquivalenceClassForTGD equivalenceClass, Dependency tgd) {
        for (FormulaVariableOccurrence occurrence : universalVariable.getConclusionRelationalOccurrences()) {
            TableAlias tableAlias = occurrence.getTableAlias();
            AttributeRef attributeRef = occurrence.getAttributeRef();
            TupleOID tupleOID = equivalenceClass.getTupleOIDForTable(tableAlias);
            if (tupleOID == null) {
                tupleOID = new TupleOID(oidGenerator.getNextOID(tableAlias.getTableName()));
                equivalenceClass.putTupleOIDForTableAlias(tableAlias, tupleOID);
            }
            CellGroupCell newCell = new CellGroupCell(tupleOID, ChaseUtility.unAlias(attributeRef), value, new NullValue(LunaticConstants.NULL), LunaticConstants.TYPE_OCCURRENCE, true);
            equivalenceClass.getTargetCellForVariable(universalVariable).addNewCell(newCell);
        }
    }

    private void addCellToCellGroupMap(CellGroupCell cell, TargetCellsToInsertForTGD targetCellsToInsert, Map<CellGroupCell, List<TargetCellsToInsertForTGD>> cellMap) {
        List<TargetCellsToInsertForTGD> targetCellsForCell = cellMap.get(cell);
        if (targetCellsForCell == null) {
            targetCellsForCell = new ArrayList<TargetCellsToInsertForTGD>();
            cellMap.put(cell, targetCellsForCell);
        }
        if (!targetCellsForCell.contains(targetCellsToInsert)) {
            targetCellsForCell.add(targetCellsToInsert);
        }
    }

    private List<TargetCellsToInsertForTGD> generateUpdates(Map<CellGroupCell, List<TargetCellsToInsertForTGD>> cellMap, IDatabase deltaDB, String step, Scenario scenario) {
        Set<List<TargetCellsToInsertForTGD>> analizedSets = new HashSet<List<TargetCellsToInsertForTGD>>();
        List<TargetCellsToInsertForTGD> updates = new ArrayList<TargetCellsToInsertForTGD>();
        for (List<TargetCellsToInsertForTGD> group : cellMap.values()) {
            if (analizedSets.contains(group)) {
                continue;
            }
            analizedSets.add(group);
            TargetCellsToInsertForTGD mergedCellsToInsert = mergeAll(group);
            this.occurrenceHandler.enrichCellGroups(mergedCellsToInsert.getCellGroup(), deltaDB, step, scenario);
            this.cellGroupIDFixer.correctCellGroupId(mergedCellsToInsert.getCellGroup());
            checkAndSetOriginalValues(mergedCellsToInsert.getCellGroup());
            addNewCells(mergedCellsToInsert);
            updates.add(mergedCellsToInsert);
        }
        return updates;
    }

    private TargetCellsToInsertForTGD mergeAll(List<TargetCellsToInsertForTGD> group) {
        Iterator<TargetCellsToInsertForTGD> iterator = group.iterator();
        TargetCellsToInsertForTGD result = iterator.next();
        while (iterator.hasNext()) {
            TargetCellsToInsertForTGD otherCellsToInsert = iterator.next();
            CellGroupUtility.mergeCells(otherCellsToInsert.getCellGroup(), result.getCellGroup());
        }
        return result;
    }

    private void addNewCells(TargetCellsToInsertForTGD mergedCellsToInsert) {
        CellGroup cellGroup = mergedCellsToInsert.getCellGroup();
        for (CellGroupCell newCell : mergedCellsToInsert.getNewCells()) {
            newCell.setOriginalCellGroupId(cellGroup.getId());
            cellGroup.addOccurrenceCell(newCell);
        }
    }

    private void applyChanges(List<TargetCellsToInsertForTGD> updates, IDatabase deltaDB, String id, Scenario scenario) {
        for (TargetCellsToInsertForTGD update : updates) {
            this.cellChanger.changeCells(update.getCellGroup(), deltaDB, id, scenario);
        }
    }

    private IValue generateNullValueForVariable(FormulaVariable existentialVariable, Map<AttributeRef, IValueGenerator> targetGenerators, Tuple tuple) {
        IValueGenerator generator = targetGenerators.get(existentialVariable.getConclusionRelationalOccurrences().get(0).getAttributeRef());
        IValue value = generator.generateValue(tuple);
        return value;
    }

    private void checkAndSetOriginalValues(CellGroup cellGroup) {
        for (CellGroupCell cell : cellGroup.getAllCells()) {
            if (cell.getOriginalValue() == null) {
                cell.setOriginalValue(cell.getValue());
                cell.setToSave(true);
            }
        }
    }

}
