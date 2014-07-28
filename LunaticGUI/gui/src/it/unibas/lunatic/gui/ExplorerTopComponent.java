package it.unibas.lunatic.gui;

import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.nodes.Node;
import org.openide.windows.TopComponent;

public abstract class ExplorerTopComponent extends TopComponent implements ExplorerManager.Provider {

    protected ExplorerManager explorer = new ExplorerManager();

    protected void associateExplorerLookup() {
        associateLookup(ExplorerUtils.createLookup(explorer, getActionMap()));
    }

    public abstract void setRootContext(Node node);

    public abstract void removeRootContext();

    @Override
    public ExplorerManager getExplorerManager() {
        return explorer;
    }
}
