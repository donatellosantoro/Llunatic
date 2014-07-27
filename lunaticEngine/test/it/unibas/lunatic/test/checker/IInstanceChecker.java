package it.unibas.lunatic.test.checker;

import it.unibas.lunatic.model.database.mainmemory.datasource.INode;

public interface IInstanceChecker {

    public void checkInstance(INode instance, String expectedInstanceFile) throws Exception ;

}
