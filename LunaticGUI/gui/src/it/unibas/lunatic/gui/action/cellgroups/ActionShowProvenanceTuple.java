package it.unibas.lunatic.gui.action.cellgroups;

import it.unibas.lunatic.IApplication;
import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.model.LoadedScenario;
import it.unibas.lunatic.gui.node.TableFinder;
import it.unibas.lunatic.gui.node.TableNode;
import it.unibas.lunatic.gui.node.cellgroup.ProvenanceTupleNode;
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
        id = R.ActionId.SHOW_PROVENANCE_TUPLE)
@ActionRegistration(
        displayName = "#CTL_ActionShowProvenanceTuple")
@ActionReference(path = "Menu/GoTo", position = 250)
@Messages("CTL_ActionShowProvenanceTuple=Go to table")
public final class ActionShowProvenanceTuple implements ActionListener {

    private Log logger = LogFactory.getLog(getClass());
    private final ProvenanceTupleNode provenanceTupleNode;
    private TableWindowManager tableWindowManager = Lookup.getDefault().lookup(TableWindowManager.class);
    private IApplication app = Lookup.getDefault().lookup(IApplication.class);
    private TableFinder scenarioTableFinder = new TableFinder();
    
    public ActionShowProvenanceTuple(ProvenanceTupleNode context) {
        this.provenanceTupleNode = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        String tableName = provenanceTupleNode.getTableName();
        logger.debug("Occurrence table name: " + tableName);
        LoadedScenario loadedScenario = app.get(R.Bean.LOADED_SCENARIO, LoadedScenario.class);
        TableNode table = scenarioTableFinder.findByName(loadedScenario.getNode(),tableName);
        logger.debug("Table node: " + table);
        tableWindowManager.openTable(table);
    }
}
