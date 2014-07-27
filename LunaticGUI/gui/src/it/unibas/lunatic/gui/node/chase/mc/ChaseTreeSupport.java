/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.node.chase.mc;

import it.unibas.lunatic.model.chasemc.DeltaChaseStep;
import java.util.Collections;
import java.util.LinkedList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.nodes.NodeNotFoundException;
import org.openide.nodes.NodeOp;

/**
 *
 * @author Antonio Galotta
 */
public class ChaseTreeSupport {

    private static Log logger = LogFactory.getLog(ChaseTreeSupport.class);
    private static ChaseTreeSupport instance;

    public static ChaseTreeSupport getInstance() {
        if (instance == null) {
            instance = new ChaseTreeSupport();
        }
        return instance;
    }

    public String createChildName(DeltaChaseStep step) {
        return step.getId();
    }

    public ChaseStepNode findChaseStepNode(ChaseTreeRoot root, DeltaChaseStep step) throws NodeNotFoundException {
        LinkedList<String> path = new LinkedList<String>();
        DeltaChaseStep currentStep = step;
        while (!currentStep.isRoot()) {
            path.addFirst(createChildName(currentStep));
            currentStep = currentStep.getFather();
        }
        if (logger.isTraceEnabled()) logger.trace("Path to find: " + path);
        return (ChaseStepNode) NodeOp.findPath(root, Collections.enumeration(path));

    }
}
