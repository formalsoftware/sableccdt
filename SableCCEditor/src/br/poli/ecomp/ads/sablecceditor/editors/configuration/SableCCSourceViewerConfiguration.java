package br.poli.ecomp.ads.sablecceditor.editors.configuration;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.URLHyperlinkDetector;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.IFileEditorInput;

import br.poli.ecomp.ads.sablecceditor.SableCCPlugin;
import br.poli.ecomp.ads.sablecceditor.editors.SableCCEditor;
import br.poli.ecomp.ads.sablecceditor.editors.codecomplete.SableCCContentAssistProcessor;
import br.poli.ecomp.ads.sablecceditor.editors.codecomplete.SableCCLexerCompletionProcessor;
import br.poli.ecomp.ads.sablecceditor.editors.codecomplete.SableCCPaserCompletionProcessor;
import br.poli.ecomp.ads.sablecceditor.editors.color.ISableCCConstants;
import br.poli.ecomp.ads.sablecceditor.editors.color.SableCCColorManager;
import br.poli.ecomp.ads.sablecceditor.editors.opendeclaration.MyHyperlinkDetector;
import br.poli.ecomp.ads.sablecceditor.editors.scanner.SableCCPartitionScanner;
import br.poli.ecomp.ads.sablecceditor.editors.scanner.SableCCScanner;
import br.poli.ecomp.ads.sablecceditor.preferences.PreferenceConstants;

public class SableCCSourceViewerConfiguration extends SourceViewerConfiguration {
	private SableCCDoubleClickStrategy doubleClickStrategy;
	private SableCCScanner scanner;
	private SableCCColorManager colorManager;
	private SableCCEditor editor;

	public SableCCSourceViewerConfiguration(SableCCColorManager colorManager,
			SableCCEditor sableCCEditor) {
		this.colorManager = colorManager;
		this.editor = sableCCEditor;
	}
	
	@Override
	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		IFile file = ((IFileEditorInput) this.editor.getEditorInput()).getFile();
	    return new IHyperlinkDetector[] { new MyHyperlinkDetector(file,editor), new URLHyperlinkDetector() };
	}


	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return SableCCPartitionScanner.PARTITION_TYPES;
	}

	public ITextDoubleClickStrategy getDoubleClickStrategy(
			ISourceViewer sourceViewer, String contentType) {
		if (doubleClickStrategy == null)
			doubleClickStrategy = new SableCCDoubleClickStrategy();
		return doubleClickStrategy;
	}

	protected SableCCScanner getXMLScanner() {
		if (scanner == null) {
			scanner = new SableCCScanner(colorManager);
			scanner.setDefaultReturnToken(new Token(new TextAttribute(
					colorManager.getColor(ISableCCConstants.DEFAULT))));
		}
		return scanner;
	}

	public IAutoEditStrategy[] getAutoEditStrategies(
			ISourceViewer sourceViewer, String contentType) {
		IAutoEditStrategy strategy = (IDocument.DEFAULT_CONTENT_TYPE
				.equals(contentType) ? new SableCCAutoIndentStrategy()
				: new DefaultIndentLineAutoEditStrategy());
		return new IAutoEditStrategy[] { strategy };
	}

	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		IFile file = ((IFileEditorInput) this.editor.getEditorInput()).getFile();
		ContentAssistant assistant = new ContentAssistant();
		assistant.setContentAssistProcessor(new SableCCContentAssistProcessor(
				editor), IDocument.DEFAULT_CONTENT_TYPE);
				
		assistant.setContentAssistProcessor(
				new SableCCPaserCompletionProcessor(file),
				SableCCPartitionScanner.PARSER);
		
		assistant.setContentAssistProcessor(
				new SableCCLexerCompletionProcessor(file),
				SableCCPartitionScanner.LEXER);

		
		assistant.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
		assistant.setAutoActivationDelay(10);
		assistant.enableAutoActivation(true);
	    assistant.setProposalPopupOrientation(IContentAssistant.CONTEXT_INFO_BELOW);
	    assistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_BELOW);
	    System.out.println("getContentAssistant");
		return assistant;
	}

	public IReconciler getReconciler(ISourceViewer sourceViewer) {

		SablaeCCReconcilingStrategy strategy = new SablaeCCReconcilingStrategy();
		strategy.setEditor(editor);
		MonoReconciler reconciler = new MonoReconciler(strategy, false);
		return reconciler;

	}
	
	public IPresentationReconciler getPresentationReconciler(
			ISourceViewer sourceViewer) {

		PresentationReconciler reconciler = new PresentationReconciler();
		IPreferenceStore prefs = SableCCPlugin.getDefault().getPreferenceStore();
		
		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(SableCCEditor.getSableCCScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
		
		dr = new DefaultDamagerRepairer(SableCCEditor.getSableCCScanner());
		reconciler.setDamager(dr, SableCCPartitionScanner.LEXER);
		reconciler.setRepairer(dr, SableCCPartitionScanner.LEXER);
		
		dr = new DefaultDamagerRepairer(SableCCEditor.getSableCCScanner());
		reconciler.setDamager(dr, SableCCPartitionScanner.PARSER);
		reconciler.setRepairer(dr, SableCCPartitionScanner.PARSER);		

		NonRuleBasedDamagerRepairer ndr =
			new NonRuleBasedDamagerRepairer(
					new TextAttribute(colorManager.getColor(PreferenceConverter.getColor(prefs, PreferenceConstants.COLOR_STRING))));
		reconciler.setDamager(dr, SableCCPartitionScanner.STRING);
		reconciler.setRepairer(dr, SableCCPartitionScanner.STRING);

		ndr =new NonRuleBasedDamagerRepairer(
				new TextAttribute(colorManager.getColor(PreferenceConverter.getColor(prefs, PreferenceConstants.COLOR_COMMENT))));
		reconciler.setDamager(ndr, SableCCPartitionScanner.SABLECC_COMMENT);
		reconciler.setRepairer(ndr, SableCCPartitionScanner.SABLECC_COMMENT);

		return reconciler;
	}
}