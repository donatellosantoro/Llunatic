/*****************************************************************************
JEP 2.4.1, Extensions 1.1.1
April 30 2007
(c) Copyright 2007, Nathan Funk and Richard Morris
See LICENSE-*.txt for license information.
 *****************************************************************************/
package org.nfunk.jep.function;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Date extends PostfixMathCommand {
        
    public Date() {
        numberOfParameters = 0;
    }

    public void run(Stack inStack) {
        try {
            inStack.push(date().toString());
            return;
        } catch (ParseException ex) {
            inStack.push(null);
        }
    }

    public Object date() throws ParseException {
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
        return dateFormat.format(new java.util.Date());
    }
    
    public String getXQueryName() {
        return "fn:current-date";
    }
}
