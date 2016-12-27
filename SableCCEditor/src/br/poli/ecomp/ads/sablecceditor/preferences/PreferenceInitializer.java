package br.poli.ecomp.ads.sablecceditor.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import br.poli.ecomp.ads.sablecceditor.SableCCPlugin;
import br.poli.ecomp.ads.sablecceditor.editors.color.ISableCCConstants;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
	 * initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore aStore = SableCCPlugin.getDefault().getPreferenceStore();
		aStore.setDefault(PreferenceConstants.DIRETORIO_COMPILACAO,
				ISableCCConstants.DIRETORIO_DEFAULT);
		PreferenceConverter.setDefault(aStore,
				PreferenceConstants.COLOR_DEFAULT, ISableCCConstants.DEFAULT);
		PreferenceConverter.setDefault(aStore,
				PreferenceConstants.COLOR_KEYWORD, ISableCCConstants.KEYWORD);
		PreferenceConverter.setDefault(aStore,
				PreferenceConstants.COLOR_STRING, ISableCCConstants.STRING);
		PreferenceConverter.setDefault(aStore,
				PreferenceConstants.COLOR_COMMENT, ISableCCConstants.COMMENT);
		PreferenceConverter.setDefault(aStore,
				PreferenceConstants.COLOR_OPERATION, ISableCCConstants.OPERATION);
	}

}
