package it.unibas.lunatic.model.chase.chasede.operators;

import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.Repair;
import it.unibas.lunatic.model.chase.commons.operators.ChaseUtility;
import it.unibas.lunatic.model.dependency.Dependency;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import speedy.model.database.AttributeRef;

public class ChaseDEEGDUtility {

    private static Lock lock = new java.util.concurrent.locks.ReentrantLock();

    public static void maintainSatisfiedEGDs(DeltaChaseStep root, Dependency egd, Repair repair) {
        lock.lock();
        try {
            Set<AttributeRef> affectedAttributes = ChaseUtility.extractAffectedAttributes(repair);
            for (Iterator<Dependency> it = root.getSatisfiedEGDs().iterator(); it.hasNext();) {
                Dependency previouslySatisfiedEGD = it.next();
                if (ChaseUtility.hasModifiedQueriedAttributes(affectedAttributes, previouslySatisfiedEGD.getQueriedAttributes())) {
                    it.remove();
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public static void addSatisfiedEGD(DeltaChaseStep root, Dependency egd) {
        lock.lock();
        try {
            root.addSatisfiedEGD(egd);
        } finally {
            lock.unlock();
        }
    }
}
