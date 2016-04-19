package it.unibas.lunatic.model.chase.commons.thread;

public class ListeningThread extends Thread {

    private ThreadManager threadManager;
    private IBackgroundThread thread;

    public ListeningThread(IBackgroundThread thread, ThreadManager threadManager) {
        this.thread = thread;
        this.threadManager = threadManager;
    }

    @Override
    public final void run() {
        try {
            this.thread.execute();
        } finally {
            this.threadManager.removeActiveThread(this);
        }
    }
}
