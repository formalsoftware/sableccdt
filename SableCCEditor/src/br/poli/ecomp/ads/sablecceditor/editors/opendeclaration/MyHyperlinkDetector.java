package br.poli.ecomp.ads.sablecceditor.editors.opendeclaration;

import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;

import br.poli.ecomp.ads.sablecceditor.editors.SableCCEditor;
import br.poli.ecomp.adssableccbuilder.builder.Echo;
import br.poli.ecomp.adssableccbuilder.builder.Element;

public class MyHyperlinkDetector implements IHyperlinkDetector {
	IFile file;
	SableCCEditor editor;
	public MyHyperlinkDetector(IFile file, SableCCEditor editor){
		this.file = file;
		this.editor = editor;
		
	}

	private IDocument document;
	
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, 
	    boolean canShowMultipleHyperlinks) {
		IDocument document; 
	    IRegion lineInfo;
	    String line;
	    canShowMultipleHyperlinks = true;
	    
	    Echo echo = new Echo();
		echo.file = file;
		echo.printAST();
		
	    document = textViewer.getDocument();
		//REGIAO SELECIONADA
		//IRegion selecao = getSelection(region,document);
	    // O Metodo retornara a region selecionada caso seja um token ja definido
	    IRegion selecao = getSelectionRegion(region,document,echo.links.iterator());
	    IHyperlink hyperLink = getHyperLink(document,selecao,echo.elementos);
		if(hyperLink==null){
			return null;
		}
		IHyperlink[] arrayLinks = new MyHyperlink[1];
		arrayLinks[0] = hyperLink;
		
		Iterator<Element> iterator = echo.links.iterator();
		while(iterator.hasNext()){
			int offset = 0;
			Element el = iterator.next();
			try {
				offset = document.getLineOffset(el.getLine()-1)+el.getPos();
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return arrayLinks;

	    }
	
	public IRegion getSelectionRegion(IRegion region, IDocument document, Iterator<Element> it){
		IRegion newRegion = null;
		
		try {
			IRegion lineInfo= document.getLineInformationOfOffset(region.getOffset());
			int currentOffset = region.getOffset();
			int beginElementOffset = 0;
			int endElementOffset = 0;
			Element word = null;
			
			while(it.hasNext()){
				Element el = it.next();
				beginElementOffset = document.getLineOffset(el.getLine()-1)+el.getPos();
				endElementOffset = beginElementOffset+el.getToken().length();
				if(currentOffset>=beginElementOffset && currentOffset<=endElementOffset){
					word = el;
					break;
				}
			}
			
			if(word==null){
				return null;
			}if(beginElementOffset-1>=0){
				beginElementOffset--;
			}
			 newRegion = new Region(beginElementOffset,word.getToken().length());
			 
			 
		} catch (org.eclipse.jface.text.BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return newRegion;
	}
		
		
		public String getTextFromRegion(IRegion region, IDocument document){
			String text = null;
			if(region == null){
				return null;
			}
			
			try {
				text = document.get(region.getOffset(), region.getLength());
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return text;
		}
		
		//PEGA O CONTEUDO DA REGION E COMPARA COM AS DEFINIÇÕES DO DOCUMENTO
		public IHyperlink getHyperLink(IDocument document, IRegion regiaoSelecao, LinkedList<Element> lista){
			Iterator<Element> iterator = lista.iterator();
			String key = this.getTextFromRegion(regiaoSelecao, document);
			if(key == null){
				return null;
			}
			key = key.trim();
			IHyperlink myLink = null;
			while(iterator.hasNext()){
	            Element elemento = iterator.next();
	            if(key.equals(elemento.getToken())){
	            	myLink = new MyHyperlink(regiaoSelecao,key,this.editor,elemento);
	            }
			}
			return myLink;
		}
	
	
	}
