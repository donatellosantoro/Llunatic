package it.unibas.lunatic.gui.node.chase.mc.stack;

import it.unibas.lunatic.gui.model.McChaseResult;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

public class RankedSolutionsTreeRoot extends AbstractNode {

    private McChaseResult result;

    public RankedSolutionsTreeRoot(McChaseResult chaseResult) {
        super(Children.create(new RankedSolutionsTreeFactory(chaseResult.getResult(), chaseResult.getLoadedScenario().getScenario()), true));
        this.result = chaseResult;
        if (chaseResult.getResult().getRankedSolutions() == null) {
            setDisplayName("Enable 'remove duplicates' in order to rank solutions");
        } else {
            setDisplayName("Solutions ordered by score (" + result.getLoadedScenario().getDataObject().getName() + ")");
            setIconBaseWithExtension("it/unibas/lunatic/icons/rank.png");
        }
    }

    public McChaseResult getResult() {
        return result;
    }
}
