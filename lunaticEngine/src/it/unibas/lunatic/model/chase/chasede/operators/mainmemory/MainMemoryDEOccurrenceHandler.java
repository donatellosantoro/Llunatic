package it.unibas.lunatic.model.chase.chasede.operators.mainmemory;

import it.unibas.lunatic.model.chase.chasede.operators.IValueOccurrenceHandlerDE;
import it.unibas.lunatic.model.database.CellRef;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.mainmemory.MainMemoryDB;
import it.unibas.lunatic.model.database.NullValue;
import java.util.ArrayList;
import java.util.List;

public class MainMemoryDEOccurrenceHandler implements IValueOccurrenceHandlerDE {

    public List<CellRef> getOccurrencesForNull(IDatabase database, NullValue value) {
        MainMemoryDB mainMemoryDB = (MainMemoryDB)database;
        return mainMemoryDB.getSkolemOccurrences().get(value);
    }

    public void addOccurrenceForNull(IDatabase database, NullValue value, CellRef cellRef) {
        MainMemoryDB mainMemoryDB = (MainMemoryDB)database;
        List<CellRef> cellRefs = mainMemoryDB.getSkolemOccurrences().get(value);
        if (cellRefs == null) {
            cellRefs = new ArrayList<CellRef>();
            mainMemoryDB.getSkolemOccurrences().put(value, cellRefs);
        }
        cellRefs.add(cellRef);
    }

    public void removeOccurrenceForNull(IDatabase database, NullValue value, CellRef cellRef) {
        MainMemoryDB mainMemoryDB = (MainMemoryDB)database;
        List<CellRef> cellRefs = mainMemoryDB.getSkolemOccurrences().get(value);
        cellRefs.remove(cellRef);
    }

    public void removeOccurrencesForNull(IDatabase database, NullValue value) {
        MainMemoryDB mainMemoryDB = (MainMemoryDB)database;
        mainMemoryDB.getSkolemOccurrences().remove(value);
    }


}
