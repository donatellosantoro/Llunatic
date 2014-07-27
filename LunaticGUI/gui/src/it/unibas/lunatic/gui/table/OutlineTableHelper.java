/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.table;

import javax.swing.table.TableColumnModel;
import org.netbeans.swing.etable.ETableColumn;
import org.netbeans.swing.etable.ETableColumnModel;
import org.openide.explorer.view.OutlineView;
import org.openide.nodes.Node;

/**
 *
 * @author Antonio Galotta
 */
public class OutlineTableHelper {

//    public void setColumns(OutlineView outlineView, Node node) {
//        outlineView.setPropertyColumns();
//        Node.PropertySet[] propertySets = node.getPropertySets();
//        for (int i = 0; i < propertySets.length; i++) {
//            Node.PropertySet set = propertySets[i];
//            Node.Property<?>[] properties = set.getProperties();
//            for (int j = 0; j < properties.length; j++) {
//                Node.Property p = properties[j];
//                outlineView.addPropertyColumn(p.getName(), p.getDisplayName(), p.getShortDescription());
//            }
//        }
//    }

    public void hideNodesColumn(OutlineView outlineView) {
        TableColumnModel columnModel = outlineView.getOutline().getColumnModel();
        ETableColumn column = (ETableColumn) columnModel.getColumn(0);
        ((ETableColumnModel) columnModel).setColumnHidden(column, true);
    }
}
