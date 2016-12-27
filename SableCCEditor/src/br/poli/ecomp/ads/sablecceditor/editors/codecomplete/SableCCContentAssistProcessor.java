package br.poli.ecomp.ads.sablecceditor.editors.codecomplete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

import br.poli.ecomp.ads.sablecceditor.editors.SableCCEditor;
import br.poli.ecomp.ads.sablecceditor.editors.scanner.SableCCScanner;

public class SableCCContentAssistProcessor implements IContentAssistProcessor {
	
	private static final char[] AUTO_ACTIVATION_CHARS = new char[] {
		   '(', '|', ':' };
	
	private SableCCEditor fEditor;

	public SableCCContentAssistProcessor(SableCCEditor fEditor) {
		super();
		this.fEditor = fEditor;
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
	
	
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset) {
		
		List<ICompletionProposal> proposals = 
            new ArrayList<ICompletionProposal>();
		
		String prefix = new SableCCTextGuesser(viewer.getDocument(), offset, false).getText();
		String [] fgProposals = SableCCScanner.SableCC_KEYWORDS;
		String fgProposal ="";
		
        for (int i= 0; i < fgProposals.length; i++) {
        	if(fgProposals[i].toLowerCase().contains(prefix.toLowerCase())){ 
        		fgProposal = fgProposals[i]+"\n\n//"+fgProposals[i];
            proposals.add(new CompletionProposal(fgProposal,offset-prefix.length(), prefix.length(), fgProposal.length()));
        	}
    	}
        Collections.sort(proposals, PROPOSAL_COMPARATOR);

        return proposals.toArray(new ICompletionProposal[proposals.size()]);
	}

	
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
