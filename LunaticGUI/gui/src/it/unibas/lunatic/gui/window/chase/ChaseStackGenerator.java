/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.window.chase;

import it.unibas.lunatic.AbstractSelectionListener;
import it.unibas.lunatic.gui.node.chase.mc.stack.ChaseAncestorsRootNode;
import it.unibas.lunatic.gui.node.chase.mc.ChaseStepNode;
import java.util.Collection;
import org.openide.explorer.ExplorerManager;

/**
 *
 * @author Antonio Galotta
 */
public class ChaseStackGenerator extends AbstractSelectionListener<ChaseStepNode> {

    private ExplorerManager.Provider provider;

    @Override
    public void onChange(Collection<? extends ChaseStepNode> selection) {
        if (!selection.isEmpty()) {
            ChaseStepNode selected = selection.iterator().next();
            ChaseAncestorsRootNode stack = new ChaseAncestorsRootNode(selected.getChaseStep(), selected.getScenario());
            provider.getExplorerManager().setRootContext(stack);
        }
    }

    public void register(ExplorerManager.Provider provider) {
        this.provider = provider;
        registerBean(ChaseStepNode.class);
    }
}
