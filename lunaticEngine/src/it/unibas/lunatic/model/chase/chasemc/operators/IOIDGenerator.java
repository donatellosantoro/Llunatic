package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.ITable;
import it.unibas.lunatic.model.database.mainmemory.datasource.OID;

public interface IOIDGenerator {

    OID getNextOID(String tableName);
    void addCounter(String tableName, int size);
    void initializeOIDs(IDatabase database);
}
