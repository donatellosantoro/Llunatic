package it.unibas.lunatic.model.chase.chasemc.operators.cache;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.database.CellRef;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.IValue;

public interface ICacheManager {

    public CellGroup loadCellGroupFromId(IValue value, String stepId, IDatabase deltaDB, Scenario scenario);
    public void putCellGroup(CellGroup cellGroup, String stepId, IDatabase deltaDB, Scenario scenario);
    public void removeCellGroup(IValue value, String stepId);

    public IValue getClusterId(CellRef cellRef, String stepId, IDatabase deltaDB, Scenario scenario);
    public void putClusterId(CellRef cellRef, IValue value, String stepId, IDatabase deltaDB, Scenario scenario);
    public void removeClusterId(CellRef cellRef, String stepId);
    
    public void reset();
    public void generateCellGroupStats(DeltaChaseStep step);
    
}
