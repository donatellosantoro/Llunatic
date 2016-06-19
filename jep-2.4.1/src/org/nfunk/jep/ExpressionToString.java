package org.nfunk.jep;

import java.util.StringTokenizer;

public class ExpressionToString {

    public String toString(JEP jepExpression) {
        try {
            ToStringVisitor visitor = new ToStringVisitor(jepExpression);
            if (jepExpression == null || jepExpression.getTopNode() == null) {
                return null;
            }
            jepExpression.getTopNode().jjtAccept(visitor, null);
            return visitor.getResult();
        } catch (ParseException ex) {
            return null;
        }
    }

    public String toVariableDelimitedString(JEP jepExpression) {
        try {
            ToStringVisitor visitor = new ToStringVisitor(jepExpression, false, false, true, false);
            if (jepExpression == null || jepExpression.getTopNode() == null) {
                return null;
            }
            jepExpression.getTopNode().jjtAccept(visitor, null);
            return visitor.getResult();
        } catch (ParseException ex) {
            return null;
        }
    }

    public String toStringWithAbsolutePaths(JEP jepExpression) {
        try {
            ToStringVisitor visitor = new ToStringVisitor(jepExpression, false, false, true);
            if (jepExpression == null || jepExpression.getTopNode() == null) {
                return null;
            }
            jepExpression.getTopNode().jjtAccept(visitor, null);
            return visitor.getResult();
        } catch (ParseException ex) {
            return null;
        }
    }

    public String toStringWithSlashes(JEP jepExpression) {
        try {
            ToStringVisitor visitor = new ToStringVisitor(jepExpression, true, true);
            if (jepExpression == null || jepExpression.getTopNode() == null) {
                return null;
            }
            jepExpression.getTopNode().jjtAccept(visitor, null);
            String result = visitor.getResult();
            result = result.replaceAll("==", "=");
            return result;
        } catch (ParseException ex) {
            return null;
        }
    }

    public String toStringWithDollarsAndUnderscore(JEP jepExpression) {
        try {
            ToStringVisitor visitor = new ToStringVisitor(jepExpression, false, true);
            if (jepExpression == null || jepExpression.getTopNode() == null) {
                return null;
            }
            jepExpression.getTopNode().jjtAccept(visitor, null);
            String result = visitor.getResult();
            if (result.startsWith("(") && result.endsWith(")")) {
                result = result.substring(1, result.length() - 1);
//                result = result.replaceAll("==", "=");
            }
            return result;
        } catch (ParseException ex) {
            return null;
        }
    }

    public String toStringWithDollarsAndBrackets(JEP jepExpression) {
        try {
            ToStringVisitor visitor = new ToStringVisitor(jepExpression, false, true, false, false, true);
            if (jepExpression == null || jepExpression.getTopNode() == null) {
                return null;
            }
            jepExpression.getTopNode().jjtAccept(visitor, null);
            String result = visitor.getResult();
            if (result.startsWith("(") && result.endsWith(")")) {
                result = result.substring(1, result.length() - 1);
//                result = result.replaceAll("==", "=");
            }
            return result;
        } catch (ParseException ex) {
            return null;
        }
    }
}

class ToStringVisitor implements ParserVisitor {

    private JEP jepExpression;
    private StringBuffer result = new StringBuffer();
    private boolean withSlashes;
    private boolean withDollar;
    private boolean withVariableDelimiters;
    private boolean absolutePaths;
    private boolean useBracketsForFunctions;

    public ToStringVisitor(JEP jepExpression) {
        this.jepExpression = jepExpression;
    }

    public ToStringVisitor(JEP jepExpression, boolean withSlashes, boolean withDollar) {
        this.jepExpression = jepExpression;
        this.withSlashes = withSlashes;
        this.withDollar = withDollar;
    }

    public ToStringVisitor(JEP jepExpression, boolean withSlashes, boolean withDollar, boolean absolutePaths) {
        this.jepExpression = jepExpression;
        this.withSlashes = withSlashes;
        this.withDollar = withDollar;
        this.absolutePaths = absolutePaths;
    }

    public ToStringVisitor(JEP jepExpression, boolean withSlashes, boolean withDollar, boolean withVariableDelimiters, boolean absolutePaths) {
        this.jepExpression = jepExpression;
        this.withSlashes = withSlashes;
        this.withDollar = withDollar;
        this.withVariableDelimiters = withVariableDelimiters;
        this.absolutePaths = absolutePaths;
    }

    public ToStringVisitor(JEP jepExpression, boolean withSlashes, boolean withDollar, boolean withVariableDelimiters, boolean absolutePaths, boolean useBracketsForFunctions) {
        this.jepExpression = jepExpression;
        this.withSlashes = withSlashes;
        this.withDollar = withDollar;
        this.withVariableDelimiters = withVariableDelimiters;
        this.absolutePaths = absolutePaths;
        this.useBracketsForFunctions = useBracketsForFunctions;
    }

    public String getResult() {
        return result.toString();
    }

    public Object visit(SimpleNode node, Object data) throws ParseException {
        return null;
    }

    public Object visit(ASTStart node, Object data) throws ParseException {
        result.append("START" + node + "(");
        node.childrenAccept(this, data);
        result.append(")");
        return null;
    }

    public Object visit(ASTFunNode node, Object data) throws ParseException {
        if (isOperator(node)) {
            if (isUnaryOperator(node)) {
                result.append(node.getOperator().getName() + "(");
                Node child = node.jjtGetChild(0);
                child.jjtAccept(this, data);
                result.append(")");
            } else {
                result.append("(");
                for (int i = 0; i < node.jjtGetNumChildren(); i++) {
                    Node child = node.jjtGetChild(i);
                    child.jjtAccept(this, data);
                    if (i < node.jjtGetNumChildren() - 1) {
                        result.append(" " + node.getOperator().getName() + " ");
                    }
                }
                result.append(")");
            }
        } else if (isFunction(node)) {
            if (!withSlashes) {
                if (withDollar && !useBracketsForFunctions) {
                    result.append("_");
                }
                if (withDollar && useBracketsForFunctions) {
                    result.append("{");
                }
                result.append(node.getName() + "(");
            } else {
                result.append(node.getXQueryName() + "(");
            }
            for (int i = 0; i < node.jjtGetNumChildren(); i++) {
                Node child = node.jjtGetChild(i);
                child.jjtAccept(this, data);
                if (i < node.jjtGetNumChildren() - 1 && i != node.jjtGetNumChildren() - 2) {
                    result.append(", ");
                }
                if (i == node.jjtGetNumChildren() - 2) {
                    if (this.withSlashes && node.getXQueryName().equals("fn:tokenize")) {
                        result.append(")[");
                    } else {
                        result.append(", ");
                    }
                }
            }
            if (this.withSlashes && node.getXQueryName().equals("fn:tokenize")) {
                result.append("]");
            } else {
                result.append(")");
                if (withDollar && useBracketsForFunctions) {
                    result.append("}");
                }
            }
        }
        return null;
    }

    public Object visit(ASTVarNode node, Object data) throws ParseException {
        Variable var = jepExpression.getVar(node.getVarName());
        String varDescription = null;
        if (!absolutePaths) {
            Object description = var.getDescription();
            varDescription = description.toString();
        } else {
            varDescription = var.getOriginalDescription().toString();
        }
        if (withDollar) {
            varDescription = "$" + varDescription;
        }
        if (withVariableDelimiters) {
            varDescription = "§" + varDescription + "#";
        }
        if (withSlashes) {
            varDescription = varDescription.replaceAll("\\.", "/") + "/text()";
            varDescription = removeFirstStepFromPathWithSlashes(varDescription);
        }
        result.append(varDescription);
        return null;
    }

    private String removeFirstStepFromPathWithSlashes(String originalPath) {
        StringTokenizer tokenizer = new StringTokenizer(originalPath, "/", true);
        String correctPath = "";
        int tokenNumber = 0;
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (tokenNumber != 2 && tokenNumber != 3) {
                correctPath += token;
            }
            tokenNumber++;
        }
        return correctPath;
    }

    public Object visit(ASTConstant node, Object data) throws ParseException {
        String valueString = node.getValue().toString();
        if (((ASTConstant) node).getType() == ASTConstant.STRING) {
            valueString = "\"" + valueString + "\"";
        } else {
            Double doubleValue = Double.parseDouble(valueString);
            int intValue = doubleValue.intValue();
            if (doubleValue - intValue == 0) {
                if (withSlashes) {
                    intValue++;
                }
                valueString = "" + intValue;
            }
        }
        if (withVariableDelimiters) {
            valueString = "§" + valueString + "#";
        }
        result.append(valueString);
        return null;
    }

    private boolean isFunction(ASTFunNode node) {
        return node.getPFMC() != null;
    }

    private boolean isOperator(ASTFunNode node) {
        return node.getOperator() != null;
    }

    private boolean isUnaryOperator(ASTFunNode node) {
        return node.jjtGetNumChildren() == 1;
    }
}
