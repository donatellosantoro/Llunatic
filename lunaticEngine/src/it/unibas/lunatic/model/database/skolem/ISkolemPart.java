package it.unibas.lunatic.model.database.skolem;

import it.unibas.lunatic.model.database.Tuple;
import java.util.List;

public interface ISkolemPart {

    public String getValue(Tuple tuple);

    public List<ISkolemPart> getChildren();

    public void addChild(ISkolemPart child);

    public String toString();
    
}
