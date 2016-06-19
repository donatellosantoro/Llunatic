package it.unibas.lunatic.core.persistence;

import it.unibas.lunatic.OperatorFactory;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.persistence.relational.GenerateModifiedCells;
import java.io.IOException;

public class DaoChaseStep {

    private static DaoChaseStep instance;

    public static DaoChaseStep getInstance() {
        if (instance == null) {
            instance = new DaoChaseStep();
        }
        return instance;
    }

    public void persist(Scenario s, DeltaChaseStep result, String fileName) throws IOException {
        GenerateModifiedCells operator = new GenerateModifiedCells(OperatorFactory.getInstance().getOccurrenceHandler(s));
        if (s.isMainMemory()) {
            operator.generate(result, fileName);
        } else {
            operator.generate(result, fileName);
        }
    }
}
