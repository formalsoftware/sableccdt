package br.poli.ecomp.ads.sablecceditor.editors.configuration;

import java.util.ArrayList;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.swt.widgets.Display;

import br.poli.ecomp.ads.sablecceditor.editors.SableCCEditor;
import br.poli.ecomp.ads.sablecceditor.editors.scanner.SableCCScanner;

public class SablaeCCReconcilingStrategy implements IReconcilingStrategy {

	private SableCCEditor editor;

	private IDocument fDocument;

	/** holds the calculated positions */
	protected final ArrayList fPositions = new ArrayList();

	/** The offset of the next character to be read */
	protected int fOffset;

	/** The end offset of the range to be scanned */
	protected int fRangeEnd;

	public SableCCEditor getEditor() {
		return editor;
	}

	public void setEditor(SableCCEditor editor) {
		this.editor = editor;
	}

	public void initialReconcile() {
		fOffset = 0;
		fRangeEnd = fDocument.getLength();
		calculatePositions();
	}

	@Override
	public void reconcile(IRegion arg0) {
		initialReconcile();

	}

	@Override
	public void reconcile(DirtyRegion arg0, IRegion arg1) {
		initialReconcile();

	}

	@Override
	public void setDocument(IDocument document) {
		this.fDocument = document;

	}

	/**
	 * next character position - used locally and only valid while
	 * {@link #calculatePositions()} is in progress.
	 */
	protected int cNextPos = 0;

	/** number of newLines found by {@link #classifyTag()} */
	protected int cNewLines = 0;

	private final String[] TIPOS = SableCCScanner.SableCC_KEYWORDS;

	protected char cLastNLChar = ' ';

	protected int current = START;

	protected static final int START = 0;

	protected static final int LEXER = 1;

	protected static final int TOKEN = 10;

	protected static final int IGNORED = 11;

	protected static final int PARSER = 2;

	protected static final int PRODUCAO = 21;

	protected static final int EXPRESSAO = 21;

	protected static final int PRIORITY = 3;

	protected static final int LEAF_TAG = 2;

	protected static final int END_TAG = 3;

	protected static final int COMMENT_MULTILINHA = 4;

	protected static final int COMMENT_MONOLINHA = 5;

	/**
	 * uses {@link #fDocument}, {@link #fOffset} and {@link #fRangeEnd} to
	 * calculate {@link #fPositions}. About syntax errors: this method is not a
	 * validator, it is useful.
	 */
	protected void calculatePositions() {
		fPositions.clear();
		cNextPos = fOffset;

		try {
			positions(0);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		// Collections.sort(fPositions, new RangeTokenComparator());

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				editor.updateFoldingStructure(fPositions);
			}
		});
	}

	private String getLinha(int linha) throws BadLocationException {
		int offset = fDocument.getLineOffset(linha);
		int tamanho = fDocument.getLineLength(linha);
		return fDocument.get(offset, tamanho);
	}

	protected void emitPosition(int startOffset, int length) {
		fPositions.add(new Position(startOffset, length));
	}

	protected int positions(int offset) throws BadLocationException {
		String atual = "";
		int linha = 0;
		int offsetTipoFinal = 0;
		String tipoAtual = "";
//		boolean isEmitido = false;
		
		for (; linha < fDocument.getNumberOfLines(); linha++) {
			atual = getLinha(linha).trim();
			boolean isEmitido = false;
			
			for (int i = 0; i < TIPOS.length; i++) {
				 if (atual.startsWith(TIPOS[i])) {
					tipoAtual=TIPOS[i];
					
					 if(atual.startsWith("Language")){
							emitPosition(
									fDocument.getLineOffset(linha),
									fDocument.getLineOffset(fDocument.getNumberOfLines()-1)
											- fDocument.getLineOffset(linha));
							isEmitido=true;
							break;
						}
					 
					 // Parser
						if (atual.startsWith("Parser")) {
							int aux = linha;

							for (; aux < fDocument.getNumberOfLines(); aux++) {
								atual = getLinha(aux).trim();

								// Expressão
								if (atual.contains("=")) {
									int aux2 = aux;
									for (; aux2 < fDocument.getNumberOfLines(); aux2++) {
										atual = getLinha(aux2).trim();
										if (atual.endsWith(";")) {
											aux2++;
											break;
										}
									}
									emitPosition(
											fDocument.getLineOffset(aux),
											fDocument.getLineOffset(aux2)
													- fDocument.getLineOffset(aux));
								}
								if (atual.endsWith("//Parser")) {
									break;
								}
							}
						
							emitPosition(
									fDocument.getLineOffset(linha),
									fDocument.getLineOffset(aux+1)
											- fDocument.getLineOffset(linha));
							isEmitido=true;
							break;
						}
						
						//Outros
							int aux = linha;
							for (; aux < fDocument.getNumberOfLines(); aux++) {
								atual = getLinha(aux).trim();
								if (atual.endsWith("//"+tipoAtual)) {
									break;
								}
							}
							
							if(!(aux>=fDocument.getNumberOfLines())){
							emitPosition(
									fDocument.getLineOffset(linha),
									fDocument.getLineOffset(aux+1)
											- fDocument.getLineOffset(linha));
							}
							isEmitido=true;
							break;
						}
						
			}
			if (isEmitido)
				continue;

			
			// Comentário multilinha
			if (atual.startsWith("/*")) {
				int aux = linha;

				for (; linha < fDocument.getNumberOfLines(); linha++) {
					atual = getLinha(linha).trim();
					if (atual.endsWith("*/")) {
						break;
					}
				}

				emitPosition(
						fDocument.getLineOffset(aux),
						fDocument.getLineOffset(linha+1)
								- fDocument.getLineOffset(aux));
				continue;
			}
		}
		return offsetTipoFinal;
	}
}














































// protected int recursiveTokens(int offset) throws BadLocationException {
// String atual = "";
// boolean isTipo = false;
// int nivel = 0;
// int linha = 0;
// int offsetTipoInicial = 0;
// int offsetTipoFinal = 0;
// int linhaTipo = 0;
// boolean isEmitido = false;
// boolean isParserLexer = false;
// String tipoAtual = "";
// for (; linha < fDocument.getNumberOfLines(); linha++) {
// isEmitido = false;
// atual = getLinha(linha).trim();
// for (int i = 0; i < TIPOS.length; i++) {
// if (atual.startsWith(TIPOS[i])) {
// if (atual.endsWith(";")) {
// emitPosition(fDocument.getLineOffset(linha),
// fDocument.getLineLength(linha));
// tipoAtual = TIPOS[i];
// isEmitido = true;
// break;
// }
// if (TIPOS[i].equals("Parser") || TIPOS[i].equals("Lexer")) {
// if (isParserLexer) {
// emitPosition(
// fDocument.getLineOffset(offsetTipoInicial),
// fDocument.getLineLength(offsetTipoFinal));
// }
// offsetTipoInicial = fDocument.getLineOffset(linha);
// offsetTipoFinal = fDocument.getLineLength(linha);
// isEmitido = true;
// break;
// }
//
// int aux = linha;
// for (; linha < fDocument.getNumberOfLines(); aux++) {
// atual = getLinha(linha).trim();
// offsetTipoFinal += fDocument.getLineLength(linha);
// if (atual.endsWith(";")) {
// break;
// }
// }
// emitPosition(
// fDocument.getLineOffset(linha),
// fDocument.getLineOffset(aux)
// + fDocument.getLineOffset(aux));
// isEmitido = false;
// break;
// }
// }
// if (isEmitido)
// continue;
//
// // Comentário multilinha
// if (atual.startsWith("/*")) {
//
// int aux = linha;
// // offsetTipoFinal =0;
//
// for (; linha < fDocument.getNumberOfLines(); linha++) {
// atual = getLinha(linha).trim();
// // offsetTipoFinal += fDocument.getLineLength(linha);
// if (atual.endsWith("*/")) {
// break;
// }
// }
//
// emitPosition(
// fDocument.getLineOffset(aux),
// fDocument.getLineOffset(linha)
// - fDocument.getLineOffset(aux));
// continue;
// }
// // Expressão
// if (atual.contains("=")) {
// int aux = linha;
// // offsetTipoFinal =0;
// for (; aux < fDocument.getNumberOfLines(); aux++) {
// atual = getLinha(aux).trim();
// // offsetTipoFinal += fDocument.getLineLength(aux);
// if (atual.endsWith(";")) {
// break;
// }
// }
// emitPosition(
// fDocument.getLineOffset(linha),
// fDocument.getLineOffset(aux)
// - fDocument.getLineOffset(linha));
// continue;
// }
//
// }
// return offsetTipoFinal;
// }
//
// protected int classifyTag() {
// try {
// char ch = fDocument.getChar(cNextPos++);
// cNewLines = 0;
//
// // processing instruction?
// if ('*' == ch) {
// boolean flag = false;
// while (cNextPos < fRangeEnd) {
// ch = fDocument.getChar(cNextPos++);
// if (('/' == ch) && flag)
// break;
// flag = ('*' == ch);
// }
// return COMMENT_MULTILINHA;
// }
//
// if ('/' == ch) {
// while (cNextPos < fRangeEnd) {
// ch = fDocument.getChar(cNextPos++);
// if ('\n' == ch)
// break;
// }
// return COMMENT_MONOLINHA;
// }
//
// // consume whitespaces
// while ((' ' == ch) || ('\t' == ch) || ('\n' == ch) || ('\r' == ch)) {
// ch = fDocument.getChar(cNextPos++);
// if (cNextPos > fRangeEnd)
// return -1;
// }
//
// if ('=' == ch) {
// while (cNextPos < fRangeEnd) {
// ch = fDocument.getChar(cNextPos++);
// if (';' == ch)
// break;
// }
// return EXPRESSAO;
// }
//
// while (cNextPos < fRangeEnd) {
// ch = fDocument.getChar(cNextPos++);
// if (';' == ch)
// break;
// }
// return EXPRESSAO;
//
// } catch (BadLocationException e) {
// // should not happen, but we treat it as end of range
// return COMMENT_MULTILINHA;
// }
// }
//
// protected int classify(int pai, int nivel) {
// char ch;
// int resultado = -1;
// String elemento = "";
// try {
// // consume whitespaces
// do {
// ch = fDocument.getChar(cNextPos++);
// if (cNextPos > fRangeEnd)
// return -1;
// } while ((' ' == ch) || ('\t' == ch) || ('\n' == ch)
// || ('\r' == ch));
// cNextPos--;
// elemento = getElemento();
// if (!elemento.equals("")) {
// if (pai == START) {
// if (elemento.equalsIgnoreCase("lexer")) {
// return LEXER;
// }
// if (elemento.equalsIgnoreCase("parser")) {
// return PARSER;
// }
// }
// if (pai == PARSER || pai == LEXER) {
// if (elemento.equalsIgnoreCase("priority")) {
// return PRIORITY;
// }
// }
//
// if (pai == LEXER) {
// if (elemento.equalsIgnoreCase("token")) {
// return TOKEN;
// }
// if (elemento.equalsIgnoreCase("ignored")) {
// return IGNORED;
// }
// return EXPRESSAO;
// }
// if (pai == PARSER) {
// return EXPRESSAO;
// }
//
// } else if (nivel > 1) {
// return EXPRESSAO;
// }
// } catch (BadLocationException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// }
// return resultado;
//
// }
//
// private String getElemento() throws BadLocationException {
// String elemento = "";
// cNextPos -= 1;
// char ch;
// while (cNextPos < fRangeEnd) {
// ch = fDocument.getChar(cNextPos++);
// if (' ' == ch || ch == '\n' || ch == '\r' || ch == '\t') {
// break;
// }
// elemento += ch;
// }
//
// return elemento;
// }
//
// protected int eatToEndOfLine() throws BadLocationException {
// if (cNextPos >= fRangeEnd) {
// return 0;
// }
// char ch = fDocument.getChar(cNextPos++);
// // 1. eat all spaces and tabs
// while ((cNextPos < fRangeEnd) && ((' ' == ch) || ('\t' == ch))) {
// ch = fDocument.getChar(cNextPos++);
// }
// if (cNextPos >= fRangeEnd) {
// cNextPos--;
// return 0;
// }
//
// // now ch is a new line or a non-whitespace
// if ('\n' == ch) {
// if (cNextPos < fRangeEnd) {
// ch = fDocument.getChar(cNextPos++);
// if ('\r' != ch) {
// cNextPos--;
// }
// } else {
// cNextPos--;
// }
// return 1;
// }
//
// if ('\r' == ch) {
// if (cNextPos < fRangeEnd) {
// ch = fDocument.getChar(cNextPos++);
// if ('\n' != ch) {
// cNextPos--;
// }
// } else {
// cNextPos--;
// }
// return 1;
// }
//
// return 0;
// }
