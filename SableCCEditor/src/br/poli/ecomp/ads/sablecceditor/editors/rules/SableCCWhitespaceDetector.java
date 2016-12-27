package br.poli.ecomp.ads.sablecceditor.editors.rules;

import org.eclipse.jface.text.rules.IWhitespaceDetector;

public class SableCCWhitespaceDetector implements IWhitespaceDetector {

	public boolean isWhitespace(char aChar) {
		return Character.isWhitespace(aChar);
	}
//	
//	public boolean isWhitespace(char c) {
//		return (c == ' ' || c == '\t' || c == '\n' || c == '\r');
//	}
}
