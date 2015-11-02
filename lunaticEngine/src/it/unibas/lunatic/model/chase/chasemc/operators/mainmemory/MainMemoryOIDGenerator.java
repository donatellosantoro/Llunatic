package it.unibas.lunatic.model.chase.chasemc.operators.mainmemory;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.operators.IOIDGenerator;
import speedy.model.database.IDatabase;
import speedy.model.database.mainmemory.datasource.IntegerOIDGenerator;
import speedy.model.database.mainmemory.datasource.OID;

public class MainMemoryOIDGenerator implements IOIDGenerator{

    public void initializeOIDs(IDatabase database, Scenario scenario) {
        //Nothing to do
    }

    public OID getNextOID(String tableName) {
        return IntegerOIDGenerator.getNextOID();
    }

    public void addCounter(String tableName, int size) {
        for (int i = 0; i < size; i++) {
            getNextOID(tableName);
        }
    }

}
