/*****************************************************************************
 JEP 2.4.1, Extensions 1.1.1
 April 30 2007
 (c) Copyright 2007, Nathan Funk and Richard Morris
 See LICENSE-*.txt for license information.
 *****************************************************************************/
package org.nfunk.jep.function;

import java.util.*;
import org.nfunk.jep.*;

public class IsNull extends PostfixMathCommand {

    public IsNull() {
        numberOfParameters = 1;
    }

    public void run(Stack stack) throws ParseException {
        checkStack(stack);// check the stack

        String value = stack.pop().toString();

        Double result = null;

        if (value.equals("NULL") || value.startsWith("_SK-")) {
            result = new Double(1.0);
        } else {
            result = new Double(0.0);
        }

        stack.push(result);

        return;
    }

    public Object isNull(Object param1) throws ParseException {
        return param1.toString().equals("NULL") || param1.toString().startsWith("_SK-");
    }

    @Override
    public String getSQLName(Node node, JEP jepExpression) {
        String value = getValue(node.jjtGetChild(0), jepExpression);
        return value + " IS NULL";
    }
}
