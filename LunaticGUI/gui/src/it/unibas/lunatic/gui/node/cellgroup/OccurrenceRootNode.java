/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.node.cellgroup;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 *
 * @author Antonio Galotta
 */
public class OccurrenceRootNode extends AbstractNode {

    public OccurrenceRootNode(StepCellGroupNode cellGroupNode) {
        super(Children.create(new OccurrenceTupleFactory(cellGroupNode), true));
    }
}
