// $ANTLR 3.5.1 /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g 2014-09-10 11:40:27

package it.unibas.lunatic.parser.output;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class DependenciesLexer extends Lexer {
	public static final int EOF=-1;
	public static final int T__14=14;
	public static final int T__15=15;
	public static final int T__16=16;
	public static final int T__17=17;
	public static final int T__18=18;
	public static final int T__19=19;
	public static final int T__20=20;
	public static final int T__21=21;
	public static final int T__22=22;
	public static final int T__23=23;
	public static final int T__24=24;
	public static final int T__25=25;
	public static final int T__26=26;
	public static final int T__27=27;
	public static final int T__28=28;
	public static final int T__29=29;
	public static final int T__30=30;
	public static final int T__31=31;
	public static final int T__32=32;
	public static final int T__33=33;
	public static final int DIGIT=4;
	public static final int EXPRESSION=5;
	public static final int IDENTIFIER=6;
	public static final int LETTER=7;
	public static final int LINE_COMMENT=8;
	public static final int NULL=9;
	public static final int NUMBER=10;
	public static final int OPERATOR=11;
	public static final int STRING=12;
	public static final int WHITESPACE=13;


	public void emitErrorMessage(String msg) {
		throw new it.unibas.lunatic.exceptions.ParserException(msg);
	}


	// delegates
	// delegators
	public Lexer[] getDelegates() {
		return new Lexer[] {};
	}

	public DependenciesLexer() {} 
	public DependenciesLexer(CharStream input) {
		this(input, new RecognizerSharedState());
	}
	public DependenciesLexer(CharStream input, RecognizerSharedState state) {
		super(input,state);
	}
	@Override public String getGrammarFileName() { return "/Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g"; }

	// $ANTLR start "T__14"
	public final void mT__14() throws RecognitionException {
		try {
			int _type = T__14;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:12:7: ( '#fail' )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:12:9: '#fail'
			{
			match("#fail"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__14"

	// $ANTLR start "T__15"
	public final void mT__15() throws RecognitionException {
		try {
			int _type = T__15;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:13:7: ( '(' )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:13:9: '('
			{
			match('('); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__15"

	// $ANTLR start "T__16"
	public final void mT__16() throws RecognitionException {
		try {
			int _type = T__16;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:14:7: ( ')' )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:14:9: ')'
			{
			match(')'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__16"

	// $ANTLR start "T__17"
	public final void mT__17() throws RecognitionException {
		try {
			int _type = T__17;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:15:7: ( ',' )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:15:9: ','
			{
			match(','); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__17"

	// $ANTLR start "T__18"
	public final void mT__18() throws RecognitionException {
		try {
			int _type = T__18;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:16:7: ( '->' )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:16:9: '->'
			{
			match("->"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__18"

	// $ANTLR start "T__19"
	public final void mT__19() throws RecognitionException {
		try {
			int _type = T__19;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:17:7: ( '.' )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:17:9: '.'
			{
			match('.'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__19"

	// $ANTLR start "T__20"
	public final void mT__20() throws RecognitionException {
		try {
			int _type = T__20;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:18:7: ( ':' )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:18:9: ':'
			{
			match(':'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__20"

	// $ANTLR start "T__21"
	public final void mT__21() throws RecognitionException {
		try {
			int _type = T__21;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:19:7: ( 'DCs:' )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:19:9: 'DCs:'
			{
			match("DCs:"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__21"

	// $ANTLR start "T__22"
	public final void mT__22() throws RecognitionException {
		try {
			int _type = T__22;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:20:7: ( 'DED-EGDs:' )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:20:9: 'DED-EGDs:'
			{
			match("DED-EGDs:"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__22"

	// $ANTLR start "T__23"
	public final void mT__23() throws RecognitionException {
		try {
			int _type = T__23;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:21:7: ( 'DED-ExtTGDs:' )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:21:9: 'DED-ExtTGDs:'
			{
			match("DED-ExtTGDs:"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__23"

	// $ANTLR start "T__24"
	public final void mT__24() throws RecognitionException {
		try {
			int _type = T__24;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:22:7: ( 'DED-STTGDs:' )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:22:9: 'DED-STTGDs:'
			{
			match("DED-STTGDs:"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__24"

	// $ANTLR start "T__25"
	public final void mT__25() throws RecognitionException {
		try {
			int _type = T__25;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:23:7: ( 'EGDs:' )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:23:9: 'EGDs:'
			{
			match("EGDs:"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__25"

	// $ANTLR start "T__26"
	public final void mT__26() throws RecognitionException {
		try {
			int _type = T__26;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:24:7: ( 'ExtEGDs:' )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:24:9: 'ExtEGDs:'
			{
			match("ExtEGDs:"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__26"

	// $ANTLR start "T__27"
	public final void mT__27() throws RecognitionException {
		try {
			int _type = T__27;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:25:7: ( 'ExtTGDs:' )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:25:9: 'ExtTGDs:'
			{
			match("ExtTGDs:"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__27"

	// $ANTLR start "T__28"
	public final void mT__28() throws RecognitionException {
		try {
			int _type = T__28;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:26:7: ( 'STTGDs:' )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:26:9: 'STTGDs:'
			{
			match("STTGDs:"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__28"

	// $ANTLR start "T__29"
	public final void mT__29() throws RecognitionException {
		try {
			int _type = T__29;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:27:7: ( '[' )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:27:9: '['
			{
			match('['); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__29"

	// $ANTLR start "T__30"
	public final void mT__30() throws RecognitionException {
		try {
			int _type = T__30;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:28:7: ( '\\$' )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:28:9: '\\$'
			{
			match('$'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__30"

	// $ANTLR start "T__31"
	public final void mT__31() throws RecognitionException {
		try {
			int _type = T__31;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:29:7: ( ']' )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:29:9: ']'
			{
			match(']'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__31"

	// $ANTLR start "T__32"
	public final void mT__32() throws RecognitionException {
		try {
			int _type = T__32;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:30:7: ( 'and not exists' )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:30:9: 'and not exists'
			{
			match("and not exists"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__32"

	// $ANTLR start "T__33"
	public final void mT__33() throws RecognitionException {
		try {
			int _type = T__33;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:31:7: ( '|' )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:31:9: '|'
			{
			match('|'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__33"

	// $ANTLR start "OPERATOR"
	public final void mOPERATOR() throws RecognitionException {
		try {
			int _type = OPERATOR;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:156:9: ( '==' | '!=' | '>' | '<' | '>=' | '<=' )
			int alt1=6;
			switch ( input.LA(1) ) {
			case '=':
				{
				alt1=1;
				}
				break;
			case '!':
				{
				alt1=2;
				}
				break;
			case '>':
				{
				int LA1_3 = input.LA(2);
				if ( (LA1_3=='=') ) {
					alt1=5;
				}

				else {
					alt1=3;
				}

				}
				break;
			case '<':
				{
				int LA1_4 = input.LA(2);
				if ( (LA1_4=='=') ) {
					alt1=6;
				}

				else {
					alt1=4;
				}

				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 1, 0, input);
				throw nvae;
			}
			switch (alt1) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:156:12: '=='
					{
					match("=="); 

					}
					break;
				case 2 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:156:19: '!='
					{
					match("!="); 

					}
					break;
				case 3 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:156:26: '>'
					{
					match('>'); 
					}
					break;
				case 4 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:156:32: '<'
					{
					match('<'); 
					}
					break;
				case 5 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:156:38: '>='
					{
					match(">="); 

					}
					break;
				case 6 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:156:45: '<='
					{
					match("<="); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "OPERATOR"

	// $ANTLR start "IDENTIFIER"
	public final void mIDENTIFIER() throws RecognitionException {
		try {
			int _type = IDENTIFIER;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:158:13: ( ( LETTER ) ( LETTER | DIGIT | '_' )* )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:158:18: ( LETTER ) ( LETTER | DIGIT | '_' )*
			{
			if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
				input.consume();
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:158:27: ( LETTER | DIGIT | '_' )*
			loop2:
			while (true) {
				int alt2=2;
				int LA2_0 = input.LA(1);
				if ( ((LA2_0 >= '0' && LA2_0 <= '9')||(LA2_0 >= 'A' && LA2_0 <= 'Z')||LA2_0=='_'||(LA2_0 >= 'a' && LA2_0 <= 'z')) ) {
					alt2=1;
				}

				switch (alt2) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:
					{
					if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop2;
				}
			}

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "IDENTIFIER"

	// $ANTLR start "STRING"
	public final void mSTRING() throws RecognitionException {
		try {
			int _type = STRING;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:160:9: ( '\"' ( LETTER | DIGIT | '-' | '.' | ' ' )+ '\"' )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:160:14: '\"' ( LETTER | DIGIT | '-' | '.' | ' ' )+ '\"'
			{
			match('\"'); 
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:160:18: ( LETTER | DIGIT | '-' | '.' | ' ' )+
			int cnt3=0;
			loop3:
			while (true) {
				int alt3=2;
				int LA3_0 = input.LA(1);
				if ( (LA3_0==' '||(LA3_0 >= '-' && LA3_0 <= '.')||(LA3_0 >= '0' && LA3_0 <= '9')||(LA3_0 >= 'A' && LA3_0 <= 'Z')||(LA3_0 >= 'a' && LA3_0 <= 'z')) ) {
					alt3=1;
				}

				switch (alt3) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:
					{
					if ( input.LA(1)==' '||(input.LA(1) >= '-' && input.LA(1) <= '.')||(input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					if ( cnt3 >= 1 ) break loop3;
					EarlyExitException eee = new EarlyExitException(3, input);
					throw eee;
				}
				cnt3++;
			}

			match('\"'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "STRING"

	// $ANTLR start "NUMBER"
	public final void mNUMBER() throws RecognitionException {
		try {
			int _type = NUMBER;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:161:8: ( ( '-' )? ( DIGIT )+ ( '.' ( DIGIT )+ )? )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:161:12: ( '-' )? ( DIGIT )+ ( '.' ( DIGIT )+ )?
			{
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:161:12: ( '-' )?
			int alt4=2;
			int LA4_0 = input.LA(1);
			if ( (LA4_0=='-') ) {
				alt4=1;
			}
			switch (alt4) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:161:13: '-'
					{
					match('-'); 
					}
					break;

			}

			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:161:19: ( DIGIT )+
			int cnt5=0;
			loop5:
			while (true) {
				int alt5=2;
				int LA5_0 = input.LA(1);
				if ( ((LA5_0 >= '0' && LA5_0 <= '9')) ) {
					alt5=1;
				}

				switch (alt5) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:
					{
					if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					if ( cnt5 >= 1 ) break loop5;
					EarlyExitException eee = new EarlyExitException(5, input);
					throw eee;
				}
				cnt5++;
			}

			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:161:26: ( '.' ( DIGIT )+ )?
			int alt7=2;
			int LA7_0 = input.LA(1);
			if ( (LA7_0=='.') ) {
				alt7=1;
			}
			switch (alt7) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:161:27: '.' ( DIGIT )+
					{
					match('.'); 
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:161:31: ( DIGIT )+
					int cnt6=0;
					loop6:
					while (true) {
						int alt6=2;
						int LA6_0 = input.LA(1);
						if ( ((LA6_0 >= '0' && LA6_0 <= '9')) ) {
							alt6=1;
						}

						switch (alt6) {
						case 1 :
							// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:
							{
							if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
								input.consume();
							}
							else {
								MismatchedSetException mse = new MismatchedSetException(null,input);
								recover(mse);
								throw mse;
							}
							}
							break;

						default :
							if ( cnt6 >= 1 ) break loop6;
							EarlyExitException eee = new EarlyExitException(6, input);
							throw eee;
						}
						cnt6++;
					}

					}
					break;

			}

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "NUMBER"

	// $ANTLR start "NULL"
	public final void mNULL() throws RecognitionException {
		try {
			int _type = NULL;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:162:9: ( '#NULL#' )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:162:18: '#NULL#'
			{
			match("#NULL#"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "NULL"

	// $ANTLR start "DIGIT"
	public final void mDIGIT() throws RecognitionException {
		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:163:15: ( '0' .. '9' )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:
			{
			if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
				input.consume();
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "DIGIT"

	// $ANTLR start "LETTER"
	public final void mLETTER() throws RecognitionException {
		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:164:16: ( 'a' .. 'z' | 'A' .. 'Z' )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:
			{
			if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
				input.consume();
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "LETTER"

	// $ANTLR start "WHITESPACE"
	public final void mWHITESPACE() throws RecognitionException {
		try {
			int _type = WHITESPACE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:165:12: ( ( '\\t' | ' ' | '\\r' | '\\n' | '\\u000C' )+ )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:165:16: ( '\\t' | ' ' | '\\r' | '\\n' | '\\u000C' )+
			{
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:165:16: ( '\\t' | ' ' | '\\r' | '\\n' | '\\u000C' )+
			int cnt8=0;
			loop8:
			while (true) {
				int alt8=2;
				int LA8_0 = input.LA(1);
				if ( ((LA8_0 >= '\t' && LA8_0 <= '\n')||(LA8_0 >= '\f' && LA8_0 <= '\r')||LA8_0==' ') ) {
					alt8=1;
				}

				switch (alt8) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:
					{
					if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||(input.LA(1) >= '\f' && input.LA(1) <= '\r')||input.LA(1)==' ' ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					if ( cnt8 >= 1 ) break loop8;
					EarlyExitException eee = new EarlyExitException(8, input);
					throw eee;
				}
				cnt8++;
			}

			 skip(); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "WHITESPACE"

	// $ANTLR start "LINE_COMMENT"
	public final void mLINE_COMMENT() throws RecognitionException {
		try {
			int _type = LINE_COMMENT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:166:14: ( '//' (~ ( '\\r' | '\\n' ) )* )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:166:18: '//' (~ ( '\\r' | '\\n' ) )*
			{
			match("//"); 

			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:166:23: (~ ( '\\r' | '\\n' ) )*
			loop9:
			while (true) {
				int alt9=2;
				int LA9_0 = input.LA(1);
				if ( ((LA9_0 >= '\u0000' && LA9_0 <= '\t')||(LA9_0 >= '\u000B' && LA9_0 <= '\f')||(LA9_0 >= '\u000E' && LA9_0 <= '\uFFFF')) ) {
					alt9=1;
				}

				switch (alt9) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:
					{
					if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '\t')||(input.LA(1) >= '\u000B' && input.LA(1) <= '\f')||(input.LA(1) >= '\u000E' && input.LA(1) <= '\uFFFF') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop9;
				}
			}

			 skip(); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "LINE_COMMENT"

	// $ANTLR start "EXPRESSION"
	public final void mEXPRESSION() throws RecognitionException {
		try {
			int _type = EXPRESSION;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:167:11: ( '{' ( . )* '}' )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:167:18: '{' ( . )* '}'
			{
			match('{'); 
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:167:21: ( . )*
			loop10:
			while (true) {
				int alt10=2;
				int LA10_0 = input.LA(1);
				if ( (LA10_0=='}') ) {
					alt10=2;
				}
				else if ( ((LA10_0 >= '\u0000' && LA10_0 <= '|')||(LA10_0 >= '~' && LA10_0 <= '\uFFFF')) ) {
					alt10=1;
				}

				switch (alt10) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:167:22: .
					{
					matchAny(); 
					}
					break;

				default :
					break loop10;
				}
			}

			match('}'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "EXPRESSION"

	@Override
	public void mTokens() throws RecognitionException {
		// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:1:8: ( T__14 | T__15 | T__16 | T__17 | T__18 | T__19 | T__20 | T__21 | T__22 | T__23 | T__24 | T__25 | T__26 | T__27 | T__28 | T__29 | T__30 | T__31 | T__32 | T__33 | OPERATOR | IDENTIFIER | STRING | NUMBER | NULL | WHITESPACE | LINE_COMMENT | EXPRESSION )
		int alt11=28;
		alt11 = dfa11.predict(input);
		switch (alt11) {
			case 1 :
				// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:1:10: T__14
				{
				mT__14(); 

				}
				break;
			case 2 :
				// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:1:16: T__15
				{
				mT__15(); 

				}
				break;
			case 3 :
				// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:1:22: T__16
				{
				mT__16(); 

				}
				break;
			case 4 :
				// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:1:28: T__17
				{
				mT__17(); 

				}
				break;
			case 5 :
				// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:1:34: T__18
				{
				mT__18(); 

				}
				break;
			case 6 :
				// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:1:40: T__19
				{
				mT__19(); 

				}
				break;
			case 7 :
				// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:1:46: T__20
				{
				mT__20(); 

				}
				break;
			case 8 :
				// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:1:52: T__21
				{
				mT__21(); 

				}
				break;
			case 9 :
				// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:1:58: T__22
				{
				mT__22(); 

				}
				break;
			case 10 :
				// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:1:64: T__23
				{
				mT__23(); 

				}
				break;
			case 11 :
				// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:1:70: T__24
				{
				mT__24(); 

				}
				break;
			case 12 :
				// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:1:76: T__25
				{
				mT__25(); 

				}
				break;
			case 13 :
				// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:1:82: T__26
				{
				mT__26(); 

				}
				break;
			case 14 :
				// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:1:88: T__27
				{
				mT__27(); 

				}
				break;
			case 15 :
				// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:1:94: T__28
				{
				mT__28(); 

				}
				break;
			case 16 :
				// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:1:100: T__29
				{
				mT__29(); 

				}
				break;
			case 17 :
				// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:1:106: T__30
				{
				mT__30(); 

				}
				break;
			case 18 :
				// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:1:112: T__31
				{
				mT__31(); 

				}
				break;
			case 19 :
				// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:1:118: T__32
				{
				mT__32(); 

				}
				break;
			case 20 :
				// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:1:124: T__33
				{
				mT__33(); 

				}
				break;
			case 21 :
				// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:1:130: OPERATOR
				{
				mOPERATOR(); 

				}
				break;
			case 22 :
				// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:1:139: IDENTIFIER
				{
				mIDENTIFIER(); 

				}
				break;
			case 23 :
				// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:1:150: STRING
				{
				mSTRING(); 

				}
				break;
			case 24 :
				// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:1:157: NUMBER
				{
				mNUMBER(); 

				}
				break;
			case 25 :
				// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:1:164: NULL
				{
				mNULL(); 

				}
				break;
			case 26 :
				// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:1:169: WHITESPACE
				{
				mWHITESPACE(); 

				}
				break;
			case 27 :
				// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:1:180: LINE_COMMENT
				{
				mLINE_COMMENT(); 

				}
				break;
			case 28 :
				// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:1:193: EXPRESSION
				{
				mEXPRESSION(); 

				}
				break;

		}
	}


	protected DFA11 dfa11 = new DFA11(this);
	static final String DFA11_eotS =
		"\10\uffff\3\21\3\uffff\1\21\13\uffff\14\21\2\uffff\4\21\4\uffff\3\21\2"+
		"\uffff\5\21\3\uffff";
	static final String DFA11_eofS =
		"\75\uffff";
	static final String DFA11_minS =
		"\1\11\1\116\3\uffff\1\60\2\uffff\1\103\1\107\1\124\3\uffff\1\156\13\uffff"+
		"\1\163\2\104\1\164\1\124\1\144\1\72\1\55\1\163\1\105\1\107\1\40\1\uffff"+
		"\1\105\1\72\2\107\1\104\1\uffff\1\107\2\uffff\2\104\1\163\2\uffff\2\163"+
		"\3\72\3\uffff";
	static final String DFA11_maxS =
		"\1\174\1\146\3\uffff\1\76\2\uffff\1\105\1\170\1\124\3\uffff\1\156\13\uffff"+
		"\1\163\2\104\1\164\1\124\1\144\1\72\1\55\1\163\1\124\1\107\1\40\1\uffff"+
		"\1\123\1\72\2\107\1\104\1\uffff\1\170\2\uffff\2\104\1\163\2\uffff\2\163"+
		"\3\72\3\uffff";
	static final String DFA11_acceptS =
		"\2\uffff\1\2\1\3\1\4\1\uffff\1\6\1\7\3\uffff\1\20\1\21\1\22\1\uffff\1"+
		"\24\1\25\1\26\1\27\1\30\1\32\1\33\1\34\1\1\1\31\1\5\14\uffff\1\10\5\uffff"+
		"\1\23\1\uffff\1\13\1\14\3\uffff\1\11\1\12\5\uffff\1\17\1\15\1\16";
	static final String DFA11_specialS =
		"\75\uffff}>";
	static final String[] DFA11_transitionS = {
			"\2\24\1\uffff\2\24\22\uffff\1\24\1\20\1\22\1\1\1\14\3\uffff\1\2\1\3\2"+
			"\uffff\1\4\1\5\1\6\1\25\12\23\1\7\1\uffff\3\20\2\uffff\3\21\1\10\1\11"+
			"\15\21\1\12\7\21\1\13\1\uffff\1\15\3\uffff\1\16\31\21\1\26\1\17",
			"\1\30\27\uffff\1\27",
			"",
			"",
			"",
			"\12\23\4\uffff\1\31",
			"",
			"",
			"\1\32\1\uffff\1\33",
			"\1\34\60\uffff\1\35",
			"\1\36",
			"",
			"",
			"",
			"\1\37",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\40",
			"\1\41",
			"\1\42",
			"\1\43",
			"\1\44",
			"\1\45",
			"\1\46",
			"\1\47",
			"\1\50",
			"\1\51\16\uffff\1\52",
			"\1\53",
			"\1\54",
			"",
			"\1\55\15\uffff\1\56",
			"\1\57",
			"\1\60",
			"\1\61",
			"\1\62",
			"",
			"\1\63\60\uffff\1\64",
			"",
			"",
			"\1\65",
			"\1\66",
			"\1\67",
			"",
			"",
			"\1\70",
			"\1\71",
			"\1\72",
			"\1\73",
			"\1\74",
			"",
			"",
			""
	};

	static final short[] DFA11_eot = DFA.unpackEncodedString(DFA11_eotS);
	static final short[] DFA11_eof = DFA.unpackEncodedString(DFA11_eofS);
	static final char[] DFA11_min = DFA.unpackEncodedStringToUnsignedChars(DFA11_minS);
	static final char[] DFA11_max = DFA.unpackEncodedStringToUnsignedChars(DFA11_maxS);
	static final short[] DFA11_accept = DFA.unpackEncodedString(DFA11_acceptS);
	static final short[] DFA11_special = DFA.unpackEncodedString(DFA11_specialS);
	static final short[][] DFA11_transition;

	static {
		int numStates = DFA11_transitionS.length;
		DFA11_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA11_transition[i] = DFA.unpackEncodedString(DFA11_transitionS[i]);
		}
	}

	protected class DFA11 extends DFA {

		public DFA11(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 11;
			this.eot = DFA11_eot;
			this.eof = DFA11_eof;
			this.min = DFA11_min;
			this.max = DFA11_max;
			this.accept = DFA11_accept;
			this.special = DFA11_special;
			this.transition = DFA11_transition;
		}
		@Override
		public String getDescription() {
			return "1:1: Tokens : ( T__14 | T__15 | T__16 | T__17 | T__18 | T__19 | T__20 | T__21 | T__22 | T__23 | T__24 | T__25 | T__26 | T__27 | T__28 | T__29 | T__30 | T__31 | T__32 | T__33 | OPERATOR | IDENTIFIER | STRING | NUMBER | NULL | WHITESPACE | LINE_COMMENT | EXPRESSION );";
		}
	}

}
