package it.unibas.lunatic.model.dependency;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import speedy.model.database.AttributeRef;
import speedy.utility.SpeedyUtility;

public class AttributesInSameCellGroups {

    private Map<AttributeRef, Set<AttributeRef>> attributeMap = new HashMap<AttributeRef, Set<AttributeRef>>();

    public void addAttribute(AttributeRef attribute, Set<AttributeRef> relatedAttributes) {
        this.attributeMap.put(attribute, relatedAttributes);
    }

    public Set<AttributeRef> getRelatedAttribute(AttributeRef attribute) {
        return this.attributeMap.get(attribute);
    }

    @Override
    public String toString() {
        return "Attribute Map " + SpeedyUtility.printMap(attributeMap);
    }
}
