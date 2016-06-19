package it.unibas.lunatic.gui.action.chase.task;

import it.unibas.lunatic.gui.model.IChaseResult;
import it.unibas.lunatic.gui.model.LoadedScenario;

interface IChaseOperator {

    IChaseResult chase(LoadedScenario loadedScenario);
    
}
