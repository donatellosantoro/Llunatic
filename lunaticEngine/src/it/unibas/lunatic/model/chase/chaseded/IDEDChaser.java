package it.unibas.lunatic.model.chase.chaseded;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.commons.control.IChaseState;
import speedy.model.database.IDatabase;

public interface IDEDChaser {

    public IDatabase doChase(Scenario scenario);
    
    public IDatabase doChase(Scenario scenario, IChaseState chaseState);
}
