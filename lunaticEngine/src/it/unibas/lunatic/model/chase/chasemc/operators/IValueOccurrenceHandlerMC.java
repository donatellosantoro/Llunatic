package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.database.Cell;
import it.unibas.lunatic.model.database.CellRef;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.model.database.Tuple;
import java.util.List;


//Assumption: CellGroup ids are consistent across steps, i.e.: if C1 belongs to cell group x at step S, 
//            it belongs to the same cell group also at step S+1
public interface IValueOccurrenceHandlerMC {

    CellGroup loadCellGroupFromId(IValue cellGroupId, IDatabase deltaDB, String step, Scenario scenario);
    CellGroup loadCellGroupFromValue(Cell cell, IDatabase deltaDB, String stepId, Scenario scenario);
    List<CellGroup> loadAllCellGroupsForDebugging(IDatabase deltaDB, String stepId, Scenario scenario);
    List<CellGroup> loadAllCellGroupsInStepForDebugging(IDatabase deltaDB, String stepId, Scenario scenario);
    void saveNewCellGroup(CellGroup cellGroup, IDatabase deltaDB, String stepId, Scenario scenario);
    void deleteCellGroup(CellGroup cellGroup, IDatabase deltaDB, String stepId);
    void updateOccurrencesForNewTuple(Tuple tuple, IValue occurrenceValue, IDatabase deltaDB, String tableName, String attributeName);
    IValue findClusterId(CellRef cell, String stepId, IDatabase deltaDB, Scenario scenario);
    CellGroup enrichCellGroups(CellGroup cellGroup, IDatabase deltaDB, String step, Scenario scenario);
    void reset();
    void generateCellGroupStats(DeltaChaseStep step);
}
