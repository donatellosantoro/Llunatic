package it.unibas.lunatic.gui.action.cellgroups;

import it.unibas.lunatic.IApplication;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.gui.IViewManager;
import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.core.CellGroupHelper;
import it.unibas.lunatic.gui.model.LoadedScenario;
import it.unibas.lunatic.gui.model.McChaseResult;
import it.unibas.lunatic.core.StepCellGroups;
import it.unibas.lunatic.gui.node.TableTupleNode;
import it.unibas.lunatic.gui.node.cellgroup.StepCellGroupNode;
import it.unibas.lunatic.gui.node.chase.mc.ChaseStepNode;
import it.unibas.lunatic.gui.node.chase.mc.ChaseTreeRoot;
import it.unibas.lunatic.gui.node.chase.mc.ChaseTreeSupport;
import it.unibas.lunatic.gui.window.cellgroup.CellGroupMultiViewManager;
import it.unibas.lunatic.gui.window.db.TableWindow;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import speedy.model.database.Cell;
import speedy.model.database.IValue;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Window",
        id = R.ActionId.SHOW_CELL_GROUP_EDITOR)
@ActionRegistration(
        displayName = "#CTL_ActionShowCellGroup", asynchronous = true)
@Messages({
    "CTL_ActionShowCellGroup=Cell group",
    "MSG_NoCellGroupForSelectedCell=Selected cell doesn't belong to any cell group"})
public final class ActionShowCellGroupsFromTable implements ActionListener {

    private IViewManager view = Lookup.getDefault().lookup(IViewManager.class);
    private IApplication app = Lookup.getDefault().lookup(IApplication.class);
    private CellGroupHelper cgHelper = CellGroupHelper.getInstance();
    private Log logger = LogFactory.getLog(getClass());
    private StatusDisplayer status = StatusDisplayer.getDefault();
    private ChaseTreeSupport chaseTree = ChaseTreeSupport.getInstance();
    private CellGroupMultiViewManager cellGroupMultiViewFactory = CellGroupMultiViewManager.getInstance();

    @Override
    public void actionPerformed(ActionEvent ev) {
        try {
            TableWindow table = (TableWindow) view.getActivatedWindow();
            TableTupleNode tuple = table.getSelectedNode();
            assert tuple != null;
            assert tuple.isMcResultNode();
            Cell cell = table.getSelectedCell();
            assert cell != null;
            IValue value = cell.getValue();
            LoadedScenario ls = app.get(R.Bean.LOADED_SCENARIO, LoadedScenario.class);
            Scenario scenario = ls.getScenario();
            assert scenario.isMCScenario();
            DeltaChaseStep step = tuple.getChaseStep();
            McChaseResult chaseResult = ls.get(R.BeanProperty.CHASE_RESULT, McChaseResult.class);
            ChaseTreeRoot node = chaseResult.getNode();
            //ChaseStepNode cannot be saved in tables because they changes during interactive chase
            ChaseStepNode chaseStep = chaseTree.findChaseStepNode(node, step);
            StepCellGroups cellGroups = null;
            if (chaseStep.hasCellGroupsLoaded()) {
                cellGroups = chaseStep.getCellGroups();
            } else {
                cellGroups = cgHelper.retrieveStepCellGroups(scenario, step);
                chaseStep.cacheCellGroups(cellGroups);
            }
            CellGroup cg = cgHelper.findCellGroup(cellGroups.getAll(), value);
            if (cg != null) {
                StepCellGroupNode cellGroupNode = new StepCellGroupNode(cg, chaseStep);
                view.invokeLater(new Result(ls, cellGroupNode));
            } else {
                status.setStatusText(Bundle.MSG_NoCellGroupForSelectedCell());
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.debug(e);
        }
    }

    private class Result implements Runnable {

        private final StepCellGroupNode cInfo;
        private final LoadedScenario ls;

        private Result(LoadedScenario ls, StepCellGroupNode cInfo) {
            this.ls = ls;
            this.cInfo = cInfo;
        }

        @Override
        public void run() {
            ls.put(R.BeanProperty.SELECTED_CELL_GROUP_NODE, cInfo);
            cellGroupMultiViewFactory.open();
        }
    }
}
