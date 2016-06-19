package it.unibas.lunatic.gui.visualdeps.components;

import java.awt.Point;
import java.awt.Rectangle;
import org.netbeans.api.visual.anchor.Anchor;
import org.netbeans.api.visual.widget.Widget;

public class BestPathObjectAnchor extends Anchor {

    public BestPathObjectAnchor(Widget widget) {
        super(widget);
    }

    @Override
    public Result compute(Entry entry) {
        Widget widget = getRelatedWidget();
        Widget otherWidget = entry.getOppositeAnchor().getRelatedWidget();
        Rectangle bounds = widget.convertLocalToScene(widget.getBounds());
        Rectangle otherBounds = otherWidget.convertLocalToScene(otherWidget.getBounds());
        Point center = getCenter(bounds);
        Connection minimum = getMin(new Connection(bounds.x, otherBounds.x, Direction.LEFT), new Connection(bounds.x, otherBounds.x + otherBounds.width, Direction.LEFT));
        minimum = getMin(minimum, new Connection(bounds.x + bounds.width, otherBounds.x, Direction.RIGHT));
        minimum = getMin(minimum, new Connection(bounds.x + bounds.width, otherBounds.x + otherBounds.width, Direction.RIGHT));
        return new Anchor.Result(new Point(minimum.getStartX(), center.y), minimum.getDirection());
    }

    public static Point getCenter(Rectangle rectangle) {
        return new Point(rectangle.x + rectangle.width / 2, rectangle.y + rectangle.height / 2);
    }

    private static Connection getMin(Connection a, Connection b) {
        if (a.getDistance() <= b.getDistance()) {
            return a;
        } else {
            return b;
        }
    }

    private final static class Connection {

        private final int startX;
        private final int endX;
        private final int distance;
        private final Direction direction;

        public Connection(int startX, int endX, Direction direction) {
            this.startX = startX;
            this.endX = endX;
            this.distance = Math.abs(startX - endX);
            this.direction = direction;
        }

        public Direction getDirection() {
            return direction;
        }

        public int getDistance() {
            return distance;
        }

        public int getStartX() {
            return startX;
        }

        public int getEndX() {
            return endX;
        }
    }
}
