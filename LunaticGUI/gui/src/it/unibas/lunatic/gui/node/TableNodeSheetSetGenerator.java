package it.unibas.lunatic.gui.node;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.gui.node.utils.StringProperty;
import java.lang.reflect.InvocationTargetException;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;
import speedy.model.database.ITable;

@NbBundle.Messages({
    "PROP_tableName=Table Name",
    "PROP_size=Size",
    "PROP_attributes=Number of attributes",
    "PROP_auth=Authoritative"
})
public class TableNodeSheetSetGenerator {

    public Sheet.Set createSheetSet(final TableNode tableNode) {
        final Scenario scenario = tableNode.getScenario();
        final ITable table = tableNode.getTable();
        Sheet.Set set = Sheet.createPropertiesSet();
        set.put(new StringProperty(Bundle.PROP_tableName()) {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return table.getName();
            }
        });
        set.put(new StringProperty(Bundle.PROP_auth()) {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return scenario.getAuthoritativeSources().contains(table.getName()) + "";
            }
        });
        set.put(new StringProperty(Bundle.PROP_size()) {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return table.getSize() + "";
            }
        });
        set.put(new StringProperty(Bundle.PROP_attributes()) {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return table.getAttributes().size() + "";
            }
        });
        return set;
    }
}
