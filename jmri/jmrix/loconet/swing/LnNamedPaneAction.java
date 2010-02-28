// LnNamedPaneAction.java

package jmri.jmrix.loconet.swing;

import javax.swing.*;

import jmri.jmrix.loconet.LocoNetSystemConnectionMemo;
import jmri.util.swing.*;

/**
 * Action to create and load a JmriPanel from just its name.
 *
 * @author		Bob Jacobsen Copyright (C) 2010
 * @version		$Revision: 1.1 $
 */
 
public class LnNamedPaneAction extends jmri.util.swing.JmriNamedPaneAction {

    /**
     * Enhanced constructor for placing the pane in various 
     * GUIs
     */
 	public LnNamedPaneAction(String s, WindowInterface wi, String paneClass, LocoNetSystemConnectionMemo memo) {
    	super(s, wi, paneClass);
    	this.memo = memo;
    }
    
 	public LnNamedPaneAction(String s, Icon i, WindowInterface wi, String paneClass, LocoNetSystemConnectionMemo memo) {
    	super(s, i, wi, paneClass);
    	this.memo = memo;
    }
    
    LocoNetSystemConnectionMemo memo;
    
    public JmriPanel makePanel() {
        JmriPanel p = super.makePanel();
        if (p == null) return null;
        
        try {
            ((LnPanelInterface)p).initComponents(memo);
            return p;
        } catch (Exception ex) {
            log.warn("could not init pane class: "+paneClass+" due to:"+ex);
            ex.printStackTrace();
        }      
        
        return p;
    }

    static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(LnNamedPaneAction.class.getName());
}

/* @(#)LnNamedPaneAction.java */
