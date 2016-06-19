package it.unibas.lunatic.gui.node.scenario.editor;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.core.UserManagerProvider;
import it.unibas.lunatic.model.chase.chasemc.usermanager.IUserManager;
import java.beans.PropertyEditorSupport;
import java.util.Collection;
import javax.swing.JComboBox;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;

@SuppressWarnings("rawtypes")
public class UserManagerEditor extends PropertyEditorSupport implements ExPropertyEditor, InplaceEditor.Factory {

    private UserManagerProvider userManagerProvider = UserManagerProvider.getInstance();
    private final Scenario scenario;

    public UserManagerEditor(Scenario scenario) {
        this.scenario = scenario;
    }

    @Override
    public void attachEnv(PropertyEnv env) {
        env.registerInplaceEditorFactory(this);
    }
    private InplaceEditor ed = null;

    @Override
    @SuppressWarnings("unchecked")
    public InplaceEditor getInplaceEditor() {
        if (ed == null) {
            Collection<IUserManager> userManagers = userManagerProvider.getAll(scenario);
            JComboBox comboBox = new JComboBox(userManagers.toArray(new IUserManager[userManagers.size()]));
            comboBox.setEditable(false);
            ed = new InplaceComboBoxEditor(comboBox);
        }
        return ed;
    }
}
