package it.unibas.lunatic.model.chase.chasede.operators;

import it.unibas.lunatic.model.database.CellRef;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.NullValue;
import java.util.List;

public interface IValueOccurrenceHandlerDE {

    List<CellRef> getOccurrencesForNull(IDatabase database, NullValue value);
    void addOccurrenceForNull(IDatabase database, NullValue value, CellRef cellRef);
    void removeOccurrenceForNull(IDatabase database, NullValue value, CellRef cellRef);
    void removeOccurrencesForNull(IDatabase database, NullValue value);
    
}
