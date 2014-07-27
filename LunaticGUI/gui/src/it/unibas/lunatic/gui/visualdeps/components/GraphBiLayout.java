/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.visualdeps.components;

import it.unibas.lunatic.gui.visualdeps.model.EdgeNode;
import it.unibas.lunatic.gui.visualdeps.model.GraphNode;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.netbeans.api.visual.graph.layout.GraphLayout;
import org.netbeans.api.visual.graph.layout.UniversalGraph;
import org.netbeans.api.visual.model.ObjectScene;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author Antonio Galotta
 */
public class GraphBiLayout extends GraphLayout<GraphNode, EdgeNode> {

    public enum Align {

        LEFT, RIGHT;
    }
    public static final Integer LOCATION_RIGHT = 1;
    public static final Integer LOCATION_LEFT = 0;
    public static final String LAYOUT_NODE_LOCATION = "nodeLayoutLocation";
    public static final int LEFT_SPACING = 60;
    public static final int TOP_SPACING = 20;
    private int verticalGap = 64;
    private int horizontalGap = 100;
    public static final int RIGHT_START_X = 500;

    /**
     * Creates a grid graph layout.
     */
    public GraphBiLayout() {
    }

    public GraphBiLayout setGaps(int verticalGap, int horizontalGap) {
        this.verticalGap = verticalGap;
        this.horizontalGap = horizontalGap;
        return this;
    }

    /**
     * Performs the grid graph layout on an universal graph.
     *
     * @param graph the universal graph
     */
    @Override
    protected void performGraphLayout(UniversalGraph<GraphNode, EdgeNode> graph) {
        ObjectScene scene = graph.getScene();
        List<GraphNode> leftNodes = new ArrayList<GraphNode>();
        List<GraphNode> rightNodes = new ArrayList<GraphNode>();
        for (GraphNode node : graph.getNodes()) {
            Integer rightAlign = (Integer) node.getValue(LAYOUT_NODE_LOCATION);
            if (rightAlign != null && rightAlign == LOCATION_RIGHT) {
                rightNodes.add(node);
            } else {
                leftNodes.add(node);
            }
        }
        List<GraphLocation> leftWidgets = createRelativeLocations(scene, leftNodes, Align.LEFT);
        List<GraphLocation> rightWidgets = createRelativeLocations(scene, rightNodes, Align.LEFT);
        Rectangle leftBoundMax = findMaxBound(leftWidgets);
        Point leftStart = new Point(LEFT_SPACING, TOP_SPACING);
        Point rightStart = new Point(RIGHT_START_X, TOP_SPACING);
        if (RIGHT_START_X < (LEFT_SPACING + leftBoundMax.width + horizontalGap)) {
            rightStart = new Point(LEFT_SPACING + leftBoundMax.width + horizontalGap, TOP_SPACING);
        }
        resolveNodeLocations(graph, leftWidgets, leftStart);
        resolveNodeLocations(graph, rightWidgets, rightStart);
    }

    private List<GraphLocation> createRelativeLocations(ObjectScene scene, List<GraphNode> nodes, Align align) {
        List<GraphLocation> widgetLocations = new ArrayList<GraphLocation>();
        int startY = 0;
        for (GraphNode node : nodes) {
            Widget widget = scene.findWidget(node);
            if (widget == null) {
                continue;
            }
            Rectangle bounds = widget.getBounds();
            if (bounds == null) {
                continue;
            }
            int startX = 0;
            if ( align == Align.RIGHT){
                startX = -bounds.width;
            }
            widgetLocations.add(new GraphLocation(node, startX, startY,bounds));
            startY += bounds.height + verticalGap;
            
        }
        return widgetLocations;
    }

    private void resolveNodeLocations(UniversalGraph<GraphNode, EdgeNode> graph, List<GraphLocation> locations, Point startPoint) {
        for (GraphLocation location : locations) {
            setResolvedNodeLocation(graph, location.getNode(), new Point(location.getX() + startPoint.x, location.getY() + startPoint.y));
        }
    }

    private Rectangle findMaxBound(List<GraphLocation> nodes) {
        Rectangle maxBounds = new Rectangle();
        for (GraphLocation n : nodes) {
            Rectangle bounds = n.getBounds();
            if (maxBounds.width < bounds.width) {
                maxBounds.setSize(bounds.width, maxBounds.height);
            }
            if (maxBounds.height < bounds.height) {
                maxBounds.setSize(maxBounds.width, bounds.height);
            }
        }
        return maxBounds;
    }

    private static class GraphLocation {

        private int xAxis;
        private int yAxis;
        private GraphNode node;
        private Rectangle bounds;

        public GraphLocation(GraphNode node,
                int xAxis, int yAxis, Rectangle bounds) {
            this.xAxis = xAxis;
            this.yAxis = yAxis;
            this.node = node;
            this.bounds = bounds;
        }

        public GraphNode getNode() {
            return node;
        }

        public int getX() {
            return xAxis;
        }

        public int getY() {
            return yAxis;
        }

        public Rectangle getBounds() {
            return bounds;
        }
    }

    /**
     * Should perform nodes layout. Currently unsupported.
     *
     * @param graph the universal graph
     * @param nodes the collection of nodes to resolve
     */
    @Override
    protected void performNodesLayout(UniversalGraph<GraphNode, EdgeNode> graph, Collection<GraphNode> nodes) {
        throw new UnsupportedOperationException(); // TODO
    }
}
