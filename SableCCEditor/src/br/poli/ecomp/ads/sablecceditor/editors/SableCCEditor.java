package br.poli.ecomp.ads.sablecceditor.editors;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.MarkerUtilities;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import br.poli.ecomp.ads.sablecceditor.SableCCPlugin;
import br.poli.ecomp.ads.sablecceditor.editors.color.SableCCColorManager;
import br.poli.ecomp.ads.sablecceditor.editors.configuration.SableCCSourceViewerConfiguration;
import br.poli.ecomp.ads.sablecceditor.editors.marker.MarkingErrorHandler;
import br.poli.ecomp.ads.sablecceditor.editors.outline2.ReadmeContentOutlinePage;
import br.poli.ecomp.ads.sablecceditor.editors.scanner.SableCCScanner;
import br.poli.ecomp.ads.sablecceditor.preferences.PreferenceConstants;

public class SableCCEditor extends TextEditor {

	private SableCCColorManager colorManager;
	private ProjectionSupport projectionSupport;
	private Annotation[] oldAnnotations;
	private ProjectionAnnotationModel annotationModel;
	private static SableCCScanner sableCCScanner;
	private ReadmeContentOutlinePage page;
	public static final String ERROR_MARKER_ID = SableCCPlugin.PLUGIN_ID+".sableccProblem";
	
	public SableCCEditor() {
	
		super();
		setDocumentProvider(new SableCCDocumentProvider());
		this.sableCCScanner = new SableCCScanner(this.colorManager);
		SableCCPlugin.getDefault().getPreferenceStore()
				.addPropertyChangeListener(new IPropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent event) {
						System.out.println(event.getProperty());
						if (event.getProperty().equals(
								PreferenceConstants.COLOR_COMMENT)
								|| event.getProperty().equals(
										PreferenceConstants.COLOR_DEFAULT)
								|| event.getProperty().equals(
										PreferenceConstants.COLOR_KEYWORD)
								|| event.getProperty().equals(
										PreferenceConstants.COLOR_STRING)
								|| event.getProperty().equals(
										PreferenceConstants.COLOR_OPERATION)) {

							try {
								String content = getDocumentProvider()
										.getDocument(getEditorInput()).get();
								init(getEditorSite(), getEditorInput());
								getDocumentProvider().getDocument(
										getEditorInput()).set(content);
							} catch (PartInitException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					}
				});
	}

	public static SableCCScanner getSableCCScanner() {
		return sableCCScanner;
	}

	protected void adjustHighlightRange(int offset, int length) {
		ISourceViewer viewer = getSourceViewer();
		if (viewer instanceof ITextViewerExtension5) {
			ITextViewerExtension5 extension = (ITextViewerExtension5) viewer;
			extension.exposeModelRange(new Region(offset, length));
		}
	}
	
	@Override
	public void doSave(IProgressMonitor progressMonitor) {
		// TODO Auto-generated method stub
		super.doSave(progressMonitor);
		this.validateAndMark();
	}

	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}

	@Override
	protected void initializeEditor() {
		super.initializeEditor();
		colorManager = new SableCCColorManager();
		setSourceViewerConfiguration(new SableCCSourceViewerConfiguration(
				colorManager, this));
	}

	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		ProjectionViewer viewer = (ProjectionViewer) getSourceViewer();
		projectionSupport = new ProjectionSupport(viewer,
				getAnnotationAccess(), getSharedColors());
		projectionSupport.install();
		// turn projection mode on
		viewer.doOperation(ProjectionViewer.TOGGLE);
		annotationModel = viewer.getProjectionAnnotationModel();

	}

	public ProjectionAnnotationModel getAnnotationModel() {
		return annotationModel;
	}

	@SuppressWarnings("all")
	public void updateFoldingStructure(ArrayList positions) {
		Annotation[] annotations = new Annotation[positions.size()];
		// this will hold the new annotations along
		// with their corresponding positions
		HashMap newAnnotations = new HashMap();
		for (int i = 0; i < positions.size(); i++) {
			ProjectionAnnotation annotation = new ProjectionAnnotation();
			newAnnotations.put(annotation, positions.get(i));
			annotations[i] = annotation;
		}
		annotationModel.modifyAnnotations(oldAnnotations, newAnnotations, null);
		oldAnnotations = annotations;
	}

	protected ISourceViewer createSourceViewer(Composite parent,
			IVerticalRuler ruler, int styles) {
		ISourceViewer viewer = new ProjectionViewer(parent, ruler,
				getOverviewRuler(), isOverviewRulerVisible(), styles);
		getSourceViewerDecorationSupport(viewer);

		return viewer;
	}
	
	public Object getAdapter(Class key) {
        if (key.equals(IContentOutlinePage.class)) {
            IEditorInput input = getEditorInput();
            if (input instanceof IFileEditorInput) {
                page = new ReadmeContentOutlinePage(((IFileEditorInput) input)
                        .getFile());
                page.setEditor(this); //Adicionado
                return page;
            }
        }
        return super.getAdapter(key);
    }
	protected void doSetInput(IEditorInput newInput) throws CoreException
	{
		super.doSetInput(newInput);
		validateAndMark();
		System.out.println("doSetInput");
	}
	
	protected void validateAndMark()
	{
		IFile file = ((IFileEditorInput) getEditorInput()).getFile();
		
		IDocument document = this.getDocumentProvider().getDocument(getEditorInput());
		MarkingErrorHandler markingError = new MarkingErrorHandler(file, document);
		markingError.markErrors(true);
	/*	try {
			System.out.println("file: "+file.getContents().toString());
			IEditorInput input = this.getEditorInput();
	        IResource resource = (IResource) ((IAdaptable) input).getAdapter(IResource.class);
	        //resource.deleteMarkers(SableCCPlugin.PLUGIN_ID+".myproblem", true, IResource.DEPTH_INFINITE);
	        MarkerUtilities.createMarker(resource, map, SableCCPlugin.PLUGIN_ID+".myproblem");
		    file = null;
		    //resource.deleteMarkers(SableCCPlugin.PLUGIN_ID+".myproblem", true, IResource.DEPTH_INFINITE);
		} catch (CoreException e) {
		    //something went terribly wrong
			System.out.println("erroM");
		}
*/
	}


}
