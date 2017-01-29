/**
 * ***************************************************************************
 * JEP 2.4.1, Extensions 1.1.1
 * April 30 2007
 * (c) Copyright 2007, Nathan Funk and Richard Morris
 * See LICENSE-*.txt for license information.
 ****************************************************************************
 */
package org.nfunk.jep.function;

import java.util.*;
import org.nfunk.jep.*;
import org.nfunk.jep.type.*;

/**
 * Append function. Supports any number of parameters although typically only 2
 * parameters are used.
 */
public class Append extends PostfixMathCommand {

    public Append() {
        numberOfParameters = -1;
    }

    public void run(Stack stack) throws ParseException {
        checkStack(stack);// check the stack

        StringBuffer result = new StringBuffer(stack.pop().toString());
        String param;
        int i = 1;

        // repeat summation for each one of the current parameters
        while (i < curNumberOfParameters) {
            // get the parameter from the stack
            param = stack.pop().toString();
            result.insert(0, param);
            i++;
        }

        stack.push(result.toString());

        return;
    }

    public Object append(Object param1, Object param2) throws ParseException {
        return param1.toString() + param2.toString();
    }

    @Override
    public String getSQLName(Node node, JEP jepExpression) {
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            String value = getValue(node.jjtGetChild(i), jepExpression);
            sb.append(value);
            if (i < node.jjtGetNumChildren() - 1) {
                sb.append(" || ");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public String getXQueryName() {
        return "fn:concat";
    }

}
