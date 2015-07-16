package it.unibas.lunatic.model.chase.chasede.operators.mainmemory;

import it.unibas.lunatic.model.chase.chasede.operators.IValueOccurrenceHandlerDE;
import it.unibas.lunatic.model.database.Cell;
import it.unibas.lunatic.model.database.CellRef;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.mainmemory.MainMemoryDB;
import it.unibas.lunatic.model.database.NullValue;
import java.util.ArrayList;
import java.util.List;

public class MainMemoryDEOccurrenceHandler implements IValueOccurrenceHandlerDE {

    @Override
    public List<Cell> getOccurrencesForNull(IDatabase database, NullValue value) {
        MainMemoryDB mainMemoryDB = (MainMemoryDB) database;
        return mainMemoryDB.getSkolemOccurrences().get(value);
    }

    @Override
    public void addOccurrenceForNull(IDatabase database, NullValue value, Cell cell) {
        MainMemoryDB mainMemoryDB = (MainMemoryDB) database;
        List<Cell> cells = mainMemoryDB.getSkolemOccurrences().get(value);
        if (cells == null) {
            cells = new ArrayList<Cell>();
            mainMemoryDB.getSkolemOccurrences().put(value, cells);
        }
        cells.add(cell);
    }

    @Override
    public void removeOccurrenceForNull(IDatabase database, NullValue value, Cell cell) {
        MainMemoryDB mainMemoryDB = (MainMemoryDB) database;
        List<Cell> cells = mainMemoryDB.getSkolemOccurrences().get(value);
        cells.remove(cell);
    }

    @Override
    public void removeOccurrencesForNull(IDatabase database, NullValue value) {
        MainMemoryDB mainMemoryDB = (MainMemoryDB) database;
        mainMemoryDB.getSkolemOccurrences().remove(value);
    }

}
