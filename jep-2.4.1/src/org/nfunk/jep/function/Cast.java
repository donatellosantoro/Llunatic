package org.nfunk.jep.function;

import java.util.*;
import org.nfunk.jep.*;

public class Cast extends PostfixMathCommand {

    public Cast() {
        numberOfParameters = 2;
    }

    public void run(Stack stack) throws ParseException {
        checkStack(stack);// check the stack
        String stringValue = stack.pop().toString();
//        String stringType = stack.pop().toString();
        stack.push(stringValue);
        return;
    }

    public Object indexof(Object param1, Object param2) throws ParseException {
        return param1.toString().startsWith(param2.toString());
    }

    @Override
    public String getSQLName(Node node, JEP jepExpression) {
        String stringValue = getValue(node.jjtGetChild(0), jepExpression);
        String stringType = getValue(node.jjtGetChild(1), jepExpression);
        return "cast(" + stringValue + " as " + stringType + ")";
    }
}
