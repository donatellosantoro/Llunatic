package it.unibas.lunatic.model.chase.chasemc.operators;

import speedy.model.database.IDatabase;
import speedy.model.database.mainmemory.datasource.OID;

public interface IOIDGenerator {

    OID getNextOID(String tableName);
    void addCounter(String tableName, int size);
    void initializeOIDs(IDatabase database);
}
