package pogvue.gui;

import pogvue.gui.event.*;
import pogvue.util.ListenList;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Hashtable;
import java.util.Vector;


/**
 * An event controller forwarding multiple types of event to registered listeners.
 */
public final class Controller extends ListenList implements 
                                                      FontChangeListener,
                                                      AlignViewportListener,
                                                      StatusListener,
                                                      SequenceSelectionListener,
                                                      WindowListener {
  private final Hashtable windowHash = new Hashtable();
  private long prevmillis = 0;

  public Controller() {
    super();
  }

  // addListener adds a listener and if the listener is a known type
  // which is displayed in a window it adds a WindowListener to
  // to allow automatic removal of the listeners when the Window closes
  public void addListener (EventListener l) {
    Window w = null;
    Object wobj;

    super.addListener(l);

    if (l instanceof ControlledObjectI) {
      wobj = ((ControlledObjectI)l).getControllerWindow();
      if (wobj instanceof Window) {
        w = (Window)wobj;
        //              System.err.println("YES Window returned from getControllerWindow for " + l.getClass().getName() + " is " + wobj);
      }
      else if (wobj != null) {
        System.err.println("Non Window returned from getControllerWindow for " + l.getClass().getName() + " is " + wobj);
      }

    } else {
      System.out.println("Non ControlledObjectI Controller listener " +
                         l.getClass().getName());
    }

    if (w != null) {
      addWindowChild(l,w);

      // System.out.println("Success adding listener " + l.getClass().getName());
    } else {
      // System.out.println("Controller listener with no parent " +
      //                    l.getClass().getName());
    }

  }

  private void addWindowChild(EventListener l, Window w) {
    ControlledWindow cw;
    if (windowHash.containsKey(w)) {
      cw = (ControlledWindow)windowHash.get(w);
    } else {
      w.addWindowListener(this);
      cw = new ControlledWindow(w);
      windowHash.put(w,cw);
    }
    if (cw != null) {
      cw.addChild(l);
    }
  }

  public Vector getWindowList() {
    return getWindowList(null);
  }

  private Vector getWindowList(Window skipMe) {
    Vector out = new Vector();
    Enumeration e = windowHash.keys();
    while(e.hasMoreElements()) {
      Window win = (Window) e.nextElement();
      if (!win.equals(skipMe))
        out.addElement(win);
    }
    return out;
  }

  public void removeListener (EventListener l) {
    super.removeListener(l);

    if (l instanceof ControlledObjectI) {
      removeControlledObject((ControlledObjectI)l);
    }
  }

  // These implement the listener interfaces

  public boolean handleFontChangeEvent (FontChangeEvent evt) {
    for (int i=listeners.size()-1 ; i >= 0; i--) {
      EventListener l = (EventListener)listeners.elementAt(i);

      if (l instanceof FontChangeListener && l != evt.getSource()) {
        ((FontChangeListener)l).handleFontChangeEvent(evt);
      }
    }
    return true;
  }

  public boolean handleSequenceSelectionEvent (SequenceSelectionEvent evt) {
    for (int i=listeners.size()-1 ; i >= 0; i--) {
      EventListener l = (EventListener)listeners.elementAt(i);

      if (l instanceof SequenceSelectionListener && l != evt.getSource()) {
        ((SequenceSelectionListener)l).handleSequenceSelectionEvent(evt);
      }
    }
    return true;
  }

  public boolean handleAlignViewportEvent (AlignViewportEvent evt) {

    long currmillis = System.currentTimeMillis();

    //if (currmillis - prevmillis > 100) {
      for (int i=listeners.size()-1 ; i >= 0; i--) {
	EventListener l = (EventListener)listeners.elementAt(i);
	
	
	if (l instanceof AlignViewportListener) {// && l != evt.getSource()) {
	  //  System.out.println("Handling event " + l) ;
	  ((AlignViewportListener)l).handleAlignViewportEvent(evt);
	}
      }
      //}
    prevmillis = System.currentTimeMillis();
    return true;
  }

  public boolean handleStatusEvent (StatusEvent evt) {
    for (int i=listeners.size()-1 ; i >= 0; i--) {
      EventListener l = (EventListener)listeners.elementAt(i);

      if (l instanceof StatusListener && l != evt.getSource()) {
        ((StatusListener)l).handleStatusEvent(evt);
      }
    }
    return true;
  }

  // Other events that we'd want to handle in this class
  //  - QuitEvent
  //  - WindowCloseEvent
  //  - NewWindowEvent

  private void removeControlledObject(ControlledObjectI co) {
    Window w = getWindowForChild(co);
    if (w != null) {
      ControlledWindow cw = (ControlledWindow)windowHash.get(w);
      if (cw != null) {
        cw.removeChild(co);
  
        Vector children = cw.getChildren();
        if (children.size() == 0) {
          cw.getWindow().removeWindowListener(this);
        }
      }
    }
  }

  public void changeWindow(ControlledObjectI co) {
    // System.out.println("Controller got changeWindow");
    removeControlledObject(co);

    Object wobj = co.getControllerWindow();
    if (wobj instanceof Window) {
      Window w = (Window)wobj;
      // System.out.println("New window for listener = " + w);
      addWindowChild((EventListener)co,w);
    }
  }

  private Window getWindowForChild(Object child) {
    Vector out = new Vector();
    Enumeration e = windowHash.keys();
    while(e.hasMoreElements()) {
      Window win = (Window) e.nextElement();
      if (((ControlledWindow)windowHash.get(win)).contains(child)) {
        return win;
      }
    }
    return null;
  }


  // WindowListener methods
  public void windowClosed(WindowEvent e) {
    System.out.println("Controller got WindowClosed event");
    removeListeners(e);
  }

  private void removeListeners(WindowEvent e) {
    ControlledWindow cw = (ControlledWindow)windowHash.get(e.getSource());

    if (cw != null) {
      Vector children = cw.getChildren();

      for (int i=0; i<children.size(); i++) {
        removeListener((EventListener)children.elementAt(i));
      }
      windowHash.remove(e.getSource());
      cw.getWindow().removeWindowListener(this);
    }
  }

  public void windowClosing(WindowEvent e) {
    System.out.println("Controller got WindowClosing event");
    removeListeners(e);
  }

  public void windowOpened(WindowEvent e) {}

  public void windowIconified(WindowEvent e) {}

  public void windowDeiconified(WindowEvent e) {}

  public void windowActivated(WindowEvent e) {}

  public void windowDeactivated(WindowEvent e) {}

}

