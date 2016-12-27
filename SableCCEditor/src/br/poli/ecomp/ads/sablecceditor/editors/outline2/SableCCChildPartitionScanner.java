package br.poli.ecomp.ads.sablecceditor.editors.outline2;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;

import br.poli.ecomp.ads.sablecceditor.editors.rules.SableCCWhitespaceDetector;

public class SableCCChildPartitionScanner extends RuleBasedPartitionScanner {
	public final static String SABLECC_COMMENT = "__sablecc_comment";
	public final static String STRING ="__string";
	public final static String LEXER ="__lexer";
	public final static String OPERATION = "__operation";
	
    public final static String[] PARTITION_TYPES = new String[] {IDocument.DEFAULT_CONTENT_TYPE,SABLECC_COMMENT, 
    	STRING,LEXER,OPERATION};

    @SuppressWarnings("all")
    public SableCCChildPartitionScanner() {
    	String newLine = System.getProperty("line.separator");  
		IToken sableccComment = new Token(SABLECC_COMMENT);
		IToken sableccString = new Token(STRING);
		IToken sableccLexer = new Token(LEXER); 
		IToken sableccOperation = new Token(OPERATION);
		
		List rules = new ArrayList();
		// Add rule for single line comments
		rules.add(new EndOfLineRule("//", sableccComment));
		rules.add(new MultiLineRule("/*", "*/", sableccComment));
		// Add rule for strings and character constants.
		rules.add(new SingleLineRule("\"", "\"", sableccString, '\n'));
		rules.add(new SingleLineRule("'", "'", sableccString,'\n'));
		
		// Add rule for Lexer
		//rules.add(new MultiLineRule("Lexer","//Lexer",sableccLexer));
		// Add rule for Parser
		rules.add(new SingleLineRule("{", "}", sableccOperation, '\n'));

		IPredicateRule[] result = new IPredicateRule[rules.size()];
		rules.toArray(result);
		setPredicateRules(result);  
	}

}