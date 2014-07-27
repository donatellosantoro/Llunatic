/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.core;

import it.unibas.lunatic.model.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chasemc.operators.dbms.BuildSQLDBForChaseStep;
import it.unibas.lunatic.model.chasemc.operators.mainmemory.BuildMainMemoryDBForChaseStep;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.dbms.DBMSDB;
import it.unibas.lunatic.model.database.mainmemory.MainMemoryDB;

/**
 *
 * @author Antonio Galotta
 */
public class DbExtractor {

    private BuildMainMemoryDBForChaseStep mainMemoryBuilder = new BuildMainMemoryDBForChaseStep();
    private BuildSQLDBForChaseStep sqlBuilder = new BuildSQLDBForChaseStep();

    public IDatabase extractDb(DeltaChaseStep step) {
        IDatabase database = null;
        if (step instanceof DeltaChaseStep) {
            IDatabase deltaDB = step.getDeltaDB();
            IDatabase originalDB = step.getOriginalDB();
            if (deltaDB instanceof MainMemoryDB) {
                database = mainMemoryBuilder.extractDatabase(step.getId(), deltaDB, originalDB);
            }
            if (deltaDB instanceof DBMSDB) {
                database = sqlBuilder.extractDatabase(step.getId(), deltaDB, originalDB);
            }
        }
        return database;
    }
}
