/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.window;

import it.unibas.lunatic.AbstractListener;
import it.unibas.lunatic.IModel;
import it.unibas.lunatic.gui.ExplorerTopComponent;
import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.model.LoadedScenario;
import it.unibas.lunatic.gui.node.scenario.OrderingAttributesRootNode;

/**
 *
 * @author Antonio Galotta
 */
public class OrderingAttributesListener extends AbstractListener<LoadedScenario> {

    private ExplorerTopComponent tc;

    @Override
    public void onChange(IModel model, LoadedScenario ls) {
        if (ls != null && ls.getScenario().getOrderingAttributes() != null) {
            tc.setRootContext(new OrderingAttributesRootNode(getBean().getScenario()));
        } else {
            tc.removeRootContext();
        }
    }

    public void register(ExplorerTopComponent tc) {
        this.tc = tc;
        super.registerBean(R.Bean.LOADED_SCENARIO, LoadedScenario.class);
    }
}
