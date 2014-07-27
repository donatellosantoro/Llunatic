/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.node.chase.mc.stack;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chasemc.DeltaChaseStep;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 *
 * @author Antonio Galotta
 */
public class ChaseAncestorsRootNode extends AbstractNode {

    public ChaseAncestorsRootNode(DeltaChaseStep step, Scenario s) {
        super(Children.create(new ChaseStepAncestorFactory(step, s), false));
    }

    
}
