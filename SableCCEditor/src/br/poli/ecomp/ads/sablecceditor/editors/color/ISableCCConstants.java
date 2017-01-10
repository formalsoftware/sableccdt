package br.poli.ecomp.ads.sablecceditor.editors.color;

import org.eclipse.swt.graphics.RGB;

/**
 * enumeracao das cores da sintaxe highlight
 * @author GUSTAVO, RENATO
 *
 */

public interface ISableCCConstants {
	public static final String DIRETORIO_DEFAULT = "C:\\SABLECC";
	public static final RGB DEFAULT = new RGB(0, 0, 0);
	public static final RGB KEYWORD = new RGB(0, 0, 255);
	public static final RGB STRING = new RGB(255, 0, 0);
	public static final RGB COMMENT = new RGB(0, 128, 0);
	public static final RGB OPERATION = new RGB(128, 128, 0);	
}
