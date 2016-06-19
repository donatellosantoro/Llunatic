package it.unibas.lunatic.gui.window.chase;

import it.unibas.lunatic.gui.ExplorerTopComponent;
import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.model.McChaseResult;
import it.unibas.lunatic.gui.node.chase.mc.stack.RankedSolutionsTreeRoot;
import javax.swing.JComponent;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.nodes.Node;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;

@ConvertAsProperties(
        dtd = "-//it.unibas.lunatic.gui//MCChaseResultRankedSolutions//EN",
        autostore = false)
@TopComponent.Description(
        preferredID = R.Window.MC_CHASE_RESULT_RANKED_SOLUTIONS,
        persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "explorer", openAtStartup = false)
@Messages({
    "CTL_MCChaseResultRankedSolutionsAction=Ranked Solutions",
    "CTL_MCChaseResultRankedSolutionsTopComponent=Ranked Solutions",
    "HINT_MCChaseResultRankedSolutionsTopComponent=Ranked Solutions window"
})
public final class MCChaseResultRankedSolutionsTopComponent extends ExplorerTopComponent implements McResultView {

//    private ExplorerManager explorerManager;
    public MCChaseResultRankedSolutionsTopComponent() {
        initComponents();
        setName(R.Window.MC_CHASE_RESULT_RANKED_SOLUTIONS);
        setDisplayName(Bundle.CTL_MCChaseResultRankedSolutionsTopComponent());
        setToolTipText(Bundle.HINT_MCChaseResultRankedSolutionsTopComponent());
        associateExplorerLookup();
        updateResults();
    }
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        treeView = new org.openide.explorer.view.BeanTreeView();

        setLayout(new java.awt.BorderLayout());
        add(treeView, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.openide.explorer.view.BeanTreeView treeView;
    // End of variables declaration//GEN-END:variables
    private McResultUpdateListener listener = new McResultUpdateListener();

    @Override
    public void componentOpened() {
        listener.register(this);
//        selectedView.componentOpened();
    }

    @Override
    public void componentClosed() {
        listener.remove();
//        selectedView.componentClosed();
    }

    @Override
    public void setRootContext(Node node) {
        explorer.setRootContext(node);
    }

    @Override
    public void removeRootContext() {
        explorer.setRootContext(Node.EMPTY);
    }

    @Override
    public void onChaseResultUpdate(McChaseResult result) {
        updateResults();
        explorer.setRootContext(new RankedSolutionsTreeRoot(result));
    }

    @Override
    public void onChaseResultClose() {
        close();
    }

    @Override
    public JComponent toComponent() {
        return this;
    }

    private void updateResults() {
    }

    void writeProperties(java.util.Properties p) {
        p.setProperty("version", "1.3");
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
    }
}
