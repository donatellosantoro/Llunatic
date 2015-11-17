package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import speedy.model.database.IDatabase;

public interface IExportSolution {

    void export(DeltaChaseStep step, String suffix, Scenario scenario);

    void overrideWorkSchema(DeltaChaseStep step, String suffix, Scenario scenario, boolean cleanPreviousSteps);

    void overrideWorkSchema(IDatabase database, DeltaChaseStep step, String suffix, Scenario scenario, boolean cleanPreviousSteps);

}
