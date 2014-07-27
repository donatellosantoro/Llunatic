package it.unibas.lunatic.model.chase.chasede.operators;

import it.unibas.lunatic.model.database.CellRef;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.IValue;


public interface IUpdateCell {

    void execute(CellRef cellRef, IValue value, IDatabase database);

}
