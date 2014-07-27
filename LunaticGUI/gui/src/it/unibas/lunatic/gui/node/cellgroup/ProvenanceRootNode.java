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
public class ProvenanceRootNode extends AbstractNode {

    public ProvenanceRootNode(StepCellGroupNode stepCellGroupNode) {
        super(Children.create(new ProvenanceTupleFactory(stepCellGroupNode,stepCellGroupNode.getCellGroup().getProvenances()), true));
    }
}
