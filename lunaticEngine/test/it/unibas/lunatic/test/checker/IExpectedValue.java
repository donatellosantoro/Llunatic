package it.unibas.lunatic.test.checker;

public interface IExpectedValue {

    public String getValue();

    public boolean isUniversal();

    public boolean isNullOrSkolem();

    public boolean equalsSkolem(IExpectedValue otherValue);

    public boolean equals(Object otherValue);

    public String toString();

}
