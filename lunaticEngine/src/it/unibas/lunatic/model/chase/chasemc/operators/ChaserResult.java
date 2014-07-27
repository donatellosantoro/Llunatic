package it.unibas.lunatic.model.chase.chasemc.operators;

public class ChaserResult {

    private boolean newNodes;
    private boolean userInteractionRequired;

    public ChaserResult(boolean newNodes, boolean userInteractionRequired) {
        this.newNodes = newNodes;
        this.userInteractionRequired = userInteractionRequired;
    }

    public boolean isNewNodes() {
        return newNodes;
    }

    public boolean isUserInteractionRequired() {
        return userInteractionRequired;
    }
}
