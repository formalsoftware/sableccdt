package br.poli.ecomp.ads.sablecceditor.preferences;

import br.poli.ecomp.ads.sablecceditor.SableCCPlugin;
import br.poli.ecomp.ads.sablecceditor.editors.color.ISableCCConstants;

/**
 * Constant definitions for plug-in preferences
 */
public class PreferenceConstants {
	public static final String PREFIX = SableCCPlugin.PLUGIN_ID + ".";

	public static final String DIRETORIO_COMPILACAO = PREFIX
			+ "diretorio_compilacao";

	public static final String PREFIX_COLOR = PREFIX + "color.";

	public static final String COLOR_DEFAULT = PREFIX_COLOR
			+ ISableCCConstants.DEFAULT;
	public static final String COLOR_KEYWORD = PREFIX_COLOR
			+ ISableCCConstants.KEYWORD;
	public static final String COLOR_STRING = PREFIX_COLOR
			+ ISableCCConstants.STRING;
	public static final String COLOR_COMMENT = PREFIX_COLOR
			+ ISableCCConstants.COMMENT;
	public static final String COLOR_OPERATION = PREFIX_COLOR
	+ ISableCCConstants.OPERATION;
}
