/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.visualdeps.components;

import java.awt.Point;
import java.awt.Rectangle;
import org.netbeans.api.visual.anchor.Anchor;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author Antonio Galotta
 */
public class BestPathAnchor extends Anchor {

    public BestPathAnchor(Widget widget) {
        super(widget);
    }

    @Override
    public Result compute(Entry entry) {
        Widget widget = getRelatedWidget();
        Widget otherWidget = entry.getOppositeAnchor().getRelatedWidget();
        Rectangle bounds = widget.convertLocalToScene(widget.getBounds());
        Rectangle otherBounds = otherWidget.convertLocalToScene(otherWidget.getBounds());
        Point center = getCenter(bounds);
        int leftBound = bounds.x;
        int rightBound = bounds.x + bounds.width;
        int oppositeLeftBound = otherBounds.x;
        int oppositeRightBound = otherBounds.x + otherBounds.width;
        if (leftBound >= oppositeRightBound) {
            return new Anchor.Result(new Point(leftBound, center.y), Direction.LEFT);
        } else if (rightBound <= oppositeLeftBound) {
            return new Anchor.Result(new Point(rightBound, center.y), Direction.RIGHT);
        } else {
            int leftDist = Math.abs(leftBound - oppositeLeftBound);
            int rightDist = Math.abs(rightBound - oppositeRightBound);
            if (leftDist <= rightDist) {
                return new Anchor.Result(new Point(leftBound, center.y), Direction.LEFT);
            } else {
                return new Anchor.Result(new Point(rightBound, center.y), Direction.RIGHT);
            }
        }
    }

    public static Point getCenter(Rectangle rectangle) {
        return new Point(rectangle.x + rectangle.width / 2, rectangle.y + rectangle.height / 2);
    }
}
