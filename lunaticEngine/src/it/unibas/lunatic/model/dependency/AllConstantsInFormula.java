package it.unibas.lunatic.model.dependency;

import it.unibas.lunatic.utility.DependencyUtility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import speedy.utility.comparator.StringComparator;

public class AllConstantsInFormula {
    
    private Dependency dependency;
    private Map<String, ConstantInFormula> constantMap = new HashMap<String, ConstantInFormula>();

    public AllConstantsInFormula(Dependency dependency) {
        this.dependency = dependency;
    }

    public Dependency getDependency() {
        return dependency;
    }

    public Map<String, ConstantInFormula> getConstantMap() {
        return constantMap;
    }

    public String getTableName() {
        return DependencyUtility.buildTableNameForConstants(dependency);
    }
    
    @SuppressWarnings("unchecked")
    public List<String> getOrderedKeys() {
        List<String> orderedKeys = new ArrayList<String>(constantMap.keySet());
        Collections.sort(orderedKeys, new StringComparator()); 
        return orderedKeys;
    }
    
    public List<String> getAttributeNames() {
        List<String> result = new ArrayList<String>();
        for (String key : getOrderedKeys()) {
            result.add(DependencyUtility.buildAttributeNameForConstant(constantMap.get(key).getConstantValue()));
        }
        return result;
    }

    public List<Object> getConstantValues() {
        List<Object> result = new ArrayList<Object>();
        for (String key : getOrderedKeys()) {
            result.add(constantMap.get(key).getConstantValue());
        }
        return result;
    }
    
    public boolean isEmpty() {
        return this.constantMap.isEmpty();
    }

    @Override
    public String toString() {
        return "ConstantsInFormula{" + "dependency=" + dependency + ", constantMap=" + constantMap + '}';
    }    

}
