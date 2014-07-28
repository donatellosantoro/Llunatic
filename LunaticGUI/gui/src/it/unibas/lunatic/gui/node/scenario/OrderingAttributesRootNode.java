
package it.unibas.lunatic.gui.node.scenario;

import it.unibas.lunatic.Scenario;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

public class OrderingAttributesRootNode extends AbstractNode{

    public OrderingAttributesRootNode(Scenario s) {
        super(Children.create(new OrderingAttributeNodeFactory(s.getOrderingAttributes()), false));
    }
    
    

}
