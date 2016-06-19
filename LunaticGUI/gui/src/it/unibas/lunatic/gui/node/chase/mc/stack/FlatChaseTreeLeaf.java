package it.unibas.lunatic.gui.node.chase.mc.stack;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.gui.node.chase.mc.ChaseStepNode;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import java.text.DecimalFormat;

public class FlatChaseTreeLeaf extends ChaseStepNode {

    private DecimalFormat df = new DecimalFormat("##.0");

    public FlatChaseTreeLeaf(DeltaChaseStep key, Scenario s, boolean showScore) {
        super(key, s);
        setDisplayName(key.getId() + " - Score: " + df.format(key.getScore()));
    }

}
