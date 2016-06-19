package it.unibas.lunatic.gui.node.scenario;

import it.unibas.lunatic.model.chase.chasemc.partialorder.OrderingAttribute;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

public class OrderingAttributeNodeFactory extends ChildFactory<OrderingAttribute> {

    private final List<OrderingAttribute> orderingAttributes;

    public OrderingAttributeNodeFactory(List<OrderingAttribute> orderingAttributes) {
        this.orderingAttributes = orderingAttributes;
    }

    @Override
    protected boolean createKeys(List<OrderingAttribute> toPopulate) {
        for(OrderingAttribute attrib : orderingAttributes){
            if ( Thread.interrupted()){
                return false;
            }
            toPopulate.add(attrib);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(OrderingAttribute key) {
        return new OrderingAttributeNode(key);
    }
    
    
}
