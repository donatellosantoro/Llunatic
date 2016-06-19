package it.unibas.lunatic.model.chase.chasede.operators;

import it.unibas.lunatic.Scenario;
import speedy.model.database.IDatabase;

public interface IReplaceDatabase {
    
    public void replaceTargetDB(IDatabase newDatabase, Scenario scenario);

}
