package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.database.CellRef;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.model.database.Tuple;
import java.util.List;

public interface IValueOccurrenceHandlerMC {

    CellGroup loadCellGroupFromId(IValue cellGroupId, IDatabase deltaDB, String step);
    CellGroup loadCellGroupFromValue(IValue value, CellRef cellRef, IDatabase deltaDB, String stepId);
    List<CellGroup> loadAllCellGroups(IDatabase deltaDB, String stepId);
    List<CellGroup> loadAllCellGroupsInStep(IDatabase deltaDB, String stepId);
    void saveNewCellGroup(CellGroup cellGroup, IDatabase deltaDB, String stepId, boolean forceSave);
    void deleteCellGroup(CellGroup cellGroup, IDatabase deltaDB, String stepId, boolean forceSave);
    void updateCellGroup(CellGroup cellGroup, IValue newValue, IDatabase deltaDB, String stepId, boolean forceSave);
    void updateOccurrencesForNewTuple(Tuple tuple, IValue occurrenceValue, IDatabase deltaDB, String tableName, String attributeName, boolean forceSave);
    IValue findClusterId(CellRef cell, String stepId, IDatabase deltaDB);
    void enrichOccurrencesAndProvenances(CellGroup cellGroup, IDatabase deltaDB, String step);
    void reset();
    void generateCellGroupStats(DeltaChaseStep step);
}
