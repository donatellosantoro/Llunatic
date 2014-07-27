/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.action.cellgroups;

import it.unibas.lunatic.IApplication;
import it.unibas.lunatic.gui.IViewManager;
import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.model.LoadedScenario;
import it.unibas.lunatic.gui.node.cellgroup.StepCellGroupNode;
import it.unibas.lunatic.gui.window.cellgroup.CellGroupMultiViewManager;
import it.unibas.lunatic.model.chasemc.CellGroup;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Window",
        id = R.ActionId.SHOW_CELL_GROUP_DETAILS)
@ActionRegistration(
        displayName = "#CTL_ActionShowCellGroupDetails", asynchronous = false)
@Messages("CTL_ActionShowCellGroupDetails=Show")
public final class ActionShowCellGroupDetails implements ActionListener {

    private IApplication app = Lookup.getDefault().lookup(IApplication.class);
    private Log logger = LogFactory.getLog(getClass());
    private IViewManager view = Lookup.getDefault().lookup(IViewManager.class);
    private final StepCellGroupNode cellGroupNode;
    private CellGroupMultiViewManager cellGroupMultiViewFactory = CellGroupMultiViewManager.getInstance();

    public ActionShowCellGroupDetails(StepCellGroupNode node) {
        this.cellGroupNode = node;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        LoadedScenario ls = app.get(R.Bean.LOADED_SCENARIO, LoadedScenario.class);
        CellGroup cg = cellGroupNode.getCellGroup();
        if (cg != null) {
            ls.put(R.BeanProperty.SELECTED_CELL_GROUP_NODE, cellGroupNode);
            cellGroupMultiViewFactory.open();
        }
    }
}
