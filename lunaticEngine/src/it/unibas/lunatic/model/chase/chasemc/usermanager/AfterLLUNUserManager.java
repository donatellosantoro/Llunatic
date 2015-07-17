package it.unibas.lunatic.model.chase.chasemc.usermanager;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.model.database.LLUNValue;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.OccurrenceHandlerMC;
import java.util.List;

public class AfterLLUNUserManager implements IUserManager {

    private OccurrenceHandlerMC occurrenceHandler;

    public AfterLLUNUserManager(OccurrenceHandlerMC occurrenceHandler) {
        this.occurrenceHandler = occurrenceHandler;
    }

    public boolean isUserInteractionRequired(List<DeltaChaseStep> newSteps, DeltaChaseStep root, Scenario scenario) {
        for (DeltaChaseStep deltaChaseStep : newSteps) {
            if (containsLLUN(deltaChaseStep)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "After LLUN";
    }

    private boolean containsLLUN(DeltaChaseStep step) {
        List<CellGroup> cellGroups = occurrenceHandler.loadAllCellGroupsForDebugging(step.getDeltaDB(), step.getId(), step.getScenario());
        for (CellGroup cellGroup : cellGroups) {
            IValue cellValue = cellGroup.getValue();
            if (cellValue instanceof LLUNValue) {
                return true;
            }
        }
        return false;
    }
}
