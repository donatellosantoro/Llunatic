package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.utility.LunaticUtility;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseFailedException;
import it.unibas.lunatic.model.chase.chasemc.BackwardAttribute;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.chase.commons.ChaseUtility;
import it.unibas.lunatic.model.chase.commons.control.IChaseState;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import it.unibas.lunatic.model.chase.chasemc.ChangeDescription;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClassForSymmetricEGD;
import it.unibas.lunatic.model.chase.chasemc.Repair;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.EGDEquivalenceClassTuple;
import it.unibas.lunatic.model.chase.chasemc.NewChaseSteps;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClassForEGDProxy;
import it.unibas.lunatic.model.chase.chasemc.costmanager.CostManagerConfiguration;
import it.unibas.lunatic.model.chase.chasemc.costmanager.CostManagerFactory;
import it.unibas.lunatic.model.chase.chasemc.costmanager.ICostManager;
import it.unibas.lunatic.model.dependency.ComparisonAtom;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.ExtendedDependency;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
import speedy.model.database.Tuple;
import speedy.model.database.TupleOID;
import speedy.model.database.operators.IRunQuery;

public class ChaseSymmetricEGDEquivalenceClass implements IChaseEGDEquivalenceClass {

    private static final Logger logger = LoggerFactory.getLogger(ChaseSymmetricEGDEquivalenceClass.class);

    private final IRunQuery queryRunner;
    private final OccurrenceHandlerMC occurrenceHandler;
    private final IBuildDatabaseForChaseStep databaseBuilder;
    private final ChangeCell cellChanger;
    private Tuple lastTuple;
    private boolean lastTupleHandled;

    public ChaseSymmetricEGDEquivalenceClass(IRunQuery queryRunner, OccurrenceHandlerMC occurrenceHandler, IBuildDatabaseForChaseStep databaseBuilder, ChangeCell cellChanger) {
        this.queryRunner = queryRunner;
        this.occurrenceHandler = occurrenceHandler;
        this.databaseBuilder = databaseBuilder;
        this.cellChanger = cellChanger;
    }

    @Override
    public NewChaseSteps chaseDependency(DeltaChaseStep currentNode, Dependency egd, IAlgebraOperator premiseQuery, Scenario scenario, IChaseState chaseState, IDatabase databaseForStep) {
        if (logger.isDebugEnabled()) logger.debug("***** Step: " + currentNode.getId() + " - Chasing dependency: " + egd);
        this.lastTuple = null;
        this.lastTupleHandled = false;
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
                EquivalenceClassForSymmetricEGD equivalenceClass = readNextEquivalenceClass(it, egd, currentNode.getDeltaDB(), currentNode.getId(), chaseState, scenario);
                long equivalenceClasEnd = new Date().getTime();
                ChaseStats.getInstance().addStat(ChaseStats.EGD_EQUIVALENCE_CLASS_TIME, equivalenceClasEnd - equivalenceClassStart);
                if (equivalenceClass == null) {
                    break;
                }
                ICostManager costManager = CostManagerFactory.getCostManager(egd, scenario);
                List<Repair> repairsForEquivalenceClass = costManager.chooseRepairStrategy(new EquivalenceClassForEGDProxy(equivalenceClass), currentNode.getRoot(), repairsForDependency, scenario, currentNode.getId(), occurrenceHandler);
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
        NewChaseSteps newSteps = applyRepairs(currentNode, repairsForDependency, egd, premiseQuery, scenario);
        long repairEnd = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.EGD_REPAIR_TIME, repairEnd - repairStart);
        return newSteps;
    }

    private boolean noMoreTuples(ITupleIterator it) {
        return (!it.hasNext() && lastTupleHandled);
    }

    private EquivalenceClassForSymmetricEGD readNextEquivalenceClass(ITupleIterator it, Dependency egd, IDatabase deltaDB, String stepId, IChaseState chaseState, Scenario scenario) {
        if (!it.hasNext() && (this.lastTupleHandled || this.lastTuple == null)) {
            return null;
        }
        EquivalenceClassForSymmetricEGD equivalenceClass = createEquivalenceClass(egd);
        if (lastTuple != null && !this.lastTupleHandled) {
            if (logger.isDebugEnabled()) logger.debug("Reading tuple : " + this.lastTuple.toStringWithOIDAndAlias());
            addTuple(this.lastTuple, equivalenceClass, scenario.getCostManagerConfiguration(), deltaDB, stepId, scenario);
            this.lastTupleHandled = true;
        }
        if (logger.isDebugEnabled()) logger.debug("Reading next equivalence class...");
        while (it.hasNext()) {
            if (chaseState.isCancelled()) ChaseUtility.stopChase(chaseState); //throw new ChaseException("Chase interrupted by user");
            Tuple tuple = it.next();
            if (logger.isDebugEnabled()) logger.debug("Reading tuple : " + tuple.toStringWithOIDAndAlias());
            if (lastTuple == null || equivalenceClass.isEmpty() || EquivalenceClassUtility.sameEquivalenceClass(tuple, this.lastTuple, egd)) {
                addTuple(tuple, equivalenceClass, scenario.getCostManagerConfiguration(), deltaDB, stepId, scenario);
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

    private EquivalenceClassForSymmetricEGD createEquivalenceClass(Dependency egd) {
        FormulaVariable v1 = ((ComparisonAtom) egd.getConclusion().getAtoms().get(0)).getVariables().get(0);
        AttributeRef variableOccurrence = v1.getPremiseRelationalOccurrences().get(0).getAttributeRef();
        AttributeRef conclusionAttribute = EquivalenceClassUtility.correctAttributeForSymmetricEGDs(variableOccurrence, egd);
        List<BackwardAttribute> attributesForBackwardChasing = findAttributesForBackwardChasing(egd);
        return new EquivalenceClassForSymmetricEGD(egd, conclusionAttribute, attributesForBackwardChasing);
    }

    private List<BackwardAttribute> findAttributesForBackwardChasing(Dependency egd) {
        List<BackwardAttribute> attributesForBackwardChasing = new ArrayList<BackwardAttribute>();
        for (ExtendedDependency extendedDependency : egd.getExtendedDependencies()) {
            if (extendedDependency.isForward()) {
                continue;
            }
            AttributeRef occurrenceAttribute = EquivalenceClassUtility.correctAttributeForSymmetricEGDs(extendedDependency.getOccurrence().getAttributeRef(), egd);
            FormulaVariable variable = LunaticUtility.findPremiseVariableInDepedency(extendedDependency.getOccurrence(), egd);
            BackwardAttribute backwardAttribute = new BackwardAttribute(occurrenceAttribute, variable);
            if (attributesForBackwardChasing.contains(backwardAttribute)) {
                continue;
            }
            attributesForBackwardChasing.add(backwardAttribute);
        }
        return attributesForBackwardChasing;
    }

    private void addTuple(Tuple tuple, EquivalenceClassForSymmetricEGD equivalenceClass, CostManagerConfiguration costManagerConfiguration, IDatabase deltaDB, String stepId, Scenario scenario) {
        if (logger.isDebugEnabled()) logger.trace("Adding tuple " + tuple + " to equivalence class: " + equivalenceClass);
        AttributeRef conclusionAttribute = equivalenceClass.getConclusionAttribute();
        Cell cellToChangeForForwardChasing = tuple.getCell(conclusionAttribute);
        if (logger.isDebugEnabled()) logger.trace("Attribute: " + conclusionAttribute + " - Cell: " + cellToChangeForForwardChasing);
        IValue conclusionValue = cellToChangeForForwardChasing.getValue();
        TupleOID originalOid = new TupleOID(ChaseUtility.getOriginalOid(tuple, conclusionAttribute));
        CellRef cellRef = new CellRef(originalOid, ChaseUtility.unAlias(conclusionAttribute));
        CellGroupCell targetCell = new CellGroupCell(cellRef, conclusionValue, null, LunaticConstants.TYPE_OCCURRENCE, null);
        CellGroup forwardCellGroup = new CellGroup(conclusionValue, true);
        forwardCellGroup.addOccurrenceCell(targetCell);
        addAdditionalAttributes(forwardCellGroup, originalOid, tuple, equivalenceClass.getEGD());
        CellGroup enrichedCellGroup = this.occurrenceHandler.enrichCellGroups(forwardCellGroup, deltaDB, stepId, scenario);
        EGDEquivalenceClassTuple tupleCells = new EGDEquivalenceClassTuple(enrichedCellGroup);
//        if (costManagerConfiguration.isDoBackward()) {
            for (BackwardAttribute backwardAttribute : equivalenceClass.getAttributesToChangeForBackwardChasing()) {
                AttributeRef attributeForBackwardChasing = backwardAttribute.getAttributeRef();
                Cell cellForBackward = tuple.getCell(attributeForBackwardChasing);
                TupleOID tupleOid = new TupleOID(ChaseUtility.getOriginalOid(tuple, attributeForBackwardChasing));
                Cell backwardCell = new Cell(tupleOid, ChaseUtility.unAlias(attributeForBackwardChasing), cellForBackward.getValue());
                IValue value = backwardCell.getValue();
                CellGroup backwardCellGroup = new CellGroup(value, true);
                backwardCellGroup.addOccurrenceCell(new CellGroupCell(backwardCell, null, LunaticConstants.TYPE_OCCURRENCE, null));
                CellGroup enrichedBackwardCellGroup = this.occurrenceHandler.enrichCellGroups(backwardCellGroup, deltaDB, stepId, scenario);
                tupleCells.setCellGroupForBackwardAttribute(backwardAttribute, enrichedBackwardCellGroup);
            }
//        }
        equivalenceClass.addTupleCells(tupleCells);
        equivalenceClass.addTupleCellsForValue(conclusionValue, tupleCells);
        indexCells(tupleCells, equivalenceClass);
        if (logger.isDebugEnabled()) logger.trace("Equivalence class: " + equivalenceClass);
    }

    private void indexCells(EGDEquivalenceClassTuple tupleCells, EquivalenceClassForSymmetricEGD equivalenceClass) {
        for (Cell cell : tupleCells.getAllCells()) {
            equivalenceClass.indexTupleCellsForCell(cell, tupleCells);
        }
    }

    private void addAdditionalAttributes(CellGroup cellGroup, TupleOID originalOIDForConclusionValue, Tuple tuple, Dependency egd) {
        for (AttributeRef additionalAttribute : egd.getAdditionalAttributes()) {
            for (Cell additionalCell : tuple.getCells()) {
                AttributeRef unaliasedAttribute = ChaseUtility.unAlias(additionalCell.getAttributeRef());
                if (!unaliasedAttribute.equals(additionalAttribute)) {
                    continue;
                }
                TupleOID originalOIDForCell = new TupleOID(ChaseUtility.getOriginalOid(tuple, additionalCell.getAttributeRef()));
                if (!originalOIDForCell.equals(originalOIDForConclusionValue)) {
                    continue;
                }
                CellGroupCell additionalCellGroupCell = new CellGroupCell(originalOIDForCell, unaliasedAttribute, additionalCell.getValue(), null, LunaticConstants.TYPE_ADDITIONAL, null);
                cellGroup.addAdditionalCell(additionalAttribute, additionalCellGroupCell);
            }
        }
    }

    private NewChaseSteps applyRepairs(DeltaChaseStep currentNode, List<Repair> repairs, Dependency egd, IAlgebraOperator premiseQuery, Scenario scenario) {
        if (logger.isDebugEnabled()) logger.debug("---Applying repairs...");
        NewChaseSteps newChaseSteps = new NewChaseSteps(egd);
        for (int i = 0; i < repairs.size(); i++) {
            Repair repair = repairs.get(i);
            boolean consistentRepair = purgeOverlappingContexts(egd, repair, scenario); //TODO needed or not for FDs????
            CellGroupUtility.checkCellGroupConsistency(repair); //TODO keep???
            String egdId = egd.getId();
            String localId = ChaseUtility.generateChaseStepIdForEGDs(egdId, i, repair);
            DeltaChaseStep newStep = new DeltaChaseStep(scenario, currentNode, localId, egd, repair, repair.getChaseModes());
            for (ChangeDescription changeSet : repair.getChangeDescriptions()) {
                this.cellChanger.changeCells(changeSet.getCellGroup(), newStep.getDeltaDB(), newStep.getId(), scenario);
            }
            if (isEGDSatisfied(egd, consistentRepair, scenario)) {
                if (logger.isDebugEnabled()) logger.debug("EGD " + egd.getId() + " is satisfied in this step...");
                newStep.addSatisfiedEGD(egd);
            }
            List<AttributeRef> affectedAttributes = extractAffectedAttributes(repair);
            newStep.setAffectedAttributes(affectedAttributes);
            if (logger.isDebugEnabled()) logger.debug("Generated step " + newStep.getId() + " for repair: " + repair);
            newChaseSteps.addChaseStep(newStep);
        }
        if (repairs.isEmpty()) {
            newChaseSteps.setNoRepairsNeeded(true);
        }
        return newChaseSteps;
    }

    // TODO needed for FDs????
//    private boolean dependencyIsSatisfied(DeltaChaseStep currentNode, IAlgebraOperator queryOperator, Dependency dependency, Scenario scenario) {
//        IDatabase databaseForStep = databaseBuilder.extractDatabase(currentNode.getId(), currentNode.getDeltaDB(), currentNode.getOriginalDB(), dependency);
//        if (logger.isDebugEnabled()) logger.debug("Checking dependency satisfaction for suspicious egd: " + dependency.getId() + "\nDatabase for step: " + databaseForStep);
//        ITupleIterator it = queryRunner.run(queryOperator, scenario.getSource(), databaseForStep);
//        boolean isEmpty = !it.hasNext();
//        it.close();
//        if (logger.isDebugEnabled()) logger.debug("Returning: " + isEmpty);
//        return isEmpty;
//    }

    private boolean purgeOverlappingContexts(Dependency egd, Repair repair, Scenario scenario) {
        if (egd.hasSymmetricChase() || scenario.getConfiguration().isUseLimit1ForEGDs()) {
            return true;
        }
        if (logger.isDebugEnabled()) logger.debug("Checking independence of violation contexts for egd " + egd);
        boolean consistent = true;
        Set<CellGroupCell> cellsToChange = new HashSet<CellGroupCell>();
        for (Iterator<ChangeDescription> it = repair.getChangeDescriptions().iterator(); it.hasNext();) {
            ChangeDescription violationContexts = it.next();
            if (occurrencesOverlap(violationContexts, cellsToChange) || witnessOverlaps(violationContexts, cellsToChange)) {
                if (logger.isDebugEnabled()) logger.debug("Violation context has overlaps: " + violationContexts);
                it.remove();
                consistent = false;
            } else {
                cellsToChange.addAll(violationContexts.getCellGroup().getOccurrences());
            }
        }
        return consistent;
    }

    private boolean occurrencesOverlap(ChangeDescription violationContext, Set<CellGroupCell> cellsToChange) {
        CellGroup cellGroup = violationContext.getCellGroup();
        boolean inconsistent = containsCellRefs(CellGroupUtility.extractAllCellRefs(cellGroup), cellsToChange);
        if (inconsistent && logger.isDebugEnabled()) logger.debug("Occurrences Overlap:\n" + violationContext);
        return inconsistent;
    }

    private boolean witnessOverlaps(ChangeDescription changeSet, Set<CellGroupCell> cellsToChange) {
        if (changeSet.getChaseMode().equals(LunaticConstants.CHASE_BACKWARD)) {
            return false;
        }
        Set<CellRef> witnessCells = CellGroupUtility.extractAllCellRefs(changeSet.getWitnessCells());
        if (containsCellRefs(witnessCells, cellsToChange)) {
            if (logger.isDebugEnabled()) logger.debug("Witness Overlaps:\n" + witnessCells);
            return true;
        }
        return false;
    }

    private boolean containsCellRefs(Set<CellRef> witnessCells, Set<CellGroupCell> cellsToChange) {
        Set<CellRef> cellRefsToChange = ChaseUtility.createCellRefsFromCells(cellsToChange);
        for (CellRef cell : witnessCells) {
            if (cellRefsToChange.contains(cell)) {
                return true;
            }
        }
        return false;
    }

    private boolean isEGDSatisfied(Dependency egd, boolean consistentRepair, Scenario scenario) {
        return egd.hasSymmetricChase() && consistentRepair && !scenario.getConfiguration().isUseLimit1ForEGDs() && !egd.isOverlapBetweenAffectedAndQueried();
    }

    private List<AttributeRef> extractAffectedAttributes(Repair repair) {
        List<AttributeRef> affectedAttributes = new ArrayList<AttributeRef>();
        for (ChangeDescription changeSet : repair.getChangeDescriptions()) {
            CellGroup cellGroupToChange = changeSet.getCellGroup();
            for (Cell occurrenceCell : cellGroupToChange.getOccurrences()) {
                if (!affectedAttributes.contains(occurrenceCell.getAttributeRef())) {
                    affectedAttributes.add(occurrenceCell.getAttributeRef());
                }
            }
        }
        return affectedAttributes;
    }

}
