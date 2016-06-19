package it.unibas.lunatic.gui.node;

import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;

interface IChaseNode {

    DeltaChaseStep getChaseStep();

    boolean isMcResultNode();
    
}
