package it.unibas.lunatic.test.checker;

import java.util.List;

public interface IExpectedTuple {

    void addValue(IExpectedValue value);

    boolean containsNullOrSkolem();

    List<Integer> containsSameSkolem(IExpectedValue otherValue);

    String getFatherOid();

    String getOid();

    String getRelationName();

    List<IExpectedValue> getValues();

    void setFatherOid(String fatherOid);

    void setOid(String oid);

    @Override
    String toString();

}
