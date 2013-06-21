package pogvue.gui;

import pogvue.gui.event.*;

public class ControllerDebugListener implements AlignViewportListener,
                                                ControlledObjectI,
                                                FontChangeListener,
                                                SequenceSelectionListener {
  Controller controller;
  public ControllerDebugListener(Controller c) {
    setController(c);
  }

  public void setController(Controller c) {
    controller = c;
    controller.addListener(this);
  }
 
  public Controller getController() {
    return controller;
  }
 
  public Object getControllerWindow() {
    return null;
  }

  public boolean handleAlignViewportEvent(AlignViewportEvent evt) {
    return true;
  }
  public boolean handleFontChangeEvent(FontChangeEvent evt) {
    return true;
  }
  public boolean handleSequenceSelectionEvent(SequenceSelectionEvent evt) {
    System.out.println("handleSequenceSelectionEvent with " + evt.getSelection());
    return true;
  }
}
