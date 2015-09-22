package it.unibas.lunatic.model.database.skolem;

import it.unibas.lunatic.model.generators.IValueGenerator;
import java.util.List;
import speedy.model.database.Tuple;

public class SubGeneratorSkolemPart implements ISkolemPart {

    private IValueGenerator generator;

    public SubGeneratorSkolemPart(IValueGenerator generator) {
        this.generator = generator;
    }

    public String getValue(Tuple sourceTuple) {
        return this.generator.generateValue(sourceTuple) + "";
    }

    public List<ISkolemPart> getChildren() {
        throw new UnsupportedOperationException("Not supported.");
    }

    public void addChild(ISkolemPart child) {
        throw new UnsupportedOperationException("Not supported.");
    }

    public String toString() {
        return this.generator.toString();
    }

}
