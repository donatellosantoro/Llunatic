/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.node.chase.de;

import it.unibas.lunatic.gui.model.DeChaseResult;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 *
 * @author Antonio Galotta
 */
public class DeResultNode extends AbstractNode {

    private DeChaseResult chase;

    public DeResultNode(DeChaseResult chaseResult) {
        super(Children.create(new DeChaseResultChildFactory(chaseResult), false));
        this.chase = chaseResult;
        setDisplayName(chase.getLoadedScenario().getDataObject().getName());
        setIconBaseWithExtension("it/unibas/lunatic/icons/datasource.png");
    }

    public DeChaseResult getChase() {
        return chase;
    }
}
