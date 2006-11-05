// NetworkDriverAdapter.java

package jmri.jmrix.nce.networkdriver;

import jmri.jmrix.nce.NcePortController;
import jmri.jmrix.nce.NceProgrammer;
import jmri.jmrix.nce.NceProgrammerManager;
import jmri.jmrix.nce.NceTrafficController;

import java.io.*;
import java.net.*;
import java.util.Vector;

/**
 * Implements SerialPortAdapter for the NCE system network connection.
 * <P>This connects
 * an NCE command station via a telnet connection.
 * Normally controlled by the NetworkDriverFrame class.
 *
 * @author	Bob Jacobsen   Copyright (C) 2001, 2002, 2003
 * @version	$Revision: 1.2 $
 */
public class NetworkDriverAdapter extends NcePortController {

    /**
     * set up all of the other objects to operate with an NCE command
     * station connected to this port
     */
    public void configure() {
        // connect to the traffic controller
        NceTrafficController.instance().connectPort(this);

        jmri.InstanceManager.setProgrammerManager(
                new NceProgrammerManager(
                    new NceProgrammer()));

        jmri.InstanceManager.setPowerManager(new jmri.jmrix.nce.NcePowerManager());

        jmri.InstanceManager.setTurnoutManager(new jmri.jmrix.nce.NceTurnoutManager());

        jmri.jmrix.nce.ActiveFlag.setActive();
    }

    private Thread sinkThread;

    // base class methods for the NcePortController interface
    public DataInputStream getInputStream() {
        if (!opened) {
            log.error("getInputStream called before load(), stream not available");
        }
        try {
            return new DataInputStream(socket.getInputStream());
        } catch (java.io.IOException ex1) {
            log.error("Exception getting input stream: "+ex1);
            return null;
        }
    }

    public void connect(String host, int port) {
        try {
            socket = new Socket(host, port);
            opened = true;
        } catch (Exception e) {
            log.error("error opening NCE network connection: "+e);
        }
    }

    public DataOutputStream getOutputStream() {
        if (!opened) log.error("getOutputStream called before load(), stream not available");
        try {
            return new DataOutputStream(socket.getOutputStream());
        }
     	catch (java.io.IOException e) {
            log.error("getOutputStream exception: "+e);
     	}
     	return null;
    }

    public boolean status() {return opened;}

    // private control members
    private boolean opened = false;

    static public NetworkDriverAdapter instance() {
        if (mInstance == null) mInstance = new NetworkDriverAdapter();
        return mInstance;
    }
    static NetworkDriverAdapter mInstance = null;

    Socket socket;

    public Vector getPortNames() {
        log.error("Unexpected call to getPortNames");
        return null;
    }
    public String openPort(String portName, String appName)  {
        log.error("Unexpected call to openPort");
        return null;
    }
    public String[] validBaudRates() {
        log.error("Unexpected call to validBaudRates");
        return null;
    }

    static org.apache.log4j.Category log = org.apache.log4j.Category.getInstance(NetworkDriverAdapter.class.getName());

}
