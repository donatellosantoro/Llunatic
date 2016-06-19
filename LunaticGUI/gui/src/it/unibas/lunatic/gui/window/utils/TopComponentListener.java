package it.unibas.lunatic.gui.window.utils;

import it.unibas.lunatic.gui.IViewManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;

public class TopComponentListener implements PropertyChangeListener {

    private TopComponent tc;
    private String mainComponent;
    private IViewManager view = Lookup.getDefault().lookup(IViewManager.class);

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(TopComponent.Registry.PROP_TC_CLOSED)) {
            TopComponent newValue = (TopComponent) evt.getNewValue();
            if (newValue != null && newValue.getName() != null && newValue.getName().equals(mainComponent)) {
                tc.close();
            }
        }
    }

    public void register(TopComponent slaveComponent, String masterComponentName) {
        this.tc = slaveComponent;
        this.mainComponent = masterComponentName;
        TopComponent.getRegistry().addPropertyChangeListener(this);
        TopComponent main = view.findWindowByName(masterComponentName);
        if (main == null || !main.isOpened()) {
            slaveComponent.close();
        }
    }

    public void remove() {
        TopComponent.getRegistry().removePropertyChangeListener(this);
    }
}
