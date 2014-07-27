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

public class Split extends PostfixMathCommand {

    public Split() {
        numberOfParameters = 3;
    }

    public void run(Stack stack) throws ParseException {
        checkStack(stack);// check the stack

        int position = Utility.convertInteger(stack.pop().toString());
        String separators = stack.pop().toString();
        String string = stack.pop().toString();

        String result = splitString(string, separators, position);
        stack.push(result);

        return;
    }

    private String splitString(String string, String separators, int position) throws ParseException {
        StringTokenizer tokenizer = new StringTokenizer(string, separators);
        
        if (position > tokenizer.countTokens()) {
            throw new ParseException("Token n. " + position + " does not exist in string " + string + " with separators " + separators);
        }
        
        String result = null;
        int i = -1;
        while (tokenizer.hasMoreTokens() && i < position) {
            result = tokenizer.nextToken();
            i++;
        }
        return result;
    }
    
    public Object split(Object param1, Object param2, Object param3) throws ParseException {
        return splitString(param1.toString(), param2.toString(), Integer.parseInt(param3.toString()));
    }

    public String getXQueryName() {
        return "fn:tokenize";
    }
    
}
