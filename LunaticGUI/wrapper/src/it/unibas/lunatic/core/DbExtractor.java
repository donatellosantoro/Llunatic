package it.unibas.lunatic.core;

import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.dbms.BuildSQLDBForChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.mainmemory.BuildMainMemoryDBForChaseStep;
import speedy.model.database.IDatabase;
import speedy.model.database.dbms.DBMSDB;
import speedy.model.database.mainmemory.MainMemoryDB;

public class DbExtractor {

    private BuildMainMemoryDBForChaseStep mainMemoryBuilder = new BuildMainMemoryDBForChaseStep(false);
    private BuildSQLDBForChaseStep sqlBuilder = new BuildSQLDBForChaseStep(false);

    public IDatabase extractDb(DeltaChaseStep step) {
        IDatabase database = null;
        if (step instanceof DeltaChaseStep) {
            IDatabase deltaDB = step.getDeltaDB();
            IDatabase originalDB = step.getOriginalDB();
            if (deltaDB instanceof MainMemoryDB) {
                database = mainMemoryBuilder.extractDatabase(step.getId(), deltaDB, originalDB, step.getScenario());
            }
            if (deltaDB instanceof DBMSDB) {
                database = sqlBuilder.extractDatabase(step.getId(), deltaDB, originalDB, step.getScenario());
            }
        }
        return database;
    }
}
