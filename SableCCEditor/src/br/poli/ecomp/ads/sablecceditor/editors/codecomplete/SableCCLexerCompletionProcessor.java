package br.poli.ecomp.ads.sablecceditor.editors.codecomplete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

import br.poli.ecomp.ads.sablecceditor.editors.scanner.SableCCScanner;
import br.poli.ecomp.adssableccbuilder.builder.Echo;
import br.poli.ecomp.adssableccbuilder.builder.Element;
import br.poli.ecomp.adssableccbuilder.builder.SableCCElements;

public class SableCCLexerCompletionProcessor implements
		IContentAssistProcessor {
	
	private IFile file;
	private static final char[] AUTO_ACTIVATION_CHARS = new char[] {
		   '=','|'};
	
	public SableCCLexerCompletionProcessor(IFile file){
		this.file = file;
	}

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset) {
		
		//PEGA OS ELEMENTOS DA AST
		Echo echo = new Echo();
		echo.file = file;
		echo.printAST();
		

		List<ICompletionProposal> proposals = 
            new ArrayList<ICompletionProposal>();
		
		String prefix = new SableCCTextGuesser(viewer.getDocument(), offset, false).getText();

		
        Set<String> set = SableCCElements.lexicos.keySet();
        List<String> fgProposals = new ArrayList<String>();
        
        //Lista com os elementos do documento obtidos pela TREE
 		LinkedList<Element> listaElementos = echo.lexers;
 		Iterator<Element> elIter = listaElementos.iterator();
 		while(elIter.hasNext()){
 			Element elemento = (Element) elIter.next();
 			fgProposals.add(elemento.getToken());
 		}
        
        // Iterate over the Set to see what it contains.
         Iterator<String> iter = set.iterator();
        while (iter.hasNext())
        {
            Object o = iter.next();
            fgProposals.add(o.toString());
        }

        for (String fgProposal : fgProposals) {
        	if(fgProposal.toLowerCase().contains(prefix.toLowerCase())){ 
            proposals.add(new CompletionProposal(fgProposal,offset-prefix.length(), prefix.length(), fgProposal.length()));
        }
        }
        Collections.sort(proposals, PROPOSAL_COMPARATOR);
        
        return proposals.toArray(new ICompletionProposal[proposals.size()]);
	}
	
	private static final Comparator PROPOSAL_COMPARATOR = new Comparator() {
		public int compare(Object aProposal1, Object aProposal2) {
			String text1 = ((CompletionProposal)aProposal1).getDisplayString();
			String text2 = ((CompletionProposal)aProposal2).getDisplayString();
			return text1.compareTo(text2);
		}

		public boolean equals(Object aProposal) {
			return false;
		}
	};

	@Override
	public IContextInformation[] computeContextInformation(ITextViewer arg0,
			int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		return AUTO_ACTIVATION_CHARS;
	}
	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IContextInformationValidator getContextInformationValidator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}

}
