/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.action.cellgroups;

import it.unibas.lunatic.IApplication;
import it.unibas.lunatic.gui.IViewManager;
import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.core.CellGroupHelper;
import it.unibas.lunatic.gui.model.LoadedScenario;
import it.unibas.lunatic.core.StepCellGroups;
import it.unibas.lunatic.gui.node.chase.mc.ChaseStepNode;
import it.unibas.lunatic.model.chasemc.DeltaChaseStep;
import org.openide.util.Lookup;

public final class RetrieveCellGroupsAction {

    private IViewManager view = Lookup.getDefault().lookup(IViewManager.class);
    private IApplication app = Lookup.getDefault().lookup(IApplication.class);
    private CellGroupHelper cgHelper = CellGroupHelper.getInstance();

    public void retrieve(ChaseStepNode node) {
        LoadedScenario ls = app.get(R.Bean.LOADED_SCENARIO, LoadedScenario.class);
        DeltaChaseStep step = node.getChaseStep();
        StepCellGroups stepCellGroups = cgHelper.retrieveStepCellGroups(ls.getScenario(), step);
        view.invokeLater(new Result(ls, stepCellGroups));
    }

    private class Result implements Runnable {

        private final LoadedScenario ls;
        private final StepCellGroups stepCg;

        private Result(LoadedScenario ls, StepCellGroups stepCellGroups) {
            this.ls = ls;
            this.stepCg = stepCellGroups;
        }

        @Override
        public void run() {
            ls.put(R.BeanProperty.STEP_CELL_GROUPS, stepCg);
            view.show(R.Window.CELL_GROUP_EXPLORER);
        }
    }
}
