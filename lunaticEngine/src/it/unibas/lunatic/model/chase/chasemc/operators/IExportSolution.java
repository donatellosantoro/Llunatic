package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;

public interface IExportSolution {

    void export(DeltaChaseStep step, String suffix, Scenario scenario);

    void overrideWorkSchema(DeltaChaseStep step, String suffix, Scenario scenario, boolean cleanPreviousSteps);

}
