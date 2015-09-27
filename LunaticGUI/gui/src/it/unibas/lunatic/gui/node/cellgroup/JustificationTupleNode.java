package it.unibas.lunatic.gui.node.cellgroup;

import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.node.chase.mc.ChaseStepNode;
import it.unibas.lunatic.gui.node.utils.ITableColumnGenerator;
import it.unibas.lunatic.gui.node.utils.StringProperty;
import speedy.model.database.Cell;
import java.lang.reflect.InvocationTargetException;
import javax.swing.Action;
import org.openide.awt.Actions;
import org.openide.explorer.view.OutlineView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;

public class JustificationTupleNode extends AbstractNode {

    private final Cell cell;
    private final StepCellGroupNode stepCellGroup;
    private Action[] actions = new Action[]{Actions.forID("Window", R.ActionId.SHOW_PROVENANCE_TUPLE)};

    public JustificationTupleNode(Cell key, StepCellGroupNode node) {
        super(Children.LEAF);
        this.cell = key;
        this.stepCellGroup = node;
    }

    @Override
    public Action[] getActions(boolean context) {
        return actions;
    }

    @Override
    public Action getPreferredAction() {
        return actions[0];
    }

    public String getTableName() {
        return cell.getAttributeRef().getTableName();
    }

    public ChaseStepNode getChaseStepNode() {
        return stepCellGroup.getChaseStepNode();
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        sheet.put(set);
        set.put(new StringProperty("tid") {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return cell.getTupleOID().getValue().toString();
            }

            //TODO: implement inplace editor
            @Override
            public boolean canWrite() {
                return true;
            }
        });
        set.put(new StringProperty("table") {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return cell.getAttributeRef().getTableName();
            }

            //TODO: implement inplace editor
            @Override
            public boolean canWrite() {
                return true;
            }
        });
        set.put(new StringProperty("attribute") {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return cell.getAttributeRef().getName();
            }

            //TODO: implement inplace editor
            @Override
            public boolean canWrite() {
                return true;
            }
        });
        set.put(new StringProperty("value") {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return cell.getValue().toString();
            }

            //TODO: implement inplace editor
            @Override
            public boolean canWrite() {
                return true;
            }
        });
        return sheet;
    }
    private static ITableColumnGenerator columnGenerator;

    public static ITableColumnGenerator getColumnGenerator() {
        if (columnGenerator == null) {
            columnGenerator = new ColumnGenerator();
        }
        return columnGenerator;
    }

    private static class ColumnGenerator implements ITableColumnGenerator {

        @Override
        public void createTableColumns(OutlineView outlineView) {
            outlineView.addPropertyColumn("tid", "tid");
            outlineView.addPropertyColumn("table", "table");
            outlineView.addPropertyColumn("attribute", "attribute");
            outlineView.addPropertyColumn("value", "value");
        }
    }
}
