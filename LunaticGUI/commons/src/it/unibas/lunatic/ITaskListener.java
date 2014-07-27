/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic;

import javax.swing.SwingWorker;

/**
 *
 * @author Antonio Galotta
 */
public interface ITaskListener<Task extends SwingWorker>{

    public void onTaskStarted(Task task);
    
    public void onTaskCompleted(Task task);

    public void onTaskKilled(Task task);
}
