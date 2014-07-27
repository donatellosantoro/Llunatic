/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.action;

import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.node.TableNode;
import it.unibas.lunatic.gui.window.db.TableWindowManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Window",
        id = R.ActionId.SHOW_TABLE)
@ActionRegistration(
        displayName = "#CTL_ActionShowTable")
@ActionReference(path = "Menu/GoTo", position = 250)
@Messages("CTL_ActionShowTable=Show table")
public final class ActionShowTable implements ActionListener {

    private final List<TableNode> context;
    private TableWindowManager tableWindowFactory = Lookup.getDefault().lookup(TableWindowManager.class);

    public ActionShowTable(List<TableNode> context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        for (TableNode tableNode : context) {
            openTable(tableNode);
        }
    }

    private void openTable(TableNode ts) {
        tableWindowFactory.openTable(ts);
    }
}
