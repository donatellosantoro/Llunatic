package it.unibas.lunatic.gui.data;

import it.unibas.lunatic.gui.R;
import java.io.IOException;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.util.NbBundle.Messages;

@MIMEResolver.NamespaceRegistration(
        displayName = "#LBL_Scenario_LOADER",
        mimeType = "text/scenario+xml",
        elementName = "scenario")
@DataObject.Registration(
        mimeType = "text/scenario+xml",
        displayName = "#LBL_Scenario_LOADER",
        iconBase = "it/unibas/lunatic/icons/scenario.png",
        position = 10)
@ActionReferences({
    @ActionReference(
            path = "Loaders/text/scenario+xml/Actions",
            id =
            @ActionID(category = "File", id = R.ActionId.LOAD_SCENARIO),
            position = 100),
    @ActionReference(
            path = "Loaders/text/scenario+xml/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.OpenAction"),
            position = 150,
            separatorAfter = 200),
    @ActionReference(
            path = "Loaders/text/scenario+xml/Actions",
            id =
            @ActionID(category = "Edit", id = "org.openide.actions.CutAction"),
            position = 300),
    @ActionReference(
            path = "Loaders/text/scenario+xml/Actions",
            id =
            @ActionID(category = "Edit", id = "org.openide.actions.CopyAction"),
            position = 400,
            separatorAfter = 500),
    @ActionReference(
            path = "Loaders/text/scenario+xml/Actions",
            id =
            @ActionID(category = "Edit", id = "org.openide.actions.DeleteAction"),
            position = 600),
    @ActionReference(
            path = "Loaders/text/scenario+xml/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.RenameAction"),
            position = 700,
            separatorAfter = 800),
    @ActionReference(
            path = "Loaders/text/scenario+xml/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.SaveAsTemplateAction"),
            position = 900,
            separatorAfter = 1000),
    @ActionReference(
            path = "Loaders/text/scenario+xml/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.FileSystemAction"),
            position = 1100,
            separatorAfter = 1200),
    @ActionReference(
            path = "Loaders/text/scenario+xml/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.ToolsAction"),
            position = 1300),
    @ActionReference(
            path = "Loaders/text/scenario+xml/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.PropertiesAction"),
            position = 1400)
})
@Messages({
    "LBL_Scenario_LOADER=Files of Scenario"
})
public class ScenarioDataObject extends MultiDataObject {

    public ScenarioDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        registerEditor("text/scenario+xml", false);
    }

    @Override
    protected int associateLookup() {
        return 1;
    }
}
