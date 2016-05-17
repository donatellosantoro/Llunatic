package it.unibas.lunatic.parser.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ParserException;
import it.unibas.lunatic.model.dependency.*;
import it.unibas.lunatic.parser.ParserOutput;
import it.unibas.lunatic.parser.output.DependenciesCFLexer;
import it.unibas.lunatic.parser.output.DependenciesCFParser;
import it.unibas.lunatic.utility.DependencyUtility;
import java.util.ArrayList;
import java.util.List;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.SpeedyConstants;
import speedy.model.database.Attribute;
import speedy.model.database.IDatabase;
import speedy.model.database.ITable;
import speedy.utility.DBMSUtility;

@SuppressWarnings("unchecked")
public class ParseDependenciesCF {

    public final static String NULL = "#NULL#";
    private static final Logger logger = LoggerFactory.getLogger(ParseDependenciesCF.class);

    private final ParserOutput parserOutput = new ParserOutput();
    private Scenario scenario;

    public ParserOutput getParserOutput() {
        return parserOutput;
    }

    public void generateDependencies(String text, Scenario scenario) throws Exception {
        try {
            this.scenario = scenario;
            DependenciesCFLexer lex = new DependenciesCFLexer(new ANTLRStringStream(text));
            CommonTokenStream tokens = new CommonTokenStream(lex);
            DependenciesCFParser g = new DependenciesCFParser(tokens);
            try {
                g.setGenerator(this);
                g.prog();
            } catch (RecognitionException ex) {
                logger.error("Unable to load mapping task: " + ex.getMessage());
                throw new ParserException(ex);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage());
            throw new ParserException(e);
        }
    }

    public String findAttributeName(String tableName, int attributePosition, boolean inPremise, boolean stTGD) {
        IDatabase database = scenario.getTarget();
        if (inPremise && stTGD) {
            database = scenario.getSource();
        }
        ITable table = database.getTable(tableName);
        List<Attribute> tableAttributes = getAttributesWithNoOIDs(table.getAttributes());
        return tableAttributes.get(attributePosition).getName();
    }

    private List<Attribute> getAttributesWithNoOIDs(List<Attribute> attributes) {
        List<Attribute> result = new ArrayList<Attribute>();
        for (Attribute attribute : attributes) {
            if (attribute.getName().equals(SpeedyConstants.OID)) {
                continue;
            }
            result.add(attribute);
        }
        return result;
    }

    public void addSTTGD(Dependency d) {
        parserOutput.getStTGDs().add(d);
    }

    public void addExtTGD(Dependency d) {
        parserOutput.geteTGDs().add(d);
    }

    public void addDC(Dependency d) {
        if (!(d.getConclusion() instanceof NullFormula)) {
            throw new ParserException("DC must have no conclusion");
        }
        parserOutput.getDcs().add(d);
    }

    public void addEGD(Dependency d) {
        parserOutput.getEgds().add(d);
    }

    public void addExtEGD(Dependency d) {
        parserOutput.geteEGDs().add(d);
    }

    public void addDEDSTTGD(DED ded) {
        parserOutput.getDedstTGDs().add(ded);
    }

    public void addDEDExtTGD(DED ded) {
        parserOutput.getDedeTGDs().add(ded);
    }

    public void addDEDExtEGD(DED ded) {
        parserOutput.getDedegds().add(ded);
    }

    public void addQuery(Dependency query) {
        parserOutput.getQueries().add(query);
    }

    public String clean(String expressionString) {
        return DependencyUtility.clean(expressionString);
    }

    public String cleanTableName(String tableName) {
        return DBMSUtility.cleanTableName(tableName);
    }

    public String convertSymbol(String symbol) {
        if (scenario.getValueEncoder() == null) {
            return "\"" + symbol + "\"";
        }
        return scenario.getValueEncoder().encode(symbol);
    }

}
