package it.unibas.lunatic.model.chase.chasemc.operators.cache;

import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.database.CellRef;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.IValue;

public interface ICacheManager {

    public CellGroup getCellGroup(IValue value, String stepId, IDatabase deltaDB);
    public void putCellGroup(CellGroup cellGroup, String stepId, IDatabase deltaDB);
    public void removeCellGroup(IValue value, String stepId);

    public IValue getClusterId(CellRef cellRef, String stepId, IDatabase deltaDB);
    public void putClusterId(CellRef cellRef, IValue value, String stepId, IDatabase deltaDB);
    public void removeClusterId(CellRef cellRef, String stepId);
    
    public void reset();
    public void generateCellGroupStats(DeltaChaseStep step);
    
}
