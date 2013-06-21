package pogvue.gui;

import pogvue.util.WindowUtil;

import javax.swing.*;
import java.util.EventListener;

public class ControlledPanel extends JPanel implements ControlledObjectI,
					               EventListener {
  Controller controller;

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
}
