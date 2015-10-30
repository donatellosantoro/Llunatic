/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import java.util.List;
import speedy.model.database.Cell;
import speedy.model.database.CellRef;
import speedy.model.database.IDatabase;
import speedy.model.database.IValue;


public interface IOccurrenceHandler {

    void deleteCellGroup(CellGroup cellGroup, IDatabase deltaDB, String stepId);

    CellGroup enrichCellGroups(CellGroup preliminaryCellGroup, IDatabase deltaDB, String step, Scenario scenario);

    IValue findClusterId(CellRef cellRef, String stepId, IDatabase deltaDB, Scenario scenario);

    void generateCellGroupStats(DeltaChaseStep step);
    
    /////////////////////////////////////////////////////////
    //////    DEBUGGING
    /////////////////////////////////////////////////////////
    List<CellGroup> loadAllCellGroupsForDebugging(IDatabase deltaDB, String stepId, Scenario scenario);

    List<CellGroup> loadAllCellGroupsInStepForDebugging(IDatabase deltaDB, String stepId, Scenario scenario);

//    CellGroup loadCellGroupFromId(IValue value, IDatabase deltaDB, String stepId, Scenario scenario);

    CellGroup loadCellGroupFromValue(Cell cell, IDatabase deltaDB, String stepId, Scenario scenario);

    void reset();

    /////////////////////////////////////////////////////////////////////////
    void saveCellGroupCell(IDatabase deltaDB, IValue groupId, CellGroupCell cell, String stepId, Scenario scenario);

    void saveNewCellGroup(CellGroup cellGroup, IDatabase deltaDB, String stepId, Scenario scenario);
    
}
