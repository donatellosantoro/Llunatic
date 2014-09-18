package it.unibas.lunatic.model.algebra;

import it.unibas.lunatic.utility.LunaticUtility;
import it.unibas.lunatic.model.algebra.operators.*;
import it.unibas.lunatic.model.database.*;
import it.unibas.lunatic.model.database.mainmemory.datasource.IntegerOIDGenerator;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CartesianProduct extends AbstractOperator {

    private static Logger logger = LoggerFactory.getLogger(CartesianProduct.class);

    public String getName() {
        return "CARTESIAN PRODUCT";
    }

    public ITupleIterator execute(IDatabase source, IDatabase target) {
        List<Tuple> result = new ArrayList<Tuple>();
        ITupleIterator leftTuples = children.get(0).execute(source, target);
        ITupleIterator rightTuples = children.get(1).execute(source, target);
        materializeResult(leftTuples, rightTuples, result);
        if (logger.isDebugEnabled()) logger.debug(getName() + " - Result: \n" + LunaticUtility.printCollection(result));
        leftTuples.close();
        rightTuples.close();
        return new ListTupleIterator(result);
    }

    public void materializeResult(ITupleIterator leftTuples, ITupleIterator rightTuples, List<Tuple> result) {
        while (leftTuples.hasNext()) {
            Tuple leftTuple = leftTuples.next();
            if (logger.isDebugEnabled()) logger.debug("Left tuple in cartesian product: " + leftTuple);
            while (rightTuples.hasNext()) {
                Tuple rightTuple = rightTuples.next();
                if (logger.isDebugEnabled()) logger.debug("Right tuple in cartesian product: " + rightTuple);
                Tuple joinedTuple = joinTuples(leftTuple, rightTuple);
                result.add(joinedTuple);
            }
            rightTuples.reset();
        }
        leftTuples.reset();
    }

    private Tuple joinTuples(Tuple firstTuple, Tuple secondTuple) {
        Tuple joinedTuple = firstTuple.clone();
        joinedTuple.setOid(new TupleOID(IntegerOIDGenerator.getNextOID()));
        joinedTuple.getCells().addAll(secondTuple.clone().getCells());
        if (logger.isDebugEnabled()) logger.trace("Joined tuple: \n" + joinedTuple);
        return joinedTuple;
    }

    public void accept(IAlgebraTreeVisitor visitor) {
        visitor.visitCartesianProduct(this);
    }

    public List<AttributeRef> getAttributes(IDatabase source, IDatabase target) {
        List<AttributeRef> result = new ArrayList<AttributeRef>();
        result.addAll(this.children.get(0).getAttributes(source, target));
        result.addAll(this.children.get(1).getAttributes(source, target));
        return result;
    }
}
