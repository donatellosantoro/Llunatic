/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.unibas.lunatic.gui.node.scenario;

import it.unibas.lunatic.Scenario;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 *
 * @author Antonio Galotta
 */
public class OrderingAttributesRootNode extends AbstractNode{

    public OrderingAttributesRootNode(Scenario s) {
        super(Children.create(new OrderingAttributeNodeFactory(s.getOrderingAttributes()), false));
    }
    
    

}
