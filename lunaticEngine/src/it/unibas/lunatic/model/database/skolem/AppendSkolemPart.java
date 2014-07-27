package it.unibas.lunatic.model.database.skolem;

import it.unibas.lunatic.model.database.Tuple;
import java.util.ArrayList;
import java.util.List;

public class AppendSkolemPart implements ISkolemPart {

    private String leftDelimiter;
    private String rightDelimiter;
    private String oddSeparator;
    private String evenSeparator;
    private List<ISkolemPart> children = new ArrayList<ISkolemPart>();

    public AppendSkolemPart() {
    }

    public AppendSkolemPart(String leftDelimiter, String rightDelimiter, String oddSeparator, String evenSeparator) {
        this.leftDelimiter = leftDelimiter;
        this.rightDelimiter = rightDelimiter;
        this.oddSeparator = oddSeparator;
        this.evenSeparator = evenSeparator;
    }

    public AppendSkolemPart(String leftDelimiter, String rightDelimiter, String separator) {
        this(leftDelimiter, rightDelimiter, separator, separator);
    }

    public String getValue(Tuple sourceTuple) {
        StringBuilder result = new StringBuilder();
        List<String> values = new ArrayList<String>();
        for (ISkolemPart child : children) {
            values.add(child.getValue(sourceTuple));
        }
        result.append(printString(leftDelimiter));
        for (int i = 0; i < values.size(); i++) {
            result.append(values.get(i));
            if (i != values.size() - 1) {
                result.append(getSeparator(i));
            }
        }
        result.append(printString(rightDelimiter));
        return result.toString();
    }

    private String printString(String string) {
        if (string == null) {
            return "";
        }
        return string;
    }

    private String getSeparator(int i) {
        if (i % 2 == 0) {
            return printString(oddSeparator);
        } else {
            return printString(evenSeparator);
        }
    }

    public List<ISkolemPart> getChildren() {
        return this.children;
    }

    public void addChild(ISkolemPart child) {
        this.children.add(child);
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(printString(leftDelimiter));
        for (int i = 0; i < children.size(); i++) {
            result.append(children.get(i));
            if (i != children.size() - 1) {
                result.append(getSeparator(i));
            }
        }
        result.append(printString(rightDelimiter));
        return result.toString();
    }

}
