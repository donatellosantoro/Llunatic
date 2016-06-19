package it.unibas.lunatic.gui.window.db;

import it.unibas.lunatic.gui.IViewManager;
import it.unibas.lunatic.gui.node.TableNode;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.TopComponent;

@ServiceProvider(service = TableWindowManager.class)
public class TableWindowManager {

    private static TableWindowManager instance = new TableWindowManager();
    private IViewManager view = Lookup.getDefault().lookup(IViewManager.class);

    public static TableWindowManager getInstance() {
        return instance;
    }

    private TableWindow create(TableNode tableNode) {
        String name = createWindowName(tableNode);
        TableWindow tableWindow = getOpenedTable(tableNode);
        if (tableWindow == null) {
            if (tableNode.isMcResultNode()) {
//                tableWindow = new DbTableTopComponent(tableNode, name);
                tableWindow = new DbPagedTableTopComponent(tableNode, name);
            } else {
                tableWindow = new DbPagedTableTopComponent(tableNode, name);
            }
        }
        return tableWindow;
    }

    public TableWindow getOpenedTable(TableNode tableNode) {
        return getOpenedTable(createWindowName(tableNode));
    }

    public TableWindow getOpenedTable(String name) {
        TopComponent tc = view.findOpenedWindow(name);
        if (tc != null) {
            return (TableWindow) tc;
        }
        return null;
    }

    public String createWindowName(TableNode tableNode) {
        String qualifier = tableNode.getDbDisplayName();
        if (tableNode.isMcResultNode()) {
            qualifier = qualifier.concat(":").concat(tableNode.getChaseStep().getId());
        }
        return qualifier.concat("/").concat(tableNode.getDisplayName());
    }

    public void openTable(TableNode table) {
        TableWindow tableWindow = create(table);
        tableWindow.open();
    }
}
