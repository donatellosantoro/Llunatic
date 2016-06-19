package it.unibas.lunatic.gui.visualdeps;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.anchor.PointShape;
import org.netbeans.api.visual.anchor.PointShapeFactory;
import org.netbeans.api.visual.border.Border;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.model.ObjectState;
import org.netbeans.api.visual.vmd.VMDColorScheme;
import org.netbeans.api.visual.vmd.VMDConnectionWidget;
import org.netbeans.api.visual.vmd.VMDFactory;
import org.netbeans.api.visual.vmd.VMDNodeAnchor;
import org.netbeans.api.visual.vmd.VMDNodeWidget;
import org.netbeans.api.visual.vmd.VMDPinWidget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.openide.util.ImageUtilities;

public class GraphColorScheme extends VMDColorScheme {
    
    static final Color COLOR_NORMAL = new Color(0xBACDF0);
    private static final Color COLOR_HOVERED = Color.BLACK;
    private static final Color COLOR_SELECTED = new Color(0x748CC0);
    static final Color COLOR_HIGHLIGHTED = new Color(0x316AC5);
//    private static final Color COLOR0 = new Color (169, 197, 235);
    static final Color COLOR1 = new Color(221, 235, 246);
    static final Color COLOR2 = new Color(255, 255, 255);
    static final Color COLOR3 = new Color(214, 235, 255);
    static final Color COLOR4 = new Color(241, 249, 253);
    static final Color COLOR5 = new Color(255, 255, 255);
    public static final Border BORDER_NODE = VMDFactory.createVMDNodeBorder(COLOR_NORMAL, 1, COLOR1, COLOR2, COLOR3, COLOR4, COLOR5);
    static final Color BORDER_CATEGORY_BACKGROUND = new Color(0xCDDDF8);
    static final Border BORDER_MINIMIZE = BorderFactory.createRoundedBorder(2, 2, null, COLOR_NORMAL);
    static final Border HEADER_BORDER_PIN = BorderFactory.createOpaqueBorder(2, 8, 2, 8);
    private static final Border BORDER_PIN_HOVERED = BorderFactory.createLineBorder(2, 8, 2, 8, Color.BLACK);
    static final Border BORDER_PIN = HEADER_BORDER_PIN;    
    static final PointShape POINT_SHAPE_IMAGE = PointShapeFactory.createImagePointShape(ImageUtilities.loadImage("org/netbeans/modules/visual/resources/vmd-pin.png")); // NOI18N
    private static GraphColorScheme instance = new GraphColorScheme();
    
    public static GraphColorScheme getInstance() {
        return instance;
    }
    
    @Override
    public void installUI(VMDNodeWidget widget) {
        widget.setBorder(VMDFactory.createVMDNodeBorder());
        widget.setOpaque(false);
        
        Widget header = widget.getHeader();
        header.setBorder(HEADER_BORDER_PIN);
        header.setBackground(COLOR_SELECTED);
        header.setOpaque(false);
        
        Widget minimize = widget.getMinimizeButton();
        minimize.setBorder(BORDER_MINIMIZE);
        
        Widget pinsSeparator = widget.getPinsSeparator();
        pinsSeparator.setForeground(BORDER_CATEGORY_BACKGROUND);
    }
    
    @Override
    public void updateUI(VMDNodeWidget widget, ObjectState previousState, ObjectState state) {
        if (!previousState.isSelected() && state.isSelected()) {
            widget.bringToFront();
        } else if (!previousState.isHovered() && state.isHovered()) {
            widget.bringToFront();
        }
        Widget header = widget.getHeader();
        header.setOpaque(state.isSelected());
        if (state.isFocused() || state.isHovered()) {
            header.setBorder(BORDER_PIN_HOVERED);
        } else {
            header.setBorder(HEADER_BORDER_PIN);
        }
        
    }
    
    @Override
    public boolean isNodeMinimizeButtonOnRight(VMDNodeWidget widget) {
        return false;
    }
    
    @Override
    public Image getMinimizeWidgetImage(VMDNodeWidget widget) {
        return widget.isMinimized()
                ? ImageUtilities.loadImage("org/netbeans/modules/visual/resources/vmd-expand.png") // NOI18N
                : ImageUtilities.loadImage("org/netbeans/modules/visual/resources/vmd-collapse.png"); // NOI18N
    }
    
    @Override
    public Widget createPinCategoryWidget(VMDNodeWidget widget, String categoryDisplayName) {
        return createPinCategoryWidgetCore(widget, categoryDisplayName, true);
    }
    
    @Override
    public void installUI(VMDConnectionWidget widget) {
        widget.setSourceAnchorShape(AnchorShape.NONE);
        widget.setTargetAnchorShape(AnchorShape.TRIANGLE_FILLED);
        widget.setPaintControlPoints(true);
    }
    
    @Override
    public void updateUI(VMDConnectionWidget widget, ObjectState previousState, ObjectState state) {
//        if (state.isHovered()) {
//            widget.setForeground(COLOR_HOVERED);
//        } else if (state.isSelected()) {
//            widget.setForeground(COLOR_SELECTED);
//        } else if (state.isHighlighted()) {
//            widget.setForeground(COLOR_HIGHLIGHTED);
//        } else if (state.isFocused()) {
//            widget.setForeground(COLOR_HOVERED);
//        } else {
//            widget.setForeground(COLOR_NORMAL);
//        }

        if (state.isSelected()) {
            widget.setControlPointShape(PointShape.SQUARE_FILLED_SMALL);
            widget.setEndPointShape(PointShape.SQUARE_FILLED_BIG);
        } else {
            widget.setControlPointShape(PointShape.NONE);
            widget.setEndPointShape(POINT_SHAPE_IMAGE);
        }
    }
    
    @Override
    public void installUI(VMDPinWidget widget) {
        widget.setBorder(BORDER_PIN);
        widget.setBackground(COLOR_SELECTED);
        widget.setOpaque(false);
    }
    
    @Override
    public void updateUI(VMDPinWidget widget, ObjectState previousState, ObjectState state) {
        widget.setOpaque(state.isSelected());
        if (state.isFocused() || state.isHovered()) {
            widget.setBorder(BORDER_PIN_HOVERED);
        } else {
            widget.setBorder(BORDER_PIN);
        }
//        LookFeel lookFeel = getScene ().getLookFeel ();
//        setBorder (BorderFactory.createCompositeBorder (BorderFactory.createEmptyBorder (8, 2), lookFeel.getMiniBorder (state)));
//        setForeground (lookFeel.getForeground (state));
    }
    
    @Override
    public int getNodeAnchorGap(VMDNodeAnchor anchor) {
        return 8;
    }
    
    static Widget createPinCategoryWidgetCore(VMDNodeWidget widget, String categoryDisplayName, boolean changeFont) {
        Scene scene = widget.getScene();
        LabelWidget label = new LabelWidget(scene, categoryDisplayName);
        label.setOpaque(true);
        label.setBackground(BORDER_CATEGORY_BACKGROUND);
        label.setForeground(Color.GRAY);
        if (changeFont) {
            Font fontPinCategory = scene.getDefaultFont().deriveFont(10.0f);
            label.setFont(fontPinCategory);
        }
        label.setAlignment(LabelWidget.Alignment.CENTER);
        label.setCheckClipping(true);
        return label;
    }
}
