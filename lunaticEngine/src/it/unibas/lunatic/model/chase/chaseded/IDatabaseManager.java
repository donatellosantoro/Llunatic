package it.unibas.lunatic.model.chase.chaseded;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.database.IDatabase;


public interface IDatabaseManager {
    
    public IDatabase cloneTarget(Scenario scenario);
    
    public void restoreTarget(IDatabase original, Scenario scenario);
    
    public void removeClone(IDatabase clone, Scenario scenario);

}
