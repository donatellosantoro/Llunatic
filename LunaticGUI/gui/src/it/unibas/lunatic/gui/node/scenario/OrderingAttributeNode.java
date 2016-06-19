package it.unibas.lunatic.gui.node.scenario;

import it.unibas.lunatic.gui.node.utils.StringProperty;
import it.unibas.lunatic.model.chase.chasemc.partialorder.OrderingAttribute;
import java.lang.reflect.InvocationTargetException;
import org.openide.explorer.view.OutlineView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;

@NbBundle.Messages({
    "COL_AssociatedAttribute=associated attribute",
    "COL_Sort=sort"
})
public class OrderingAttributeNode extends AbstractNode {

    public static final String ASSOCIATED_ATTRINUTE = "associatedAttribute";
    public static final String SORT = "sort";

    public static void createTableColumns(OutlineView outlineView1) {
        outlineView1.addPropertyColumn(ASSOCIATED_ATTRINUTE, Bundle.COL_AssociatedAttribute());
        outlineView1.addPropertyColumn(SORT, Bundle.COL_Sort());
    }
    private final OrderingAttribute attrib;

    public OrderingAttributeNode(OrderingAttribute key) {
        super(Children.LEAF);
        this.attrib = key;
        setDisplayName(key.getAttribute().toString());
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        sheet.put(set);
        set.put(new StringProperty(ASSOCIATED_ATTRINUTE, Bundle.COL_AssociatedAttribute()) {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return attrib.getAssociatedAttribute().toString();
            }
        });
        set.put(new StringProperty(SORT, Bundle.COL_Sort()) {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return attrib.getValueComparator().getSort();
            }
        });
        return sheet;
    }

    public OrderingAttribute getOrderingAttribute() {
        return attrib;
    }
}
