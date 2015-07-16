package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.model.database.LLUNValue;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.dependency.Dependency;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckSolution {

    private static Logger logger = LoggerFactory.getLogger(CheckSolution.class);
    private CheckUnsatisfiedDependencies unsatisfiedDependenciesChecker;
    private IValueOccurrenceHandlerMC occurrenceHandler;

    public CheckSolution(CheckUnsatisfiedDependencies unsatisfiedDependenciesChecker, IValueOccurrenceHandlerMC occurrenceHandler, IRunQuery queryRunner, IBuildDatabaseForChaseStep databaserBuilder) {
        this.unsatisfiedDependenciesChecker = unsatisfiedDependenciesChecker;
        this.occurrenceHandler = occurrenceHandler;
    }

    public void markLeavesAsSolutions(DeltaChaseStep chaseStep, Scenario scenario) {
        for (DeltaChaseStep child : chaseStep.getChildren()) {
            if (child.isLeaf()) {
                if (child.isDuplicate() || child.isInvalid() || child.isEditedByUser()) {
                    continue;
                }
                child.setSolution(true);
                if (scenario.getConfiguration().isCheckGroundSolutions()) {
                    checkForGroundSolution(child, scenario);
                }
            } else {
                markLeavesAsSolutions(child, scenario);
            }
        }
    }

    public void checkSolutions(DeltaChaseStep chaseStep, Scenario scenario) {
        for (DeltaChaseStep child : chaseStep.getChildren()) {
            if (child.isLeaf()) {
                if (child.isDuplicate() || child.isInvalid() || child.isEditedByUser() || child.isSolution()) {
                    continue;
                }
                if (areStatisfiedEGDs(child, scenario) && areSatisfiedTGDs(child, scenario)) {
                    child.setSolution(true);
                    checkForGroundSolution(child, scenario);
                }
            } else {
                checkSolutions(child, scenario);
            }
        }
    }

    private void checkForGroundSolution(DeltaChaseStep step, Scenario scenario) {
        if (!scenario.getConfiguration().isCheckGroundSolutions()) {
            return;
        }
        List<CellGroup> cellGroups = occurrenceHandler.loadAllCellGroupsForDebugging(step.getDeltaDB(), step.getId(), scenario);
        for (CellGroup cellGroup : cellGroups) {
            IValue cellValue = cellGroup.getValue();
            if (cellValue instanceof LLUNValue) {
                return;
            }
        }
        step.setGround(true);
    }

    private boolean areStatisfiedEGDs(DeltaChaseStep currentNode, Scenario scenario) {
        List<Dependency> extEGDs = scenario.getExtEGDs();
        if (extEGDs.isEmpty()) {
            return true;
        }
        if (logger.isDebugEnabled()) logger.debug("Checking EGDs...");
        List<Dependency> unsatisfiedDependencies;
        if (scenario.getConfiguration().isCheckSolutionsQuery()) {
            if (logger.isDebugEnabled()) logger.debug("...using queries");
            unsatisfiedDependencies = unsatisfiedDependenciesChecker.findUnsatisfiedEGDsQuery(currentNode, extEGDs, scenario);
        } else {
            unsatisfiedDependencies = unsatisfiedDependenciesChecker.findUnsatisfiedEGDsNoQuery(currentNode, extEGDs);
        }
        if (unsatisfiedDependencies.isEmpty()) {
            if (logger.isDebugEnabled()) logger.debug("All EGDs are satisfied on node " + currentNode.getId());
            return true;
        }
        if (logger.isDebugEnabled()) logger.debug("EGDs " + unsatisfiedDependencies + " can be unsatisfied... Node " + currentNode.getId() + " is not a solution");
        return false;
    }

    private boolean areSatisfiedTGDs(DeltaChaseStep currentNode, Scenario scenario) {
        List<Dependency> extTGDs = scenario.getExtTGDs();
        if (extTGDs.isEmpty()) {
            return true;
        }
        if (logger.isDebugEnabled()) logger.debug("Checking EGDs...");
        List<Dependency> unsatisfiedDependencies;
        unsatisfiedDependencies = unsatisfiedDependenciesChecker.findUnsatisfiedTGDs(currentNode, extTGDs, scenario);
        if (unsatisfiedDependencies.isEmpty()) {
            if (logger.isDebugEnabled()) logger.debug("All EGDs are satisfied on node " + currentNode.getId());
            return true;
        }
        if (logger.isDebugEnabled()) logger.debug("EGDs " + unsatisfiedDependencies + " can be unsatisfied... Node " + currentNode.getId() + " is not a solution");
        return false;
    }
}
