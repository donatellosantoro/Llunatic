package org.nfunk.jep.function;

public class Utility {

    public static int convertInteger(Object param) {
        return new Double(param.toString()).intValue();
    }

    public static double convertDouble(Object param) {
        return new Double(param.toString());
    }

    public static Object convertParamToNumber(Object param) {
        if (param instanceof Number) {
            return (Number) param;
        }
        if (param instanceof String) {
            try{
                Double value = Double.parseDouble((String)param);
                return value;
            }catch(NumberFormatException ex){}
        }
        return param;
    }
}
