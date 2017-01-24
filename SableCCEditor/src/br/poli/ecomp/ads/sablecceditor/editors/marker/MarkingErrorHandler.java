
/*
 * Created on Oct 11, 2004
 */
package br.poli.ecomp.ads.sablecceditor.editors.marker;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.ui.texteditor.MarkerUtilities;

import br.poli.ecomp.ads.sablecceditor.SableCCPlugin;
import br.poli.ecomp.adssableccbuilder.builder.Echo;
import br.poli.ecomp.adssableccbuilder.builder.MarkParserException;

/**
 * Esta classe refere-se à marcação de erros do código.
 * @author GUSTAVO, RENATO
 *
 */
public class MarkingErrorHandler
{

	public static final String ERROR_MARKER_ID = SableCCPlugin.PLUGIN_ID+".myproblem";

	private IFile file;
	private IDocument document;
	private Echo parser;

	public MarkingErrorHandler(IFile file, IDocument document)
	{
		super();
		this.file = file;
		this.document = document;
		parser = new Echo();
		this.parser.file = file;
	}
	/**
	 * Este método deleta todos os marcadores do código
	 */
	public void removeExistingMarkers()
	{
		try
		{
			file.deleteMarkers(ERROR_MARKER_ID, true, IResource.DEPTH_ZERO);
		}
		catch (CoreException e1)
		{
			e1.printStackTrace();
		}
	}
	
	/**
	 * Este método verifica se há erros nos marcadores.
	 * @param isFatal
	 */

	public void markErrors(boolean isFatal)
	{
		this.removeExistingMarkers();
		this.parser.getError();
		if(this.parser.exception!=null){
			System.out.println();
			MarkParserException e = this.parser.exception;
			Map map = new HashMap();
			int lineNumber = e.getLinha();
			int columnNumber = e.getPos();
			MarkerUtilities.setLineNumber(map, lineNumber);
			MarkerUtilities.setMessage(map, e.getMessage());
			map.put(IMarker.LOCATION, file.getFullPath().toString());
	
			Integer charStart = getCharStart(lineNumber, columnNumber);
			if (charStart != null)
				map.put(IMarker.CHAR_START, charStart);
	
			Integer charEnd = getCharEnd(lineNumber, columnNumber);
			if (charEnd != null)
				map.put(IMarker.CHAR_END, charEnd);
	
			map.put(IMarker.SEVERITY, new Integer(IMarker.SEVERITY_ERROR));
	
			try
			{
				MarkerUtilities.createMarker(file, map, ERROR_MARKER_ID);
			}
			catch (CoreException ee)
			{
				ee.printStackTrace();
			}
		
		}

		//return validationError;

	}

	private Integer getCharEnd(int lineNumber, int columnNumber)
	{
		try
		{
			return new Integer(document.getLineOffset(lineNumber - 1) + columnNumber);
		}
		catch (BadLocationException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	private Integer getCharStart(int lineNumber, int columnNumber)
	{
		try
		{
			int lineStartChar = document.getLineOffset(lineNumber - 1);
			Integer charEnd = getCharEnd(lineNumber, columnNumber);
			if (charEnd != null)
			{
				ITypedRegion typedRegion = document.getPartition(charEnd.intValue()-2);
				int partitionStartChar = typedRegion.getOffset();
				return new Integer(partitionStartChar);
			}
			else
				return new Integer(lineStartChar);
		}
		catch (BadLocationException e)
		{
			e.printStackTrace();
			return null;
		}
	}

}
