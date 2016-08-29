package it.unibas.lunatic.model.chase.chasede.operators;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.OperatorFactory;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.chase.chasede.CheckConflictsResult;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import it.unibas.lunatic.model.chase.chasemc.ChaseTree;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.ChaseMCScenario;
import it.unibas.lunatic.model.chase.chasemc.operators.OccurrenceHandlerMC;
import it.unibas.lunatic.model.chase.commons.operators.ChaseUtility;
import it.unibas.lunatic.model.chase.commons.operators.ChaserFactoryMC;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaAttribute;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.model.dependency.FormulaVariableOccurrence;
import it.unibas.lunatic.model.dependency.IFormula;
import it.unibas.lunatic.model.dependency.IFormulaAtom;
import it.unibas.lunatic.model.dependency.RelationalAtom;
import it.unibas.lunatic.model.dependency.operators.DependencyUtility;
import it.unibas.lunatic.model.dependency.operators.ExtendedEdge;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.SpeedyConstants;
import speedy.model.database.Attribute;
import speedy.model.database.AttributeRef;
import speedy.model.database.Cell;
import speedy.model.database.IDatabase;
import speedy.model.database.ITable;
import speedy.model.database.IValue;
import speedy.model.database.TableAlias;
import speedy.utility.PrintUtility;
import speedy.utility.SpeedyUtility;

public class CheckConflicts {

    private final static Logger logger = LoggerFactory.getLogger(CheckConflicts.class);

    public CheckConflictsResult doCheck(Scenario scenario) {
        if (!scenario.isDEScenario()) {
            throw new ChaseException("The check is possible only for de scenario");
        }
        ChaseUtility.copyEGDsToExtEGDs(scenario);
        scenario.getConfiguration().setUseCompactAttributeName(false);
        scenario.getCostManagerConfiguration().setDoBackward(false);
        scenario.getCostManagerConfiguration().setDoPermutations(false);
        scenario.getConfiguration().setExportChanges(false);
        scenario.getConfiguration().setExportSolutions(false);
        scenario.getConfiguration().setPrintSteps(false);
        Map<AttributeRef, Set<AttributeRef>> provenanceMap = findProvenance(scenario);
        ChaseMCScenario chaser = ChaserFactoryMC.getChaser(scenario);
        DeltaChaseStep result = chaser.doChase(scenario);
        List<DeltaChaseStep> leaves = ChaseUtility.getAllLeaves(result.getRoot());
        DeltaChaseStep solution = leaves.get(0);
        List<CellGroup> cellGroups = findAllEGDCellGroups(solution, scenario);
        Map<String, Set<AttributeRef>> constantsToRemove = findConstantsToRemove(cellGroups, provenanceMap);
        ChaseTree chaseTree = new ChaseTree(scenario);
        chaseTree.setRoot(result);
        scenario.setExtEGDs(new ArrayList<Dependency>());
        scenario.setEGDs(scenario.getEgdsFromParser());
        return new CheckConflictsResult(chaseTree, constantsToRemove);
    }

    private List<CellGroup> findAllEGDCellGroups(DeltaChaseStep step, Scenario scenario) {
        OccurrenceHandlerMC occurrenceHandler = OperatorFactory.getInstance().getOccurrenceHandlerMC(scenario);
        List<CellGroup> result = new ArrayList<CellGroup>();
        while (step != null) {
            if (!step.getLocalId().equals(LunaticConstants.CHASE_STEP_ROOT) && !step.getLocalId().startsWith(LunaticConstants.CHASE_STEP_TGD)) {
                List<CellGroup> cellGroupsInStep = occurrenceHandler.loadAllCellGroupsInStepForDebugging(step.getDeltaDB(), step.getId(), scenario);
                result.addAll(cellGroupsInStep);
            }
            step = step.getFather();
        }
        return result;
    }

    private Map<String, Set<AttributeRef>> findConstantsToRemove(List<CellGroup> cellGroups, Map<AttributeRef, Set<AttributeRef>> provenanceMap) {
        Map<String, Set<AttributeRef>> constantsToRemove = new HashMap<String, Set<AttributeRef>>();
        for (CellGroup cellGroup : cellGroups) {
            List<CellGroupCell> targetCells = findConflictingCells(cellGroup);
            addSourceConstants(targetCells, constantsToRemove, provenanceMap);
        }
        return constantsToRemove;
    }

    private List<CellGroupCell> findConflictingCells(CellGroup cellGroup) {
        List<CellGroupCell> result = new ArrayList<CellGroupCell>();
        for (CellGroupCell cellGroupCell : cellGroup.getOccurrences()) {
            IValue originalValue = cellGroupCell.getOriginalValue();
            if (originalValue == null || originalValue.toString().equalsIgnoreCase(SpeedyConstants.NULL)) {
                continue;
            }
            result.add(cellGroupCell);
        }
        result.remove(0);
        return result;
    }

    private void addSourceConstants(List<CellGroupCell> targetCells, Map<String, Set<AttributeRef>> constantsToRemove, Map<AttributeRef, Set<AttributeRef>> provenanceMap) {
        for (CellGroupCell targetCell : targetCells) {
            String value = targetCell.getOriginalValue().toString();
            Set<AttributeRef> attributesForConstant = constantsToRemove.get(value);
            if (attributesForConstant == null) {
                attributesForConstant = new HashSet<AttributeRef>();
                constantsToRemove.put(value, attributesForConstant);
            }
            Set<AttributeRef> relatedAttributes = provenanceMap.get(targetCell.getAttributeRef());
            if (logger.isDebugEnabled()) logger.debug("** Attributes related to " + targetCell.getAttributeRef() + ": " + relatedAttributes);
            attributesForConstant.addAll(relatedAttributes);
        }
    }

    private Map<AttributeRef, Set<AttributeRef>> findProvenance(Scenario scenario) {
        DirectedGraph<AttributeRef, DefaultEdge> propagationGraph = buildTGDPropagationGraph(scenario);
        if (logger.isDebugEnabled()) logger.debug("Propagation graph: " + propagationGraph);
        Map<AttributeRef, Set<AttributeRef>> result = new HashMap<AttributeRef, Set<AttributeRef>>();
        for (String tableName : scenario.getTarget().getTableNames()) {
            ITable table = scenario.getTarget().getTable(tableName);
            for (Attribute attribute : table.getAttributes()) {
                if (attribute.getName().equals(SpeedyConstants.OID)) {
                    continue;
                }
                AttributeRef targetAttribute = new AttributeRef(tableName, attribute.getName());
                Set<AttributeRef> sourceAttributes = findReachableSourceAttributes(targetAttribute, propagationGraph);
                result.put(targetAttribute, sourceAttributes);
            }
        }
        return result;
    }

    private DirectedGraph<AttributeRef, DefaultEdge> buildTGDPropagationGraph(Scenario scenario) {
        List<Dependency> tgds = new ArrayList<Dependency>();
        tgds.addAll(scenario.getSTTgds());
        tgds.addAll(scenario.getExtTGDs());
        DirectedGraph<AttributeRef, DefaultEdge> dependencyGraph = new DefaultDirectedGraph<AttributeRef, DefaultEdge>(DefaultEdge.class);
        addVertices(scenario.getSource(), true, dependencyGraph);
        addVertices(scenario.getTarget(), false, dependencyGraph);
        for (Dependency tgd : tgds) {
            addEdges(dependencyGraph, tgd);
        }
        return dependencyGraph;
    }

    private void addVertices(IDatabase database, boolean isSource, DirectedGraph<AttributeRef, DefaultEdge> dependencyGraph) {
        for (String tableName : database.getTableNames()) {
            ITable table = database.getTable(tableName);
            for (Attribute attribute : table.getAttributes()) {
                if (attribute.getName().equals(SpeedyConstants.OID)) {
                    continue;
                }
                AttributeRef attributeRef = new AttributeRef(new TableAlias(table.getName(), isSource), attribute.getName());
                if (logger.isDebugEnabled()) logger.debug("Adding vertex " + attributeRef);
                dependencyGraph.addVertex(attributeRef);
            }
        }
    }

    private void addEdges(DirectedGraph<AttributeRef, DefaultEdge> dependencyGraph, Dependency extTGD) {
        if (logger.isDebugEnabled()) logger.debug("Analyzing dependency " + extTGD.toLogicalString());
        for (FormulaVariable variable : extTGD.getPremise().getLocalVariables()) {
            if (variable.getConclusionRelationalOccurrences().isEmpty()) {
                continue;
            }
            for (FormulaVariableOccurrence premiseRelationalOccurrence : variable.getPremiseRelationalOccurrences()) {
                AttributeRef premiseNode = getPosition(premiseRelationalOccurrence);
                for (FormulaVariableOccurrence conclusionRelationalOccurrence : variable.getConclusionRelationalOccurrences()) {
                    AttributeRef conclusionNode = getPosition(conclusionRelationalOccurrence);
                    dependencyGraph.addEdge(premiseNode, conclusionNode);
                }
            }
        }
    }

    private AttributeRef getPosition(FormulaVariableOccurrence relationalOccurrence) {
        return ChaseUtility.unAlias(relationalOccurrence.getAttributeRef());
    }

    private Set<AttributeRef> findReachableSourceAttributes(AttributeRef targetAttribute, DirectedGraph<AttributeRef, DefaultEdge> dependencyGraph) {
        Set<AttributeRef> result = new HashSet<AttributeRef>();
        for (AttributeRef attribute : dependencyGraph.vertexSet()) {
            if (!attribute.isSource()) {
                continue;
            }
            if (result.contains(attribute)) {
                continue;
            }
            List path = DijkstraShortestPath.findPathBetween(dependencyGraph, attribute, targetAttribute);
            if (path != null) {
                result.add(attribute);
            }
        }
        return result;
    }

}
