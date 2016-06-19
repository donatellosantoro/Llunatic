package it.unibas.lunatic.gui.model;

import java.util.List;

public interface IChaseResult {

    LoadedScenario getLoadedScenario();

    public List<String> getWindowsToOpen();

    public boolean IsDataExchange();
}
