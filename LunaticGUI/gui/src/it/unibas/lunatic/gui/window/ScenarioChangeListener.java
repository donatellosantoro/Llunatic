/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.window;

import it.unibas.lunatic.AbstractListener;
import it.unibas.lunatic.IModel;
import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.model.LoadedScenario;

/**
 *
 * @author Antonio Galotta
 */
public class ScenarioChangeListener extends AbstractListener<LoadedScenario> {

    private LoadedScenario scenario;
    private Target target;

    @Override
    public void onChange(IModel m, LoadedScenario ls) {
        if ( scenario != null && ls == null){
            target.onScenarioClose(scenario);
        }else if (scenario != null && !scenario.equals(ls)) {
            logger.debug("Scenario changed");
            target.onScenarioChange(scenario, ls);
        }        
        this.scenario = ls;
    }

    public void register(Target target) {
        this.target = target;
        super.registerBean(R.Bean.LOADED_SCENARIO, LoadedScenario.class);
    }

    public interface Target {

        void onScenarioChange(LoadedScenario oldScenario, LoadedScenario newScenario);

        void onScenarioClose(LoadedScenario scenario);
    }
}
