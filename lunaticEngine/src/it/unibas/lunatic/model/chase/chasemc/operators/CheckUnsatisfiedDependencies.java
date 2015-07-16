package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.algebra.IAlgebraOperator;
import it.unibas.lunatic.model.algebra.operators.BuildAlgebraTreeForEGD;
import it.unibas.lunatic.model.algebra.operators.BuildAlgebraTreeForTGD;
import it.unibas.lunatic.model.algebra.operators.ITupleIterator;
import it.unibas.lunatic.model.chase.commons.ChaseUtility;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.database.Cell;
import it.unibas.lunatic.model.database.CellRef;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.model.database.Tuple;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.dependency.ComparisonAtom;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.model.dependency.IFormulaAtom;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckUnsatisfiedDependencies {

    private static Logger logger = LoggerFactory.getLogger(CheckUnsatisfiedDependencies.class);

    private BuildAlgebraTreeForTGD treeBuilderForTGD = new BuildAlgebraTreeForTGD();
    private BuildAlgebraTreeForEGD treeBuilderForEGD = new BuildAlgebraTreeForEGD();
    private IBuildDatabaseForChaseStep databaseBuilder;
    private IValueOccurrenceHandlerMC occurrenceHandler;
    private IRunQuery queryRunner;

    public CheckUnsatisfiedDependencies(IBuildDatabaseForChaseStep databaseBuilder, IValueOccurrenceHandlerMC occurrenceHandler, IRunQuery queryRunner) {
        this.databaseBuilder = databaseBuilder;
        this.occurrenceHandler = occurrenceHandler;
        this.queryRunner = queryRunner;
    }

    ///////        DURING CHASE
    public List<Dependency> findUnsatisfiedDependencies(DeltaChaseStep currentNode, IDatabase databaseForStep, Scenario scenario) {
        List<Dependency> unsatisfiedDepenencies = new ArrayList<Dependency>();
        unsatisfiedDepenencies.addAll(findUnsatisfiedEGDsNoQuery(currentNode, scenario.getExtEGDs()));
        unsatisfiedDepenencies.addAll(findUnsatisfiedTGDs(currentNode, scenario.getExtTGDs(), scenario));
        return unsatisfiedDepenencies;
    }

    public List<Dependency> findUnsatisfiedTGDs(DeltaChaseStep currentNode, List<Dependency> dependencies, Scenario scenario) {
        if (logger.isDebugEnabled()) logger.debug("Searching satisfied dependencies among " + LunaticUtility.printDependencyIds(dependencies));
        Map<Dependency, IAlgebraOperator> tgdTreeMap = treeBuilderForTGD.buildAlgebraTreesForTGDViolations(scenario.getExtTGDs(), scenario);
        List<Dependency> unsatisfiedDepdendencies = new ArrayList<Dependency>();
        for (Dependency dependency : dependencies) {
            IDatabase databaseForStep = databaseBuilder.extractDatabase(currentNode.getId(), currentNode.getDeltaDB(), currentNode.getOriginalDB(), dependency);
//            IDatabase databaseForStep = databaseBuilder.extractDatabase(currentNode.getId(), currentNode.getDeltaDB(), currentNode.getOriginalDB());
            if (isTGDSatisfied(dependency, currentNode, tgdTreeMap, databaseForStep, scenario)) {
                if (logger.isDebugEnabled()) logger.debug("Dependency " + dependency.getId() + " is satisfied, skipping...");
                continue;
            }
            unsatisfiedDepdendencies.add(dependency);
        }
        if (logger.isDebugEnabled()) logger.info("Unsatisfied:  " + LunaticUtility.printDependencyIds(unsatisfiedDepdendencies));
        return unsatisfiedDepdendencies;
    }

    private boolean isTGDSatisfied(Dependency eTgd, DeltaChaseStep currentNode, Map<Dependency, IAlgebraOperator> tgdTreeMap, IDatabase databaseForStep, Scenario scenario) {
        IAlgebraOperator tgdQuery = tgdTreeMap.get(eTgd);
        ITupleIterator it = queryRunner.run(tgdQuery, scenario.getSource(), databaseForStep);
        boolean existsViolation = it.hasNext();
        it.close();
        if (existsViolation) {
            if (logger.isDebugEnabled()) logger.debug("TGD " + eTgd + " is violated... Node " + currentNode.getId() + " is not a solution");
            return false;
        }
        return true;
    }

    ///////        POST CHASE
    public void checkEGDSatisfactionWithQuery(DeltaChaseStep currentNode, Scenario scenario) {
        logger.debug("Checking solution with query...");
        for (Dependency egd : currentNode.getSatisfiedEGDs()) {
            IDatabase databaseForStep = databaseBuilder.extractDatabase(currentNode.getId(), currentNode.getDeltaDB(), currentNode.getOriginalDB(), egd);
            boolean satisfied = isEGDSatisfiedQuery(egd, currentNode, databaseForStep, scenario);
            if (!satisfied) {
                if (scenario.isDBMS()) {
                    throw new ChaseException("Dependency " + egd + "\nis not satisfied in node " + currentNode.toShortStringWithSort());
                } else {
                    throw new ChaseException("Dependency " + egd + "\nis not satisfied in node " + currentNode.toStringWithSort() + "\nDelta db: " + currentNode.getDeltaDB());
                }
            }
        }
    }

    public List<Dependency> findUnsatisfiedEGDsQuery(DeltaChaseStep currentNode, List<Dependency> dependencies, Scenario scenario) {
        if (logger.isDebugEnabled()) logger.debug("Searching satisfied dependencies among " + LunaticUtility.printDependencyIds(dependencies));
        List<Dependency> unsatisfiedDepdendencies = new ArrayList<Dependency>();
        for (Dependency dependency : dependencies) {
            IDatabase databaseForStep = databaseBuilder.extractDatabase(currentNode.getId(), currentNode.getDeltaDB(), currentNode.getOriginalDB(), dependency);
            if (isEGDSatisfiedQuery(dependency, currentNode, databaseForStep, scenario)) {
                if (logger.isDebugEnabled()) logger.debug("Dependency " + dependency.getId() + " is satisfied, skipping...");
                continue;
            }
            unsatisfiedDepdendencies.add(dependency);
        }
        if (logger.isDebugEnabled()) logger.info("Unsatisfied:  " + LunaticUtility.printDependencyIds(unsatisfiedDepdendencies));
        return unsatisfiedDepdendencies;
    }

    public boolean isEGDSatisfiedQuery(Dependency egd, DeltaChaseStep currentNode, IDatabase databaseForStep, Scenario scenario) {
        if (logger.isDebugEnabled()) logger.debug("Checking satisfaction for egd " + egd.getId() + " in node " + currentNode.toShortStringWithSort());
        IAlgebraOperator violationQuery = treeBuilderForEGD.buildTreeForExtEGDPremise(egd, false, scenario);
        if (logger.isDebugEnabled()) logger.debug("Violation query\n" + violationQuery);
        ITupleIterator it = queryRunner.run(violationQuery, scenario.getSource(), databaseForStep);
        while (it.hasNext()) {
            Tuple tuple = it.next();
            if (!isSatisfiedAfterRepairs(egd, tuple, currentNode)) {
                logger.error("EGD " + egd + " is violated... Node " + currentNode.getId() + " is not a solution\nViolation tuple: " + tuple.toStringWithAlias());
                it.close();
                return false;
            }
        }
        it.close();
        return true;
    }

    private boolean isSatisfiedAfterRepairs(Dependency egd, Tuple tuple, DeltaChaseStep currentNode) {
        for (IFormulaAtom atom : egd.getConclusion().getAtoms()) {
            if (!(atom instanceof ComparisonAtom)) {
                throw new ChaseException("Illegal egd. Only comparisons are allowed in the conclusion: " + egd);
            }
            ComparisonAtom comparison = (ComparisonAtom) atom;
            if (comparison.getVariables().size() != 2) {
                throw new ChaseException("Unable to handle extended egd: constants appear in conclusion;  " + egd);
            }
            FormulaVariable v1 = comparison.getVariables().get(0);
            FormulaVariable v2 = comparison.getVariables().get(1);
            if (!comparisonIsSatisfiedAfterRepairs(v1, v2, tuple, currentNode)) {
                return false;
            }
        }
        return true;
    }

    private boolean comparisonIsSatisfiedAfterRepairs(FormulaVariable v1, FormulaVariable v2, Tuple tuple, DeltaChaseStep currentNode) {
        if (logger.isDebugEnabled()) logger.debug("Checking satisfaction on tuple\n" + tuple.toStringWithAlias());
        IValue val1 = ChaseUtility.findValueForVariable(v1, tuple);
        IValue val2 = ChaseUtility.findValueForVariable(v2, tuple);
        if (val1.equals(val2)) {
            if (logger.isDebugEnabled()) logger.debug("Equal values.. Returning true");
            return true;
        }
        List<Cell> cellsForV1 = ChaseUtility.findCellsForVariable(v1, tuple);
        CellGroup cellGroupForV1 = findCellGroup(cellsForV1, val1, currentNode);
        List<Cell> cellsForV2 = ChaseUtility.findCellsForVariable(v2, tuple);
        CellGroup cellGroupForV2 = findCellGroup(cellsForV2, val2, currentNode);
        if (logger.isDebugEnabled()) logger.debug("Cell group for v1:\n" + cellGroupForV1);
        if (logger.isDebugEnabled()) logger.debug("Cell group for v2:\n" + cellGroupForV2);
        if (!cellGroupForV1.getOccurrences().isEmpty() && !cellGroupForV2.getOccurrences().isEmpty()) {
            //Different target values
            if (logger.isDebugEnabled()) logger.debug("Different target values... Returning false");
            return false;
        }
        List<CellGroup> cellGroups = Arrays.asList(new CellGroup[]{cellGroupForV1, cellGroupForV2});
        boolean lubIsIdempotent = currentNode.getScenario().getCostManager().checkContainment(cellGroups, currentNode.getScenario());
        if (logger.isDebugEnabled()) logger.debug("LUB is idempotent? " + lubIsIdempotent);
        if (logger.isDebugEnabled() && !lubIsIdempotent) logger.debug("Delta db:\n" + currentNode.getDeltaDB().printInstances());
        return lubIsIdempotent;
    }

    private CellGroup findCellGroup(List<Cell> cells, IValue value, DeltaChaseStep chaseStep) {
        Cell targetCell = getFirstTargetCell(cells);
        if (targetCell == null) {
            return buildCellGroupForJustifications(value, cells);
        }
        if (logger.isTraceEnabled()) logger.debug("Reading cell group for cell " + targetCell + " on step " + chaseStep.getId() + " in \n" + chaseStep.getDeltaDB());
        CellGroup cellGroup = occurrenceHandler.loadCellGroupFromValue(targetCell, chaseStep.getDeltaDB(), chaseStep.getId(), chaseStep.getScenario());
        if (cellGroup == null) {
            cellGroup = CellGroupUtility.createNewCellGroupFromCell(targetCell);
        }
        if (logger.isDebugEnabled()) logger.debug("Result: " + cellGroup);
        return cellGroup;
    }

    private Cell getFirstTargetCell(List<Cell> cells) {
        Cell targetCell = null;
        for (Cell cell : cells) {
            if (cell.getAttributeRef().isTarget()) {
                targetCell = cell;
                break;
            }
        }
        return targetCell;
    }

    private CellGroup buildCellGroupForJustifications(IValue value, List<Cell> justifications) {
        CellGroup cellGroup = new CellGroup(value, true);
        for (Cell just : justifications) {
            CellGroupCell cellGroupCell = new CellGroupCell(just.getTupleOID(), just.getAttributeRef(), value, value, LunaticConstants.TYPE_JUSTIFICATION, true);
            cellGroup.addJustificationCell(cellGroupCell);
        }
        return cellGroup;
    }

    public List<Dependency> findUnsatisfiedEGDsNoQuery(DeltaChaseStep currentNode, List<Dependency> dependencies) {
        if (logger.isDebugEnabled()) logger.debug("Searching satisfied dependencies among " + LunaticUtility.printDependencyIds(dependencies));
        List<Dependency> unsatisfiedDepdendencies = new ArrayList<Dependency>();
        for (Dependency dependency : dependencies) {
            if (isEGDSatisfiedNoQuery(dependency, currentNode)) {
                if (logger.isDebugEnabled()) logger.debug("Dependency " + dependency.getId() + " is satisfied, skipping...");
                continue;
            }
            unsatisfiedDepdendencies.add(dependency);
        }
        if (logger.isDebugEnabled()) logger.info("Unsatisfied:  " + LunaticUtility.printDependencyIds(unsatisfiedDepdendencies));
        return unsatisfiedDepdendencies;
    }

    private boolean isEGDSatisfiedNoQuery(Dependency dependency, DeltaChaseStep currentNode) {
        if (currentNode.getSatisfiedEGDs().contains(dependency)) {
            return true;
        }
        if (currentNode.isEditedByUser() || hasSatisfiedTgd(currentNode)) {
            return false;
        }
        Dependency firstDependency = currentNode.getFirstSatisfiedEGD();
        if (firstDependency != null && firstDependency.equals(dependency)) {
            return true;
        }
        while (currentNode.getFather() != null) {
            if (currentNode.getSatisfiedEGDs().contains(dependency)) {
                return true;
            }
            if (hasModifiedQueriedAttributes(currentNode.getAffectedAttributes(), dependency.getQueriedAttributes())) {
                return false;
            }
            currentNode = currentNode.getFather();
        }
        return false;
    }

    private boolean hasSatisfiedTgd(DeltaChaseStep currentNode) {
        if (currentNode.getChaseMode() != null && currentNode.getChaseMode().equals(LunaticConstants.CHASE_STEP_TGD)) {
            return true;
        }
        return false;
    }

    private boolean hasModifiedQueriedAttributes(List<AttributeRef> affectedAttributes, List<AttributeRef> queriedAttributes) {
        if (logger.isDebugEnabled()) logger.debug("Checking if dependency is satisfied. Affected attributes: " + affectedAttributes + " - Queried attributes: " + queriedAttributes);
        for (AttributeRef affectedAttribute : affectedAttributes) {
            if (queriedAttributes.contains(affectedAttribute)) {
                return true;
            }
        }
        return false;
    }
}
