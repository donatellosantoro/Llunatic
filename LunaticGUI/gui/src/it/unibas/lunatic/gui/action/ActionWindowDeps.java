/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.action;

import it.unibas.lunatic.ContextAwareActionProxy;
import it.unibas.lunatic.IModel;
import it.unibas.lunatic.gui.IViewManager;
import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.model.LoadedScenario;
import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.JButton;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

@ActionID(
        category = "Window",
        id = R.ActionId.SHOW_DEPS)
@ActionRegistration(
        displayName = "#CTL_ActionShowDeps",
        iconInMenu = false, lazy = false)
@ActionReferences({
    @ActionReference(path = "Menu/Window" /*, position = 333 */), //    @ActionReference(path = "Toolbars/Window" /*, position = 0*/)
})
@Messages("CTL_ActionShowDeps=Dependencies")
public final class ActionWindowDeps extends ContextAwareActionProxy<LoadedScenario> implements Presenter.Toolbar {

    public ActionWindowDeps() {
        putValue(Action.NAME, Bundle.CTL_ActionShowDeps());
        putValue(Action.SHORT_DESCRIPTION, Bundle.CTL_ActionShowDeps());
        putValue(Action.LARGE_ICON_KEY, ImageUtilities.loadImageIcon("it/unibas/lunatic/icons/dep-window.24.png", false));
    }

    @Override
    protected void register() {
        super.registerBean(R.Bean.LOADED_SCENARIO, LoadedScenario.class);
    }
    private IViewManager view = Lookup.getDefault().lookup(IViewManager.class);

    @Override
    public void actionPerformed(ActionEvent e) {
        view.show(R.Window.DEPENDENCIES);
    }

    @Override
    public Component getToolbarPresenter() {
        JButton btn = new JButton(this);
        btn.setText("");
        return btn;
    }

    @Override
    public void onChange(IModel o, LoadedScenario bean) {
        super.defaultChange(o, bean);
    }
}
