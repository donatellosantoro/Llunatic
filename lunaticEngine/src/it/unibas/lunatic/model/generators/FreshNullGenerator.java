package it.unibas.lunatic.model.generators;

import it.unibas.lunatic.model.generators.operators.MainMemoryGenerateFreshNullsForStandardChase;
import it.unibas.lunatic.model.generators.operators.SQLGenerateFreshNullForStandardChase;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import speedy.model.database.IValue;
import speedy.model.database.Tuple;

public class FreshNullGenerator implements IValueGenerator {

    private MainMemoryGenerateFreshNullsForStandardChase mmGenerator;
    private SQLGenerateFreshNullForStandardChase sqlGenerator;
    private String variableInDependency;
    private String type;

    public FreshNullGenerator(MainMemoryGenerateFreshNullsForStandardChase generator,
            SQLGenerateFreshNullForStandardChase sqlGenerator,
            FormulaVariable variable, Dependency dependency, String type) {
        this.mmGenerator = generator;
        this.sqlGenerator = sqlGenerator;
        this.variableInDependency = variable.getId() + "." + dependency.getId();
        this.type = type;
    }

    public String getVariableInDependency() {
        return variableInDependency;
    }

    public IValue generateValue(Tuple sourceTuple) {
        return mmGenerator.generateValue(sourceTuple, variableInDependency, type);
    }

    public String getType() {
        return type;
    }

    public String toSQLString() {
        return sqlGenerator.generateSQL(variableInDependency, type);
    }

    @Override
    public IValueGenerator clone() {
        try {
            return (FreshNullGenerator) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new IllegalArgumentException("Unable to clone " + this);
        }
    }

}
