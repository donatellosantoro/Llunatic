package it.unibas.lunatic.model.chase.chasede.operators;

import speedy.model.database.CellRef;
import speedy.model.database.IDatabase;
import speedy.model.database.IValue;

public interface IUpdateCell {

    void execute(CellRef cellRef, IValue value, IDatabase database);

}
