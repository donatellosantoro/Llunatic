package it.unibas.lunatic.model.chase.chasede.operators;

import it.unibas.lunatic.model.chase.chasemc.operators.*;
import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseFailedException;
import it.unibas.lunatic.model.chase.chasede.costmanager.ICostManagerDE;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import it.unibas.lunatic.model.chase.chasemc.ChangeDescription;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.DependencyVariables;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClassForEGD;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClassForEGDProxy;
import it.unibas.lunatic.model.chase.chasemc.Repair;
import it.unibas.lunatic.model.chase.chasemc.ViolationContext;
import it.unibas.lunatic.model.chase.chasemc.costmanager.CostManagerFactory;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.chase.commons.operators.ChaseUtility;
import it.unibas.lunatic.model.chase.commons.IChaseState;
import it.unibas.lunatic.model.dependency.ComparisonAtom;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.model.dependency.FormulaVariableOccurrence;
import it.unibas.lunatic.model.dependency.VariableEquivalenceClass;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.algebra.operators.ITupleIterator;
import speedy.model.database.AttributeRef;
import speedy.model.database.Cell;
import speedy.model.database.CellRef;
import speedy.model.database.IDatabase;
import speedy.model.database.IValue;
import speedy.model.database.TableAlias;
import speedy.model.database.Tuple;
import speedy.model.database.TupleOID;
import speedy.model.database.operators.IRunQuery;
import speedy.utility.comparator.StringComparator;

public class ChaseEGDEquivalenceClass implements IChaseEGDEquivalenceClass {

    private static final Logger logger = LoggerFactory.getLogger(ChaseEGDEquivalenceClass.class);

    private final IRunQuery queryRunner;
    private final OccurrenceHandlerDE occurrenceHandler;
    private final ChangeCellDE cellChanger;
    private Tuple lastTuple;
    private boolean lastTupleHandled;

    public ChaseEGDEquivalenceClass(IRunQuery queryRunner, OccurrenceHandlerDE occurrenceHandler, ChangeCellDE cellChanger) {
        this.queryRunner = queryRunner;
        this.occurrenceHandler = occurrenceHandler;
        this.cellChanger = cellChanger;
    }

    @Override
    public boolean chaseDependency(DeltaChaseStep rootNode, Dependency egd, IAlgebraOperator premiseQuery, Scenario scenario, IChaseState chaseState, IDatabase databaseForStep) {
        if (logger.isDebugEnabled()) logger.info("***** Step: " + rootNode.getId() + " - Chasing dependency: " + egd);
        if (logger.isTraceEnabled()) logger.trace(databaseForStep.printInstances());
        this.lastTuple = null;
        this.lastTupleHandled = false;
        DependencyVariables dv = buildDependencyVariables(egd);
        if (logger.isDebugEnabled()) logger.debug("Executing premise query: " + premiseQuery);
        if (logger.isTraceEnabled()) logger.debug("Result:\n" + LunaticUtility.printIterator(queryRunner.run(premiseQuery, scenario.getSource(), databaseForStep)));
        long violationQueryStart = new Date().getTime();
        ITupleIterator it = queryRunner.run(premiseQuery, scenario.getSource(), databaseForStep);
        long violationQueryEnd = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.EGD_VIOLATION_QUERY_TIME, violationQueryEnd - violationQueryStart);
        List<Repair> repairsForDependency = new ArrayList<Repair>();
        try {
            while (true) {
                long equivalenceClassStart = new Date().getTime();
                EquivalenceClassForEGD equivalenceClass = readNextEquivalenceClass(it, dv, rootNode.getDeltaDB(), rootNode.getId(), chaseState, scenario);
                long equivalenceClasEnd = new Date().getTime();
                ChaseStats.getInstance().addStat(ChaseStats.EGD_EQUIVALENCE_CLASS_TIME, equivalenceClasEnd - equivalenceClassStart);
                if (equivalenceClass == null) {
                    break;
                }
                ICostManagerDE costManager = CostManagerFactory.getCostManagerDE(egd, scenario);
                List<Repair> repairsForEquivalenceClass = costManager.chooseRepairStrategy(new EquivalenceClassForEGDProxy(equivalenceClass), rootNode.getRoot(), repairsForDependency, scenario, rootNode.getId(), occurrenceHandler);
                if (logger.isDebugEnabled()) logger.debug("Repairs for equivalence class: " + LunaticUtility.printCollection(repairsForEquivalenceClass));
                repairsForDependency = ChaseUtility.accumulateRepairs(repairsForDependency, repairsForEquivalenceClass);
                if (noMoreTuples(it)) {
                    break;
                }
                if (logger.isDebugEnabled()) logger.debug("Repairs for equivalence classes so far: " + repairsForDependency.size());
            }
        } catch (ChaseFailedException e) {
            throw e;
        } finally {
            it.close();
        }
        if (logger.isDebugEnabled()) logger.debug("Total repairs for dependency: " + LunaticUtility.printCollection(repairsForDependency));
        long repairStart = new Date().getTime();
        boolean changes = applyRepairs(rootNode, repairsForDependency, egd, scenario);
        long repairEnd = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.EGD_REPAIR_TIME, repairEnd - repairStart);
        return changes;
    }

    private boolean noMoreTuples(ITupleIterator it) {
        return (!it.hasNext() && lastTupleHandled);
    }

    private EquivalenceClassForEGD readNextEquivalenceClass(ITupleIterator it, DependencyVariables dv, IDatabase deltaDB, String stepId, IChaseState chaseState, Scenario scenario) {
        if (!it.hasNext() && (this.lastTupleHandled || this.lastTuple == null)) {
            return null;
        }
        int contextCounter = 0;
        Dependency egd = dv.getEgd();
        EquivalenceClassForEGD equivalenceClass = new EquivalenceClassForEGD(dv);
        if (lastTuple != null && !this.lastTupleHandled) {
            if (logger.isDebugEnabled()) logger.debug("Reading tuple : " + this.lastTuple.toStringWithOIDAndAlias());
            addTuple(this.lastTuple, equivalenceClass, contextCounter++, deltaDB, stepId, scenario);
            this.lastTupleHandled = true;
        }
        Set<String> analizedTupleFingerprints = new HashSet<String>();
        if (logger.isDebugEnabled()) logger.debug("Reading next equivalence class...");
        while (it.hasNext()) {
            if (chaseState.isCancelled()) ChaseUtility.stopChase(chaseState); //throw new ChaseException("Chase interrupted by user");
            Tuple tuple = it.next();
            if (logger.isDebugEnabled()) logger.debug("Reading tuple : " + tuple.toStringWithOIDAndAlias());
            if (scenario.getConfiguration().isDiscardDuplicateTuples()) {
                String fingerprint = generateFingerprintForTuple(tuple, dv);
                if (analizedTupleFingerprints.contains(fingerprint)) {
                    if (logger.isDebugEnabled()) logger.debug("Tuple " + tuple + " has been already analized");
                    continue;
                }
                analizedTupleFingerprints.add(fingerprint);
            }
            if (lastTuple == null || equivalenceClass.isEmpty() || EquivalenceClassUtility.sameEquivalenceClass(tuple, this.lastTuple, egd)) {
                addTuple(tuple, equivalenceClass, contextCounter++, deltaDB, stepId, scenario);
                this.lastTuple = tuple;
                this.lastTupleHandled = true;
            } else {
                if (logger.isDebugEnabled()) logger.debug("Equivalence class is finished...");
                if (equivalenceClass.isEmpty()) {
                    throw new IllegalArgumentException("Unable to create equivalence class for egd:\n" + egd + "\nLast tuple: \n" + lastTuple + "\nCurrent tuple: \n" + tuple);
                }
                this.lastTuple = tuple;
                this.lastTupleHandled = false;
                break;
            }
        }
        if (logger.isDebugEnabled()) logger.debug("Equivalence class loaded");
        if (logger.isDebugEnabled()) logger.debug("-------- Equivalence class:\n" + equivalenceClass + "\n---------------");
        return equivalenceClass;
    }

    private String generateFingerprintForTuple(Tuple tuple, DependencyVariables dv) {
        Map<String, Set<TupleOID>> tupleOidsMap = new HashMap<String, Set<TupleOID>>();
        for (VariableEquivalenceClass witnessVariable : dv.getWitnessVariables()) {
            List<FormulaVariableOccurrence> positiveRelationalOccurrences = ChaseUtility.findPositivePremiseOccurrences(dv.getEgd(), witnessVariable);
            for (FormulaVariableOccurrence relationalOccurrence : positiveRelationalOccurrences) {
                TableAlias tableAlias = relationalOccurrence.getTableAlias();
                AttributeRef premiseAttribute = relationalOccurrence.getAttributeRef();
                TupleOID originalOid = new TupleOID(ChaseUtility.getOriginalOid(tuple, premiseAttribute));
                addTupleOid(tableAlias, originalOid, tupleOidsMap);
            }
        }
        StringBuilder sb = new StringBuilder();
        List<String> sortedTableAlias = new ArrayList<String>(tupleOidsMap.keySet());
        Collections.sort(sortedTableAlias);
        for (String tableName : sortedTableAlias) {
            sb.append(tableName).append(LunaticConstants.FINGERPRINT_SEPARATOR);
            List<TupleOID> sortedTupleOIDs = new ArrayList<TupleOID>(tupleOidsMap.get(tableName));
            Collections.sort(sortedTupleOIDs, new StringComparator());
            for (TupleOID tupleOID : sortedTupleOIDs) {
                sb.append(tupleOID.toString()).append(LunaticConstants.FINGERPRINT_SEPARATOR);
            }
        }
        String tupleFingerprint = sb.toString();
        if (logger.isDebugEnabled()) logger.debug("Tuple fingerprint " + tupleFingerprint);
        return tupleFingerprint;
    }

    private void addTupleOid(TableAlias tableAlias, TupleOID tupleOID, Map<String, Set<TupleOID>> tupleOidsMap) {
        Set<TupleOID> tupleOIDs = tupleOidsMap.get(tableAlias.getTableName());
        if (tupleOIDs == null) {
            tupleOIDs = new HashSet<TupleOID>();
            tupleOidsMap.put(tableAlias.getTableName(), tupleOIDs);
        }
        tupleOIDs.add(tupleOID);
    }

    private void addTuple(Tuple tuple, EquivalenceClassForEGD equivalenceClass, int contextCounter, IDatabase deltaDB, String stepId, Scenario scenario) {
        if (logger.isDebugEnabled()) logger.trace("Adding tuple " + tuple + " to equivalence class: " + equivalenceClass);
        ViolationContext violationContext = new ViolationContext(contextCounter);
        //First creates cell groups for conclusion variable - forward repairs
        for (VariableEquivalenceClass conclusionClass : equivalenceClass.getDependencyVariables().getConclusionVariables()) {
            CellGroup cellGroupForVariable = findCellsForConclusionVariables(equivalenceClass.getEGD(), conclusionClass, tuple);
            CellGroup enrichedCellGroup = this.occurrenceHandler.enrichCellGroups(cellGroupForVariable, deltaDB, scenario);
            violationContext.setCellGroupForConclusionVariable(conclusionClass, enrichedCellGroup);
            indexContext(enrichedCellGroup, violationContext, equivalenceClass);
            indexConclusionValues(enrichedCellGroup, equivalenceClass);
        }
        equivalenceClass.addViolationContext(violationContext);
        if (logger.isDebugEnabled()) logger.trace("Equivalence class: " + equivalenceClass);
    }

    private CellGroup findCellsForConclusionVariables(Dependency egd, VariableEquivalenceClass veq, Tuple tuple) {
        IValue value = findValue(veq, tuple);
        CellGroup result = new CellGroup(value, true);
        for (FormulaVariable variable : veq.getVariables()) {
            List<FormulaVariableOccurrence> positiveRelationalOccurrences = ChaseUtility.findPositivePremiseOccurrences(egd, variable);
            for (FormulaVariableOccurrence premiseRelationalOccurrence : positiveRelationalOccurrences) {
                addCellToCellGroup(premiseRelationalOccurrence, tuple, result);
                addAdditionalAttributes(result, egd, tuple);
            }
        }
        return result;
    }

    private void addAdditionalAttributes(CellGroup cellGroup, Dependency egd, Tuple tuple) {
        for (AttributeRef additionalAttribute : egd.getAdditionalAttributes()) {
//            TupleOID originalOid = new TupleOID(ChaseUtility.getOriginalOid(tuple, additionalAttribute));
            for (Cell tupleCell : tuple.getCells()) {
                AttributeRef unaliasedAttribute = ChaseUtility.unAlias(tupleCell.getAttributeRef());
                if (!unaliasedAttribute.equals(additionalAttribute)) {
                    continue;
                }
                TupleOID originalOIDForCell = new TupleOID(ChaseUtility.getOriginalOid(tuple, tupleCell.getAttributeRef()));
//                if (!originalOIDForCell.equals(originalOid)) {
//                    continue;
//                }
                CellGroupCell additionalCellGroupCell = new CellGroupCell(originalOIDForCell, unaliasedAttribute, tupleCell.getValue(), null, LunaticConstants.TYPE_ADDITIONAL, null);
                cellGroup.addAdditionalCell(additionalAttribute, additionalCellGroupCell);
            }
        }
    }

    private void addCellToCellGroup(FormulaVariableOccurrence premiseRelationalOccurrence, Tuple tuple, CellGroup cellGroup) {
        IValue value = cellGroup.getValue();
        AttributeRef premiseAttribute = premiseRelationalOccurrence.getAttributeRef();
        TupleOID originalOid = new TupleOID(ChaseUtility.getOriginalOid(tuple, premiseAttribute));
        CellRef cellRef = new CellRef(originalOid, ChaseUtility.unAlias(premiseAttribute));
        if (premiseAttribute.isSource()) {
            CellGroupCell sourceCell = new CellGroupCell(cellRef, value, value, LunaticConstants.TYPE_JUSTIFICATION, null);
            cellGroup.addJustificationCell(sourceCell);
            return;
        }
        CellGroupCell targetCell = new CellGroupCell(cellRef, value, null, LunaticConstants.TYPE_OCCURRENCE, null);
        cellGroup.addOccurrenceCell(targetCell);
    }

    private IValue findValue(VariableEquivalenceClass veq, Tuple tuple) {
        AttributeRef firstOccurrence = veq.getPremiseRelationalOccurrences().get(0).getAttributeRef();
        return tuple.getCell(firstOccurrence).getValue();
    }

    private void indexContext(CellGroup cellGroup, ViolationContext violationContext, EquivalenceClassForEGD equivalenceClass) {
        for (Cell cell : cellGroup.getOccurrences()) {
            equivalenceClass.addViolationContextForCell(cell, violationContext);
        }
    }

    private void indexConclusionValues(CellGroup cellGroup, EquivalenceClassForEGD equivalenceClass) {
        for (Cell cell : cellGroup.getAllCells()) {
            IValue value = cell.getValue();
            equivalenceClass.addCellGroupForValue(value, cellGroup);
        }
    }

    private VariableEquivalenceClass findVariableEquivalenceClass(FormulaVariable v, Dependency egd) {
        for (VariableEquivalenceClass veq : egd.getPremise().getPositiveFormula().getLocalVariableEquivalenceClasses()) {
            if (veq.contains(v)) {
                return veq;
            }
        }
        throw new IllegalArgumentException("Unable to find variable equivalence class for variable " + v + "\n\t Premise: " + egd.getPremise());
    }

    private boolean applyRepairs(DeltaChaseStep rootNode, List<Repair> repairs, Dependency egd, Scenario scenario) {
        if (logger.isDebugEnabled()) logger.debug("---Applying repairs in step " + rootNode.getId() + " for egd " + egd.getId() + "...");
        for (int i = 0; i < repairs.size(); i++) {
            Repair repair = repairs.get(i);
            CellGroupUtility.checkCellGroupConsistency(repair);
            for (ChangeDescription changeSet : repair.getChangeDescriptions()) {
                if (logger.isDebugEnabled()) logger.debug("Applying change set " + changeSet);
                this.cellChanger.changeCells(changeSet.getCellGroup(), rootNode.getDeltaDB(), scenario);
            }
            if (logger.isDebugEnabled()) logger.debug("EGD " + egd.getId() + " is satisfied in this step...");
            ChaseDEEGDUtility.maintainSatisfiedEGDs(rootNode, egd, repair);
            if (logger.isDebugEnabled()) logger.debug("Changed root for repair: " + repair);
            if (logger.isDebugEnabled()) logger.debug(rootNode.getDeltaDB().printInstances());
        }
        ChaseDEEGDUtility.addSatisfiedEGD(rootNode, egd);
        this.cellChanger.flush(rootNode.getDeltaDB());
        return !repairs.isEmpty();
    }

    private DependencyVariables buildDependencyVariables(Dependency egd) {
        DependencyVariables dv = new DependencyVariables(egd);
        FormulaVariable v1 = ((ComparisonAtom) egd.getConclusion().getAtoms().get(0)).getVariables().get(0);
        VariableEquivalenceClass vEq1 = findVariableEquivalenceClass(v1, egd);
        dv.addConclusionVariable(vEq1);
        FormulaVariable v2 = ((ComparisonAtom) egd.getConclusion().getAtoms().get(0)).getVariables().get(1);
        VariableEquivalenceClass vEq2 = findVariableEquivalenceClass(v2, egd);
        dv.addConclusionVariable(vEq2);
        List<VariableEquivalenceClass> witnessVariables = ChaseUtility.findJoinVariablesInTarget(egd);
        dv.setWitnessVariables(witnessVariables);
        return dv;
    }

}
