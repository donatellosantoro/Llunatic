package it.unibas.lunatic.model.database.skolem;

import java.util.List;
import speedy.model.database.Tuple;

public interface ISkolemPart {

    public String getValue(Tuple tuple);

    public List<ISkolemPart> getChildren();

    public void addChild(ISkolemPart child);

    public String toString();
    
}
