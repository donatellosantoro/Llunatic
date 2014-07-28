package it.unibas.lunatic.gui.node.chase.mc.stack;

import it.unibas.lunatic.gui.model.McChaseResult;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

public class FlatChaseTreeRoot extends AbstractNode {

    private McChaseResult result;

    public FlatChaseTreeRoot(McChaseResult chaseResult) {
        super(Children.create(new FlatChaseTreeFactory(chaseResult.getResult(), chaseResult.getLoadedScenario().getScenario()), false));
        this.result = chaseResult;
        setDisplayName(result.getLoadedScenario().getDataObject().getName());
        setIconBaseWithExtension("it/unibas/lunatic/icons/datasource.png");
    }

    public McChaseResult getResult() {
        return result;
    }
}
