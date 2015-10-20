package it.unibas.lunatic.model.database.skolem;

import java.util.List;
import speedy.model.database.Tuple;

public class StringSkolemPart implements ISkolemPart {

    private String string;

    public StringSkolemPart(String string) {
        this.string = string;
    }

    public String getValue(Tuple sourceTuple) {
        return this.string;
    }

    public List<ISkolemPart> getChildren() {
        throw new UnsupportedOperationException("Not supported.");
    }

    public void addChild(ISkolemPart child) {
        throw new UnsupportedOperationException("Not supported.");
    }

    public String toString() {
        return this.string;
    }

}
