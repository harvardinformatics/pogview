package pogvue.gui.menus;


import pogvue.gui.AlignViewport;
import pogvue.gui.Controller;
import pogvue.gui.event.StatusEvent;

import java.applet.Applet;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.*;

public class HelpMenu extends PanelMenu {
  public HelpMenu(JPanel panel,AlignViewport av,Controller c) {
    super("Help",panel,av,c);
  }

  protected void init() {
  }
}
