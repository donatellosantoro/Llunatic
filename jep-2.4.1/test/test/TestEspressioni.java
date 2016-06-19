package test;

import junit.framework.TestCase;
import org.nfunk.jep.JEP;
import org.nfunk.jep.Variable;

public class TestEspressioni extends TestCase {

    public TestEspressioni(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    // TODO add test methods here. The name must begin with 'test'. For example:
    // public void testHello() {}
    public void testClonazione1() {
        JEP jepExpression = new JEP();
        jepExpression.setAllowUndeclared(true);
        jepExpression.addStandardConstants();
        jepExpression.addStandardFunctions();

        jepExpression.parseExpression("(a + 1) * (b - 3)");
        System.out.println("JEP: " + jepExpression);
        System.out.println(jepExpression.getTopNode().toLongString());
        System.out.println("Errori: " + jepExpression.getErrorInfo());
        Variable a = jepExpression.getVar("a");
        Variable b = jepExpression.getVar("b");
        jepExpression.setVarValue("a", 1);
        jepExpression.setVarValue("b", 2);
        System.out.println("Value: " + jepExpression.getValueAsObject());

        JEP clone = (JEP) jepExpression.clone();
        Variable aClone = clone.getVar("a");
        aClone.setDescription("a.a");
        Variable bClone = clone.getVar("b");
        bClone.setDescription("b.b");
        System.out.println(clone.getTopNode().toLongString());
        clone.setVarValue("a", 10);
        clone.setVarValue("b", 30);

        System.out.println("Value originale: " + jepExpression.getValueAsObject());
        System.out.println("Value clone: " + clone.getValueAsObject());

        System.out.println("Variable a originale: " + a.toString() + " - " + a.hashCode());
        System.out.println("Variable a clone: " + aClone.toString() + " - " + aClone.hashCode());

        System.out.println("Variable b originale: " + b.toString() + " - " + b.hashCode());
        System.out.println("Variable b clone: " + bClone.toString() + " - " + bClone.hashCode());

        System.out.println("Symbol table: " + jepExpression.getSymbolTable() + " - " + jepExpression.getSymbolTable().hashCode());
        System.out.println("Symbol table clone: " + clone.getSymbolTable() + " - " + clone.getSymbolTable().hashCode());
    }

    public void testClonazione2() {
        JEP jepExpression = new JEP();
        jepExpression.setAllowUndeclared(true);
        jepExpression.addStandardConstants();
        jepExpression.addStandardFunctions();

        jepExpression.parseExpression("log(a) + sin(b)");
        System.out.println("JEP: " + jepExpression);
        System.out.println(jepExpression.getTopNode().toLongString());
        Variable a = jepExpression.getVar("a");
        Variable b = jepExpression.getVar("b");
        jepExpression.setVarValue("a", 1);
        jepExpression.setVarValue("b", 2);
        System.out.println("Value: " + jepExpression.getValueAsObject());

        JEP clone = (JEP) jepExpression.clone();
        Variable aClone = clone.getVar("a");
        aClone.setDescription("a.a");
        Variable bClone = clone.getVar("b");
        bClone.setDescription("b.b");
        System.out.println(clone.getTopNode().toLongString());
        clone.setVarValue("a", 10);
        clone.setVarValue("b", 30);

        System.out.println("Value originale: " + jepExpression.getValueAsObject());
        System.out.println("Value clone: " + clone.getValueAsObject());

        System.out.println("Variable a originale: " + a.toString() + " - " + a.hashCode());
        System.out.println("Variable a clone: " + aClone.toString() + " - " + aClone.hashCode());

        System.out.println("Variable b originale: " + b.toString() + " - " + b.hashCode());
        System.out.println("Variable b clone: " + bClone.toString() + " - " + bClone.hashCode());

        System.out.println("Symbol table: " + jepExpression.getSymbolTable() + " - " + jepExpression.getSymbolTable().hashCode());
        System.out.println("Symbol table clone: " + clone.getSymbolTable() + " - " + clone.getSymbolTable().hashCode());
    }

    public void testClonazione3() {
        JEP jepExpression = new JEP();
        jepExpression.setAllowUndeclared(true);
        jepExpression.addStandardConstants();
        jepExpression.addStandardFunctions();

        jepExpression.parseExpression("append(a, b, c)");
        System.out.println("JEP: " + jepExpression);
        System.out.println(jepExpression.getTopNode().toLongString());
        jepExpression.setVarValue("a", "Uno");
        jepExpression.setVarValue("b", "Due");
        jepExpression.setVarValue("c", "Tre");
        System.out.println("Value: " + jepExpression.getValueAsObject());

        JEP clone = (JEP) jepExpression.clone();
        clone.setVarValue("a", "Dieci");
        clone.setVarValue("b", "Undici");
        clone.setVarValue("c", "Dodici");

        System.out.println("Value originale: " + jepExpression.getValueAsObject());
        System.out.println("Value clone: " + clone.getValueAsObject());

    }

    public void test4() {
        JEP jepExpression = new JEP();
        jepExpression.setAllowUndeclared(true);
        jepExpression.addStandardConstants();
        jepExpression.addStandardFunctions();
//        jepExpression.parseExpression("((paolo + marco + giovanni) * log(franco) ) == 1.0");
        jepExpression.parseExpression("( ( (paolo + marco + giovanni) * log(franco) ) == 1.0 ) && (!luigi)");
        System.out.println("JEP: " + jepExpression);

        Variable a = jepExpression.getVar("paolo");
        a.setDescription("v2.paolo");
        System.out.println("JEP: " + jepExpression);
    }

    public void test5() {
        JEP jepExpression = new JEP();
        jepExpression.setAllowUndeclared(true);
        jepExpression.addStandardConstants();
        jepExpression.addStandardFunctions();
        jepExpression.parseExpression("append(\"abc\", \"def\")");
        System.out.println("JEP: " + jepExpression);
        System.out.println("Value: " + jepExpression.getValueAsObject());
        jepExpression.parseExpression(jepExpression.toString());
        System.out.println("JEP: " + jepExpression);
        System.out.println("Value: " + jepExpression.getValueAsObject());
    }

    public void test6() {
        JEP jepExpression = new JEP();
        jepExpression.setAllowUndeclared(true);
        jepExpression.addStandardConstants();
        jepExpression.addStandardFunctions();
        jepExpression.parseExpression("year > 1980");
        System.out.println("JEP: " + jepExpression);
        jepExpression.setVarValue("year", 1981);
        Object value1 = jepExpression.getValueAsObject();
        System.out.println("Value: " + value1);
        assertEquals(1.0, value1);
        jepExpression.setVarValue("year", 1971);
        Object value2 = jepExpression.getValueAsObject();
        System.out.println("Value: " + value2);
        assertEquals(0.0, value2);        
        jepExpression.setVarValue("year", "1971");
        System.out.println("Value: " + jepExpression.getValueAsObject());
    }

    public void test7() {
        JEP jepExpression = new JEP();
        jepExpression.setAllowUndeclared(true);
        jepExpression.addStandardConstants();
        jepExpression.addStandardFunctions();
        jepExpression.parseExpression("newId()");
        System.out.println("JEP: " + jepExpression);
        System.out.println("Value: " + jepExpression.getValueAsObject());
        System.out.println("Value: " + jepExpression.getValueAsObject());
        System.out.println("Value: " + jepExpression.getValueAsObject());
    }

   public void test8() {
        JEP jepExpression = new JEP();
        jepExpression.setAllowUndeclared(true);
        jepExpression.addStandardConstants();
        jepExpression.addStandardFunctions();
        jepExpression.parseExpression("date()");
        System.out.println("JEP: " + jepExpression);
        System.out.println("Value: " + jepExpression.getValueAsObject());
        System.out.println("Value: " + jepExpression.getValueAsObject());
    }

   public void test9() {
        JEP jepExpression = new JEP();
        jepExpression.setAllowUndeclared(true);
        jepExpression.addStandardConstants();
        jepExpression.addStandardFunctions();
        jepExpression.parseExpression("\"AAA\"");
        System.out.println("JEP: " + jepExpression);
        System.out.println("Value: " + jepExpression.getValueAsObject());
    }
   
   public void test10() {
        JEP jepExpression = new JEP();
        jepExpression.setAllowUndeclared(true);
        jepExpression.addStandardConstants();
        jepExpression.addStandardFunctions();
        jepExpression.parseExpression("split(S, Sep, X)");
        System.out.println("JEP: " + jepExpression);
        jepExpression.setVarValue("S", "Mario, Rossi");
        jepExpression.setVarValue("Sep", ", ");
        jepExpression.setVarValue("X", 1);
        System.out.println(jepExpression.getTopNode().toLongString());
        System.out.println(jepExpression.getErrorInfo());
        System.out.println("Value: " + jepExpression.getValueAsObject());
    }

   public void test11() {
        JEP jepExpression = new JEP();
        jepExpression.setAllowUndeclared(true);
        jepExpression.addStandardConstants();
        jepExpression.addStandardFunctions();
        jepExpression.parseExpression("substring(\"prova\", 2)");
        System.out.println("JEP: " + jepExpression);
        System.out.println(jepExpression.getTopNode().toLongString());
        System.out.println(jepExpression.getErrorInfo());
        System.out.println("Value: " + jepExpression.getValueAsObject());
    }

}
