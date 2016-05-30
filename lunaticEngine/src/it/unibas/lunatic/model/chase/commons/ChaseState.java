package it.unibas.lunatic.model.chase.commons;

import java.util.Observable;

public class ChaseState extends Observable implements IChaseState{

    private boolean chaseCancelled;
    private boolean valid = true;

    @Override
    public void cancel() {
        chaseCancelled = true;
    }

    @Override
    public boolean isCancelled() {
        return chaseCancelled;
    }

    @Override
    public void notifyChaseInterruption() {
        valid = false;
        super.setChanged();
        super.notifyObservers();
    }

    public boolean isValid() {
        return valid;
    }
}
