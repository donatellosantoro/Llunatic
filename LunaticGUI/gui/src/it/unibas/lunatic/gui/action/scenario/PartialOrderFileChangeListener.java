/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.action.scenario;

import it.unibas.lunatic.gui.data.ScenarioDataObject;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileRenameEvent;

/**
 *
 * @author Antonio Galotta
 */
public class PartialOrderFileChangeListener implements FileChangeListener {

    private ScenarioDataObject scenario;

    public PartialOrderFileChangeListener(ScenarioDataObject scenario) {
        this.scenario = scenario;
    }

    @Override
    public void fileFolderCreated(FileEvent fe) {
    }

    @Override
    public void fileDataCreated(FileEvent fe) {
    }

    @Override
    public void fileChanged(FileEvent fe) {
        new ActionLoadScenario(scenario).execute();
    }

    @Override
    public void fileDeleted(FileEvent fe) {
        new ActionLoadScenario(scenario).execute();
    }

    @Override
    public void fileRenamed(FileRenameEvent fe) {
    }

    @Override
    public void fileAttributeChanged(FileAttributeEvent fe) {
    }
}
