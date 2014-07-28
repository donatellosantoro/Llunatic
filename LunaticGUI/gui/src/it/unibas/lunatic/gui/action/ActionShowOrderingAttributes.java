package it.unibas.lunatic.gui.action;

import it.unibas.lunatic.IApplication;
import it.unibas.lunatic.gui.IViewManager;
import it.unibas.lunatic.gui.R;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Window",
        id = R.ActionId.SHOW_ORDERING_ATTRIBUTES)
@ActionRegistration(
        displayName = "#CTL_ActionShowOrderingAttributes")
@Messages("CTL_ActionShowOrderingAttributes=Ordering attributes")
public final class ActionShowOrderingAttributes implements ActionListener {

    private IApplication app = Lookup.getDefault().lookup(IApplication.class);
    private IViewManager view = Lookup.getDefault().lookup(IViewManager.class);

    @Override
    public void actionPerformed(ActionEvent e) {
        view.show(R.Window.ORDERING_ATTRIBUTES_TABLE);
    }
}
