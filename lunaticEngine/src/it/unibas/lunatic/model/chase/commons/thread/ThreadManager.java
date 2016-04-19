package it.unibas.lunatic.model.chase.commons.thread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadManager {

    private static Logger logger = LoggerFactory.getLogger(ThreadManager.class);

    private int maxConcurrentThreads;

    private List<ListeningThread> activeThreads = Collections.synchronizedList(new ArrayList<ListeningThread>());
    private List<IBackgroundThread> waitingThreads = Collections.synchronizedList(new ArrayList<IBackgroundThread>());

    private Lock lock;
    private Condition activeThreadsCondition;

    public ThreadManager(int maxConcurrentThreads) {
        this.maxConcurrentThreads = maxConcurrentThreads;
        this.lock = new java.util.concurrent.locks.ReentrantLock();
        this.activeThreadsCondition = this.lock.newCondition();
    }

    public void startThread(IBackgroundThread thread) {
        if (activeThreads.size() >= maxConcurrentThreads) {
            this.waitingThreads.add(thread);
            if (logger.isDebugEnabled()) logger.debug("No more slots available. Adding thread to waiting queue. " + getThreadStats());
            return;
        }
        if (logger.isDebugEnabled()) logger.debug("Starting thread...");
        ListeningThread listeningThread = new ListeningThread(thread, this);
        this.addActiveThread(listeningThread);
        listeningThread.start();
    }

    private void addActiveThread(ListeningThread thread) {
        this.lock.lock();
        try {
            activeThreads.add(thread);
            this.activeThreadsCondition.signalAll();
        } finally {
            this.lock.unlock();
        }
    }

    public void removeActiveThread(ListeningThread thread) {
        this.lock.lock();
        try {
            activeThreads.remove(thread);
            if (waitingThreads.isEmpty()) {
                this.activeThreadsCondition.signalAll();
            } else {
                IBackgroundThread nextThread = this.waitingThreads.remove(0);
                startThread(nextThread);
            }
        } finally {
            this.lock.unlock();
        }

    }

    public List<ListeningThread> getActiveThread() {
        this.lock.lock();
        try {
            return activeThreads;
        } finally {
            this.lock.unlock();
        }
    }

    public int getNumberOfActiveThread() {
        this.lock.lock();
        try {
            return activeThreads.size();
        } finally {
            this.lock.unlock();
        }
    }

    public void waitForActiveThread() {
        this.lock.lock();
        try {
            while ((activeThreads.size() + waitingThreads.size()) > 0) {
                if (logger.isDebugEnabled()) logger.debug("Waiting for " + getThreadStats());
                try {
                    this.activeThreadsCondition.await();
                } catch (InterruptedException ex) {
                }
            }
        } finally {
            this.lock.unlock();
        }
    }

    private String getThreadStats() {
        return "Active: " + this.activeThreads.size() + " Waiting: " + this.waitingThreads.size();
    }
}
