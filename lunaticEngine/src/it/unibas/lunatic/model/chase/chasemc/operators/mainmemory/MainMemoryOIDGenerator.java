package it.unibas.lunatic.model.chase.chasemc.operators.mainmemory;

import it.unibas.lunatic.model.chase.chasemc.operators.IOIDGenerator;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.ITable;
import it.unibas.lunatic.model.database.mainmemory.datasource.IntegerOIDGenerator;
import it.unibas.lunatic.model.database.mainmemory.datasource.OID;

public class MainMemoryOIDGenerator implements IOIDGenerator{

    public void initializeOIDs(IDatabase database) {
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
