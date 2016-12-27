package br.poli.ecomp.ads.sablecceditor.editors.scanner;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.rules.*;
import org.eclipse.jface.text.*;


import br.poli.ecomp.ads.sablecceditor.SableCCPlugin;
import br.poli.ecomp.ads.sablecceditor.editors.color.SableCCColorManager;
import br.poli.ecomp.ads.sablecceditor.editors.rules.SableCCWhitespaceDetector;
import br.poli.ecomp.ads.sablecceditor.preferences.PreferenceConstants;

public class SableCCScanner extends RuleBasedScanner {
	
	public static final String[] SableCC_KEYWORDS = new String[] {
"Alternative","And","Any","Context","Dangling","Diff","Diff","Group","Ignored",
"Inlined","Investigator","Language","Left","Lexer","Longest","Look","Not","New","Null",
"Parser","Priority","Production","Restartable","Right","Separator","Shortest","Start","Token",
"Transformation","Tree"};
	
	public SableCCScanner() {
		
	}
	public SableCCScanner(SableCCColorManager manager) {
		IPreferenceStore prefs = SableCCPlugin.getDefault().getPreferenceStore();
		IToken keyword = new Token(new TextAttribute(
				manager.getColor(PreferenceConverter.getColor(prefs,
						PreferenceConstants.COLOR_KEYWORD))));
		IToken other = new Token(new TextAttribute(
				manager.getColor(PreferenceConverter.getColor(prefs,
						PreferenceConstants.COLOR_DEFAULT))));
		IToken string = new Token(new TextAttribute(
				manager.getColor(PreferenceConverter.getColor(prefs,
						PreferenceConstants.COLOR_STRING))));
		IToken comment = new Token(new TextAttribute(
				manager.getColor(PreferenceConverter.getColor(prefs,
						PreferenceConstants.COLOR_COMMENT))));
		IToken operation = new Token(new TextAttribute(
				manager.getColor(PreferenceConverter.getColor(prefs,
						PreferenceConstants.COLOR_OPERATION))));
		
		List rules = new ArrayList();
		// Add rule for comments
		rules.add(new MultiLineRule("/*", "*/", comment));		
		rules.add(new EndOfLineRule("//", comment));
		// Add rule for strings and character constants.
		rules.add(new SingleLineRule("\"", "\"", string, '\\'));
		rules.add(new SingleLineRule("'", "'", string, '\\'));
		// Add generic whitespacerule.
		rules.add(new WhitespaceRule(new SableCCWhitespaceDetector()));
		// Add Operation
		rules.add(new SingleLineRule("{", ":}", operation, '\\'));

		// Add KeyWords
		WordRule wordRule = new WordRule(new WordDetector(), other);
		for (int i = 0; i < SableCC_KEYWORDS.length; i++) {
			wordRule.addWord(SableCC_KEYWORDS[i], keyword);
		}
		rules.add(wordRule);
		IRule[] result = new IRule[rules.size()];
		rules.toArray(result);
		setRules(result);
	}
	
}
