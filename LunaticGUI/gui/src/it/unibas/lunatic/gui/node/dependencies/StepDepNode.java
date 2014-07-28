package it.unibas.lunatic.gui.node.dependencies;

import javax.swing.Action;
import org.openide.nodes.FilterNode;

public class StepDepNode extends FilterNode {
    
    
    public StepDepNode(DepTupleNode original) {
        super(original);
        setDisplayName(original.getDependency().getId());
    }
    
    @Override
    public Action[] getActions(boolean context) {
        return null;
    }

    @Override
    public Action getPreferredAction() {
        return null;
    }
    
    
}
