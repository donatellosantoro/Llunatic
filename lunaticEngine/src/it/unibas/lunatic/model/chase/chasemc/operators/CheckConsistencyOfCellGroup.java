package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.OperatorFactory;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.CellRef;
import speedy.model.database.IDatabase;

public class CheckConsistencyOfCellGroup {

    private static Logger logger = LoggerFactory.getLogger(CheckConsistencyOfCellGroup.class);
    private OccurrenceHandlerMC occurrenceHandler;

    public void checkSolutions(DeltaChaseStep chaseStep) {
        Scenario scenario = chaseStep.getScenario();
        initializeOperators(scenario);
        for (DeltaChaseStep child : chaseStep.getChildren()) {
            if (child.isLeaf()) {
                checkInvalidCellGroup(child);
            } else {
                checkSolutions(child);
            }
        }
    }

    private void checkInvalidCellGroup(DeltaChaseStep step) {
        IDatabase deltaDB = step.getDeltaDB();
        List<CellGroup> cellGroups = occurrenceHandler.loadAllCellGroupsInStepForDebugging(deltaDB, step.getId(), step.getScenario());
        Map<CellRef, CellGroup> cellGroupMap = new HashMap<CellRef, CellGroup>();
        for (CellGroup cellGroup : cellGroups) {
            for (CellGroupCell occurrence : cellGroup.getOccurrences()) {
                CellRef occurrenceCellRef = new CellRef(occurrence);
                if (cellGroupMap.containsKey(occurrenceCellRef)) {
                    throw new ChaseException("Cell " + occurrenceCellRef + " appears in multiple cell groups: \n\t" + cellGroupMap.get(occurrenceCellRef) + "\n\t" + cellGroup);
                }
                cellGroupMap.put(occurrenceCellRef, cellGroup);
            }
        }
    }

    private void initializeOperators(Scenario scenario) {
        this.occurrenceHandler = OperatorFactory.getInstance().getOccurrenceHandlerMC(scenario);
    }
}
