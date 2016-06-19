package it.unibas.lunatic.gui.visualdeps.components;

import java.awt.Point;
import java.awt.Rectangle;
import org.netbeans.api.visual.anchor.Anchor;
import org.netbeans.api.visual.widget.Widget;

public class FixedPathAnchor extends Anchor {

    private final Direction direction;

    public FixedPathAnchor(Widget widget, Direction direction) {
        super(widget);
        this.direction = direction;
    }

    @Override
    public Result compute(Entry entry) {
        Widget widget = getRelatedWidget();
        Rectangle bounds = widget.convertLocalToScene(widget.getBounds());
        Point center = getCenter(bounds);
        int leftBound = bounds.x;
        int rightBound = bounds.x + bounds.width;
        if (direction == Direction.LEFT) {
            return new Anchor.Result(new Point(leftBound, center.y), Direction.LEFT);
        } else {
            return new Anchor.Result(new Point(rightBound, center.y), Direction.RIGHT);
        }
    }

    public static Point getCenter(Rectangle rectangle) {
        return new Point(rectangle.x + rectangle.width / 2, rectangle.y + rectangle.height / 2);
    }
}
