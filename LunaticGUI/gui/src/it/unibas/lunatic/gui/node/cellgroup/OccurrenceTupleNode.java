package it.unibas.lunatic.gui.node.cellgroup;

import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.node.chase.mc.ChaseStepNode;
import it.unibas.lunatic.gui.node.utils.ITableColumnGenerator;
import it.unibas.lunatic.gui.node.utils.StringProperty;
import it.unibas.lunatic.model.database.CellRef;
import it.unibas.lunatic.model.database.IValue;
import java.lang.reflect.InvocationTargetException;
import javax.swing.Action;
import org.openide.awt.Actions;
import org.openide.explorer.view.OutlineView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;

@NbBundle.Messages({
    "COL_OriginalValue=original value",
    "COL_PreviousValue=previous value"
})
public class OccurrenceTupleNode extends AbstractNode {

    public static final String TID = "tid";
    public static final String TABLE = "table";
    public static final String ATTRIBUTE = "attribute";
    public static final String PREVIOUS_VALUE = "previousValue";
    public static final String ORIGINAL_VALUE = "originalValue";
    private final CellRef cellref;
    private IValue originalValue;
    private IValue previousValue;
    private Action[] actions = new Action[]{Actions.forID("Window", R.ActionId.SHOW_OCCURRENCE_TUPLE)};
    private final ChaseStepNode chaseStepNode;

    public OccurrenceTupleNode(ChaseStepNode chaseStepNode, CellRef cellRef, IValue original) {
        super(Children.LEAF);
        this.cellref = cellRef;
        this.originalValue = original;
        this.chaseStepNode = chaseStepNode;
    }

    public void setPreviousValue(IValue previousValue) {
        this.previousValue = previousValue;
    }

    @Override
    public Action[] getActions(boolean context) {
        return actions;
    }

    @Override
    public Action getPreferredAction() {
        return actions[0];
    }

    public CellRef getOccurrence() {
        return cellref;
    }

    public IValue getOriginalValue() {
        return originalValue;
    }

    public IValue getPreviousValue() {
        return previousValue;
    }

    public ChaseStepNode getChaseStepNode() {
        return chaseStepNode;
    }

    public String getTableName() {
        return cellref.getAttributeRef().getTableName();
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
            outlineView.addPropertyColumn(TID, TID);
            outlineView.addPropertyColumn(TABLE, TABLE);
            outlineView.addPropertyColumn(ATTRIBUTE, ATTRIBUTE);
            outlineView.addPropertyColumn(PREVIOUS_VALUE, Bundle.COL_PreviousValue());
            outlineView.addPropertyColumn(ORIGINAL_VALUE, Bundle.COL_OriginalValue());
        }
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        sheet.put(set);
        set.put(new StringProperty(TID) {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return cellref.getTupleOID().getValue().toString();
            }

            //TODO: implement inplace editor
            @Override
            public boolean canWrite() {
                return true;
            }
        });
        set.put(new StringProperty(TABLE) {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return cellref.getAttributeRef().getTableName();
            }

            //TODO: implement inplace editor
            @Override
            public boolean canWrite() {
                return true;
            }
        });
        set.put(new StringProperty(ATTRIBUTE) {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return cellref.getAttributeRef().getName();
            }

            //TODO: implement inplace editor
            @Override
            public boolean canWrite() {
                return true;
            }
        });
        set.put(new StringProperty(PREVIOUS_VALUE, Bundle.COL_PreviousValue()) {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                if (previousValue == null) {
                    return "-";
                }
                return previousValue.toString();
            }

            //TODO: implement inplace editor
            @Override
            public boolean canWrite() {
                return true;
            }
        });

        set.put(new StringProperty(ORIGINAL_VALUE, Bundle.COL_OriginalValue()) {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return originalValue.toString();
            }

            //TODO: implement inplace editor
            @Override
            public boolean canWrite() {
                return true;
            }
        });
        return sheet;
    }
}
