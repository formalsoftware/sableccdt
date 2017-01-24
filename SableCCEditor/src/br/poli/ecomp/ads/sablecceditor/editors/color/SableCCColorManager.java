package br.poli.ecomp.ads.sablecceditor.editors.color;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * Esta classe mapea cores RGB para o tipo de cor utilizado pelo Eclipse.
 * 
 * @author GUSTAVO, RENATO
 *
 */

public class SableCCColorManager {

	protected Map fColorTable = new HashMap(10);

	public void dispose() {
		Iterator e = fColorTable.values().iterator();
		while (e.hasNext())
			 ((Color) e.next()).dispose();
	}
	
	/**
	 * 
	 * @param rgb
	 * @return
	 */
	
	public Color getColor(RGB rgb) {
		Color color = (Color) fColorTable.get(rgb);
		if (color == null) {
			color = new Color(Display.getCurrent(), rgb);
			fColorTable.put(rgb, color);
		}
		return color;
	}
}
