/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.core.UserManagerProvider;
import it.unibas.lunatic.model.chasemc.usermanager.IUserManager;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.util.Collection;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.explorer.propertysheet.PropertyModel;

/**
 *
 * @author Antonio Galotta
 */
public class UserManagerSwitch extends PropertyEditorSupport implements ExPropertyEditor, InplaceEditor.Factory {

    private final Scenario scenario;

    public UserManagerSwitch(Scenario scenario) {
        this.scenario = scenario;
    }

    @Override
    public void attachEnv(PropertyEnv env) {
        env.registerInplaceEditorFactory(this);
    }

    private InplaceEditor ed = null;

    @Override
    public InplaceEditor getInplaceEditor() {
        if (ed == null) {
            ed = new InplaceUserManagerSwitch(scenario);
        }
        return ed;
    }

    private static class InplaceUserManagerSwitch implements InplaceEditor {

        private Log logger = LogFactory.getLog(getClass());
        private UserManagerProvider userManagerProvider = UserManagerProvider.getInstance();
//        private final JComboBox<IUserManager> comboBox;
        private final JComboBox comboBox;
        private PropertyEditor editor = null;

        private InplaceUserManagerSwitch(Scenario scenario) {
            Collection<IUserManager> userManagers = userManagerProvider.getAll(scenario);
//            comboBox = new JComboBox<IUserManager>(userManagers.toArray(new IUserManager[userManagers.size()]));
            comboBox = new JComboBox(userManagers.toArray(new IUserManager[userManagers.size()]));
            comboBox.setEditable(false);
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
            logger.debug("Selected user manager: " + selectedItem);
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
            logger.debug("Reset combo");
            comboBox.setSelectedIndex(0);
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
}
