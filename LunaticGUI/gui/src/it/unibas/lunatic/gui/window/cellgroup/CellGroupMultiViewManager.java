package it.unibas.lunatic.gui.window.cellgroup;

import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewFactory;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

@NbBundle.Messages({
    "CTL_CellGroupAction=CellGroup",
    "CTL_CellGroupTopComponent=Cell group details",
    "HINT_CellGroupTopComponent=This is a CellGroup window"
})
public class CellGroupMultiViewManager {

    private TopComponent multiView;
    private OccurrencesPanel occurrencesPanel;
    private ProvenancesPanel provenancesPanel;
    private AdditionalCellsPanel additionalCellsPanel;
    private MultiViewDescription[] views;
    private static CellGroupMultiViewManager instance;

    public static CellGroupMultiViewManager getInstance() {
        if (instance == null) {
            instance = new CellGroupMultiViewManager();
        }
        return instance;
    }

    public CellGroupMultiViewManager() {
        occurrencesPanel = new OccurrencesPanel(new CellGroupDetails());
        provenancesPanel = new ProvenancesPanel(new CellGroupDetails());
        additionalCellsPanel = new AdditionalCellsPanel(new CellGroupDetails());
        views = new MultiViewDescription[]{
            occurrencesPanel.getMultiViewDescription(),
            provenancesPanel.getMultiViewDescription(),
            additionalCellsPanel.getMultiViewDescription()
        };
        multiView = MultiViewFactory.createMultiView(views, occurrencesPanel.getMultiViewDescription());
        multiView.setName(Bundle.CTL_CellGroupTopComponent());
        multiView.setDisplayName(Bundle.CTL_CellGroupTopComponent());
    }

    public TopComponent open() {
        multiView.open();
        multiView.requestActive();
        return multiView;
    }
}
