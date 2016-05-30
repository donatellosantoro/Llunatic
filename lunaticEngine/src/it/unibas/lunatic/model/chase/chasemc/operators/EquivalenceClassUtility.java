package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.model.chase.commons.operators.ChaseUtility;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.operators.DependencyUtility;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.AttributeRef;
import speedy.model.database.IValue;
import speedy.model.database.TableAlias;
import speedy.model.database.Tuple;

public class EquivalenceClassUtility {

    private static Logger logger = LoggerFactory.getLogger(EquivalenceClassUtility.class);

    public static boolean sameEquivalenceClass(Tuple tuple, Tuple lastTuple, Dependency egd) {
        List<AttributeRef> joinAttributes = getTargetJoinAttributes(egd);
        joinAttributes = ChaseUtility.filterConclusionOccurrences(joinAttributes, egd);
        if (logger.isDebugEnabled()) logger.debug("Target join attributes: " + joinAttributes);
        for (AttributeRef attribute : joinAttributes) {
            IValue tupleValue = tuple.getCell(attribute).getValue();
            IValue lastTupleValue = lastTuple.getCell(attribute).getValue();
            if (!tupleValue.equals(lastTupleValue)) {
                return false;
            }
        }
        return true;
    }

    public static AttributeRef correctAttributeForSymmetricEGDs(AttributeRef attributeRef, Dependency egd) {
        if (!egd.hasSymmetricChase()) {
            return attributeRef;
        }
        for (TableAlias tableAlias : egd.getSymmetricAtoms().getSymmetricAliases()) {
            if (attributeRef.getTableName().equals(tableAlias.getTableName())) {
                return ChaseUtility.unAlias(attributeRef);
            }
        }
        return attributeRef;
    }

    private static List<AttributeRef> getTargetJoinAttributes(Dependency egd) {
        List<AttributeRef> targetJoinAttributes = DependencyUtility.findTargetJoinAttributesInPositiveFormula(egd);
        if (!egd.hasSymmetricChase()) {
            return targetJoinAttributes;
        }
        List<AttributeRef> result = new ArrayList<AttributeRef>();
        for (AttributeRef attributeRef : targetJoinAttributes) {
            result.add(correctAttributeForSymmetricEGDs(attributeRef, egd));
        }
        return result;
    }
}
