/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.action.scenario;

import it.unibas.lunatic.ContextAwareActionProxy;
import it.unibas.lunatic.IApplication;
import it.unibas.lunatic.IModel;
import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.model.LoadedScenario;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "File",
        id = "it.unibas.lunatic.gui.action.ActionCloseScenario")
@ActionRegistration(
        displayName = "#CTL_ActionCloseScenario", lazy = false)
@ActionReference(path = "Menu/File", position = 2550, separatorAfter = 2575)
@Messages("CTL_ActionCloseScenario=Close scenario")
public final class ActionCloseScenario extends ContextAwareActionProxy<LoadedScenario> {

    public ActionCloseScenario() {
        putValue(Action.NAME, Bundle.CTL_ActionCloseScenario());
    }

    @Override
    protected void register() {
        registerBean(R.Bean.LOADED_SCENARIO, LoadedScenario.class);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        app.remove(R.Bean.LOADED_SCENARIO);
    }

    @Override
    public void onChange(IModel o, LoadedScenario bean) {
        super.defaultChange(o, bean);
    }
}
