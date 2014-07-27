package org.nfunk.jep.function;

import java.util.*;
import org.nfunk.jep.*;
import org.nfunk.jep.type.*;

public class IndexOf extends PostfixMathCommand {

    public IndexOf() {
        numberOfParameters = 2;
    }

    public void run(Stack stack) throws ParseException {
        checkStack(stack);// check the stack

        String subString = stack.pop().toString();
        String superString = stack.pop().toString();

        int result = superString.indexOf(subString); 

        stack.push(result);

        return;
    }

    public Object indexof(Object param1, Object param2) throws ParseException {
        return param1.toString().indexOf(param2.toString());
    }
}
