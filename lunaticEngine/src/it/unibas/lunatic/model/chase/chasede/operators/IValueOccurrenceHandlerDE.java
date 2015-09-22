package it.unibas.lunatic.model.chase.chasede.operators;

import speedy.model.database.Cell;
import speedy.model.database.IDatabase;
import java.util.List;
import speedy.model.database.NullValue;

public interface IValueOccurrenceHandlerDE {

    List<Cell> getOccurrencesForNull(IDatabase database, NullValue value);
    void addOccurrenceForNull(IDatabase database, NullValue value, Cell cell);
    void removeOccurrenceForNull(IDatabase database, NullValue value, Cell cell);
    void removeOccurrencesForNull(IDatabase database, NullValue value);
    
}
