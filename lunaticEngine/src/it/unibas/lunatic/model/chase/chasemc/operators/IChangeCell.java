package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import speedy.model.database.IDatabase;

public interface IChangeCell {

    //NOTICE: Call flush after inserts
    void changeCells(CellGroup cellGroup, IDatabase deltaDB, String stepId, Scenario scenario);

    void flush(IDatabase database);

}
