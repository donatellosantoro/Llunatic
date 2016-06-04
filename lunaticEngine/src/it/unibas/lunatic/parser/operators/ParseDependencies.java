package it.unibas.lunatic.parser.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ParserException;
import it.unibas.lunatic.model.dependency.*;
import it.unibas.lunatic.parser.ParserOutput;
import it.unibas.lunatic.parser.output.DependenciesLexer;
import it.unibas.lunatic.parser.output.DependenciesParser;
import it.unibas.lunatic.model.dependency.operators.DependencyUtility;
import java.io.ByteArrayInputStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.atn.PredictionMode;
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
            DependenciesLexer lex = new DependenciesLexer(new ANTLRInputStream(new ByteArrayInputStream(text.getBytes())));
            CommonTokenStream tokens = new CommonTokenStream(lex);
            DependenciesParser g = new DependenciesParser(tokens);
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
