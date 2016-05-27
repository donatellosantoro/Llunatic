package org.nfunk.jep.function;

import java.util.*;
import org.nfunk.jep.*;

public class StartsWith extends PostfixMathCommand {

    public StartsWith() {
        numberOfParameters = 2;
    }

    public void run(Stack stack) throws ParseException {
        checkStack(stack);// check the stack

        String subString = stack.pop().toString();
        String superString = stack.pop().toString();

        Double result = null;

        if (superString.startsWith(subString)) {
            result = new Double(1.0);
        } else {
            result = new Double(0.0);
        }

        stack.push(result);

        return;
    }

    public Object indexof(Object param1, Object param2) throws ParseException {
        return param1.toString().startsWith(param2.toString());
    }

    @Override
    public String getSQLName(Node node, JEP jepExpression) {
        String right = getValue(node.jjtGetChild(0),jepExpression);
        String left = getValue(node.jjtGetChild(1),jepExpression);
//        return "(" + right + "::text LIKE " + left + " || '%')";
        return " (POSITION (" + left + " IN " + right + " ::text) = 1) ";
    }
}
