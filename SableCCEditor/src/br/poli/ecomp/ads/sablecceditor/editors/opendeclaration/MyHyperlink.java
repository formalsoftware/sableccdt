package br.poli.ecomp.ads.sablecceditor.editors.opendeclaration;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;

import br.poli.ecomp.ads.sablecceditor.editors.SableCCEditor;
import br.poli.ecomp.adssableccbuilder.builder.Element;

public class MyHyperlink implements IHyperlink {
    private String location;
    private IRegion region;
    private SableCCEditor editor;
    private Element definicao;

    public MyHyperlink(IRegion region, String text, SableCCEditor editor, Element definicao) {

        this.region= region;
        this.location = text;
        this.editor = editor;
        this.definicao = definicao;
    }

    public IRegion getHyperlinkRegion() {
        return region;
    }

    public void open() {
        if(location!=null)
        {
        	System.out.println("OPEN");
            //int offset=MyAST.get().getOffset(location);
            //TextEditor editor=getActiveEditor();
            //editor.selectAndReveal(offset,0);
            //editor.setFocus();
        	IDocument document = this.editor.getDocumentProvider().getDocument(this.editor.getEditorInput());
        	int beginElementOffset;
			try {
				beginElementOffset = document.getLineOffset(definicao.getLine()-1)+definicao.getPos();
				if(beginElementOffset-1>=0){
					beginElementOffset--;
				}
				this.editor.selectAndReveal(beginElementOffset, definicao.getToken().length());
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

    public String getTypeLabel() {
        return null;
    }
    
    public String getText() {
        return this.location;
    }


    public String getHyperlinkText() {
        return null;
    }
}
