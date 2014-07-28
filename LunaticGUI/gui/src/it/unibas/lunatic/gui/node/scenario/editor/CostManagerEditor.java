package it.unibas.lunatic.gui.node.scenario.editor;

import it.unibas.lunatic.core.CostManagerProvider;
import it.unibas.lunatic.model.chase.chasemc.costmanager.ICostManager;
import java.beans.PropertyEditorSupport;
import java.util.Collection;
import javax.swing.JComboBox;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;

public class CostManagerEditor extends PropertyEditorSupport implements ExPropertyEditor, InplaceEditor.Factory {

    private CostManagerProvider costManagerProvider = CostManagerProvider.getInstance();

    @Override
    public void attachEnv(PropertyEnv env) {
        env.registerInplaceEditorFactory(this);
    }
    private InplaceEditor ed = null;

    @Override
    public InplaceEditor getInplaceEditor() {
        if (ed == null) {
            Collection<ICostManager> costManager = costManagerProvider.getAll();
//            JComboBox comboBox = new JComboBox<ICostManager>(costManager.toArray(new ICostManager[costManager.size()]));
            JComboBox comboBox = new JComboBox(costManager.toArray(new ICostManager[costManager.size()]));
            comboBox.setEditable(false);
            ed = new InplaceComboBoxEditor(comboBox);
        }
        return ed;
    }
}
