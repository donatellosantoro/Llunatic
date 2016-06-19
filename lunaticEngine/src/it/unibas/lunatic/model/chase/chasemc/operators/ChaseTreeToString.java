package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import speedy.model.database.IDatabase;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChaseTreeToString {

    private int counter;
    private final IBuildDatabaseForChaseStepMC databaseBuilder;
    private final OccurrenceHandlerMC occurrenceHandler;

    public ChaseTreeToString(IBuildDatabaseForChaseStepMC databaseBuilder, OccurrenceHandlerMC occurrenceHandler) {
        this.databaseBuilder = databaseBuilder;
        this.occurrenceHandler = occurrenceHandler;
    }

    public String toString(DeltaChaseStep root) {
        StringBuilder result = new StringBuilder();
        printTree(root, result, false, false, false, true, false);
        return result.toString();
    }

    public String toStringLeavesOnly(DeltaChaseStep root) {
        StringBuilder result = new StringBuilder();
        printTree(root, result, false, true, false, true, false);
        return result.toString();
    }

    public String toStringLeavesOnlyWithSort(DeltaChaseStep root) {
        StringBuilder result = new StringBuilder();
        printTree(root, result, false, true, true, true, false);
        return result.toString();
    }

    public String toLongString(DeltaChaseStep root) {
        StringBuilder result = new StringBuilder();
        printTree(root, result, false, false, false, true, true);
        return result.toString();
    }

    public String toStringWithSort(DeltaChaseStep root) {
        StringBuilder result = new StringBuilder();
        printTree(root, result, false, false, true, true, false);
        return result.toString();
    }

    public String toLongStringWithSort(DeltaChaseStep root) {
        StringBuilder result = new StringBuilder();
        printTree(root, result, false, false, true, true, true);
        return result.toString();
    }

    public String toLongStringLeavesOnlyWithSort(DeltaChaseStep root) {
        StringBuilder result = new StringBuilder();
        printTree(root, result, false, true, true, false, true);
        return result.toString();
    }

    public String toShortString(DeltaChaseStep root) {
        StringBuilder result = new StringBuilder();
        printTree(root, result, true, false, false, true, false);
        return result.toString();
    }

    public String toShortStringWithSort(DeltaChaseStep root) {
        StringBuilder result = new StringBuilder();
        printTree(root, result, true, false, true, true, false);
        return result.toString();
    }

    public String toShortStringWithSortWithoutDuplicates(DeltaChaseStep root) {
        StringBuilder result = new StringBuilder();
        printTree(root, result, true, false, true, false, false);
        return result.toString();
    }

    public String toStatString(DeltaChaseStep root) {
        StringBuilder result = new StringBuilder();
        printStats(root, result);
        return result.toString();
    }

    private void printTree(DeltaChaseStep node, StringBuilder result, boolean idOnly, boolean leavesOnly, boolean sort, boolean printDuplicates, boolean longFormat) {
        if (isLeaf(node)) {
            if (!printDuplicates && node.isDuplicate()) {
                return;
            }
            result.append("+++++++++++++++");
            result.append((node.isDuplicate() ? "Duplicate branch (Cluster: " + LunaticUtility.printNodeIds(node.getDuplicateNodes()) + ")" : " Solution " + ++counter));
            result.append("+++++++++++++++\n");
            if (idOnly) {
                result.append(node.getId()).append("\n");
            } else if (leavesOnly) {
                result.append(printStep(node, sort, longFormat));
            } else {
                printChaseSequence(node, result, sort, longFormat);
            }
        } else {
            for (DeltaChaseStep child : node.getChildren()) {
                printTree(child, result, idOnly, leavesOnly, sort, printDuplicates, longFormat);
            }
        }
    }

    private boolean isLeaf(DeltaChaseStep node) {
        return node.getChildren().isEmpty();
    }

    private void printChaseSequence(DeltaChaseStep node, StringBuilder result, boolean sort, boolean longFormat) {
        List<DeltaChaseStep> chaseSteps = new ArrayList<DeltaChaseStep>();
        chaseSteps.add(node);
        DeltaChaseStep father = node.getFather();
        while (father != null) {
            chaseSteps.add(father);
            father = father.getFather();
        }
        Collections.reverse(chaseSteps);
        for (DeltaChaseStep step : chaseSteps) {
            result.append(printStep(step, sort, longFormat));
        }
    }

    public String printStep(DeltaChaseStep step, boolean sort, boolean longFormat) {
        StringBuilder result = new StringBuilder();
        result.append("----------------   CHASE STEP  ").append(step.getId()).append(" ").append((step.isDuplicate() ? "(duplicate)" : "")).append(" ----------------------\n");
        result.append(step.isSolution() ? "SOLUTION " : "INTERMEDIATE ");
        result.append(step.isGround() ? "GROUND " : "");
        result.append(step.isInvalid() ? "INVALID " : "");
        result.append(step.isEditedByUser() ? "EDITED BY USER " : "");
//        result.append("\n");
        if (step.getRepair() != null) {
            result.append(step.getRepair());
            result.append("---------------------------\n");
        }
        if (step.isRoot()) {
            result.append("Starting database:\n");
        } else {
            result.append(step.getChaseMode()).append(" - ").append(printDependencyIds(step.getSatisfiedEGDs()));
        }
        IDatabase deltaDB = ((DeltaChaseStep) step).getDeltaDB();
        IDatabase originalDB = ((DeltaChaseStep) step).getOriginalDB();
        IDatabase database = databaseBuilder.extractDatabase(step.getId(), deltaDB, originalDB, step.getScenario());
        result.append(database.printInstances(sort));
        if (longFormat) {
//            result.append((step.getCellGroupStats() != null ? step.getCellGroupStats().toString() : ""));
            result.append((step.getCellGroupStats() != null ? step.getCellGroupStats().toLongString() : ""));
//            List<CellGroup> cellGroups = occurrenceHandler.loadAllCellGroupsInStepForDebugging(deltaDB, step.getId(), step.getScenario());
            List<CellGroup> cellGroups = occurrenceHandler.loadAllCellGroupsForDebugging(deltaDB, step.getId(), step.getScenario());
            result.append("--------------- CELL GROUPS -----------------\n");
            for (CellGroup cellGroup : cellGroups) {
//                result.append(cellGroup.toLongString()).append("\n");
                result.append(cellGroup.toStringWithAdditionalCells()).append("\n");
            }
            result.append("\n");
        }
        return result.toString();
    }

    private String printDependencyIds(List<Dependency> dependencies) {
        StringBuilder result = new StringBuilder();
        for (Dependency dependency : dependencies) {
            result.append(dependency.getId()).append(" ");
        }
        return result.toString();
    }

    private void printStats(DeltaChaseStep node, StringBuilder result) {
        if (isLeaf(node)) {
            if (node.isDuplicate()) {
                return;
            }
            result.append("+++++++++++++++");
            result.append(" Solution ").append(++counter);
            result.append("+++++++++++++++\n");
            result.append((node.getCellGroupStats() != null ? node.getCellGroupStats().toString()+"\n" : ""));
        } else {
            for (DeltaChaseStep child : node.getChildren()) {
                printStats(child, result);
            }
        }
    }
}
