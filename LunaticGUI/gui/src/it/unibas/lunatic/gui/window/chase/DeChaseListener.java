package it.unibas.lunatic.gui.window.chase;

import it.unibas.lunatic.gui.ExplorerTopComponent;
import it.unibas.lunatic.AbstractListener;
import it.unibas.lunatic.IModel;
import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.model.DeChaseResult;
import it.unibas.lunatic.gui.model.IChaseResult;
import it.unibas.lunatic.gui.node.chase.de.DeResultNode;
import org.openide.nodes.Node;

public class DeChaseListener extends AbstractListener<IChaseResult> {

    private ExplorerTopComponent tc;

    @Override
    public void onChange(IModel ls, IChaseResult cs) {
        if (ls != null && cs != null && cs.IsDataExchange()) {
            tc.setRootContext(new DeResultNode((DeChaseResult) cs));
            return;
        }
        tc.setRootContext(Node.EMPTY);
        tc.close();
    }

    public void register(ExplorerTopComponent tc) {
        this.tc = tc;
        super.registerBean(R.Bean.LOADED_SCENARIO, R.BeanProperty.CHASE_RESULT, IChaseResult.class);
    }
}
