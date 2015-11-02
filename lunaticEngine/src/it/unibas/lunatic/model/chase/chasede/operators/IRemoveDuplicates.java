package it.unibas.lunatic.model.chase.chasede.operators;

import it.unibas.lunatic.Scenario;
import speedy.model.database.IDatabase;

public interface IRemoveDuplicates {

    public void removeDuplicatesModuloOID(IDatabase database, Scenario scenario);

}
