/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.visualdeps.model;

import java.awt.Color;

/**
 *
 * @author Antonio Galotta
 */
public class EdgeNode extends AbstractVisualNode {

    private Color color;
    private final PinNode target;
    private final PinNode source;

    public EdgeNode(PinNode source, PinNode target, Color color) {
        super(source.getId().concat("->").concat(target.getId()));
        this.color = color;
        this.source = source;
        this.target = target;
    }

    public EdgeNode(PinNode source, PinNode target) {
        this(source, target, Color.BLACK);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public PinNode getSource() {
        return source;
    }

    public PinNode getTarget() {
        return target;
    }
}
