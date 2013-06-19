package pogvue.gui;

import pogvue.gui.event.StatusEvent;
import pogvue.gui.event.StatusListener;
import pogvue.util.WindowUtil;

import java.awt.*;

public class StatusBar extends Label implements ControlledObjectI,
                                                StatusListener {
  private Controller controller;

  public StatusBar(String text, Controller c, int pos) {
    super(text,pos);
    setController(c);
  }

  public void setController(Controller c) {
    controller = c;
    controller.addListener(this);
  }

  public Controller getController() {
    return controller;
  }

  public void addNotify() {
    super.addNotify();
    controller.changeWindow(this);
  }

  public Object getControllerWindow() {
    return WindowUtil.getWindowAncestor(this);
  }

  public boolean handleStatusEvent(StatusEvent evt) {
    // System.out.println("Handling status event for " + evt.getText());
    if (evt.getType() == StatusEvent.INFO) {
      setText("Status: " + evt.getText());
    } else if (evt.getType() == StatusEvent.WARNING) {
      setText("Warning: " + evt.getText());
    } else if (evt.getType() == StatusEvent.ERROR) {
      setText("ERROR: " + evt.getText());
      System.out.print("\07");
    } else {
      System.out.println("ERROR: Unknown Status type = " + evt.getType());
    }
    //update();
    return true;
  }
}
