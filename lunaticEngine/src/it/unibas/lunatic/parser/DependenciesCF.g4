grammar DependenciesCF;

@lexer::header {
package it.unibas.lunatic.parser.output;
}

@parser::header {
package it.unibas.lunatic.parser.output;

import it.unibas.lunatic.LunaticConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Stack;
import it.unibas.lunatic.parser.operators.ParseDependenciesCF;
import it.unibas.lunatic.model.dependency.*;
import speedy.model.database.AttributeRef;
import speedy.model.expressions.Expression;
}

@parser::members {
private static Logger logger = LoggerFactory.getLogger(DependenciesParser.class);

private ParseDependenciesCF generator = new ParseDependenciesCF();

private Stack<IFormula> formulaStack = new Stack<IFormula>();

private Dependency dependency;
private IFormula formulaWN;
private PositiveFormula positiveFormula;
private IFormulaAtom atom;
private FormulaAttribute attribute;
private StringBuilder expressionString;
private String leftConstant;
private String rightConstant;
private int counter;
private int attributePosition;
private boolean inPremise;
private boolean stTGD;

public void setGenerator(ParseDependenciesCF generator) {
      this.generator = generator;
}

}
@lexer::members {

public void emitErrorMessage(String msg) {
	throw new it.unibas.lunatic.exceptions.ParserException(msg);
}
}

prog: dependencies {  }  ;

dependencies:    
	         ('ST-TGDs:' sttgd+ { counter = 1;} )?
	         ('T-TGDs:' etgd+ { counter = 1;} )?
	         ('EGDs:' egd+ { counter = 1;} )?
	         ('Queries:' query+ { counter = 1;} )?;

sttgd:	 	 { stTGD = true; } dependency { dependency.setType(LunaticConstants.STTGD); dependency.setId("m" + counter++); generator.addSTTGD(dependency); } ;

etgd:	 	 { stTGD = false; } dependency { dependency.setType(LunaticConstants.ExtTGD); dependency.setId("t" + counter++); generator.addExtTGD(dependency); } ;

egd:	 	 { stTGD = false; } dependency { dependency.setType(LunaticConstants.EGD); dependency.setId("e" + counter++); generator.addEGD(dependency); } ;
		    
query:	 	 { stTGD = false; } querydependency { dependency.setType(LunaticConstants.QUERY); dependency.setId("q" + counter++); generator.addQuery(dependency); } ;
		    
dependency:	 (id = IDENTIFIER':')? {  dependency = new Dependency(); 
                    formulaWN = new FormulaWithNegations(); 
                    formulaStack.push(formulaWN);
                    dependency.setPremise(formulaWN);
                    inPremise = true;
                    if(((DependencyContext)_localctx).id!=null) dependency.setId(((DependencyContext)_localctx).id.getText()); }
		 positiveFormula '->' 
		 ({  formulaStack.clear(); inPremise = false;} 
                  conclusionFormula) '.' ;  
		    
querydependency	: (id = IDENTIFIER':')? {  dependency = new Dependency(); 
                    formulaWN = new FormulaWithNegations(); 
                    formulaStack.clear(); 
                    formulaStack.push(formulaWN);
                    dependency.setPremise(formulaWN);
                    inPremise = false;
                    if(((QuerydependencyContext)_localctx).id!=null) dependency.setId(((QuerydependencyContext)_localctx).id.getText()); }
		 conclusionQueryFormula '<-' 
		 ({   inPremise = true;} 
                  positiveFormula) '.' ;  
                  		    
positiveFormula: {  positiveFormula = new PositiveFormula(); 
                    positiveFormula.setFather(formulaStack.peek()); 
                    formulaStack.peek().setPositiveFormula(positiveFormula); }
                  relationalAtom (',' atom )* ;
                                    
conclusionFormula: {  positiveFormula = new PositiveFormula(); 
                      dependency.setConclusion(positiveFormula); }
                  atom (',' atom )* ;
                                    
conclusionQueryFormula: {  positiveFormula = new PositiveFormula(); 
                      dependency.setConclusion(positiveFormula); }
                  queryAtom ;

atom	:	 relationalAtom | builtin | comparison;	

relationalAtom:	 name=IDENTIFIER { atom = new RelationalAtom(generator.cleanTableName(((RelationalAtomContext)_localctx).name.getText())); attributePosition = 0; } '(' attribute (',' attribute)* ')'
		 {  positiveFormula.addAtom(atom); atom.setFormula(positiveFormula); };
		 
queryAtom:	 name=IDENTIFIER { atom = new QueryAtom(((QueryAtomContext)_localctx).name.getText()); attributePosition = 0; } '(' queryattribute (',' queryattribute)* ')'
		 {  positiveFormula.addAtom(atom); atom.setFormula(positiveFormula); };

builtin	:	 expression=EXPRESSION  
                 {  atom = new BuiltInAtom(positiveFormula, new Expression(generator.clean(((BuiltinContext)_localctx).expression.getText()))); 
                    positiveFormula.addAtom(atom);  } ;         

comparison :	 {   expressionString = new StringBuilder(); 
		     leftConstant = null;
		     rightConstant = null;}
                 leftargument 
                 oper=OPERATOR { 
                 	String operatorText = ((ComparisonContext)_localctx).oper.getText();
                 	if(operatorText.equals("=")){
                 	   operatorText = "==";
                 	}
                 	expressionString.append(" ").append(operatorText); 
                 }
                 rightargument 
                 {  Expression expression = new Expression(expressionString.toString()); 
                    atom = new ComparisonAtom(positiveFormula, expression, leftConstant, rightConstant, ((ComparisonContext)_localctx).oper.getText()); 
                    positiveFormula.addAtom(atom); } ;

leftargument:	 ('?'var=IDENTIFIER { expressionString.append(((LeftargumentContext)_localctx).var.getText()); } |
                 constant=(STRING | NUMBER | IDENTIFIER) { expressionString.append(((LeftargumentContext)_localctx).constant.getText()); leftConstant = ((LeftargumentContext)_localctx).constant.getText();}
                 );
                 
rightargument:	 ('?'var=IDENTIFIER { expressionString.append(((RightargumentContext)_localctx).var.getText()); } |
                 constant=(STRING | NUMBER | IDENTIFIER) { expressionString.append(((RightargumentContext)_localctx).constant.getText()); rightConstant = ((RightargumentContext)_localctx).constant.getText();}
                 );
                 
attribute:	 { String attributeName = generator.findAttributeName(((RelationalAtom)atom).getTableName(), attributePosition, inPremise, stTGD); 
                   attribute = new FormulaAttribute(attributeName); attributePosition++;} value
		 { ((RelationalAtom)atom).addAttribute(attribute); } ;
		 
queryattribute:	 { String attributeName = "a" + attributePosition; 
                   attribute = new FormulaAttribute(attributeName); attributePosition++;} queryvalue
		 { ((QueryAtom)atom).addAttribute(attribute); } ;
		 
value	:	 '?'var=IDENTIFIER { attribute.setValue(new FormulaVariableOccurrence(new AttributeRef(((RelationalAtom)atom).getTableName(), attribute.getAttributeName()), ((ValueContext)_localctx).var.getText())); } |
                 constant=(STRING | NUMBER) { attribute.setValue(new FormulaConstant(generator.convertValue(((ValueContext)_localctx).constant.getText()))); } |
                 symbol=IDENTIFIER { attribute.setValue(new FormulaSymbol(generator.convertValue(((ValueContext)_localctx).symbol.getText()))); } |
                 nullValue=NULL { attribute.setValue(new FormulaConstant(((ValueContext)_localctx).nullValue.getText(), true)); } |
                 expression=EXPRESSION { attribute.setValue(new FormulaExpression(new Expression(generator.clean(((ValueContext)_localctx).expression.getText())))); };


queryvalue:	 '?'var=IDENTIFIER { attribute.setValue(new FormulaVariableOccurrence(new AttributeRef(((QueryAtom)atom).getQueryId(), attribute.getAttributeName()), ((QueryvalueContext)_localctx).var.getText())); } |
                 constant=(STRING | NUMBER) { attribute.setValue(new FormulaConstant(generator.convertValue(((QueryvalueContext)_localctx).constant.getText()))); } |
                 symbol=IDENTIFIER { attribute.setValue(new FormulaSymbol(generator.convertValue(((QueryvalueContext)_localctx).symbol.getText()))); } |
                 nullValue=NULL { attribute.setValue(new FormulaConstant(((QueryvalueContext)_localctx).nullValue.getText(), true)); } |
                 expression=EXPRESSION { attribute.setValue(new FormulaExpression(new Expression(generator.clean(((QueryvalueContext)_localctx).expression.getText())))); };

OPERATOR:	 '=' | '!=' | '>' | '<' | '>=' | '<=';

IDENTIFIER  :    ((LETTER) (LETTER | DIGIT | '_' | '-' )*);

//STRING  :  	 '"' (LETTER | DIGIT| '-' | '.' | ' ' | '_' | '*' | '/' )+ '"';
STRING  :         '"' ~('\r' | '\n' | '"')* '"';
NUMBER	: 	 ('-')? DIGIT+ ('.' DIGIT+)?;
NULL    :        '#NULL#';
fragment DIGIT:  '0'..'9' ;
fragment LETTER: 'a'..'z'|'A'..'Z' ;
WHITESPACE : 	 ( '\t' | ' ' | '\r' | '\n'| '\u000C' )+ { skip(); } ;
LINE_COMMENT :   '//' ~( '\r' | '\n' )* { skip(); } ;
EXPRESSION:      '{'(.)*?'}';