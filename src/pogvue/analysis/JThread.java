package pogvue.analysis;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;
import java.beans.*;

import pogvue.datamodel.*;
import pogvue.datamodel.motif.*;
import pogvue.datamodel.comparer.*;
import pogvue.gui.AlignViewport;
import pogvue.gui.Controller;
import pogvue.gui.schemes.ResidueProperties;
import pogvue.io.FastaFile;
import pogvue.io.Pwm2File;

public class JThread extends Thread {
  Vector         out;

  Vector actionListeners;
  Vector propertyListeners;

  public boolean DONE = false;

  public JThread() {
    actionListeners = new Vector();
    propertyListeners = new Vector();
  }

  public JThread(ActionListener l) {
    addActionListener(l);
  }
  
  public void addActionListener(ActionListener l) {
    if (!actionListeners.contains(l)) {
      actionListeners.addElement(l);
    }
  }
  
  public void addPropertyChangeListener(PropertyChangeListener l) {
    if (!propertyListeners.contains(l)) {
      propertyListeners.addElement(l);
    }
  }
  
  public void setProgress(int val) {
    for (int i = 0; i < actionListeners.size(); i++) {
      ActionListener l = (ActionListener)actionListeners.elementAt(i);

      l.actionPerformed(new ActionEvent(this,val,"Progress"));
    }
    setProperty(val);
  }
  public void setProperty(int val) {
    for (int i = 0; i < propertyListeners.size(); i++) {
      PropertyChangeListener l = (PropertyChangeListener)propertyListeners.elementAt(i);

      l.propertyChange(new PropertyChangeEvent(this,"progress",0,val));
    }
  }

  protected void done() {
    DONE = true;
    
    for (int i = 0; i < actionListeners.size(); i++) {
      ActionListener l = (ActionListener)actionListeners.elementAt(i);

      ActionEvent e = new ActionEvent(this, 0, "DONE");
      l.actionPerformed(e);
    }
    
    for (int i = 0; i < propertyListeners.size(); i++) {
      PropertyChangeListener l = (PropertyChangeListener)propertyListeners.elementAt(i);

      l.propertyChange(new PropertyChangeEvent(this,"done",0,true));
    }
  }

  public Vector getOutput() {
    return out;
  }
}
