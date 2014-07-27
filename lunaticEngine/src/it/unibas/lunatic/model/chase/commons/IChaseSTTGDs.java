package it.unibas.lunatic.model.chase.commons;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.database.IDatabase;

public interface IChaseSTTGDs {

    void doChase(Scenario scenario, boolean cleanTarget);

}
