package it.unibas.lunatic.model.dependency;

import java.util.List;
import speedy.utility.SpeedyUtility;

public class FunctionalDependency {

    private final String tableName;
    private final List<String> leftAttributes;
    private final List<String> rightAttributes;

    public FunctionalDependency(String tableName, List<String> leftAttributes, List<String> rightAttributes) {
        this.tableName = tableName;
        this.leftAttributes = leftAttributes;
        this.rightAttributes = rightAttributes;
    }

    public String getTableName() {
        return tableName;
    }

    public List<String> getLeftAttributes() {
        return leftAttributes;
    }

    public List<String> getRightAttributes() {
        return rightAttributes;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(tableName + ": ");
        for (String leftAttribute : leftAttributes) {
            result.append(leftAttribute).append(", ");
        }
        SpeedyUtility.removeChars(", ".length(), result);
        result.append(" -> ");
        for (String rightAttribute : rightAttributes) {
            result.append(rightAttribute).append(", ");
        }
        SpeedyUtility.removeChars(", ".length(), result);
        return result.toString();
    }    
    
}
