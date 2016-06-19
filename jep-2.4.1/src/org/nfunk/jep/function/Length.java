/*****************************************************************************
JEP 2.4.1, Extensions 1.1.1
April 30 2007
(c) Copyright 2007, Nathan Funk and Richard Morris
See LICENSE-*.txt for license information.
 *****************************************************************************/
package org.nfunk.jep.function;

import java.util.*;
import org.nfunk.jep.*;

public class Length extends PostfixMathCommand {

    public Length() {
        numberOfParameters = 1;
    }

    public void run(Stack stack) throws ParseException {
        checkStack(stack);// check the stack

        String value = stack.pop().toString();

        int result;
        
        if (value == null) {
            result = 0;
        } else {
            result = value.length();
        }

        stack.push(result);

        return;
    }

    public Object length(Object param1) throws ParseException {
        return (param1 == null ? 0 : param1.toString().length());
    }

}
