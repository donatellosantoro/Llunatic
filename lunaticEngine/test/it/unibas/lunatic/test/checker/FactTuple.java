package it.unibas.lunatic.test.checker;

import java.util.ArrayList;
import java.util.List;

public class FactTuple implements IExpectedTuple {

    private String relationName;
    private String oid;
    private String fatherOid;
    private List<FactValue> values = new ArrayList<FactValue>();

    public FactTuple(String relationName) {
        this.relationName = relationName;
    }

    public void addValue(IExpectedValue value) {
        this.values.add((FactValue)value);
    }

    public String getFatherOid() {
        return fatherOid;
    }

    public String getOid() {
        return oid;
    }

    public String getRelationName() {
        return relationName;
    }

    public List<IExpectedValue> getValues() {
        return new ArrayList<IExpectedValue>(values);
    }

    public void setFatherOid(String fatherOid) {
        this.fatherOid = fatherOid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public boolean containsNullOrSkolem() {
        for (FactValue factValue : values) {
            if (factValue.isNullOrSkolem()) {
                return true;
            }
        }
        return false;
    }

    public List<Integer> containsSameSkolem(IExpectedValue otherValue) {
        List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < values.size(); i++) {
            IExpectedValue factValue = values.get(i);
            if (factValue.equalsSkolem(otherValue)) {
                result.add(i);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(relationName).append("(");
        for (int i = 0; i < values.size(); i++) {
            FactValue factValue = values.get(i);
            result.append(factValue);
            if (i != values.size() - 1) result.append(",");
        }
        result.append(")");
        return result.toString();
    }

}
