package it.unibas.lunatic.model.chase.chasemc.operators.cache;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import java.util.Set;
import speedy.model.database.CellRef;
import speedy.model.database.IDatabase;
import speedy.model.database.IValue;

public interface ICacheManager {

    public void loadCellGroups(String stepId, IDatabase deltaDB, Scenario scenario);
    public CellGroup loadCellGroupFromId(IValue value, String stepId, IDatabase deltaDB, Scenario scenario);
    public void putCellGroup(CellGroup cellGroup, String stepId, IDatabase deltaDB, Scenario scenario);
    public void removeCellGroup(IValue value, String stepId);

    public IValue getClusterId(CellRef cellRef, String stepId, IDatabase deltaDB, Scenario scenario);
    public void putClusterId(CellRef cellRef, IValue value, String stepId, IDatabase deltaDB, Scenario scenario);
    public void removeClusterId(CellRef cellRef, String stepId);
    
    public CellGroup getCellGroup(String key);
    public Set<String> getKeySet();
    public void reset();
    public void generateCellGroupStats(DeltaChaseStep step);
    
}
