/*****************************************************************************
JEP 2.4.1, Extensions 1.1.1
April 30 2007
(c) Copyright 2007, Nathan Funk and Richard Morris
See LICENSE-*.txt for license information.
 *****************************************************************************/
package org.nfunk.jep.function;

import java.util.*;
import org.nfunk.jep.*;
import org.nfunk.jep.type.*;

public class Substring extends PostfixMathCommand {

    public Substring() {
        numberOfParameters = 2;
    }

    public void run(Stack stack) throws ParseException {
        checkStack(stack);// check the stack

        int position = Utility.convertInteger(stack.pop().toString());
        String superString = stack.pop().toString();

        String result = superString.substring(position);

        stack.push(result);

        return;
    }

    public Object substring(Object param1, Object param2) throws ParseException {
        return param1.toString().substring(Integer.parseInt(param2.toString()));
    }

}
