package br.poli.ecomp.ads.sablecceditor.editors.scanner;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.*;

public class SableCCPartitionScanner extends RuleBasedPartitionScanner {
	public final static String SABLECC_COMMENT = "__sablecc_comment";
	public final static String STRING ="__string";
	public final static String LEXER ="__lexer";
	public final static String PARSER = "__parser";
	
    public final static String[] PARTITION_TYPES = new String[] {IDocument.DEFAULT_CONTENT_TYPE,SABLECC_COMMENT, 
    	STRING,LEXER,PARSER};

    @SuppressWarnings("all")
    public SableCCPartitionScanner() {
    	String newLine = System.getProperty("line.separator");  
		IToken sableccComment = new Token(SABLECC_COMMENT);
		IToken sableccString = new Token(STRING);
		IToken sableccLexer = new Token(LEXER); 
		IToken sableccParser = new Token(PARSER);
		
		List rules = new ArrayList();
		// Add rule for single line comments
		rules.add(new EndOfLineRule("//", sableccComment));
		rules.add(new MultiLineRule("/*", "*/", sableccComment));
		// Add rule for strings and character constants.
		rules.add(new SingleLineRule("\"", "\"", sableccString, '\\'));
		rules.add(new SingleLineRule("'", "'", sableccString, '\\'));
		// Add rule for Lexer
		rules.add(new MultiLineRule("Lexer","//Lexer",sableccLexer));
		// Add rule for Parser
		rules.add(new MultiLineRule("Parser","//Parser",sableccParser));

		IPredicateRule[] result = new IPredicateRule[rules.size()];
		rules.toArray(result);
		setPredicateRules(result);
	}

}