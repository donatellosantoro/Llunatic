package it.unibas.lunatic.model.chase.commons;

public interface IChaseState {

    void cancel();

    boolean isCancelled();

    public void notifyChaseInterruption();
}
