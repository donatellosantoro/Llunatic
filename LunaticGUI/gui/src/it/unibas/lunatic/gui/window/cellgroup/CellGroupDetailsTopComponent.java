package it.unibas.lunatic.gui.window.cellgroup;

import it.unibas.lunatic.IApplication;
import it.unibas.lunatic.gui.ExplorerTopComponent;
import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.model.LoadedScenario;
import it.unibas.lunatic.gui.node.cellgroup.AdditionalCellsRootNode;
import it.unibas.lunatic.gui.node.cellgroup.JustificationRootNode;
import it.unibas.lunatic.gui.node.cellgroup.JustificationTupleNode;
import it.unibas.lunatic.gui.node.cellgroup.OccurrenceRootNode;
import it.unibas.lunatic.gui.node.cellgroup.OccurrenceTupleNode;
import it.unibas.lunatic.gui.node.cellgroup.StepCellGroupNode;
import it.unibas.lunatic.gui.node.cellgroup.UserCellRootNode;
import it.unibas.lunatic.gui.node.cellgroup.UserCellTupleNode;
import it.unibas.lunatic.gui.node.utils.ITableColumnGenerator;
import it.unibas.lunatic.gui.table.OutlineTableHelper;
import it.unibas.lunatic.gui.window.ScenarioChangeListener;
import it.unibas.lunatic.gui.window.utils.TopComponentListener;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.Actions;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;

@ConvertAsProperties(
        dtd = "-//it.unibas.lunatic.gui.window//CellGroupDetails//EN",
        autostore = false)
@TopComponent.Description(
        preferredID = R.Window.CELL_GROUP_DETAILS,
        persistenceType = TopComponent.PERSISTENCE_NEVER)
@TopComponent.Registration(mode = "navigator", openAtStartup = false)
@Messages({
    "CellGroupDetails=Cell group details",
    "CTL_CellGroupDetailsTopComponent=Cell group details",
    "HINT_CellGroupDetailsTopComponent=Details about selected cell group"
})
public final class CellGroupDetailsTopComponent extends ExplorerTopComponent implements ScenarioChangeListener.Target {

    private Log logger = LogFactory.getLog(getClass());
    private IApplication app = Lookup.getDefault().lookup(IApplication.class);
    private CellGroupSelectionListener cellGroupSelectionListener = new CellGroupSelectionListener();
    private ScenarioChangeListener scenarioChangeListener = new ScenarioChangeListener();
    private StepCellGroupSelectionListener mySelectionListener = new StepCellGroupSelectionListener();
    private TopComponentListener windowCloseListener = new TopComponentListener();
    private OutlineTableHelper tableHelper = new OutlineTableHelper();
    private ITableColumnGenerator occColumnGenerator = OccurrenceTupleNode.getColumnGenerator();
    private ITableColumnGenerator justColumnGenerator = JustificationTupleNode.getColumnGenerator();
    private ITableColumnGenerator userColumnGenerator = UserCellTupleNode.getColumnGenerator();
//    private ITableColumnGenerator addColumnGenerator = AdditionalCellsRootNode.getColumnGenerator();
    private int selectedTab = TAB_OCCURRENCE;
    private StepCellGroupNode stepCellGroupNode;

    public CellGroupDetailsTopComponent() {
        initComponents();
        setName(Bundle.CTL_CellGroupDetailsTopComponent());
        setToolTipText(Bundle.HINT_CellGroupDetailsTopComponent());
        associateExplorerLookup();
        editButton.setAction(Actions.forID("Edit", R.ActionId.EDIT_CELL_GROUP_VALUE));
        tableCellGroupCell.getOutline().setRootVisible(false);
        tableHelper.hideNodesColumn(tableCellGroupCell);
        tableCellGroupCell.getOutline().setFullyNonEditable(true);
    }

    @Override
    public void setRootContext(Node node) {
        this.stepCellGroupNode = (StepCellGroupNode) node;
        explorer.setRootContext(node);
        if (logger.isDebugEnabled()) logger.debug("Selected node " + stepCellGroupNode);
        this.updateCellGroupDetail(stepCellGroupNode.getCellGroup());
    }

    private void updateCellGroupDetail(CellGroup cellGroup) {
        cgValue.setText(cellGroup.getValue().toString());
        setCheckBoxValue(cellGroup);
        updateTableForCellGroupCells();
        this.btnTabOccurrence.setText("Occurrences (" + cellGroup.getOccurrences().size() + ")");
        this.btnTabJustification.setText("Justifications (" + cellGroup.getJustifications().size() + ")");
        this.btnTabUserCells.setText("User Cells (" + cellGroup.getUserCells().size() + ")");
        this.btnTabAdditional.setText("Additional Cells (" + cellGroup.getAdditionalCells().size() + ")");
    }

    private void updateTableForCellGroupCells() {
        if (selectedTab == TAB_OCCURRENCE) {
            occColumnGenerator.createTableColumns(tableCellGroupCell);
            explorer.setRootContext(new OccurrenceRootNode(stepCellGroupNode));
        }
        if (selectedTab == TAB_JUSTIFICATION) {
            justColumnGenerator.createTableColumns(tableCellGroupCell);
            explorer.setRootContext(new JustificationRootNode(stepCellGroupNode));
        }
        if (selectedTab == TAB_USERCELL) {
            userColumnGenerator.createTableColumns(tableCellGroupCell);
            explorer.setRootContext(new UserCellRootNode(stepCellGroupNode));
        }
        if (selectedTab == TAB_ADDITIONALCELL) {
            tableCellGroupCell.setPropertyColumns();
            explorer.setRootContext(new AdditionalCellsRootNode(stepCellGroupNode));
        }
    }

    public void setCheckBoxValue(CellGroup cellGroup) {
        checkBoxInvalidCell.setSelected(cellGroup.hasInvalidCell());
        for (ActionListener actionListener : checkBoxInvalidCell.getActionListeners()) {
            checkBoxInvalidCell.removeActionListener(actionListener);
        }
        checkBoxInvalidCell.addActionListener(new KeepOriginalStatus(cellGroup.hasInvalidCell()));
    }

    @Override
    public void componentOpened() {
        cellGroupSelectionListener.register(this);
        scenarioChangeListener.register(this);
        mySelectionListener.register();
        LoadedScenario ls = app.get(R.Bean.LOADED_SCENARIO, LoadedScenario.class);
        StepCellGroupNode node = ls.get(R.BeanProperty.SELECTED_CELL_GROUP_NODE, StepCellGroupNode.class);
        if (node != null) {
            setRootContext(node);
        }
    }

    @Override
    public void componentClosed() {
        mySelectionListener.register();
        windowCloseListener.remove();
    }

    @Override
    public void removeRootContext() {
        explorer.setRootContext(Node.EMPTY);
    }

    @Override
    protected void componentActivated() {
        scenarioChangeListener.remove();
    }

    @Override
    public void onScenarioChange(LoadedScenario oldScenario, LoadedScenario newScenario) {
        onScenarioClose(oldScenario);
    }

    @Override
    public void onScenarioClose(LoadedScenario scenario) {
        this.close();
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnTabGroup = new javax.swing.ButtonGroup();
        javax.swing.JPanel pnlTab = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        btnTabOccurrence = new javax.swing.JToggleButton();
        btnTabJustification = new javax.swing.JToggleButton();
        jPanel3 = new javax.swing.JPanel();
        btnTabAdditional = new javax.swing.JToggleButton();
        btnTabUserCells = new javax.swing.JToggleButton();
        checkBoxInvalidCell = new javax.swing.JCheckBox();
        jPanel5 = new javax.swing.JPanel();
        lblValue = new javax.swing.JLabel();
        cgValue = new javax.swing.JLabel();
        editButton = new javax.swing.JButton();
        javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
        tableCellGroupCell = new org.openide.explorer.view.OutlineView();

        pnlTab.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlTab.setOpaque(false);

        jPanel4.setOpaque(false);

        jPanel2.setOpaque(false);

        btnTabGroup.add(btnTabOccurrence);
        btnTabOccurrence.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        btnTabOccurrence.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(btnTabOccurrence, org.openide.util.NbBundle.getMessage(CellGroupDetailsTopComponent.class, "CellGroupDetailsTopComponent.btnTabOccurrence.text")); // NOI18N
        btnTabOccurrence.setSize(new java.awt.Dimension(200, 0));
        btnTabOccurrence.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTabOccurrenceActionPerformed(evt);
            }
        });

        btnTabGroup.add(btnTabJustification);
        btnTabJustification.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnTabJustification, org.openide.util.NbBundle.getMessage(CellGroupDetailsTopComponent.class, "CellGroupDetailsTopComponent.btnTabJustification.text")); // NOI18N
        btnTabJustification.setSize(new java.awt.Dimension(200, 0));
        btnTabJustification.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTabJustificationActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(btnTabOccurrence, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnTabJustification, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnTabOccurrence)
                    .addComponent(btnTabJustification))
                .addGap(0, 0, 0))
        );

        jPanel4.add(jPanel2);

        jPanel3.setOpaque(false);

        btnTabGroup.add(btnTabAdditional);
        btnTabAdditional.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnTabAdditional, org.openide.util.NbBundle.getMessage(CellGroupDetailsTopComponent.class, "CellGroupDetailsTopComponent.btnTabAdditional.text")); // NOI18N
        btnTabAdditional.setSize(new java.awt.Dimension(200, 0));
        btnTabAdditional.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTabAdditionalActionPerformed(evt);
            }
        });

        btnTabGroup.add(btnTabUserCells);
        btnTabUserCells.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnTabUserCells, org.openide.util.NbBundle.getMessage(CellGroupDetailsTopComponent.class, "CellGroupDetailsTopComponent.btnTabUserCells.text")); // NOI18N
        btnTabUserCells.setSize(new java.awt.Dimension(200, 0));
        btnTabUserCells.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTabUserCellsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(btnTabUserCells, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnTabAdditional)
                .addGap(0, 0, 0))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnTabUserCells)
                    .addComponent(btnTabAdditional))
                .addGap(0, 0, 0))
        );

        jPanel4.add(jPanel3);

        org.openide.awt.Mnemonics.setLocalizedText(checkBoxInvalidCell, org.openide.util.NbBundle.getMessage(CellGroupDetailsTopComponent.class, "CellGroupDetailsTopComponent.checkBoxInvalidCell.text")); // NOI18N

        javax.swing.GroupLayout pnlTabLayout = new javax.swing.GroupLayout(pnlTab);
        pnlTab.setLayout(pnlTabLayout);
        pnlTabLayout.setHorizontalGroup(
            pnlTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(checkBoxInvalidCell, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        pnlTabLayout.setVerticalGroup(
            pnlTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTabLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxInvalidCell)
                .addContainerGap(13, Short.MAX_VALUE))
        );

        jPanel5.setOpaque(false);

        org.openide.awt.Mnemonics.setLocalizedText(lblValue, org.openide.util.NbBundle.getMessage(CellGroupDetailsTopComponent.class, "CellGroupDetailsTopComponent.lblValue.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(cgValue, org.openide.util.NbBundle.getMessage(CellGroupDetailsTopComponent.class, "CellGroupDetailsTopComponent.cgValue.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(editButton, org.openide.util.NbBundle.getMessage(CellGroupDetailsTopComponent.class, "CellGroupDetailsTopComponent.editButton.text")); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblValue, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(cgValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(editButton)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblValue)
                    .addComponent(cgValue)
                    .addComponent(editButton))
                .addContainerGap())
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setOpaque(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tableCellGroupCell, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tableCellGroupCell, javax.swing.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlTab, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlTab, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnTabOccurrenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTabOccurrenceActionPerformed
        this.selectedTab = TAB_OCCURRENCE;
        updateTableForCellGroupCells();
    }//GEN-LAST:event_btnTabOccurrenceActionPerformed

    private void btnTabJustificationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTabJustificationActionPerformed
        this.selectedTab = TAB_JUSTIFICATION;
        updateTableForCellGroupCells();
    }//GEN-LAST:event_btnTabJustificationActionPerformed

    private void btnTabUserCellsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTabUserCellsActionPerformed
        this.selectedTab = TAB_USERCELL;
        updateTableForCellGroupCells();
    }//GEN-LAST:event_btnTabUserCellsActionPerformed

    private void btnTabAdditionalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTabAdditionalActionPerformed
        this.selectedTab = TAB_ADDITIONALCELL;
        updateTableForCellGroupCells();
    }//GEN-LAST:event_btnTabAdditionalActionPerformed

    private static int TAB_OCCURRENCE = 1;
    private static int TAB_JUSTIFICATION = 2;
    private static int TAB_USERCELL = 3;
    private static int TAB_ADDITIONALCELL = 4;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnTabAdditional;
    private javax.swing.ButtonGroup btnTabGroup;
    private javax.swing.JToggleButton btnTabJustification;
    private javax.swing.JToggleButton btnTabOccurrence;
    private javax.swing.JToggleButton btnTabUserCells;
    private javax.swing.JLabel cgValue;
    private javax.swing.JCheckBox checkBoxInvalidCell;
    private javax.swing.JButton editButton;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JLabel lblValue;
    private org.openide.explorer.view.OutlineView tableCellGroupCell;
    // End of variables declaration//GEN-END:variables

    void writeProperties(java.util.Properties p) {
        p.setProperty("version", "1.4");
    }

    void readProperties(java.util.Properties p) {
    }

    private static class KeepOriginalStatus implements ActionListener {

        private boolean originalStatuts;

        public KeepOriginalStatus(boolean originalStatuts) {
            this.originalStatuts = originalStatuts;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JCheckBox checkBox = (JCheckBox) e.getSource();
            checkBox.setSelected(originalStatuts);
        }
    }
}
