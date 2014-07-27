package it.unibas.lunatic.model.database;

import it.unibas.lunatic.model.algebra.operators.ITupleIterator;
import it.unibas.lunatic.model.database.lazyloading.ITupleLoader;
import java.util.Iterator;
import java.util.List;

public interface ITable {

    public String getName();

    public List<Attribute> getAttributes();

    public ITupleIterator getTupleIterator();

    public Iterator<ITupleLoader> getTupleLoaderIterator();

    public String printSchema(String indent);

    public String toString(String indent);

    public String toStringWithSort(String indent);

    public String toShortString();

    public int getSize();

    public ITupleIterator getTupleIterator(int offset, int limit);

    public String getPaginationQuery(int offset, int limit);
}
