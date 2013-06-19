package pogvue.gui;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public final class PCAFrame extends Frame {
  private final Object parent;

  public PCAFrame(String title,Object parent) {
    super(title);
    this.parent = parent;
    addWindowListener(new PCAWindowListener());
  }

  class PCAWindowListener extends WindowAdapter {
    public void windowClosing(WindowEvent evt) {
      if (parent != null) {
        PCAFrame.this.hide();
        PCAFrame.this.dispose();
      } else if (parent == null) {
        System.exit(0);
      }
    }
  }
}
