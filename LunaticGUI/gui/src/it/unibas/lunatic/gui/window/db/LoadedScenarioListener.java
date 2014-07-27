/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.window.db;

import it.unibas.lunatic.AbstractListener;
import it.unibas.lunatic.IModel;
import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.model.LoadedScenario;

/**
 *
 * @author Antonio Galotta
 */
class LoadedScenarioListener extends AbstractListener<LoadedScenario> {

    private TableWindow tableWindow;

    @Override
    public void onChange(IModel m, LoadedScenario ls) {
        if (ls == null || (!ls.getScenario().equals(tableWindow.getTableNode().getScenario()))) {
            tableWindow.close();
        }
    }

    public void register(TableWindow p) {
        this.tableWindow = p;
        super.registerBean(R.Bean.LOADED_SCENARIO, LoadedScenario.class);
    }
}
