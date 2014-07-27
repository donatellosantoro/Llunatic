/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.action.scenario;

import it.unibas.lunatic.IApplication;
import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.node.scenario.ScenarioNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "File",
        id = R.ActionId.CLOSE_SCENARIO_FROM_NODE)
@ActionRegistration(
        displayName = "#CTL_ActionCloseScenarioFromNode")
@Messages("CTL_ActionCloseScenarioFromNode=Close")
public final class ActionCloseScenarioFromNode implements ActionListener {

    public ActionCloseScenarioFromNode(ScenarioNode node) {
    }
    private IApplication app = Lookup.getDefault().lookup(IApplication.class);

    @Override
    public void actionPerformed(ActionEvent e) {
        app.remove(R.Bean.LOADED_SCENARIO);
    }
}
