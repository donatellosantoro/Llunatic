package it.unibas.lunatic.gui.window.cellgroup;

import it.unibas.lunatic.AbstractSelectionListener;
import it.unibas.lunatic.IApplication;
import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.model.LoadedScenario;
import it.unibas.lunatic.gui.node.cellgroup.StepCellGroupNode;
import java.util.Collection;
import org.openide.util.Lookup;

public class StepCellGroupSelectionListener extends AbstractSelectionListener<StepCellGroupNode> {

    private IApplication app = Lookup.getDefault().lookup(IApplication.class);

    @Override
    public void onChange(Collection<? extends StepCellGroupNode> selection) {
        StepCellGroupNode selected = getBean(selection);
        if (selected != null) {
            LoadedScenario ls = app.get(R.Bean.LOADED_SCENARIO, LoadedScenario.class);
            ls.put(R.BeanProperty.SELECTED_CELL_GROUP_NODE, selected);
        }
    }

    public void register() {
        registerBean(StepCellGroupNode.class);
    }
}
