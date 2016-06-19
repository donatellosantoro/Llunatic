package it.unibas.lunatic.gui.node;

public class TupleGenerationStrategy {

    private static TupleGenerationStrategy instance;

    public static TupleGenerationStrategy getInstance() {
        if (instance == null) {
            instance = new TupleGenerationStrategy();
        }
        return instance;
    }

    public ITupleFactory getPagedBatchFactory(TableNode tableNode, int offset, int limit) {
        return new PagedBatchTupleFactory(tableNode, offset, limit);
    }

//    public ITupleFactory getFactory(TableNode tableNode) {
//        return getBatchFactory(tableNode);
//    }

    public ITupleFactory getNetbeansFactory(TableNode tableNode) {
        return new StandardTupleFactory(tableNode);
    }

//    public ITupleFactory getBatchFactory(TableNode tableNode) {
//        return new BatchTupleFactory(tableNode);
//    }

    public ITupleFactory getPagedFactory(TableNode tableNode, int offset, int pageSize) {
        return getPagedBatchFactory(tableNode, offset, pageSize);
    }
}
