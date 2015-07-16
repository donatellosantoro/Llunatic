package it.unibas.lunatic.model.chase.chasede.operators;

import it.unibas.lunatic.model.database.Cell;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.NullValue;
import java.util.List;

public interface IValueOccurrenceHandlerDE {

    List<Cell> getOccurrencesForNull(IDatabase database, NullValue value);
    void addOccurrenceForNull(IDatabase database, NullValue value, Cell cell);
    void removeOccurrenceForNull(IDatabase database, NullValue value, Cell cell);
    void removeOccurrencesForNull(IDatabase database, NullValue value);
    
}
