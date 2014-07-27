package it.unibas.lunatic.model.chase.commons.control;

public interface IChaseState {

    void cancel();

    boolean isCancelled();

    public void notifyChaseInterruption();
}
