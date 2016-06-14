package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.OperatorFactory;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.CellRef;
import speedy.model.database.IDatabase;

public class CheckConsistencyOfCellGroups {

    private static Logger logger = LoggerFactory.getLogger(CheckConsistencyOfCellGroups.class);
    private IOccurrenceHandler occurrenceHandler;

    public void checkConsistencyOfCellGroupsInStep(DeltaChaseStep chaseStep) {
        Scenario scenario = chaseStep.getScenario();
        initializeOperators(scenario);
        for (DeltaChaseStep child : chaseStep.getChildren()) {
            if (child.isLeaf()) {
                IDatabase deltaDB = child.getDeltaDB();
                List<CellGroup> cellGroups = occurrenceHandler.loadAllCellGroupsInStepForDebugging(deltaDB, child.getId(), child.getScenario());
                checkConsistencyOfCellGroups(cellGroups);
            } else {
                checkConsistencyOfCellGroupsInStep(child);
            }
        }
    }

    public void checkConsistencyOfCellGroups(List<CellGroup> cellGroups) throws ChaseException {
        long start = new Date().getTime();
        Map<CellRef, CellGroup> cellGroupMap = new HashMap<CellRef, CellGroup>();
        for (CellGroup cellGroup : cellGroups) {
            for (CellGroupCell occurrence : cellGroup.getOccurrences()) {
                CellRef occurrenceCellRef = new CellRef(occurrence);
                if (cellGroupMap.containsKey(occurrenceCellRef)) {
                    String error = "Cell " + occurrenceCellRef + " appears multiple times in cell groups: \n\t" + cellGroupMap.get(occurrenceCellRef) + "\n\t" + cellGroup;
                    logger.error(error);
                    throw new ChaseException(error);
                }
                cellGroupMap.put(occurrenceCellRef, cellGroup);
            }
        }
        long end = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.CHECK_CONS_CELL_GROUPS, end - start);
    }

    private void initializeOperators(Scenario scenario) {
        this.occurrenceHandler = OperatorFactory.getInstance().getOccurrenceHandlerMC(scenario);
    }
}
