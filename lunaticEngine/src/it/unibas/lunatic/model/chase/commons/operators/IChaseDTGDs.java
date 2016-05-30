package it.unibas.lunatic.model.chase.commons.operators;

import it.unibas.lunatic.Scenario;
import speedy.model.database.IDatabase;

public interface IChaseDTGDs {

    void doChase(IDatabase currentTarget, Scenario scenario);

}
