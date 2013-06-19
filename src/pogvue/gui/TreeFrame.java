package pogvue.gui;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public final class TreeFrame extends Frame {

  private final Object parent;
  private final TreePanel treePanel;

  public TreeFrame(Object parent,TreePanel p) {
      super("Jalview Tree");
      this.parent    = parent;
      this.treePanel = p;

      setLayout(new BorderLayout());
      add("Center",treePanel);

      addWindowListener(new TreeFrameWindowListener(this));
  }

  final class TreeFrameWindowListener extends WindowAdapter {

    final TreeFrame af;
    
    public TreeFrameWindowListener(TreeFrame f) {
      af = f;
      af.addWindowListener(this);
    }
    
    public void windowClosing(WindowEvent evt) {
      af.setVisible(false);
      af.dispose();
    }
  }
}





