package it.unibas.lunatic.gui.action.cellgroups;

import it.unibas.lunatic.gui.node.TableFinder;
import it.unibas.lunatic.ContextAwareActionProxy;
import it.unibas.lunatic.IModel;
import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.core.CellGroupHelper;
import it.unibas.lunatic.core.DbExtractor;
import it.unibas.lunatic.gui.node.TableNode;
import it.unibas.lunatic.gui.node.cellgroup.StepCellGroupNode;
import it.unibas.lunatic.gui.node.chase.mc.ChaseStepNode;
import it.unibas.lunatic.gui.window.db.TableWindow;
import it.unibas.lunatic.gui.window.db.TableWindowManager;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.operators.AddUserNode;
import speedy.model.database.ConstantValue;
import java.awt.event.ActionEvent;
import java.util.List;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Edit",
        id = R.ActionId.EDIT_CELL_GROUP_VALUE)
@ActionRegistration(
        displayName = "#CTL_ActionEditSelectedCellGroup", lazy = false)
@Messages({
    "CTL_ActionEditSelectedCellGroup=Edit cell group value",
    "MSG_NewValue=New value",
    "MSG_ReadOnlyUserNode=You cannot modify this cell group, it has already been evaluated.\n Make a new user node instead.",
    "DIALOG_TITLE_CellGroupEditor=Cell group editor"
})
public final class ActionEditSelectedCellGroup extends ContextAwareActionProxy<StepCellGroupNode> {

    private NotifyDescriptor.InputLine inputDialog = new NotifyDescriptor.InputLine(Bundle.MSG_NewValue(), Bundle.DIALOG_TITLE_CellGroupEditor());
    private NotifyDescriptor.Message readOnlyUserNode = new NotifyDescriptor.Message(Bundle.MSG_ReadOnlyUserNode());
    private DialogDisplayer dialogDisplayer = DialogDisplayer.getDefault();
    private CellGroupHelper cgHelper = CellGroupHelper.getInstance();
    private TableWindowManager tableWindowManager = TableWindowManager.getInstance();
    private TableFinder tableFinder = new TableFinder();
    private DbExtractor dbHelper = new DbExtractor();

    public ActionEditSelectedCellGroup() {
        putValue(NAME, Bundle.CTL_ActionEditSelectedCellGroup());
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        assert getBean() != null;
        StepCellGroupNode cellGroupNode = getBean();
        ChaseStepNode userNode = cellGroupNode.getChaseStepNode();
        if (userNode.hasBeenEvaluated()) {
            dialogDisplayer.notify(readOnlyUserNode);
        } else {
            inputDialog.setInputText(cellGroupNode.getValue());
            if (dialogDisplayer.notify(inputDialog).equals(NotifyDescriptor.OK_OPTION)) {
                String value = inputDialog.getInputText();
                logger.debug("Input value: " + value);
                if (value != null) {
                    AddUserNode editor = cgHelper.getEditor(userNode.getScenario());
                    StepCellGroupNode editableCellGroupNode = getBean();
                    CellGroup cellGroup = editor.addChange(editableCellGroupNode.getChaseStep(), editableCellGroupNode.getCellGroup(), new ConstantValue(value), editableCellGroupNode.getChaseStep().getScenario());
                    editableCellGroupNode.setUserCellGroup(cellGroup);
                    dbHelper.extractDb(userNode.getChaseStep());
                    logger.debug("Edited cellGroup: " + editableCellGroupNode.toString());
                    getModel().notifyChange(R.BeanProperty.SELECTED_CELL_GROUP_NODE, editableCellGroupNode.getClass());
                    updateOpenedTables(userNode);
                }
            }
        }
    }

    private void updateOpenedTables(ChaseStepNode userNode) {
        List<TableNode> tables = tableFinder.findTables(userNode);
        for (TableNode tn : tables) {
            TableWindow openedTable = tableWindowManager.getOpenedTable(tn);
            if (openedTable != null) {
                openedTable.updateTable();
            }
        }
    }

    @Override
    public void onChange(IModel model, StepCellGroupNode stepCellGroupNode) {
        if (model != null) {
            if (stepCellGroupNode != null && stepCellGroupNode.getChaseStep().isEditedByUser()) {
                setEnabled(true);
                return;
            }
        }
        setEnabled(false);
    }

    @Override
    protected void register() {
        super.registerBean(R.Bean.LOADED_SCENARIO, R.BeanProperty.SELECTED_CELL_GROUP_NODE, StepCellGroupNode.class);
    }
}
