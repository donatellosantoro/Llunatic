package it.unibas.lunatic.gui;

import java.awt.Frame;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import javax.swing.SwingUtilities;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

@ServiceProvider(service = IViewManager.class, position = 2)
public class ViewManager implements IViewManager {

    private WindowManager wm = WindowManager.getDefault();

    @Override
    public TopComponent show(String name) {
        TopComponent tc = wm.findTopComponent(name);
        if (tc != null) {
            tc.open();
            tc.requestActive();
        }
        return tc;
    }

    @Override
    public TopComponent findOpenedWindow(String name) {
        Set<TopComponent> openedWindows = wm.getRegistry().getOpened();
        for (TopComponent tc : openedWindows) {
            if (tc.getName() != null && tc.getName().equals(name)) {
                return tc;
            }
        }
        return null;
    }

    @Override
    public TopComponent findWindowByName(String component) {
        TopComponent tc = wm.findTopComponent(component);
        return tc;
    }

    @Override
    public Frame getMainFrame() {
        return wm.getMainWindow();
    }

    @Override
    public TopComponent getActivatedWindow() {
        return wm.getRegistry().getActivated();
    }

    @Override
    public void invokeLater(Runnable runnable) {
        SwingUtilities.invokeLater(runnable);
    }

    @Override
    public void invokeAndWait(Runnable runnable) throws InterruptedException, InvocationTargetException {
        SwingUtilities.invokeAndWait(runnable);
    }
}
