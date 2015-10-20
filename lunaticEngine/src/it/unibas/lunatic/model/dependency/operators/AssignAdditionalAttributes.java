package it.unibas.lunatic.model.dependency.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.partialorder.OrderingAttribute;
import speedy.model.database.AttributeRef;
import it.unibas.lunatic.model.dependency.*;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssignAdditionalAttributes {

    private static Logger logger = LoggerFactory.getLogger(AssignAdditionalAttributes.class);

    public void assignAttributes(Dependency dependency, Scenario scenario) {
        List<OrderingAttribute> orderingAttributes = scenario.getOrderingAttributes();
        if (orderingAttributes == null || orderingAttributes.isEmpty()) {
            return;
        }
        for (OrderingAttribute orderingAttribute : orderingAttributes) {
            AttributeRef attribute = orderingAttribute.getAttribute();
            AttributeRef associatedAttribute = orderingAttribute.getAssociatedAttribute();
            if (dependency.getAffectedAttributes().contains(attribute)) {
                LunaticUtility.addIfNotContained(dependency.getAdditionalAttributes(), associatedAttribute);
                if (logger.isDebugEnabled()) logger.debug("Assinging additional attribute " + associatedAttribute + " to dependency " + dependency.getId());
            }
        }
    }
}
