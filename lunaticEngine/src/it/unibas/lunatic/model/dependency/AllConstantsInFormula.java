package it.unibas.lunatic.model.dependency;

import it.unibas.lunatic.model.dependency.operators.DependencyUtility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import speedy.utility.comparator.StringComparator;

public class AllConstantsInFormula {
    
    private final Dependency dependency;
    private final Map<String, ConstantInFormula> constantMap = new HashMap<String, ConstantInFormula>();

    public AllConstantsInFormula(Dependency dependency) {
        this.dependency = dependency;
    }

    public Dependency getDependency() {
        return dependency;
    }

    public Map<String, ConstantInFormula> getConstantMap() {
        return constantMap;
    }

    public String getTableNameForPremiseConstants() {
        return DependencyUtility.buildTableNameForConstants(dependency, true);
    }
    
    public String getTableNameForConclusionConstants() {
        return DependencyUtility.buildTableNameForConstants(dependency, false);
    }

    @SuppressWarnings("unchecked")
    public List<String> getOrderedKeys() {
        List<String> orderedKeys = new ArrayList<String>(constantMap.keySet());
        Collections.sort(orderedKeys, new StringComparator()); 
        return orderedKeys;
    }

    public List<ConstantInFormula> getConstants(boolean premise) {
        List<ConstantInFormula> result = new ArrayList<ConstantInFormula>();
        for (String key : getOrderedKeys()) {
            ConstantInFormula constant = constantMap.get(key);
            if (constant.isPremise() != premise) {
                continue;
            }
            result.add(constant);
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
