package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.dependency.Dependency;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.IValue;
import speedy.model.database.LLUNValue;
import speedy.model.database.operators.IRunQuery;

public class CheckSolution {

    private static Logger logger = LoggerFactory.getLogger(CheckSolution.class);
    private CheckUnsatisfiedDependencies unsatisfiedDependenciesChecker;
    private IOccurrenceHandler occurrenceHandler;

    public CheckSolution(CheckUnsatisfiedDependencies unsatisfiedDependenciesChecker, IOccurrenceHandler occurrenceHandler, IRunQuery queryRunner, IBuildDatabaseForChaseStep databaserBuilder) {
        this.unsatisfiedDependenciesChecker = unsatisfiedDependenciesChecker;
        this.occurrenceHandler = occurrenceHandler;
    }

    public void markLeavesAsSolutions(DeltaChaseStep chaseStep, Scenario scenario) {
        if (chaseStep.isLeaf()) {
            if (chaseStep.isDuplicate() || chaseStep.isInvalid() || chaseStep.isEditedByUser()) {
                return;
            }
            chaseStep.setSolution(true);
            if (scenario.getConfiguration().isCheckGroundSolutions()) {
                checkForGroundSolution(chaseStep, scenario);
            }
        }
        for (DeltaChaseStep child : chaseStep.getChildren()) {
            markLeavesAsSolutions(child, scenario);
        }
    }

    public void checkSolutions(DeltaChaseStep chaseStep, Scenario scenario) {
        if (chaseStep.isLeaf()) {
            if (chaseStep.isDuplicate() || chaseStep.isInvalid() || chaseStep.isEditedByUser() || chaseStep.isSolution()) {
                return;
            }
            if (areStatisfiedEGDs(chaseStep, scenario) && areSatisfiedTGDs(chaseStep, scenario)) {
                chaseStep.setSolution(true);
                checkForGroundSolution(chaseStep, scenario);
            } else if (scenario.getConfiguration().isDeProxyMode()) {
                throw new ChaseException("Leaf node " + chaseStep.getId() + " is not a solution");
            }
        }
        for (DeltaChaseStep child : chaseStep.getChildren()) {
            checkSolutions(child, scenario);
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
        logger.warn("EGDs " + unsatisfiedDependencies + " can be unsatisfied... Node " + currentNode.getId() + " is not a solution");
        return false;
    }

    private boolean areSatisfiedTGDs(DeltaChaseStep currentNode, Scenario scenario) {
        List<Dependency> extTGDs = scenario.getExtTGDs();
        if (extTGDs.isEmpty()) {
            return true;
        }
        if (logger.isDebugEnabled()) logger.debug("Checking TGDs...");
        List<Dependency> unsatisfiedDependencies;
        unsatisfiedDependencies = unsatisfiedDependenciesChecker.findUnsatisfiedTGDs(currentNode, extTGDs, scenario);
        if (unsatisfiedDependencies.isEmpty()) {
            if (logger.isDebugEnabled()) logger.debug("All TGDs are satisfied on node " + currentNode.getId());
            return true;
        }
        if (logger.isDebugEnabled()) logger.debug("TGDs " + unsatisfiedDependencies + " can be unsatisfied... Node " + currentNode.getId() + " is not a solution");
        return false;
    }
}
