package it.unibas.lunatic.gui.node.scenario;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.gui.R;
import javax.swing.Action;
import org.openide.awt.Actions;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.NbBundle;

@NbBundle.Messages("NODE_Dependencies=Dependencies")
public class ScenarioDepNode extends AbstractNode {

    private Action[] actions = new Action[]{Actions.forID("Window", R.ActionId.SHOW_DEPS)};
    private DepCounter depCounter = new DepCounter();

    public ScenarioDepNode(Scenario scenario) {
        super(Children.LEAF);
        setDisplayName(String.format("%s [%d]", Bundle.NODE_Dependencies(), depCounter.getNumber(scenario)));
        setIconBaseWithExtension("it/unibas/lunatic/icons/A16.png");
    }

    @Override
    public Action[] getActions(boolean context) {
        return actions;
    }
}
