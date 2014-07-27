/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.visualdeps.model;

/**
 *
 * @author Antonio Galotta
 */
public class PinNode extends AbstractVisualNode {

    private final GraphNode graphNode;

    public PinNode(GraphNode graphNode, String localId, String name) {
        super(graphNode.getId().concat(".").concat(localId), name);
        this.graphNode = graphNode;
    }

    public PinNode(GraphNode graphNode, String localId) {
        this(graphNode, localId, localId);
    }

    public GraphNode getGraphNode() {
        return graphNode;
    }

}
