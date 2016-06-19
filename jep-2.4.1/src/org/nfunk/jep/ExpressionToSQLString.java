package org.nfunk.jep;

public class ExpressionToSQLString {

    public String toSQLString(JEP jepExpression) {
        try {
            ToSQLStringVisitor visitor = new ToSQLStringVisitor(jepExpression);
            if (jepExpression == null || jepExpression.getTopNode() == null) {
                return null;
            }
            jepExpression.getTopNode().jjtAccept(visitor, null);
            return visitor.getResult();
        } catch (ParseException ex) {
            return null;
        }
    }
}

class ToSQLStringVisitor implements ParserVisitor {

    private JEP jepExpression;
    private StringBuffer result = new StringBuffer();

    public ToSQLStringVisitor(JEP jepExpression) {
        this.jepExpression = jepExpression;
    }

    public String getResult() {
        return result.toString();
    }

    public Object visit(SimpleNode node, Object data) throws ParseException {
        return null;
    }

    public Object visit(ASTStart node, Object data) throws ParseException {
        result.append("START").append(node).append("(");
        node.childrenAccept(this, data);
        result.append(")");
        return null;
    }

    public Object visit(ASTFunNode node, Object data) throws ParseException {
        if (isOperator(node)) {
            String sqlName = node.getSQLName(node, jepExpression);
            if (sqlName == null) {
                sqlName = node.getOperator().getName();
            }
            if (isUnaryOperator(node)) {
                result.append(sqlName).append("(");
                Node child = node.jjtGetChild(0);
                child.jjtAccept(this, data);
                result.append(")");
            } else {
                result.append("(");
                for (int i = 0; i < node.jjtGetNumChildren(); i++) {
                    Node child = node.jjtGetChild(i);
                    child.jjtAccept(this, data);
                    if (i < node.jjtGetNumChildren() - 1) {
                        result.append(" ").append(sqlName).append(" ");
                    }
                }
                result.append(")");
            }
        } else if (isFunction(node)) {
            String sql = node.getSQLName(node, jepExpression);
            if (sql != null) {
                result.append(sql);
                return null;
            }
            result.append(node.getName()).append("(");
            for (int i = 0; i < node.jjtGetNumChildren(); i++) {
                Node child = node.jjtGetChild(i);
                child.jjtAccept(this, data);
                if (i < node.jjtGetNumChildren() - 1 && i != node.jjtGetNumChildren() - 2) {
                    result.append(", ");
                }
                if (i == node.jjtGetNumChildren() - 2) {
                    result.append(", ");
                }
            }
            result.append(")");
        }
        return null;
    }

    public Object visit(ASTVarNode node, Object data) throws ParseException {
        Variable var = jepExpression.getVar(node.getVarName());
        String varDescription = null;
        varDescription = var.getDescription().toString();
        result.append(varDescription);
        return null;
    }

    public Object visit(ASTConstant node, Object data) throws ParseException {
        String valueString = node.getValue().toString();
        if (((ASTConstant) node).getType() == ASTConstant.STRING) {
            valueString = valueString.replaceAll("'", "''");
            valueString = "\'" + valueString + "\'";
        } else {
            Double doubleValue = Double.parseDouble(valueString);
            int intValue = doubleValue.intValue();
            if (doubleValue - intValue == 0) {
                valueString = "" + intValue;
            }
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
