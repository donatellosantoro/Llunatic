/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.action.cellgroups;

import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.node.TableNode;
import it.unibas.lunatic.gui.node.cellgroup.OccurrenceTupleNode;
import it.unibas.lunatic.gui.node.TableFinder;
import it.unibas.lunatic.gui.window.db.TableWindowManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Window",
        id = R.ActionId.SHOW_OCCURRENCE_TUPLE)
@ActionRegistration(
        displayName = "#CTL_ActionShowOccurrenceTuple")
@ActionReference(path = "Menu/GoTo", position = 250)
@Messages("CTL_ActionShowOccurrenceTuple=Go to table")
public final class ActionShowOccurrenceTuple implements ActionListener {

    private Log logger = LogFactory.getLog(getClass());
    private final OccurrenceTupleNode occurrenceTupleNode;
    private TableWindowManager tableWindowManager = Lookup.getDefault().lookup(TableWindowManager.class);
    private TableFinder tableFinder = new TableFinder();

    public ActionShowOccurrenceTuple(OccurrenceTupleNode context) {
        this.occurrenceTupleNode = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        String tableName = occurrenceTupleNode.getTableName();
        logger.debug("Occurrence table name: " + tableName);
        TableNode table = tableFinder.findByName(occurrenceTupleNode.getChaseStepNode(), tableName);
        logger.debug("Table node: " + table);
        tableWindowManager.openTable(table);
    }
}
