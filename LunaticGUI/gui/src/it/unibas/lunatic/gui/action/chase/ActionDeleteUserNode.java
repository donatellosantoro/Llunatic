/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.action.chase;

import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.node.chase.mc.ChaseStepNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Run",
        id = R.ActionId.DELETE_USER_NODE)
@ActionRegistration(
        displayName = "#CTL_ActionDeleteUserNode")
@Messages("CTL_ActionDeleteUserNode=Delete")
public final class ActionDeleteUserNode implements ActionListener {
    
    private final ChaseStepNode context;
    private Log logger = LogFactory.getLog(getClass());
    
    public ActionDeleteUserNode(ChaseStepNode context) {
        this.context = context;
    }
    
    @Override
    public void actionPerformed(ActionEvent ev) {
        try {
            context.delete();
        } catch (IOException e) {
            logger.error(e);
        }
    }
}
