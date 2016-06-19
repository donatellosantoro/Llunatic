package it.unibas.lunatic.gui.visualdeps;

import java.awt.Color;
import org.netbeans.api.visual.border.Border;
import org.netbeans.api.visual.vmd.VMDFactory;

public class SceneUtils {
    public static final Color PREMISE_COLOR = Color.BLUE;
    public static final Color CONCLUSION_COLOR = Color.RED;

    public static Border getPremiseBorder() {
        return VMDFactory.createVMDNodeBorder(PREMISE_COLOR, 2, Color.white, Color.white, Color.white, Color.white, Color.white);
    }

    public static Border getConclusionBorder() {
        return VMDFactory.createVMDNodeBorder(CONCLUSION_COLOR, 2, Color.white, Color.white, Color.white, Color.white, Color.white);

    }

}
