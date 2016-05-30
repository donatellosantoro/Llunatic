package it.unibas.lunatic.parser.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ParserException;
import it.unibas.lunatic.model.dependency.*;
import it.unibas.lunatic.parser.ParserOutput;
import it.unibas.lunatic.parser.output.DependenciesLexer;
import it.unibas.lunatic.parser.output.DependenciesParser;
import it.unibas.lunatic.model.dependency.operators.DependencyUtility;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unchecked")
public class ParseDependencies {

    public final static String NULL = "#NULL#";
    private static final Logger logger = LoggerFactory.getLogger(ParseDependencies.class);

    private final ParserOutput parserOutput = new ParserOutput();
    private Scenario scenario;

    public ParserOutput getParserOutput() {
        return parserOutput;
    }    
    
    public void generateDependencies(String text, Scenario scenario) throws Exception {
        try {
            this.scenario = scenario;
            DependenciesLexer lex = new DependenciesLexer(new ANTLRStringStream(text));
            CommonTokenStream tokens = new CommonTokenStream(lex);
            DependenciesParser g = new DependenciesParser(tokens);
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

    public String clean(String expressionString) {
        return DependencyUtility.clean(expressionString);
    }
}
