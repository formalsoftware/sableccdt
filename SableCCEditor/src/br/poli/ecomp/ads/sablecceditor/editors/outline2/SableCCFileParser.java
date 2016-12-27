/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package br.poli.ecomp.ads.sablecceditor.editors.outline2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

import br.poli.ecomp.adssableccbuilder.builder.Echo;
import br.poli.ecomp.adssableccbuilder.builder.Element;

/**
 * This class is a simple parser implementing the IReadmeFileParser
 * interface. It parses a Readme file into sections based on the
 * existence of numbered section tags in the input. A line beginning
 * with a number followed by a dot will be taken as a section indicator
 * (for example, 1., 2., or 12.). 
 * As well, a line beginning with a subsection-style series of numbers
 * will also be taken as a section indicator, and can be used to 
 * indicate subsections (for example, 1.1, or 1.1.12).
 */
public class SableCCFileParser implements IReadmeFileParser {
    /**
     * Returns the mark element that is the logical parent
     * of the given mark number.  Each dot in a mark number
     * represents a parent-child separation.  For example,
     * the parent of 1.2 is 1, the parent of 1.4.1 is 1.4.
     * Returns null if there is no appropriate parent.
     */
	LinkedList<MarkElement> markers = new LinkedList<MarkElement>();
    protected IAdaptable getParent(Hashtable toc, String number) {
        int lastDot = number.lastIndexOf('.');
        if (lastDot < 0)
            return null;
        String parentNumber = number.substring(0, lastDot);
        return (IAdaptable) toc.get(parentNumber);
    }

    /**
     * Returns a string containing the contents of the given
     * file.  Returns an empty string if there were any errors
     * reading the file.
     */
    protected String getText(IFile file) {
        try {
            InputStream in = file.getContents();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int read = in.read(buf);
            while (read > 0) {
                out.write(buf, 0, read);
                read = in.read(buf);
            }
            return out.toString();
        } catch (CoreException e) {
            // do nothing
        } catch (IOException e) {
            // do nothing
        }
        return ""; //$NON-NLS-1$
    }

    /**
     * Parses the input given by the argument.
     *
     * @param file  the element containing the input text
     * @return an element collection representing the parsed input
     */
    public MarkElement[] parse(IFile file) {
    	Echo echo = new Echo();
		echo.file = file;
		System.out.println("Echo: "+echo.file.getName());
		return new MarkElement[8];
    }
    
    public MarkElement[] parseFile(IFile file, IDocument document) {
    	Echo echo = new Echo();
		echo.file = file;
		echo.printAST();
		IFile parent = file;
		Iterator iterator = echo.elementos.iterator();
		Element parentElement = null;
		MarkElement parentMarkElement = null;
		
		while(iterator.hasNext()){
			MarkElement m;
			Element e = (Element) iterator.next();
			
			//VERIFICA SE O ELEMENTO PAI É NULL
			if(e.getParent()==null){
				//GET O OFFSET DO ELEMENTO
				int offset = 0;
				try {
					int linha = (e.getLine()-1)>=0 ? (e.getLine()-1) : 0;  
					offset = document.getLineOffset(linha);
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//CRIA O ELEMENTO
				m = new MarkElement(parent, e.getToken(), offset, e.getToken().length());
				//SETA O PARENT CORRENTE
				parentMarkElement = m;
				parentElement = e;
				markers.add(m);
			//VERIFICAR SE ALTEROU O ELEMENTO PARENT
			}else if(parentElement != e.getParent()){
				//Mudou o parent do element
				//Consultar a lista pra ver se ja existe o parent (TO DO)
				m = new MarkElement(parentMarkElement, e.getToken(), e.getLine(), e.getToken().length());
				parentMarkElement = m;
				parentElement = e.getParent();	
			//CRIA OS ELEMENTOS FILHOS
			}else{
				int offset = 0;
				try {
					int linha = (e.getLine()-1)>=0 ? (e.getLine()-1) : 0;  
					offset = document.getLineOffset(linha);
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				m = new MarkElement(parentMarkElement, e.getToken(), offset, e.getToken().length());
				parentElement = e.getParent(); //Caso o parent seja alterado
			}
			
		}
		
		
		MarkElement[] markArray = new MarkElement[markers.size()];
		return markers.toArray(markArray);
    }


    /**
     * Creates a section name from the buffer and trims trailing
     * space characters.
     *
     * @param buffer  the string from which to create the section name
     * @param start  the start index
     * @param end  the end index
     * @return a section name
     */
    private String parseHeading(String buffer, int start, int end) {
        while (Character.isWhitespace(buffer.charAt(end - 1)) && end > start) {
            end--;
        }
        //return buffer.substring(start, end);
        return " ";
    }

    /**
     * Returns the number for this heading.  A heading consists
     * of a number (an arbitrary string of numbers and dots), followed by
     * arbitrary text.
     */
    protected String parseNumber(String heading) {
        int start = 0;
        int end = heading.length();
        char c;
        do {
            c = heading.charAt(start++);
        } while ((c == '.' || Character.isDigit(c)) && start < end);

        //disregard trailing dots
        while (heading.charAt(start - 1) == '.' && start > 0) {
            start--;
        }
        return heading.substring(0, start);
    }
}
