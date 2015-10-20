package it.unibas.lunatic.model.chase.commons.control;

public class ImmutableChaseState implements IChaseState {

    private static ImmutableChaseState instance = new ImmutableChaseState();

    public static ImmutableChaseState getInstance() {
        return instance;
    }

    private ImmutableChaseState() {
    }

    public void cancel() {
    }

    public boolean isCancelled() {
        return false;
    }

    @Override
    public void notifyChaseInterruption() {
    }
}
