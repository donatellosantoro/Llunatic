/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.visualdeps.components;

import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.vmd.VMDColorScheme;
import org.netbeans.api.visual.vmd.VMDConnectionWidget;
import org.netbeans.api.visual.widget.Scene;

/**
 *
 * @author Antonio Galotta
 */
public class CustomConnectionWidget extends VMDConnectionWidget {

    private boolean targetAnchorShapeHidden;
    private VMDColorScheme scheme;

    public CustomConnectionWidget(Scene scene, VMDColorScheme scheme) {
        super(scene, scheme);
        this.scheme = scheme;
    }

    public void setDirectional(boolean directional) {
        this.targetAnchorShapeHidden = !directional;
        scheme.installUI(this);
    }

    @Override
    public void setTargetAnchorShape(AnchorShape targetAnchorShape) {
        if (!targetAnchorShapeHidden) {
            super.setTargetAnchorShape(targetAnchorShape);
        } else {
            super.setTargetAnchorShape(AnchorShape.NONE);
        }
    }
}
