package it.unibas.lunatic.model.chase.chasemc.partialorder.valuecomparator;

import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.database.IValue;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateComparator extends StringComparator {

    private DateFormat dateFormat;

    public DateComparator(String pattern) {
        dateFormat = new SimpleDateFormat(pattern);
    }

    @Override
    public int compare(IValue v1, IValue v2) {
        try {
            Date d1 = dateFormat.parse(v1.toString());
            Date d2 = dateFormat.parse(v2.toString());
            return d1.compareTo(d2);
        } catch (ParseException ex) {
            throw new ChaseException("Unable to parse float value " + ex.getLocalizedMessage());
        }
    }

    @Override
    public String toString() {
        return "DateComparator(" + getSort() + ")";
    }
}
