package it.unibas.lunatic.model.chase.chasede;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.commons.control.IChaseState;
import speedy.model.database.IDatabase;

public interface IDEChaser {

    public IDatabase doChase(Scenario scenario, IChaseState chaseState);

    public IDatabase doChase(Scenario scenario);
}
