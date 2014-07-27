/*****************************************************************************
JEP 2.4.1, Extensions 1.1.1
April 30 2007
(c) Copyright 2007, Nathan Funk and Richard Morris
See LICENSE-*.txt for license information.
 *****************************************************************************/
package org.nfunk.jep.function;

import java.text.ParseException;
import java.util.Stack;

public class NewId extends PostfixMathCommand {

    private static int counter = 0;
        

    public NewId() {
        numberOfParameters = 0;
    }

    public void run(Stack inStack) {
        inStack.push(counter++);
        return;
    }

    public Object abs() throws ParseException {
        return counter++;
    }
    
    public String getXQueryName() {
        return "local:newId";
    }
}
