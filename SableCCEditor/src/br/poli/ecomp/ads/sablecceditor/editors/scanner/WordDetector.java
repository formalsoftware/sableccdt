/**
 * <small>
 * <p><i>Copyright (C) 2005 Torsten Juergeleit, 
 * All rights reserved. </i></p>
 * 
 * <p>USE OF THIS CONTENT IS GOVERNED BY THE TERMS AND CONDITIONS OF THIS
 * AGREEMENT AND/OR THE TERMS AND CONDITIONS OF LICENSE AGREEMENTS OR NOTICES
 * INDICATED OR REFERENCED BELOW. BY USING THE CONTENT, YOU AGREE THAT YOUR USE
 * OF THE CONTENT IS GOVERNED BY THIS AGREEMENT AND/OR THE TERMS AND CONDITIONS
 * OF ANY APPLICABLE LICENSE AGREEMENTS OR NOTICES INDICATED OR REFERENCED
 * BELOW. IF YOU DO NOT AGREE TO THE TERMS AND CONDITIONS OF THIS AGREEMENT AND
 * THE TERMS AND CONDITIONS OF ANY APPLICABLE LICENSE AGREEMENTS OR NOTICES
 * INDICATED OR REFERENCED BELOW, THEN YOU MAY NOT USE THE CONTENT.</p>
 * 
 * <p>This Content is Copyright (C) 2005 Torsten Juergeleit, 
 * and is provided to you under the terms and conditions of the Common Public 
 * License Version 1.0 ("CPL"). A copy of the CPL is provided with this Content 
 * and is also available at 
 *     <a href="http://www.eclipse.org/legal/cpl-v10.html">
 *         http://www.eclipse.org/legal/cpl-v10.html </a>.
 * 
 * For purposes of the CPL, "Program" will mean the Content.</p>
 * 
 * <p>Content includes, but is not limited to, source code, object code,
 * documentation and any other files in this distribution.</p>
 * 
 * </small>
 */
package br.poli.ecomp.ads.sablecceditor.editors.scanner;

import org.eclipse.jface.text.rules.IWordDetector;

/**
 * A ANTLR aware word detector.
 * 
 * @author Torsten Juergeleit
 */
public class WordDetector implements IWordDetector {

	/**
     * Determines if the specified character is
     * permissible as the first character in a ANTLR identifier.
     * A character may start a ANTLR identifier if and only if
     * it is one of the following:
     * <ul>
     * <li>a letter
     * <li>a connecting punctuation character ("_")
     * </ul>
     *
     * @param aChar  the character to be tested.
     * @return true if the character may start a ANTLR identifier;
     *          false otherwise.
     * @see java.lang.Character#isLetter(char)
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordStart
	 */
	public boolean isWordStart(char aChar) {
		return Character.isLetter(aChar) || aChar == '_';
	}
	
	/**
     * Determines if the specified character may be part of a ANTLR
     * identifier as other than the first character.
     * A character may be part of a ANTLR identifier if and only if
     * it is one of the following:
     * <ul>
     * <li>a letter
     * <li>a digit
     * <li>a connecting punctuation character ("_").
     * </ul>
     * 
     * @param aChar  the character to be tested.
     * @return true if the character may be part of a ANTLR identifier; 
     *          false otherwise.
     * @see java.lang.Character#isLetterOrDigit(char)
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordPart
	 */
	public boolean isWordPart(char aChar) {
		return Character.isLetterOrDigit(aChar) || aChar == '_';
	}
}
