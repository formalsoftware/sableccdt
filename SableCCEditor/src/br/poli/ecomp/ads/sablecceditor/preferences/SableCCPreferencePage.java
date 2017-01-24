package br.poli.ecomp.ads.sablecceditor.preferences;

import java.io.File;
import org.eclipse.jface.preference.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import br.poli.ecomp.ads.sablecceditor.SableCCPlugin;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class SableCCPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	private ColorFieldEditor corTexto;
	private ColorFieldEditor corKeyword;
	private ColorFieldEditor corString;
	private ColorFieldEditor corComentario;
	private ColorFieldEditor corOperation;
	

	public SableCCPreferencePage() {
		super(GRID);
		setPreferenceStore(SableCCPlugin.getDefault().getPreferenceStore());
		setDescription("Página de Preferências SableCCEditor");
	}

	public void createFieldEditors() {
		this.corTexto = new ColorFieldEditor(PreferenceConstants.COLOR_DEFAULT,
				"TEXT", getFieldEditorParent());
		addField(this.corTexto);
		this.corKeyword = new ColorFieldEditor(
				PreferenceConstants.COLOR_KEYWORD, "Keyword",
				getFieldEditorParent());
		addField(this.corKeyword);
		this.corString = new ColorFieldEditor(PreferenceConstants.COLOR_STRING,
				"String", getFieldEditorParent());
		addField(this.corString);
		this.corComentario = new ColorFieldEditor(
				PreferenceConstants.COLOR_COMMENT, "Comment",
				getFieldEditorParent());
		addField(this.corComentario);
		this.corOperation= new ColorFieldEditor(
				PreferenceConstants.COLOR_OPERATION, "Operation",
				getFieldEditorParent());
		addField(this.corOperation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

}