/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.node.dependencies;

import javax.swing.Action;
import org.openide.nodes.FilterNode;

/**
 *
 * @author Antonio Galotta
 */
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
