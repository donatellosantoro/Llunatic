package it.unibas.lunatic.gui.visualdeps;

import it.unibas.lunatic.gui.visualdeps.model.EdgeNode;
import it.unibas.lunatic.gui.visualdeps.model.GraphNode;
import it.unibas.lunatic.gui.visualdeps.model.PinNode;
import org.netbeans.api.visual.vmd.VMDConnectionWidget;
import org.netbeans.api.visual.vmd.VMDNodeWidget;
import org.netbeans.api.visual.vmd.VMDPinWidget;

public interface IVmdScene {

    VMDConnectionWidget createEdge(EdgeNode edge);

    VMDNodeWidget createNode(GraphNode graphNode, boolean createDefaultPin);

    VMDPinWidget createPin(PinNode pinNode);
}
