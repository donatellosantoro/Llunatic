/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.node.dependencies;

import it.unibas.lunatic.model.dependency.Dependency;
import java.util.List;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 *
 * @author Antonio Galotta
 */
public class DepListNode extends AbstractNode {

    public DepListNode(List<Dependency> depList, String name) {
        this(depList, name, true);
    }

    public DepListNode(List<Dependency> depList, String name, boolean async) {
        super(Children.create(new DepListChildFactory(depList), async));
        setName(name);
        setDisplayName(name);
    }
}
