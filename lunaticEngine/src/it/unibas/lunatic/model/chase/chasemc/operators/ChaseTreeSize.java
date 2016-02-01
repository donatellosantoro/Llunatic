package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;

public class ChaseTreeSize {

    private int counter;

    public int getAllNodes(DeltaChaseStep root) {
        counter = 0;
        visitForAllNodes(root);
        return counter;
    }

    public int getAllLeaves(DeltaChaseStep root) {
        counter = 0;
        visitForAllLeaves(root);
        return counter;
    }

    public int getPotentialSolutions(DeltaChaseStep root) {
        counter = 0;
        visitForPotentialSolutions(root);
        return counter;
    }

    public int getSolutions(DeltaChaseStep root) {
        counter = 0;
        visitForSolution(root);
        return counter;
    }

    public int getGroundSolutions(DeltaChaseStep root) {
        counter = 0;
        visitForGroundSolution(root);
        return counter;
    }

    public int getDuplicates(DeltaChaseStep root) {
        counter = 0;
        visitForDuplicates(root);
        return counter;
    }

    public int getInvalids(DeltaChaseStep root) {
        counter = 0;
        visitForInvalids(root);
        return counter;
    }

    private boolean isLeaf(DeltaChaseStep node) {
        return node.getChildren().isEmpty();
    }

    private void visitForAllLeaves(DeltaChaseStep step) {
        if (isLeaf(step)) {
            counter++;
        }
        for (DeltaChaseStep child : step.getChildren()) {
            visitForAllLeaves(child);
        }
    }

    private void visitForPotentialSolutions(DeltaChaseStep step) {
        if (isLeaf(step)) {
            if (!step.isDuplicate() && !step.isInvalid()) {
                counter++;
            }
        }
        for (DeltaChaseStep child : step.getChildren()) {
            visitForPotentialSolutions(child);
        }
    }

    private void visitForDuplicates(DeltaChaseStep step) {
        if (isLeaf(step)) {
            if (step.isDuplicate()) {
                counter++;
            }
        }
        for (DeltaChaseStep child : step.getChildren()) {
            visitForDuplicates(child);
        }
    }

    private void visitForInvalids(DeltaChaseStep step) {
        if (isLeaf(step)) {
            if (step.isInvalid()) {
                counter++;
            }
        }
        for (DeltaChaseStep child : step.getChildren()) {
            visitForInvalids(child);
        }
    }

    private void visitForSolution(DeltaChaseStep step) {
        if (step.isSolution()) {
            counter++;
        }
        for (DeltaChaseStep child : step.getChildren()) {
            visitForSolution(child);
        }
    }

    private void visitForGroundSolution(DeltaChaseStep step) {
        if (step.isSolution() && step.isGround()) {
            counter++;
        }
        for (DeltaChaseStep child : step.getChildren()) {
            visitForGroundSolution(child);
        }
    }

    private void visitForAllNodes(DeltaChaseStep step) {
        for (DeltaChaseStep child : step.getChildren()) {
            if (child.isInvalid()) {
                continue;
            }
            counter++;
            if (!isLeaf(child)) {
                visitForAllNodes(child);
            }
        }
    }
}
