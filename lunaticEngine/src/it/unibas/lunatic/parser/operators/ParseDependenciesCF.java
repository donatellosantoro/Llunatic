package it.unibas.lunatic.parser.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ParserException;
import it.unibas.lunatic.model.dependency.*;
import it.unibas.lunatic.parser.ParserOutput;
import it.unibas.lunatic.parser.output.DependenciesCFLexer;
import it.unibas.lunatic.parser.output.DependenciesCFParser;
import it.unibas.lunatic.model.dependency.operators.DependencyUtility;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.SpeedyConstants;
import speedy.model.database.Attribute;
import speedy.model.database.IDatabase;
import speedy.model.database.ITable;
import speedy.model.thread.IBackgroundThread;
import speedy.model.thread.ThreadManager;
import speedy.utility.DBMSUtility;

@SuppressWarnings("unchecked")
public class ParseDependenciesCF {

    public final static String NULL = "#NULL#";
    private static final Logger logger = LoggerFactory.getLogger(ParseDependenciesCF.class);

    private final ParserOutput parserOutput = new ParserOutput();
    private Scenario scenario;
    private Map<TableAndPosition, String> attributeNames = Collections.synchronizedMap(new HashMap<TableAndPosition, String>());

    public ParserOutput getParserOutput() {
        return parserOutput;
    }

    public void generateDependencies(String text, Scenario scenario) throws Exception {
        try {
            this.scenario = scenario;
            initAttributeNames();
            DependenciesCFLexer lex = new DependenciesCFLexer(new ANTLRInputStream(new ByteArrayInputStream(text.getBytes())));
            CommonTokenStream tokens = new CommonTokenStream(lex);
            DependenciesCFParser g = new DependenciesCFParser(tokens);
            g.getInterpreter().setPredictionMode(PredictionMode.SLL);
            g.setErrorHandler(new BailErrorStrategy());
            g.setGenerator(this);
            try {
                g.prog();  // STAGE 1
            } catch (Exception e) {
                tokens.reset(); // rewind input stream
                g.reset();
                g.getInterpreter().setPredictionMode(PredictionMode.LL);
                g.setErrorHandler(new DefaultErrorStrategy());
                try {
                    g.prog();  // STAGE 2
                } catch (RecognitionException ex) {
                    logger.error("Unable to load mapping task: " + ex.getMessage());
                    throw new ParserException(ex);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage());
            throw new ParserException(e);
        }
    }

    private void initAttributeNames() {
        long start = new Date().getTime();
        initAttributeNamesForDatabase(scenario.getSource(), true);
        initAttributeNamesForDatabase(scenario.getTarget(), false);
        long end = new Date().getTime();
        if (logger.isDebugEnabled()) logger.debug("Time to load attribute names " + (end - start) + " ms");
    }

    private void initAttributeNamesForDatabase(IDatabase database, boolean source) {
        ThreadManager threadManager = new ThreadManager(scenario.getConfiguration().getMaxNumberOfThreads());
        for (String tableName : database.getTableNames()) {
            InitTableSchemaThread execThread = new InitTableSchemaThread(database, tableName, source);
            threadManager.startThread(execThread);
        }
        threadManager.waitForActiveThread();
    }

    class InitTableSchemaThread implements IBackgroundThread {

        private IDatabase database;
        private String tableName;
        private boolean source;

        public InitTableSchemaThread(IDatabase database, String tableName, boolean source) {
            this.database = database;
            this.tableName = tableName;
            this.source = source;
        }

        public void execute() {
            ITable table = database.getTable(tableName);
            List<Attribute> tableAttributes = getAttributesWithNoOIDs(table.getAttributes());
            for (int i = 0; i < tableAttributes.size(); i++) {
                Attribute attribute = tableAttributes.get(i);
                TableAndPosition tableAndPosition = new TableAndPosition(tableName, source, i);
                attributeNames.put(tableAndPosition, attribute.getName());
            }
        }

    }

    public String findAttributeName(String tableName, int attributePosition, boolean inPremise, boolean stTGD) {
        boolean source = (inPremise && stTGD);
        TableAndPosition tableAndPosition = new TableAndPosition(tableName, source, attributePosition);
        return this.attributeNames.get(tableAndPosition);
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

    public String convertValue(String value) {
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }
        if (scenario.getValueEncoder() == null) {
            return "\"" + value + "\"";
        }
        String encodedSymbol = scenario.getValueEncoder().encode(value);
        if (logger.isDebugEnabled()) logger.debug("Encoding symbol: " + value + " in " + encodedSymbol);
        return encodedSymbol;
    }

    class TableAndPosition {

        private String tableName;
        private boolean source;
        private int position;

        public TableAndPosition(String tableName, boolean source, int position) {
            this.tableName = tableName;
            this.source = source;
            this.position = position;
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return this.toString().equals(obj.toString());
        }

        @Override
        public String toString() {
            return tableName + "-" + (source ? "S" : "T") + "-" + position;
        }

    }

}
