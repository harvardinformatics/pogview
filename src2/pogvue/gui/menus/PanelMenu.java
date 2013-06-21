package pogvue.gui.menus;

import pogvue.gui.AlignViewport;
import pogvue.gui.Controller;

import javax.swing.*;

public abstract class PanelMenu extends JMenu {
	private JPanel panel;
  final AlignViewport av;
  final Controller    controller;
  
  public PanelMenu(String title, JPanel panel,AlignViewport av,Controller controller) {
    super(title,true);
    this.av    = av;
    this.controller = controller;
    this.panel = panel;
  }
}
