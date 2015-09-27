package it.unibas.lunatic.core.persistence;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.core.GenerateModifiedCells;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import java.io.IOException;
import speedy.model.database.operators.IRunQuery;
import speedy.model.database.operators.dbms.SQLRunQuery;
import speedy.model.database.operators.mainmemory.MainMemoryRunQuery;

public class DaoChaseStep {

    private IRunQuery mmRunQuery = new MainMemoryRunQuery();
    private IRunQuery sqlIRunQuery = new SQLRunQuery();
    private GenerateModifiedCells mmGenerateModifiedCells = new GenerateModifiedCells(mmRunQuery);
    private GenerateModifiedCells sqlGenerateModifiedCells = new GenerateModifiedCells(sqlIRunQuery);
    private static DaoChaseStep instance;

    public static DaoChaseStep getInstance() {
        if (instance == null) {
            instance = new DaoChaseStep();
        }
        return instance;
    }

    public void persist(Scenario s, DeltaChaseStep result, String fileName) throws IOException {
        if (s.isMainMemory()) {
            mmGenerateModifiedCells.generate(result, fileName);
        } else {
            sqlGenerateModifiedCells.generate(result, fileName);
        }
    }
}
