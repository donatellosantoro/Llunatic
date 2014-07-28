package it.unibas.lunatic.gui.node.scenario.editor;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.beans.PropertyEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.explorer.propertysheet.PropertyModel;

public class InplaceComboBoxEditor implements InplaceEditor {

    private Log logger = LogFactory.getLog(getClass());
    private final JComboBox comboBox;
    private PropertyEditor editor = null;

    public InplaceComboBoxEditor(JComboBox comboBox) {
        this.comboBox = comboBox;
    }

    @Override
    public void connect(PropertyEditor propertyEditor, PropertyEnv env) {
        editor = propertyEditor;
        reset();
    }

    @Override
    public JComponent getComponent() {
        return comboBox;
    }

    @Override
    public void clear() {
        //avoid memory leaks:
        editor = null;
        model = null;
    }

    @Override
    public Object getValue() {
        Object selectedItem = comboBox.getSelectedItem();
        logger.debug("Selection: " + selectedItem);
        return selectedItem;
    }

    @Override
    public void setValue(Object object) {
        logger.debug("Set combo selection: " + object);
        comboBox.setSelectedItem(object);
        if (logger.isDebugEnabled()) getValue();
    }

    @Override
    public boolean supportsTextEntry() {
        return false;
    }

    @Override
    public void reset() {
//        logger.debug("Reset combo");
//        comboBox.setSelectedIndex(0);
    }

    @Override
    public KeyStroke[] getKeyStrokes() {
        return new KeyStroke[0];
    }

    @Override
    public PropertyEditor getPropertyEditor() {
        return editor;
    }

    @Override
    public PropertyModel getPropertyModel() {
        return model;
    }
    private PropertyModel model;

    @Override
    public void setPropertyModel(PropertyModel propertyModel) {
        this.model = propertyModel;
    }

    @Override
    public boolean isKnownComponent(Component component) {
        return component == comboBox || comboBox.isAncestorOf(component);
    }

    @Override
    public void addActionListener(ActionListener actionListener) {
        //do nothing - not needed for this component
    }

    @Override
    public void removeActionListener(ActionListener actionListener) {
        //do nothing - not needed for this component
    }
}
