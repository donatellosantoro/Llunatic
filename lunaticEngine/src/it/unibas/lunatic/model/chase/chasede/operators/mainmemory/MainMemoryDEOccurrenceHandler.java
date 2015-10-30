package it.unibas.lunatic.model.chase.chasede.operators.mainmemory;

import speedy.model.database.Cell;
import speedy.model.database.IDatabase;
import java.util.ArrayList;
import java.util.List;
import speedy.model.database.NullValue;
import speedy.model.database.mainmemory.MainMemoryDB;

public class MainMemoryDEOccurrenceHandler {

    public List<Cell> getOccurrencesForNull(IDatabase database, NullValue value) {
        MainMemoryDB mainMemoryDB = (MainMemoryDB) database;
        return mainMemoryDB.getSkolemOccurrences().get(value);
    }

    public void addOccurrenceForNull(IDatabase database, NullValue value, Cell cell) {
        MainMemoryDB mainMemoryDB = (MainMemoryDB) database;
        List<Cell> cells = mainMemoryDB.getSkolemOccurrences().get(value);
        if (cells == null) {
            cells = new ArrayList<Cell>();
            mainMemoryDB.getSkolemOccurrences().put(value, cells);
        }
        cells.add(cell);
    }

    public void removeOccurrenceForNull(IDatabase database, NullValue value, Cell cell) {
        MainMemoryDB mainMemoryDB = (MainMemoryDB) database;
        List<Cell> cells = mainMemoryDB.getSkolemOccurrences().get(value);
        cells.remove(cell);
    }

    public void removeOccurrencesForNull(IDatabase database, NullValue value) {
        MainMemoryDB mainMemoryDB = (MainMemoryDB) database;
        mainMemoryDB.getSkolemOccurrences().remove(value);
    }

}
