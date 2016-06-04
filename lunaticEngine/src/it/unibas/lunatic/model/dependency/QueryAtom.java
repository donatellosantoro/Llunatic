package it.unibas.lunatic.model.dependency;

import java.util.ArrayList;
import java.util.List;
import speedy.model.database.TableAlias;
import speedy.model.expressions.Expression;
import speedy.utility.SpeedyUtility;

public class QueryAtom extends RelationalAtom implements IFormulaAtom, Cloneable {

    private IFormula formula;
    private String queryId;
    private List<FormulaAttribute> attributes = new ArrayList<FormulaAttribute>();

    public QueryAtom(String queryId) {
        super(null);
        this.queryId = queryId;
    }

    public String getQueryId() {
        return queryId;
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    public IFormula getFormula() {
        return formula;
    }

    public void setFormula(IFormula formula) {
        this.formula = formula;
    }

    public List<FormulaAttribute> getAttributes() {
        return attributes;
    }

    public void addAttribute(FormulaAttribute attribute) {
        this.attributes.add(attribute);
    }

    public void addVariable(FormulaVariable variable) {
        throw new UnsupportedOperationException("Not supported.");
    }

    public List<FormulaVariable> getVariables() {
        throw new UnsupportedOperationException("Not supported.");
    }

    public Expression getExpression() {
        return null;
    }

    public void setExpression(Expression expression) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public QueryAtom clone() {
        // atoms are superficially cloned; see PositiveFormula.clone() for deep cloning
        try {
            QueryAtom c = (QueryAtom) super.clone();
            c.attributes = new ArrayList<FormulaAttribute>();
            for (FormulaAttribute formulaAttribute : this.attributes) {
                c.attributes.add(formulaAttribute.clone());
            }
            return c;
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(queryId).append("(");
        for (FormulaAttribute attribute : attributes) {
            result.append(attribute.getValue()).append(", ");
        }
        result.deleteCharAt(result.length() - 1);
        result.deleteCharAt(result.length() - 1);
        result.append(")");
        return result.toString();
    }

    @Override
    public String toSaveString() {
        StringBuilder result = new StringBuilder();
        result.append(queryId).append("(");
        for (FormulaAttribute attribute : attributes) {
            result.append(attribute.toSaveString()).append(", ");
        }
        result.deleteCharAt(result.length() - 1);
        result.deleteCharAt(result.length() - 1);
        result.append(")");
        return result.toString();
    }

    @Override
    public String toCFString() {
        StringBuilder result = new StringBuilder();
        result.append(queryId).append("(");
        for (FormulaAttribute attribute : attributes) {
            result.append(attribute.toCFString()).append(", ");
        }
        SpeedyUtility.removeChars(", ".length(), result);
        result.append(")");
        return result.toString();
    }

    @Override
    public String toLongString() {
        StringBuilder result = new StringBuilder();
        result.append(queryId).append("\n");
        for (FormulaAttribute attribute : attributes) {
            result.append("\t").append(attribute.toLongString()).append("\n");
        }
        return result.toString();
    }
}
