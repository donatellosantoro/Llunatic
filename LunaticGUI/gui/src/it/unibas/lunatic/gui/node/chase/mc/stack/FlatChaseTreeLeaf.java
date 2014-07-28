package it.unibas.lunatic.gui.node.chase.mc.stack;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.gui.node.chase.mc.ChaseStepNode;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;

public class FlatChaseTreeLeaf extends ChaseStepNode {

    public FlatChaseTreeLeaf(DeltaChaseStep key, Scenario s) {
        super(key, s);
        setDisplayName(key.getId());
    }

}
