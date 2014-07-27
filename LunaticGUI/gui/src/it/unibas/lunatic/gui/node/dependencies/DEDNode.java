/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.unibas.lunatic.gui.node.dependencies;

import it.unibas.lunatic.model.dependency.DED;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 *
 * @author Antonio Galotta
 */
public class DEDNode extends AbstractNode {

    public DEDNode(DED ded, String name) {
        super(Children.create(new DepListChildFactory(ded.getAssociatedDependencies()), true));
        setDisplayName(name);
    }

}
