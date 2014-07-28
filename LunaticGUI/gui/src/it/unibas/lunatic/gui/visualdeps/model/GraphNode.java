package it.unibas.lunatic.gui.visualdeps.model;

public class GraphNode extends AbstractVisualNode {

    private PinNode defautlPin;

    public GraphNode(String id) {
        super(id);
    }

    public GraphNode(String id, String name) {
        super(id, name);
    }

    public PinNode getDefaultPin() {
        return defautlPin;
    }

    public void setDefautlPin(PinNode defautlPin) {
        assert this.defautlPin == null;
        this.defautlPin = defautlPin;
    }
}
