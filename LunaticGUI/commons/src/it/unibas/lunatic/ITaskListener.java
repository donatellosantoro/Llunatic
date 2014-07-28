package it.unibas.lunatic;

import javax.swing.SwingWorker;

public interface ITaskListener<Task extends SwingWorker>{

    public void onTaskStarted(Task task);
    
    public void onTaskCompleted(Task task);

    public void onTaskKilled(Task task);
}
