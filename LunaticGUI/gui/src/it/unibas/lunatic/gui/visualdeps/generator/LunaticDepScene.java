package it.unibas.lunatic.gui.visualdeps.generator;

import it.unibas.lunatic.gui.visualdeps.components.BestPathAnchor;
import it.unibas.lunatic.gui.visualdeps.components.CustomConnectionWidget;
import it.unibas.lunatic.gui.visualdeps.IVmdScene;
import it.unibas.lunatic.gui.visualdeps.GraphColorScheme;
import it.unibas.lunatic.gui.visualdeps.model.GraphNode;
import it.unibas.lunatic.gui.visualdeps.model.EdgeNode;
import it.unibas.lunatic.gui.visualdeps.model.PinNode;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.anchor.Anchor;
import org.netbeans.api.visual.graph.GraphPinScene;
import org.netbeans.api.visual.layout.SceneLayout;
import org.netbeans.api.visual.router.Router;
import org.netbeans.api.visual.router.RouterFactory;
import org.netbeans.api.visual.vmd.VMDColorScheme;
import org.netbeans.api.visual.vmd.VMDConnectionWidget;
import org.netbeans.api.visual.vmd.VMDNodeWidget;
import org.netbeans.api.visual.vmd.VMDPinWidget;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.EventProcessingType;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;

public class LunaticDepScene extends GraphPinScene<GraphNode, EdgeNode, PinNode> implements IVmdScene {

    public static final String PIN_ID_DEFAULT_SUFFIX = "#default"; // NOI18N
    private LayerWidget backgroundLayer = new LayerWidget(this);
    private LayerWidget mainLayer = new LayerWidget(this);
    private LayerWidget connectionLayer = new LayerWidget(this);
    private LayerWidget upperLayer = new LayerWidget(this);
    private Router freeRouter = RouterFactory.createFreeRouter();
    private Router orthoRouter;
    private WidgetAction moveControlPointAction = ActionFactory.createOrthogonalMoveControlPointAction();
    private WidgetAction moveAction = ActionFactory.createMoveAction();
    private VMDColorScheme scheme;
    private SceneLayout sceneLayout;

    public LunaticDepScene() {
        this(GraphColorScheme.getInstance());
    }

    public LunaticDepScene(VMDColorScheme scheme) {
        this.scheme = scheme;
        setKeyEventProcessingType(EventProcessingType.FOCUSED_WIDGET_AND_ITS_PARENTS);
        addChild(backgroundLayer);
        addChild(mainLayer);
        addChild(connectionLayer);
        addChild(upperLayer);
        orthoRouter = RouterFactory.createOrthogonalSearchRouter(mainLayer, connectionLayer);
        getActions().addAction(ActionFactory.createZoomAction());
        getActions().addAction(ActionFactory.createPanAction());
        getActions().addAction(ActionFactory.createRectangularSelectAction(this, backgroundLayer));
    }

    /**
     * Implements attaching a widget to a node. The widget is VMDNodeWidget and
     * has object-hover, select, popup-menu and move actions.
     *
     * @param node the node
     * @return the widget attached to the node
     */
    @Override
    protected Widget attachNodeWidget(GraphNode node) {
        VMDNodeWidget widget = new VMDNodeWidget(this, scheme);
        mainLayer.addChild(widget);
        widget.getHeader().getActions().addAction(createObjectHoverAction());
        widget.getActions().addAction(createSelectAction());
        widget.getActions().addAction(moveAction);
        return widget;
    }

    /**
     * Implements attaching a widget to a pin. The widget is VMDPinWidget and
     * has object-hover and select action.
     *
     * @param node the node
     * @param pin the pin
     * @return the widget attached to the pin, null, if it is a default pin
     */
    @Override
    protected Widget attachPinWidget(GraphNode node, PinNode pin) {
        if (pin.isHidden()) {
            return null;
        }
        VMDPinWidget widget = new VMDPinWidget(this, scheme);
        ((VMDNodeWidget) findWidget(node)).attachPinWidget(widget);
        widget.getActions().addAction(createObjectHoverAction());
        widget.getActions().addAction(createSelectAction());
        return widget;
    }

    /**
     * Implements attaching a widget to an edge. the widget is ConnectionWidget
     * and has object-hover, select and move-control-point actions.
     *
     * @param edge the edge
     * @return the widget attached to the edge
     */
    @Override
    protected Widget attachEdgeWidget(EdgeNode edge) {
        VMDConnectionWidget connectionWidget = new CustomConnectionWidget(this, scheme);
        connectionLayer.addChild(connectionWidget);
        connectionWidget.getActions().addAction(createObjectHoverAction());
        connectionWidget.getActions().addAction(createSelectAction());
        connectionWidget.getActions().addAction(moveControlPointAction);
        return connectionWidget;
    }

    /**
     * Attaches an anchor of a source pin an edge. The anchor is a ProxyAnchor
     * that switches between the anchor attached to the pin widget directly and
     * the anchor attached to the pin node widget based on the minimize-state of
     * the node.
     *
     * @param edge the edge
     * @param oldSourcePin the old source pin
     * @param sourcePin the new source pin
     */
    @Override
    protected void attachEdgeSourceAnchor(EdgeNode edge, PinNode oldSourcePin, PinNode sourcePin) {
        ((ConnectionWidget) findWidget(edge)).setSourceAnchor(getPinAnchor(sourcePin));
    }

    /**
     * Attaches an anchor of a target pin an edge. The anchor is a ProxyAnchor
     * that switches between the anchor attached to the pin widget directly and
     * the anchor attached to the pin node widget based on the minimize-state of
     * the node.
     *
     * @param edge the edge
     * @param oldTargetPin the old target pin
     * @param targetPin the new target pin
     */
    @Override
    protected void attachEdgeTargetAnchor(EdgeNode edge, PinNode oldTargetPin, PinNode targetPin) {
        ((ConnectionWidget) findWidget(edge)).setTargetAnchor(getPinAnchor(targetPin));
    }

    private Anchor getPinAnchor(PinNode pin) {
        if (pin == null) {
            return null;
        }
        VMDNodeWidget nodeWidget = (VMDNodeWidget) findWidget(super.getPinNode(pin));
        Widget pinMainWidget = findWidget(pin);
        Anchor anchor;
        if (pinMainWidget != null) {
            anchor = new BestPathAnchor(pinMainWidget);
            anchor = nodeWidget.createAnchorPin(anchor);
        } else {
            anchor = nodeWidget.getNodeAnchor();
        }
        return anchor;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VMDNodeWidget createNode(GraphNode graphNode, boolean createDefaultPin) {
        VMDNodeWidget widget = (VMDNodeWidget) addNode(graphNode);
        widget.setNodeName(graphNode.getDisplayName());
        if (createDefaultPin) {
            PinNode defaultPin = new PinNode(graphNode, PIN_ID_DEFAULT_SUFFIX);
            defaultPin.setHidden(true);
            graphNode.setDefautlPin(defaultPin);
            addPin(graphNode, graphNode.getDefaultPin());
        }
        return widget;
    }

    @Override
    public VMDPinWidget createPin(PinNode pinNode) {
        VMDPinWidget widget = (VMDPinWidget) addPin(pinNode.getGraphNode(), pinNode);
        widget.setPinName(pinNode.getDisplayName());
        return widget;
    }

    @SuppressWarnings("deprecation")
    public void createHiddenPin(PinNode pinNode) {
        pinNode.setHidden(true);
        addPin(pinNode.getGraphNode(), pinNode);
    }

    @Override
    public VMDConnectionWidget createEdge(EdgeNode edge) {
        VMDConnectionWidget widget = (VMDConnectionWidget) addEdge(edge);
        widget.setForeground(edge.getColor());
        setEdgeSource(edge, edge.getSource());
        setEdgeTarget(edge, edge.getTarget());
        if (widget.getRouter() == null) {
            widget.setRouter(freeRouter);
        }
        return widget;
    }

    public VMDConnectionWidget createEdge(EdgeNode edge, boolean directional) {
        CustomConnectionWidget widget = (CustomConnectionWidget) createEdge(edge);
        widget.setDirectional(directional);
        return widget;
    }

    public Router getOrthogonalRouter() {
        return orthoRouter;
    }

    public Router getFreeRouter() {
        return freeRouter;
    }

    public void setSceneLayout(SceneLayout sceneLayout) {
        this.sceneLayout = sceneLayout;
        layout();
    }

    public void layout() {
        sceneLayout.invokeLayout();
        validate();
    }
}
