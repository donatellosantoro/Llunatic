grammar Database;

@lexer::header {
package it.unibas.lunatic.parser.output;
}

@parser::header {
package it.unibas.lunatic.parser.output;

import speedy.model.expressions.Expression;
import it.unibas.lunatic.parser.operators.ParseDatabase;
import it.unibas.lunatic.parser.*;
import java.util.Stack;
}

@parser::members {
private static org.apache.commons.logging.Log logger = org.apache.commons.logging.LogFactory.getLog(DatabaseParser.class.getName());
private ParseDatabase generator;

private ParserSchema currentSchema;
private ParserInstance currentInstance;
private ParserTable currentTable;
private ParserFact currentFact;

public void setGenerator(ParseDatabase generator) {
      this.generator = generator;
}
}
@lexer::members {

public void emitErrorMessage(String msg) {
	throw new it.unibas.lunatic.exceptions.ParserException(msg);
}
}

prog: database {  }  ;

database:     schema instance? ;

schema:	       'SCHEMA:' { currentSchema = new ParserSchema(); } relation+ { generator.setSchema(currentSchema); };

relation:	set=IDENTIFIER { currentTable = new ParserTable(((RelationContext)_localctx).set.getText()); } '(' attrName (',' attrName)* ')' { currentSchema.addTable(currentTable); };

attrName:	attr=IDENTIFIER { currentTable.addAttribute(new ParserAttribute(((AttrNameContext)_localctx).attr.getText(), null));  };

instance:      'INSTANCE:' { currentInstance = new ParserInstance(); } fact+ { generator.setInstance(currentInstance); }   ;

fact:	       set=IDENTIFIER { currentFact = new ParserFact(((FactContext)_localctx).set.getText()); } '(' attrValue (',' attrValue)* ')' { currentInstance.addFact(currentFact); };

attrValue:	attr=IDENTIFIER ':' val=(NULL | STRING | NUMBER) { currentFact.addAttribute(new ParserAttribute(((AttrValueContext)_localctx).attr.getText(), ((AttrValueContext)_localctx).val.getText()));  };

IDENTIFIER  :   (LETTER) (LETTER | DIGIT | '_' | '.' | '-' )*;

STRING  :  	'"' (LETTER | DIGIT| '-' | '.' | '@' | ' ')+ '"';
NUMBER	: 	('-')? DIGIT+ ('.' DIGIT+)?;
NULL    :       '#NULL#';
fragment DIGIT	: '0'..'9' ;
fragment LETTER	: 'a'..'z'|'A'..'Z' ;
WHITESPACE : 	( '\t' | ' ' | '\r' | '\n'| '\u000C' )+ { skip(); } ;
LINE_COMMENT :  '//' ~( '\r' | '\n' )* { skip(); } ;