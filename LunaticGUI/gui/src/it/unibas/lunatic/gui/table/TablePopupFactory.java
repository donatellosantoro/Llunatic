package it.unibas.lunatic.gui.table;

import speedy.SpeedyConstants;
import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.node.TableTupleNode;
import speedy.model.database.Cell;
import java.awt.Component;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTable;
import org.openide.awt.Actions;
import org.openide.explorer.view.NodePopupFactory;
import org.openide.nodes.Node;

public class TablePopupFactory extends NodePopupFactory {

    private Component separator = new JSeparator();
    private JMenuItem cellGroup;

    public TablePopupFactory() {
        super();
        separator.setEnabled(false);
        Action a = Actions.forID("Window", R.ActionId.SHOW_CELL_GROUP_EDITOR);
        cellGroup = new JMenuItem(a);
    }

    @Override
    public JPopupMenu createPopupMenu(int row, int column, Node[] selectedNodes, Component component) {
        JPopupMenu popup = super.createPopupMenu(row, column, selectedNodes, component);
        if (column > 0 && selectedNodes.length == 1) {
            JTable table = (JTable) component;
            if (table.getSelectedColumn() > 0) {
                TableTupleNode tuple = (TableTupleNode) selectedNodes[0];
                if (tuple.isMcResultNode()) {
                    String columnName = table.getColumnName(column);
                    Cell c = tuple.getCell(columnName);
                    if (c.getValue().getType().equals(SpeedyConstants.LLUN)) {
                        popup.add(separator);
                        popup.add(cellGroup);
                    }
                }
            }
        }
        return popup;
    }
}
