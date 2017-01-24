package br.poli.ecomp.ads.sablecceditor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import br.poli.ecomp.ads.sablecceditor.editors.scanner.SableCCPartitionScanner;


public class SableCCPlugin extends AbstractUIPlugin {
	private static final String PREFIX = "Editor.";
	public static final String PLUGIN_ID = "br.poli.ecomp.ads.sablecceditor.editors";
    public final static String SABLECC_PARTITIONING = "___sablecc__partitioning____";
    private static SableCCPlugin sableCCplugin;
    
    private SableCCPartitionScanner fPartitionScanner;
    
    public void start(BundleContext context) throws Exception {
		super.start(context);
		sableCCplugin = this;
	}

    public SableCCPartitionScanner getSableCCPartitionScanner() {
        if (fPartitionScanner == null)
            fPartitionScanner= new SableCCPartitionScanner();
        return fPartitionScanner;
    }
    
    public static SableCCPlugin getDefault() {
		return sableCCplugin;
	}
}
